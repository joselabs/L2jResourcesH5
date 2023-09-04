/*
 * $Header: MinionList.java, 25/10/2005 18:42:48 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 25/10/2005 18:42:48 $
 * $Revision: 1 $
 * $Log: MinionList.java,v $
 * Revision 1  25/10/2005 18:42:48  luisantonioa
 * Added copyright notice
 *
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.util;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2MinionData;
import net.sf.l2j.gameserver.model.actor.instance.L2MinionInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.templates.chars.L2NpcTemplate;
import net.sf.l2j.util.Rnd;

public class MinionList
{
private static Logger _log = Logger.getLogger(L2MonsterInstance.class.getName());

/** List containing the current spawned minions for this L2MonsterInstance */
private final List<L2MinionInstance> minionReferences;
protected FastMap<Long, Integer> _respawnTasks = new FastMap<Long, Integer>().shared();
private final L2MonsterInstance master;

public MinionList(L2MonsterInstance pMaster)
{
	minionReferences = new FastList<L2MinionInstance>();
	master = pMaster;
}

public int countSpawnedMinions()
{
	synchronized (minionReferences)
	{
		return minionReferences.size();
	}
}

public int countSpawnedMinionsById(int minionId)
{
	int count = 0;
	synchronized (minionReferences)
	{
		for (L2MinionInstance minion : getSpawnedMinions())
		{
			if (minion.getNpcId() == minionId)
			{
				count++;
			}
		}
	}
	return count;
}

public boolean hasMinions()
{
	return !getSpawnedMinions().isEmpty();
}

public List<L2MinionInstance> getSpawnedMinions()
{
	return minionReferences;
}

public void addSpawnedMinion(L2MinionInstance minion)
{
	synchronized (minionReferences)
	{
		minionReferences.add(minion);
	}
}

public int lazyCountSpawnedMinionsGroups()
{
	Set<Integer> seenGroups = new FastSet<Integer>();
	for (L2MinionInstance minion : getSpawnedMinions())
	{
		if (minion == null)
			continue;
		
		seenGroups.add(minion.getNpcId());
	}
	return seenGroups.size();
}

public void removeSpawnedMinion(L2MinionInstance minion)
{
	synchronized (minionReferences)
	{
		minionReferences.remove(minion);
	}
}

public void moveMinionToRespawnList(L2MinionInstance minion)
{
	Long current = System.currentTimeMillis();
	synchronized (minionReferences)
	{
		minionReferences.remove(minion);
		if (_respawnTasks.get(current) == null)
			_respawnTasks.put(current, minion.getNpcId());
		else
		{
			// nice AoE
			for (int i = 1; i < 50; i++)
			{
				if (_respawnTasks.get(current + i) == null)
				{
					_respawnTasks.put(current + i, minion.getNpcId());
					break;
				}
			}
		}
	}
}

public void clearRespawnList()
{
	_respawnTasks.clear();
}

/**
 * Manage respawning of minions for this RaidBoss.<BR><BR>
 */
public void maintainMinions()
{
	if (master == null || master.isAlikeDead() || master instanceof L2MinionInstance)
		return;
	Long current = System.currentTimeMillis();
	
	if (_respawnTasks != null)
	{
		double delay = Config.RAID_MINION_RESPAWN_TIMER;
		
		if (!master.isInCombat())
			delay /= 2;
		
		for (long deathTime : _respawnTasks.keySet())
		{
			if ((current - deathTime) > delay)
			{
				spawnSingleMinion(_respawnTasks.get(deathTime), master.getInstanceId());
				_respawnTasks.remove(deathTime);
			}
		}
	}
}

/**
 * Manage the spawn of all Minions of this RaidBoss.<BR><BR>
 *
 * <B><U> Actions</U> :</B><BR><BR>
 * <li>Get the Minion data of all Minions that must be spawn </li>
 * <li>For each Minion type, spawn the amount of Minion needed </li><BR><BR>
 *
 * @param player The L2PcInstance to attack
 *
 */
public void spawnMinions()
{
	if (master == null || master.isAlikeDead() || master instanceof L2MinionInstance)
		return;
	List<L2MinionData> minions = master.getTemplate().getMinionData();
	
	if (minions == null)
		return;
	synchronized (minionReferences)
	{
		int minionCount, minionId, minionsToSpawn;
		for (L2MinionData minion : minions)
		{
			minionCount = minion.getAmount();
			minionId = minion.getMinionId();
			
			minionsToSpawn = minionCount - countSpawnedMinionsById(minionId);
			
			for (int i = 0; i < minionsToSpawn; i++)
			{
				spawnSingleMinion(minionId, master.getInstanceId());
			}
		}
	}
}

public void recallMinions()
{
	if (master == null || master.isAlikeDead())
		return;
	
	for (L2MinionInstance minion : minionReferences)
	{
		if (minion != null && !minion.isAlikeDead())
		{
			minion.teleToLocation(master.getX(), master.getY(), master.getZ(), true);
			minion.healHP();
		}
	}
}

/**
 * Init a Minion and add it in the world as a visible object.<BR><BR>
 *
 * <B><U> Actions</U> :</B><BR><BR>
 * <li>Get the template of the Minion to spawn </li>
 * <li>Create and Init the Minion and generate its Identifier </li>
 * <li>Set the Minion HP, MP and Heading </li>
 * <li>Set the Minion leader to this RaidBoss </li>
 * <li>Init the position of the Minion and add it in the world as a visible object </li><BR><BR>
 *
 * @param minionid The I2NpcTemplate Identifier of the Minion to spawn
 *
 */
public void spawnSingleMinion(int minionId, int instanceId)
{
	// Get the template of the Minion to spawn
	L2NpcTemplate minionTemplate = NpcTable.getInstance().getTemplate(minionId);
	
	// Create and Init the Minion and generate its Identifier
	L2MinionInstance monster = new L2MinionInstance(IdFactory.getInstance().getNextId(), minionTemplate);
	
	// Set the Minion HP, MP and Heading
	monster.setCurrentHpMp(monster.getMaxHp(), monster.getMaxMp());
	monster.setHeading(master.getHeading());
	
	// Set the Minion leader to this RaidBoss
	monster.setLeader(master);
	
	//move monster to masters instance
	monster.setInstanceId(instanceId);
	
	// Init the position of the Minion and add it in the world as a visible object
	int spawnConstant;
	int randSpawnLim = 170;
	int randPlusMin = 1;
	spawnConstant = Rnd.nextInt(randSpawnLim);
	//randomize +/-
	randPlusMin = Rnd.nextInt(2);
	if (randPlusMin == 1)
		spawnConstant *= -1;
	int newX = master.getX() + Math.round(spawnConstant);
	spawnConstant = Rnd.nextInt(randSpawnLim);
	//randomize +/-
	randPlusMin = Rnd.nextInt(2);
	if (randPlusMin == 1)
		spawnConstant *= -1;
	int newY = master.getY() + Math.round(spawnConstant);
	
	monster.spawnMe(newX, newY, master.getZ()+5);
	
	if (Config.DEBUG)
		_log.fine("Spawned minion template " + minionTemplate.npcId + " with objid: " + monster.getObjectId() + " to boss " + master.getObjectId() + " ,at: " + monster.getX() + " x, " + monster.getY() + " y, " + monster.getZ() + " z");
}
}
