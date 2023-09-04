/*
 * Copyright © 2004-2019 L2JDevs
 * 
 * This file is part of L2JDevs.
 * 
 * L2JDevs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2JDevs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.individual;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.l2jdevs.Config;
import org.l2jdevs.gameserver.ai.CtrlIntention;
import org.l2jdevs.gameserver.enums.audio.Music;
import org.l2jdevs.gameserver.instancemanager.GrandBossManager;
import org.l2jdevs.gameserver.model.Location;
import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.actor.L2Attackable;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.L2Playable;
import org.l2jdevs.gameserver.model.actor.instance.L2GrandBossInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.holders.SkillHolder;
import org.l2jdevs.gameserver.model.skills.CommonSkill;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.model.zone.type.L2BossZone;
import org.l2jdevs.gameserver.network.serverpackets.MagicSkillUse;

import ai.npc.AbstractNpcAI;

/**
 * Queen Ant's AI
 * @author Emperorc, Sacrifice
 */
public final class QueenAnt extends AbstractNpcAI
{
	private static final int QUEEN = 29001;
	private static final int LARVA = 29002;
	private static final int NURSE = 29003;
	private static final int GUARD = 29004;
	private static final int ROYAL = 29005;
	
	private static final int[] MOBS =
	{
		QUEEN,
		LARVA,
		NURSE,
		GUARD,
		ROYAL
	};
	
	private static final Location OUST_LOC_1 = new Location(-19480, 187344, -5600);
	private static final Location OUST_LOC_2 = new Location(-17928, 180912, -5520);
	private static final Location OUST_LOC_3 = new Location(-23808, 182368, -5600);
	
	private static final int QUEEN_X = -21610;
	private static final int QUEEN_Y = 181594;
	private static final int QUEEN_Z = -5734;
	
	private static final byte ALIVE = 0;
	private static final byte DEAD = 1;
	
	private static L2BossZone _zone;
	
	private static SkillHolder HEAL1 = new SkillHolder(4020, 1);
	private static SkillHolder HEAL2 = new SkillHolder(4024, 1);
	
	private L2MonsterInstance _queen = null;
	private L2MonsterInstance _larva = null;
	private final List<L2MonsterInstance> _nurses = new CopyOnWriteArrayList<>();
	
	private final GrandBossManager _grandBossManager = GrandBossManager.getInstance();
	
	private QueenAnt()
	{
		super(QueenAnt.class.getSimpleName(), "ai/individual");
		addSpawnId(MOBS);
		addKillId(MOBS);
		addAggroRangeEnterId(MOBS);
		addFactionCallId(NURSE);
		
		_zone = _grandBossManager.getZone(12012);
		final StatsSet info = _grandBossManager.getStatsSet(QUEEN);
		final int status = _grandBossManager.getBossStatus(QUEEN);
		if (status == DEAD)
		{
			// load the unlock date and time for queen ant from DB
			final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
			// if queen ant is locked until a certain time, mark it so and start the unlock timer
			// the unlock time has not yet expired.
			if (temp > 0)
			{
				startQuestTimer("queen_unlock", temp, null, null);
			}
			else
			{
				// the time has already expired while the server was offline. Immediately spawn queen ant.
				final L2GrandBossInstance queen = (L2GrandBossInstance) addSpawn(QUEEN, QUEEN_X, QUEEN_Y, QUEEN_Z, 0, false, 0);
				_grandBossManager.setBossStatus(QUEEN, ALIVE);
				spawnBoss(queen);
			}
		}
		else
		{
			int loc_x = info.getInt("loc_x");
			int loc_y = info.getInt("loc_y");
			int loc_z = info.getInt("loc_z");
			final int heading = info.getInt("heading");
			final int hp = info.getInt("currentHP");
			final int mp = info.getInt("currentMP");
			if (!_zone.isInsideZone(loc_x, loc_y, loc_z))
			{
				loc_x = QUEEN_X;
				loc_y = QUEEN_Y;
				loc_z = QUEEN_Z;
			}
			final L2GrandBossInstance queen = (L2GrandBossInstance) addSpawn(QUEEN, loc_x, loc_y, loc_z, heading, false, 0);
			queen.setCurrentHpMp(hp, mp);
			spawnBoss(queen);
		}
	}
	
