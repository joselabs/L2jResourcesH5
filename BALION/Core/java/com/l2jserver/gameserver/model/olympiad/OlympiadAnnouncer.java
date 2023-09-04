/*
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
package com.l2jserver.gameserver.model.olympiad;

import java.util.Set;

import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

/**
 * @author DS
 */
public final class OlympiadAnnouncer implements Runnable
{
	private static final int OLY_MANAGER = 31688;
	
	private final Set<L2Spawn> _managers;
	private int _currentStadium = 0;
	
	public OlympiadAnnouncer()
	{
		_managers = SpawnTable.getInstance().getSpawns(OLY_MANAGER);
	}
	
	@Override
	public void run()
	{
		OlympiadGameTask task;
		for (int i = OlympiadGameManager.getInstance().getNumberOfStadiums(); --i >= 0; _currentStadium++)
		{
			if (_currentStadium >= OlympiadGameManager.getInstance().getNumberOfStadiums())
			{
				_currentStadium = 0;
			}
			
			task = OlympiadGameManager.getInstance().getOlympiadTask(_currentStadium);
			if ((task != null) && (task.getGame() != null) && task.needAnnounce())
			{
				String npcString;
				final String arenaId = String.valueOf(task.getGame().getStadiumId() + 1);
				switch (task.getGame().getType())
				{
					case NON_CLASSED:
						npcString = "Olympiad class-free individual match is going to begin in Arena " + arenaId + " in a moment.";
						break;
					case CLASSED:
						npcString = "Olympiad class individual match is going to begin in Arena " + arenaId + " in a moment.";
						break;
					case TEAMS:
						npcString = "Olympiad class-free team match is going to begin in Arena " + arenaId + " in a moment.";
						break;
					default:
						continue;
				}
				
				L2Npc manager;
				CreatureSay packet;
				for (L2Spawn spawn : _managers)
				{
					manager = spawn.getLastSpawn();
					if (manager != null)
					{
						packet = new CreatureSay(manager.getObjectId(), Say2.SHOUT, manager.getName(), npcString);
						manager.broadcastPacket(packet);
					}
				}
				break;
			}
		}
	}
}