	public static void main(String[] args)
	{
		new QueenAnt();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "heal":
			{
				boolean notCasting;
				final boolean larvaNeedHeal = (_larva != null) && (_larva.getCurrentHp() < _larva.getMaxHp());
				final boolean queenNeedHeal = (_queen != null) && (_queen.getCurrentHp() < _queen.getMaxHp());
				
				for (L2MonsterInstance nurse : _nurses)
				{
					if ((nurse == null) || nurse.isDead() || nurse.isCastingNow())
					{
						continue;
					}
					
					notCasting = nurse.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST;
					
					if (larvaNeedHeal)
					{
						if ((nurse.getTarget() != _larva) || notCasting)
						{
							nurse.setTarget(_larva);
							nurse.useMagic(getRandomBoolean() ? HEAL1.getSkill() : HEAL2.getSkill());
						}
						continue;
					}
					
					if (queenNeedHeal)
					{
						if (nurse.getLeader() == _larva)
						{
							continue;
						}
						
						if ((nurse.getTarget() != _queen) || notCasting)
						{
							nurse.setTarget(_queen);
							nurse.useMagic(HEAL1.getSkill());
						}
						continue;
					}
					
					// if nurse not casting - remove target
					if (notCasting && (nurse.getTarget() != null))
					{
						nurse.setTarget(null);
					}
				}
				break;
			}
			case "action":
			{
				if (npc != null)
				{
					if (getRandom(3) == 0)
					{
						if (getRandom(2) == 0)
						{
							npc.broadcastSocialAction(3);
						}
						else
						{
							npc.broadcastSocialAction(4);
						}
					}
				}
				break;
			}
			case "queen_unlock":
			{
				final L2GrandBossInstance queen = (L2GrandBossInstance) addSpawn(QUEEN, QUEEN_X, QUEEN_Y, QUEEN_Z, 0, false, 0);
				_grandBossManager.setBossStatus(QUEEN, ALIVE);
				spawnBoss(queen);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if ((npc == null) || (player.isGM() && player.isInvisible()))
		{
			return null;
		}
		
		final boolean isMage;
		final L2Playable character;
		
		if (isSummon)
		{
			isMage = false;
			character = player.getSummon();
		}
		else
		{
			isMage = player.isMageClass();
			character = player;
		}
		
		if (character == null)
		{
			return null;
		}
		
		if (!Config.RAID_DISABLE_CURSE && ((character.getLevel() - npc.getLevel()) > Config.L2JMOD_RAID_CURSE_LEVEL_DIFF))
		{
			Skill curse = null;
			if (isMage)
			{
				if (!character.isMuted() && (getRandom(4) == 0))
				{
					curse = CommonSkill.RAID_CURSE.getSkill();
				}
			}
			else
			{
				if (!character.isParalyzed() && (getRandom(4) == 0))
				{
					curse = CommonSkill.RAID_CURSE2.getSkill();
				}
			}
			
			if (curse != null)
			{
				npc.broadcastPacket(new MagicSkillUse(npc, character, curse.getId(), curse.getLevel(), 300, 0));
				curse.applyEffects(npc, character);
			}
			((L2Attackable) npc).stopHating(character); // for calling again
			return null;
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isSummon)
	{
		if ((caller == null) || (npc == null))
		{
			return super.onFactionCall(npc, caller, attacker, isSummon);
		}
		
		if (!npc.isCastingNow() && (npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST))
		{
			if (caller.getCurrentHp() < caller.getMaxHp())
			{
				npc.setTarget(caller);
				((L2Attackable) npc).useMagic(HEAL1.getSkill());
			}
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (npc.getId() == QUEEN)
		{
			npc.broadcastPacket(Music.BS02_D_10000.getPacket());
			_grandBossManager.setBossStatus(QUEEN, DEAD);
			// Calculate Min and Max respawn times randomly.
			long respawnTime = Config.QUEEN_ANT_SPAWN_INTERVAL + getRandom(-Config.QUEEN_ANT_SPAWN_RANDOM, Config.QUEEN_ANT_SPAWN_RANDOM);
			respawnTime *= 3600000;
			startQuestTimer("queen_unlock", respawnTime, null, null);
			cancelQuestTimer("action", npc, null);
			cancelQuestTimer("heal", null, null);
			// also save the respawn time so that the info is maintained past reboots
			final StatsSet info = GrandBossManager.getInstance().getStatsSet(QUEEN);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			_grandBossManager.setStatsSet(QUEEN, info);
			_nurses.clear();
			_larva.deleteMe();
			_larva = null;
			_queen = null;
		}
		else if ((_queen != null) && !_queen.isAlikeDead())
		{
			if (npc.getId() == ROYAL)
			{
				final L2MonsterInstance mob = (L2MonsterInstance) npc;
				if (mob.getLeader() != null)
				{
					mob.getLeader().getMinionList().onMinionDie(mob, (280 + getRandom(40)) * 1000);
				}
			}
			else if (npc.getId() == NURSE)
			{
				final L2MonsterInstance mob = (L2MonsterInstance) npc;
				_nurses.remove(mob);
				if (mob.getLeader() != null)
				{
					mob.getLeader().getMinionList().onMinionDie(mob, 10000);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		final L2MonsterInstance mob = (L2MonsterInstance) npc;
		switch (npc.getId())
		{
			case LARVA:
				mob.setIsImmobilized(true);
				mob.setIsMortal(false);
				mob.setIsRaidMinion(true);
				break;
			case NURSE:
				mob.disableCoreAI(true);
				mob.setIsRaidMinion(true);
				_nurses.add(mob);
				break;
			case ROYAL:
			case GUARD:
				mob.setIsRaidMinion(true);
				break;
		}
		return super.onSpawn(npc);
	}
	
	private void spawnBoss(L2GrandBossInstance npc)
	{
		_grandBossManager.addBoss(npc);
		
		if (getRandom(100) < 33)
		{
			_zone.movePlayersTo(OUST_LOC_1);
		}
		else if (getRandom(100) < 50)
		{
			_zone.movePlayersTo(OUST_LOC_2);
		}
		else
		{
			_zone.movePlayersTo(OUST_LOC_3);
		}
		_grandBossManager.addBoss(npc);
		startQuestTimer("action", 10000, npc, null, true);
		startQuestTimer("heal", 1000, null, null, true);
		npc.broadcastPacket(Music.BS01_A_10000.getPacket());
		_queen = npc;
		_larva = (L2MonsterInstance) addSpawn(LARVA, -21600, 179482, -5846, getRandom(360), false, 0);
	}
}
