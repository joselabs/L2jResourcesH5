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
package instances.RimKamaloka;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.l2jdevs.commons.database.pool.impl.ConnectionFactory;
import org.l2jdevs.gameserver.ThreadPoolManager;
import org.l2jdevs.gameserver.ai.CtrlIntention;
import org.l2jdevs.gameserver.data.xml.impl.NpcData;
import org.l2jdevs.gameserver.idfactory.IdFactory;
import org.l2jdevs.gameserver.instancemanager.InstanceManager;
import org.l2jdevs.gameserver.model.L2Party;
import org.l2jdevs.gameserver.model.L2Spawn;
import org.l2jdevs.gameserver.model.L2World;
import org.l2jdevs.gameserver.model.Location;
import org.l2jdevs.gameserver.model.actor.L2Attackable;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.L2Summon;
import org.l2jdevs.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2jdevs.gameserver.model.entity.Instance;
import org.l2jdevs.gameserver.model.instancezone.InstanceWorld;
import org.l2jdevs.gameserver.network.NpcStringId;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.clientpackets.Say2;
import org.l2jdevs.gameserver.network.serverpackets.NpcSay;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;

import instances.AbstractInstance;

/**
 * Rim Kamaloka Solo instance zone.
 * @author _DS_, Edoo, Sacrifice
 */

public final class RimKamaloka extends AbstractInstance
{
	private static final String SELECT = "SELECT * FROM `rim_kamaloka` ORDER BY `score` DESC";
	private static final String SELECT_SCORE = "SELECT `score` FROM `rim_kamaloka` WHERE `playerName` = ?";
	private static final String REPLACE = "REPLACE INTO `rim_kamaloka` (`playerName`, `score`) VALUES (?, ?)";
	private static final String DELETE = "DELETE FROM `rim_kamaloka`";
	
	private static final int DESPAWN_DELAY = 10000;
	
	/*
	 * Duration of the instance, minutes
	 */
	private static final int DURATION = 20;
	
	/*
	 * Time after which instance without players will be destroyed Default: 5 minutes
	 */
	private static final int EMPTY_DESTROY_TIME = 5;
	
	/*
	 * Time to destroy instance (and eject player away) Default: 10 minutes
	 */
	private static final int EXIT_TIME = 10;
	
	/*
	 * Hardcoded instance ids for kamaloka
	 */
	//@formatter:off
	private static final int[] INSTANCE_IDS = {46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56};
	//@formatter:on
	
	/*
	 * Kanabion String
	 */
	private static final NpcStringId[] KANABION_STRING =
	{
		NpcStringId.ALAS_SO_THIS_IS_HOW_IT_ALL_ENDS,
		NpcStringId.UH_IM_NOT_DYING_IM_JUST_DISAPPEARING_FOR_A_MOMENT_ILL_RESURRECT_AGAIN,
		NpcStringId.THIS_IS_UNBELIEVABLE_HOW_COULD_I_HAVE_LOST_TO_ONE_SO_INFERIOR_TO_MYSELF,
		NpcStringId.I_CARRY_THE_POWER_OF_DARKNESS_AND_HAVE_RETURNED_FROM_THE_ABYSS,
		NpcStringId.I_FINALLY_FIND_REST
	};
	
	//@formatter:off
	private static final int[][] KANABIONS =
	{
		{22452, 22453, 22454}, {22455, 22456, 22457}, {22458, 22459, 22460}, {22461, 22462, 22463},
		{22464, 22465, 22466}, {22467, 22468, 22469}, {22470, 22471, 22472}, {22473, 22474, 22475},
		{22476, 22477, 22478}, {22479, 22480, 22481}, {22482, 22483, 22484}
	};
	//@formatter:on
	
	/*
	 * Level of the kamaloka
	 */
	//@formatter:off
	private static final int[] LEVEL = {25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75};
	//@formatter:on
	
	private static final int LOCK_TIME = 10;
	
	/*
	 * Maximum level difference between players level and kamaloka level Default: 5
	 */
	private static final int MAX_LEVEL_DIFFERENCE = 5;
	
	/*
	 * Reset time for all kamaloka Default: 6:30AM on server time
	 */
	private static final int RESET_HOUR = 6;
	
	private static final int RESET_MIN = 30;
	
	//@formatter:off
	private static final int[] RESPAWN_DELAY = {5, 10, 15, 20, 25, 30};
	//@formatter:on
	
	/*
	 * Reward NPC
	 */
	private static final int REWARDER = 32485;
	
	//@formatter:off
	private static final int[][] REWARDERS =
	{
		{9261, -219862, -8021}, {16301, -219806, -8021}, {23478, -220079, -7799}, {9290, -212993, -7799},
		{16598, -212997, -7802}, {23650, -213051, -8007}, {9136, -205733, -8007}, {16508, -205737, -8007},
		{23229, -206316, -7991}, {42638, -219781, -8759}, {49014, -219737, -8759}
	};
	//@formatter:on
	
	private static final int ESSENCE_OF_KAMALOKA = 13002;
	private static final int PATHFINDER_SUPPLIES_LEVEL_40 = 10864;
	private static final int PATHFINDER_SUPPLIES_LEVEL_39 = 10863;
	private static final int PATHFINDER_SUPPLIES_LEVEL_38 = 10862;
	private static final int PATHFINDER_SUPPLIES_LEVEL_37 = 12834;
	private static final int PATHFINDER_SUPPLIES_LEVEL_36 = 10861;
	private static final int PATHFINDER_SUPPLIES_LEVEL_35 = 10860;
	private static final int PATHFINDER_SUPPLIES_LEVEL_34 = 10859;
	private static final int PATHFINDER_SUPPLIES_LEVEL_33 = 10858;
	private static final int PATHFINDER_SUPPLIES_LEVEL_32 = 10857;
	private static final int PATHFINDER_SUPPLIES_LEVEL_31 = 12833;
	private static final int PATHFINDER_SUPPLIES_LEVEL_30 = 12832;
	private static final int PATHFINDER_SUPPLIES_LEVEL_29 = 10856;
	private static final int PATHFINDER_SUPPLIES_LEVEL_28 = 10855;
	private static final int PATHFINDER_SUPPLIES_LEVEL_27 = 10854;
	private static final int PATHFINDER_SUPPLIES_LEVEL_26 = 10853;
	private static final int PATHFINDER_SUPPLIES_LEVEL_25 = 10852;
	private static final int PATHFINDER_SUPPLIES_LEVEL_24 = 12831;
	private static final int PATHFINDER_SUPPLIES_LEVEL_23 = 12830;
	private static final int PATHFINDER_SUPPLIES_LEVEL_22 = 10851;
	private static final int PATHFINDER_SUPPLIES_LEVEL_21 = 10850;
	private static final int PATHFINDER_SUPPLIES_LEVEL_20 = 10849;
	private static final int PATHFINDER_SUPPLIES_LEVEL_19 = 10848;
	private static final int PATHFINDER_SUPPLIES_LEVEL_18 = 10847;
	private static final int PATHFINDER_SUPPLIES_LEVEL_17 = 12829;
	private static final int PATHFINDER_SUPPLIES_LEVEL_16 = 12828;
	private static final int PATHFINDER_SUPPLIES_LEVEL_15 = 10846;
	private static final int PATHFINDER_SUPPLIES_LEVEL_14 = 10845;
	private static final int PATHFINDER_SUPPLIES_LEVEL_13 = 10844;
	private static final int PATHFINDER_SUPPLIES_LEVEL_12 = 10843;
	private static final int PATHFINDER_SUPPLIES_LEVEL_11 = 10842;
	private static final int PATHFINDER_SUPPLIES_LEVEL_10 = 12827;
	private static final int PATHFINDER_SUPPLIES_LEVEL_9 = 12826;
	private static final int PATHFINDER_SUPPLIES_LEVEL_8 = 10841;
	private static final int PATHFINDER_SUPPLIES_LEVEL_7 = 10840;
	private static final int PATHFINDER_SUPPLIES_LEVEL_6 = 10839;
	private static final int PATHFINDER_SUPPLIES_LEVEL_5 = 10838;
	private static final int PATHFINDER_SUPPLIES_LEVEL_4 = 10837;
	private static final int PATHFINDER_SUPPLIES_LEVEL_3 = 12825;
	private static final int PATHFINDER_SUPPLIES_LEVEL_2 = 10836;
	private static final int PATHFINDER_SUPPLIES_LEVEL_1 = 12824;
	
	//@formatter:off
	private static final int[][][] REWARDS =
	{
		{ // 20-30
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 2, PATHFINDER_SUPPLIES_LEVEL_6, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 2, PATHFINDER_SUPPLIES_LEVEL_5, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 2, PATHFINDER_SUPPLIES_LEVEL_4, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 2, PATHFINDER_SUPPLIES_LEVEL_2, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 2, PATHFINDER_SUPPLIES_LEVEL_1, 1}  // Grade S
		},
		{ // 25-35
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_5, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_4, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_2, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_7, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_3, 1}  // Grade S
		},
		{ // 30-40
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_8, 1},  // Grade D
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_11, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_12, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_13, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 3, PATHFINDER_SUPPLIES_LEVEL_9, 1}   // Grade S
		},
		{ // 35-45
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 5, PATHFINDER_SUPPLIES_LEVEL_11, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 5, PATHFINDER_SUPPLIES_LEVEL_12, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 5, PATHFINDER_SUPPLIES_LEVEL_13, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 5, PATHFINDER_SUPPLIES_LEVEL_14, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 5, PATHFINDER_SUPPLIES_LEVEL_10, 1}  // Grade S
		},
		{ // 40-50
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 7, PATHFINDER_SUPPLIES_LEVEL_15, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 7, PATHFINDER_SUPPLIES_LEVEL_18, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 7, PATHFINDER_SUPPLIES_LEVEL_19, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 7, PATHFINDER_SUPPLIES_LEVEL_20, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 7, PATHFINDER_SUPPLIES_LEVEL_16, 1}  // Grade S
		},
		{ // 45-55
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 8, PATHFINDER_SUPPLIES_LEVEL_18, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 8, PATHFINDER_SUPPLIES_LEVEL_19, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 8, PATHFINDER_SUPPLIES_LEVEL_20, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 8, PATHFINDER_SUPPLIES_LEVEL_21, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 8, PATHFINDER_SUPPLIES_LEVEL_17, 1}  // Grade S
		},
		{ // 50-60
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 10, PATHFINDER_SUPPLIES_LEVEL_22, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 10, PATHFINDER_SUPPLIES_LEVEL_25, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 10, PATHFINDER_SUPPLIES_LEVEL_26, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 10, PATHFINDER_SUPPLIES_LEVEL_27, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 10, PATHFINDER_SUPPLIES_LEVEL_23, 1}  // Grade S
		},
		{ // 55-65
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 12, PATHFINDER_SUPPLIES_LEVEL_25, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 12, PATHFINDER_SUPPLIES_LEVEL_26, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 12, PATHFINDER_SUPPLIES_LEVEL_27, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 12, PATHFINDER_SUPPLIES_LEVEL_28, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 12, PATHFINDER_SUPPLIES_LEVEL_24, 1}  // Grade S
		},
		{ // 60-70
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 13, PATHFINDER_SUPPLIES_LEVEL_29, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 13, PATHFINDER_SUPPLIES_LEVEL_32, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 13, PATHFINDER_SUPPLIES_LEVEL_33, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 13, PATHFINDER_SUPPLIES_LEVEL_34, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 13, PATHFINDER_SUPPLIES_LEVEL_30, 1}  // Grade S
		},
		{ // 65-75
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 15, PATHFINDER_SUPPLIES_LEVEL_32, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 15, PATHFINDER_SUPPLIES_LEVEL_33, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 15, PATHFINDER_SUPPLIES_LEVEL_34, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 15, PATHFINDER_SUPPLIES_LEVEL_35, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 15, PATHFINDER_SUPPLIES_LEVEL_31, 1}  // Grade S
		},
		{ // 70-80
			null, // Grade F
			{ESSENCE_OF_KAMALOKA, 17, PATHFINDER_SUPPLIES_LEVEL_36, 1}, // Grade D
			{ESSENCE_OF_KAMALOKA, 17, PATHFINDER_SUPPLIES_LEVEL_37, 1}, // Grade C
			{ESSENCE_OF_KAMALOKA, 17, PATHFINDER_SUPPLIES_LEVEL_38, 1}, // Grade B
			{ESSENCE_OF_KAMALOKA, 17, PATHFINDER_SUPPLIES_LEVEL_39, 1}, // Grade A
			{ESSENCE_OF_KAMALOKA, 17, PATHFINDER_SUPPLIES_LEVEL_40, 1}  // Grade S
		}
	};
	//@formatter:on
	
	//@formatter:off
	private static final int[][][] SPAWNLIST =
	{
		{
			{8971, -219546, -8021}, {9318, -219644, -8021}, {9266, -220208, -8021}, {9497, -220054, -8024}
		},
		{
			{16107, -219574, -8021}, {16769, -219885, -8021}, {16363, -220219, -8021}, {16610, -219523, -8021}
		},
		{
			{23019, -219730, -7803}, {23351, -220455, -7803}, {23900, -219864, -7803}, {23851, -220294, -7803}
		},
		{
			{9514, -212478, -7803}, {9236, -213348, -7803}, {8868, -212683, -7803}, {9719, -213042, -7803}
		},
		{
			{16925, -212811, -7803}, {16885, -213199, -7802}, {16487, -213339, -7803}, {16337, -212529, -7803}
		},
		{
			{23958, -213282, -8009}, {23292, -212782, -8012}, {23844, -212781, -8009}, {23533, -213301, -8009}
		},
		{
			{8828, -205518, -8009}, {8895, -205989, -8009}, {9398, -205967, -8009}, {9393, -205409, -8009}
		},
		{
			{16185, -205472, -8009}, {16808, -205929, -8009}, {16324, -206042, -8009}, {16782, -205454, -8009}
		},
		{
			{23476, -206310, -7991}, {23230, -205861, -7991}, {22644, -205888, -7994}, {23078, -206714, -7991}
		},
		{
			{42981, -219308, -8759}, {42320, -220160, -8759}, {42434, -219181, -8759}, {42101, -219550, -8759},
			{41859, -220236, -8759}, {42881, -219942, -8759}
		},
		{
			{48770, -219304, -8759}, {49036, -220190, -8759}, {49363, -219814, -8759}, {49393, -219102, -8759},
			{49618, -220490, -8759}, {48526, -220493, -8759}
		}
	};
	//@formatter:on
	
	/*
	 * Start NPC
	 */
	private static final int START_NPC = 32484;
	
	/*
	 * Teleport points into instances x, y, z
	 */
	//@formatter:off
	private static final int[][] TELEPORTS =
	{
		{10025, -219868, -8021}, {15617, -219883, -8021}, {22742, -220079, -7802}, {8559, -212987, -7802},
		{15867, -212994, -7802}, {23038, -213052, -8007}, {9139, -205132, -8007}, {15943, -205740, -8008},
		{22343, -206237, -7991}, {41496, -219694, -8759}, {48137, -219716, -8759}
	};
	//@formatter:on
	
	public RimKamaloka()
	{
		super(RimKamaloka.class.getSimpleName());
		addFirstTalkId(START_NPC);
		addStartNpc(START_NPC);
		addTalkId(START_NPC);
		addFirstTalkId(REWARDER);
		addStartNpc(REWARDER);
		addTalkId(REWARDER);
		
		for (int[] list : KANABIONS)
		{
			addFactionCallId(list[0]);
			for (int mob : list)
			{
				addAttackId(mob);
				addKillId(mob);
			}
		}
		
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		if (calendar.get(Calendar.HOUR_OF_DAY) >= 0)
		{
			calendar.roll(Calendar.DATE, true);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		
		ThreadPoolManager.getInstance().scheduleEventAtFixedRate(() ->
		{
			if (getRatingList().size() > 0)
			{
				resetRating();
			}
		}, calendar.getTimeInMillis() - System.currentTimeMillis(), 86400000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "Exit":
			{
				try
				{
					final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
					if ((world instanceof RimKamalokaWorld) && world.getAllowed().contains(player.getObjectId()))
					{
						final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
						teleportPlayer(player, inst.getExitLoc(), 0);
					}
				}
				catch (Exception e)
				{
					_log.log(Level.WARNING, getClass().getSimpleName() + ": problem with exit: ", e);
				}
				break;
			}
			case "Reward":
			{
				try
				{
					final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
					if ((world instanceof RimKamalokaWorld) && world.getAllowed().contains(player.getObjectId()))
					{
						rewardPlayer((RimKamalokaWorld) world, npc);
					}
				}
				catch (Exception e)
				{
					_log.log(Level.WARNING, getClass().getSimpleName() + ": problem with reward: ", e);
				}
				return "Rewarded.html";
			}
			case "RatingList":
			{
				String rating = null;
				var index = 1;
				for (Entry<String, Integer> ratingList : getRatingList().entrySet())
				{
					rating += "<tr><td width=70 align=center>" + index + ".-</td>";
					rating += "<td width=110 align=center> " + ratingList.getKey() + "</td>";
					rating += "<td width=80 align=center> " + ratingList.getValue() + "</td></tr>";
					index++;
					if (index > 10)
					{
						break;
					}
				}
				
				final String string = "Pathfinder:<br>" + //
					"<font color=\"LEVEL\">Rim Kamaloka Rating</font><br>" + //
					"Your current contribution: <font color=\"LEVEL\">" + getScore(player) + "</font>.<br>" + //
					"Would you like to know the names of the characters who contributed most <font color=\"LEVEL\">today</font> in opposition to the invasion of Kamaloka Neighborhoods? If their names are here, it's because they've received our special favors.<br>" + //
					"<table>" + rating + "</table><br>" + //
					"(This rating is restarted at midnight server time.)<br>" + //
					"<a action=\"bypass -h npc_%objectId%_Chat 3\">Back</a>";
				final String htmltext = showHtmlFile(player, "RatingList.html").replace("%content%", string);
				return htmltext;
			}
			default:
			{
				try
				{
					enterInstance(player, Integer.parseInt(event));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final InstanceWorld tmpWorld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpWorld instanceof RimKamalokaWorld)
		{
			final RimKamalokaWorld world = (RimKamalokaWorld) tmpWorld;
			synchronized (world.lastAttack)
			{
				world.lastAttack.put(npc.getObjectId(), System.currentTimeMillis());
			}
			
			final int maxHp = npc.getMaxHp();
			if (npc.getCurrentHp() == maxHp)
			{
				if (((damage * 100) / maxHp) > 40)
				{
					final int chance = getRandom(100);
					int nextId = 0;
					
					if (npc.getId() == world.KANABION)
					{
						if (chance < 5)
						{
							nextId = world.DOPPLER;
						}
					}
					else if (npc.getId() == world.DOPPLER)
					{
						if (chance < 5)
						{
							nextId = world.DOPPLER;
						}
						else if (chance < 10)
						{
							nextId = world.VOIDER;
						}
					}
					else if (npc.getId() == world.VOIDER)
					{
						if (chance < 5)
						{
							nextId = world.VOIDER;
						}
					}
					
					if (nextId > 0)
					{
						spawnNextMob(world, npc, nextId, attacker);
					}
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isSummon)
	{
		if (npc.getId() == caller.getId())
		{
			return null;
		}
		return super.onFactionCall(npc, caller, attacker, isSummon);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return String.valueOf(npc.getId()) + ".html";
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final InstanceWorld tmpWorld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpWorld instanceof RimKamalokaWorld)
		{
			final RimKamalokaWorld world = (RimKamalokaWorld) tmpWorld;
			synchronized (world.lastAttack)
			{
				world.lastAttack.remove(npc.getObjectId());
			}
			
			final int chance = getRandom(100);
			int nextId = 0;
			
			if (npc.getId() == world.KANABION)
			{
				if (getRandom(100) < 15)
				{
					npc.broadcastPacket(new NpcSay(npc, 0, KANABION_STRING[getRandom(KANABION_STRING.length)]));
				}
				world.kanabionsCount++;
				if (((L2Attackable) npc).isOverhit())
				{
					if (chance < 30)
					{
						nextId = world.DOPPLER;
					}
					else if (chance < 40)
					{
						nextId = world.VOIDER;
					}
				}
				else if (chance < 15)
				{
					nextId = world.DOPPLER;
				}
			}
			else if (npc.getId() == world.DOPPLER)
			{
				if (getRandom(100) < 15)
				{
					npc.broadcastPacket(new NpcSay(npc, 0, KANABION_STRING[getRandom(KANABION_STRING.length)]));
				}
				world.dopplersCount++;
				if (((L2Attackable) npc).isOverhit())
				{
					if (chance < 30)
					{
						nextId = world.DOPPLER;
					}
					else if (chance < 60)
					{
						nextId = world.VOIDER;
					}
				}
				else
				{
					if (chance < 10)
					{
						nextId = world.DOPPLER;
					}
					else if (chance < 20)
					{
						nextId = world.VOIDER;
					}
				}
			}
			else if (npc.getId() == world.VOIDER)
			{
				if (getRandom(100) < 15)
				{
					npc.broadcastPacket(new NpcSay(npc, 0, KANABION_STRING[getRandom(KANABION_STRING.length)]));
				}
				world.voidersCount++;
				if (((L2Attackable) npc).isOverhit())
				{
					if (chance < 50)
					{
						nextId = world.VOIDER;
					}
				}
				else if (chance < 20)
				{
					nextId = world.VOIDER;
				}
			}
			
			if (nextId > 0)
			{
				spawnNextMob(world, npc, nextId, player);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		if (npc.getId() == START_NPC)
		{
			broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.VERY_GOOD_YOUR_SKILL_MAKES_YOU_A_MODEL_FOR_OTHER_ADVENTURERS_TO_FOLLOW);
			return npc.getCastle().getName() + ".html";
		}
		else if (npc.getId() == REWARDER)
		{
			final InstanceWorld tmpWorld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (tmpWorld instanceof RimKamalokaWorld)
			{
				final RimKamalokaWorld world = (RimKamalokaWorld) tmpWorld;
				if (!world.isFinished)
				{
					return null;
				}
				
				switch (world.grade)
				{
					case 0:
					{
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.F_GRADE);
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.HOW_DISAPPOINTING_IT_LOOKS_LIKE_I_MADE_A_MISTAKE_IN_SENDING_YOU_INSIDE_RIM_KAMALOKA);
						return "GradeF.html";
					}
					case 1:
					{
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.D_GRADE);
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.GOOD_WORK_IF_ALL_ADVENTURERS_PRODUCE_RESULTS_LIKE_YOU_WE_WILL_SLOWLY_START_TO_SEE_THE_GLIMMER_OF_HOPE);
						String left;
						if (world.grade != 1)
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade D)</font><br>" + "This is a bad result.<br>" + "I was hoping he would be better.<br>"
								+ "Unfortunately, access to the Kamaloka Neighborhood is not given to everyone.<br>" + "Have you ever noticed that the Kanabions can grow bigger and more nutritious? This usually happens when you deal monsters a lot of damage at once.<br>"
								+ "We, the Pathfinders, have prepared an award for those who have achieved impressive results so that the next time they can complete the task even better.<br>" + "Would you like to receive a reward appropriate to your level?<br>"
								+ "<a action=\"bypass -h Quest RimKamaloka Reward\">Get an award.</a>";
						}
						else
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade D)</font><br>" + "This is a bad result.<br>" + "I was hoping he would be better.<br>"
								+ "Unfortunately, access to the Kamaloka Neighborhood is not given to everyone.<br>" + "Have you ever noticed that the Kanabions can grow bigger and more nutritious? This usually happens when you deal monsters a lot of damage at once.<br>"
								+ "We, the Pathfinders, have prepared an award for those who have achieved impressive results so that the next time they can complete the task even better.<br>" + "Would you like to receive a reward appropriate to your level?<br>"
								+ "<a action=\"bypass -h Quest RimKamaloka Reward\">Get an award.</a>";
						}
						final String htmltext = showHtmlFile(talker, "Grade.html").replace("%count%", left);
						return htmltext;
					}
					case 2:
					{
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.C_GRADE);
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.GOOD_WORK_IF_ALL_ADVENTURERS_PRODUCE_RESULTS_LIKE_YOU_WE_WILL_SLOWLY_START_TO_SEE_THE_GLIMMER_OF_HOPE);
						String left;
						if (world.grade != 2)
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade C)</font><br>" + "Unfortunately, access to the Kamaloka Neighborhood is not given to everyone.<br>"
								+ "Have you ever noticed that the Kanabions can grow bigger and more nutritious? This usually happens when you deal monsters a lot of damage at once.<br>"
								+ "We, the Pathfinders, have prepared an award for those who have achieved impressive results so that the next time they can complete the task even better.<br>" + "Want to get a reward appropriate to your level?<br>"
								+ "<a action=\"bypass -h Quest RimKamaloka Reward\">Get an award.</a>";
						}
						else
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade C)</font><br>" + "Unfortunately, access to the Kamaloka Neighborhood is not given to everyone.<br>"
								+ "Have you ever noticed that the Kanabions can grow bigger and more nutritious? This usually happens when you deal monsters a lot of damage at once.<br>"
								+ "We, the Pathfinders, have prepared an award for those who have achieved impressive results so that the next time they can complete the task even better.<br>" + "Want to get a reward appropriate to your level?<br>"
								+ "<a action=\"bypass -h Quest RimKamaloka Reward\">Get an award.</a>";
						}
						final String htmltext = showHtmlFile(talker, "Grade.html").replace("%count%", left);
						return htmltext;
					}
					case 3:
					{
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.B_GRADE);
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.GOOD_WORK_IF_ALL_ADVENTURERS_PRODUCE_RESULTS_LIKE_YOU_WE_WILL_SLOWLY_START_TO_SEE_THE_GLIMMER_OF_HOPE);
						String left;
						if (world.grade != 3)
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade B)</font><br>" + "Unfortunately, access to the Kamaloka Neighborhood is not given to everyone.<br>"
								+ "Have you ever noticed that the Kanabions can grow bigger and more nutritious? This usually happens when you deal monsters a lot of damage at once.<br>"
								+ "We, the Pathfinders, have prepared an award for those who have achieved impressive results so that the next time they can complete the task even better.<br>" + "Want to get a reward appropriate to your level?<br>"
								+ "<a action=\"bypass -h Quest RimKamaloka Reward\">Get an award.</a>";
						}
						else
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade B)</font><br>" + "Unfortunately, access to the Kamaloka Neighborhood is not given to everyone.<br>"
								+ "Have you ever noticed that the Kanabions can grow bigger and more nutritious? This usually happens when you deal monsters a lot of damage at once.<br>"
								+ "We, the Pathfinders, have prepared an award for those who have achieved impressive results so that the next time they can complete the task even better.<br>" + "Want to get a reward appropriate to your level?<br>"
								+ "<a action=\"bypass -h Quest RimKamaloka Reward\">Get an award.</a>";
						}
						final String htmltext = showHtmlFile(talker, "Grade.html").replace("%count%", left);
						return htmltext;
					}
					case 4:
					{
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.A_GRADE);
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.ADMIRABLE_YOU_GREATLY_DECREASED_THE_SPEED_OF_INVASION_THROUGH_KAMALOKA);
						String left;
						if (world.grade != 4)
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade A)</font><br>" + "This... This is a great achievement worthy of the heroes of legends!<br>"
								+ "I see that you understood the essence of our business and did an excellent job with the task assigned to you.<br>" + "<a action=\"bypass -h Quest RimKamaloka Reward\">Get reward</a>";
						}
						else
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade A)</font><br>" + "This... This is a great achievement worthy of the heroes of legends!<br>"
								+ "I see that you understood the essence of our business and did an excellent job with the task assigned to you.<br>" + "<a action=\"bypass -h Quest RimKamaloka Reward\">Get reward</a>";
						}
						final String htmltext = showHtmlFile(talker, "Grade.html").replace("%count%", left);
						return htmltext;
					}
					case 5:
					{
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.S_GRADE);
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.THIS_IS_THIS_IS_A_GREAT_ACHIEVEMENT_THAT_IS_WORTHY_OF_THE_TRUE_HEROES_OF_LEGEND);
						String left;
						if (world.grade != 5)
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade S)</font><br>" + "This... This is a great achievement worthy of the heroes of legends!<br>"
								+ "I see that you understood the essence of our business and did an excellent job with the task assigned to you.<br>" + "<a action=\"bypass -h Quest RimKamaloka Reward\">Get reward</a>";
						}
						else
						{
							left = "Pathfinder:<br>" + "Your result: " + (world.dopplersCount + world.voidersCount + world.kanabionsCount) + "<font color=\"LEVEL\"> (Grade S)</font><br>" + "This... This is a great achievement worthy of the heroes of legends!<br>"
								+ "I see that you understood the essence of our business and did an excellent job with the task assigned to you.<br>" + "<a action=\"bypass -h Quest RimKamaloka Reward\">Get reward</a>";
						}
						final String htmltext = showHtmlFile(talker, "Grade.html").replace("%count%", left);
						return htmltext;
					}
				}
			}
		}
		return super.onTalk(npc, talker);
	}
	
	/**
	 * Check if party with player as leader allowed to enter
	 * @param player party leader
	 * @param index (0-17) index of the kamaloka in arrays
	 * @return true if party allowed to enter
	 */
	@Override
	protected boolean checkConditions(L2PcInstance player, int index)
	{
		final L2Party party = player.getParty();
		// player must not be in party
		if (party != null)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_EXCEEDED_THE_LIMIT_CANT_ENTER));
			return false;
		}
		
		// get level of the instance
		final int level = LEVEL[index];
		// and client name
		final String instanceName = InstanceManager.getInstance().getInstanceIdName(INSTANCE_IDS[index]);
		
		// player level must be in range
		if (Math.abs(player.getLevel() - level) > MAX_LEVEL_DIFFERENCE)
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED);
			sm.addPcName(player);
			player.sendPacket(sm);
			return false;
		}
		// get instances reenter times for player
		final Map<Integer, Long> instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(player.getObjectId());
		if (instanceTimes != null)
		{
			for (int id : instanceTimes.keySet())
			{
				// find instance with same name (kamaloka or labyrinth)
				if (!instanceName.equals(InstanceManager.getInstance().getInstanceIdName(id)))
				{
					continue;
				}
				// if found instance still can't be reentered - exit
				if (System.currentTimeMillis() < instanceTimes.get(id))
				{
					final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET);
					sm.addPcName(player);
					player.sendPacket(sm);
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Handling enter of the players into kamaloka
	 * @param player party leader
	 * @param index (0-17) kamaloka index in arrays
	 */
	protected void enterInstance(L2PcInstance player, int index)
	{
		int templateId;
		try
		{
			templateId = INSTANCE_IDS[index];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return;
		}
		
		// check for existing instances for this player
		final InstanceWorld tmpWorld = InstanceManager.getInstance().getPlayerWorld(player);
		// player already in the instance
		if (tmpWorld != null)
		{
			// but not in kamaloka
			if (!(tmpWorld instanceof RimKamalokaWorld) || (tmpWorld.getTemplateId() != templateId))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED));
				return;
			}
			final RimKamalokaWorld world = (RimKamalokaWorld) tmpWorld;
			// check for level difference again on reenter
			if (Math.abs(player.getLevel() - LEVEL[world.index]) > MAX_LEVEL_DIFFERENCE)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED);
				sm.addPcName(player);
				player.sendPacket(sm);
				return;
			}
			// check what instance still exist
			final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
			if (inst != null)
			{
				teleportPlayer(player, TELEPORTS[index], world.getInstanceId());
			}
		}
		// Creating new kamaloka instance
		else
		{
			if (!checkConditions(player, index))
			{
				return;
			}
			
			// Creating dynamic instance without template
			final int instanceId = InstanceManager.getInstance().createDynamicInstance(null);
			final Instance inst = InstanceManager.getInstance().getInstance(instanceId);
			// set name for the kamaloka
			inst.setName(InstanceManager.getInstance().getInstanceIdName(templateId));
			// set return location
			inst.setExitLoc(new Location(player));
			// disable summon friend into instance
			inst.setAllowSummon(false);
			// Creating new instanceWorld, using our instanceId and templateId
			final RimKamalokaWorld world = new RimKamalokaWorld();
			world.setInstanceId(instanceId);
			world.setTemplateId(templateId);
			// set index for easy access to the arrays
			world.index = index;
			InstanceManager.getInstance().addWorld(world);
			
			// spawn npcs
			spawnKamaloka(world);
			world.finishTask = ThreadPoolManager.getInstance().scheduleAi(new FinishTask(world), DURATION * 60000);
			world.lockTask = ThreadPoolManager.getInstance().scheduleAi(new LockTask(world), LOCK_TIME * 60000);
			world.despawnTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new DespawnTask(world), 1000, 1000);
			
			world.getAllowed().add(player.getObjectId());
			
			teleportPlayer(player, TELEPORTS[index], instanceId);
		}
	}
	
	@Override
	protected void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance)
	{
		
	}
	
	/**
	 * Get's the database data stored
	 * @return A Map containing player and scores obtained
	 */
	private Map<String, Integer> getRatingList()
	{
		final Map<String, Integer> map = new LinkedHashMap<>();
		var playerName = "";
		var score = 0;
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement(SELECT))
		{
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					playerName = rset.getString("playerName");
					score = rset.getInt("score");
					map.put(playerName, score);
				}
				rset.close();
			}
			ps.execute();
			ps.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		return map;
	}
	
	/**
	 * Get's the player score
	 * @param player
	 * @return score, 0 otherwise
	 */
	private int getScore(L2PcInstance player)
	{
		var score = 0;
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement(SELECT_SCORE))
		{
			ps.setString(1, player.getName());
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					score = rset.getInt("score");
				}
				rset.close();
			}
			ps.execute();
			ps.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		return score;
	}
	
	private void resetRating()
	{
		final var ratingListSize = getRatingList().size();
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement(DELETE))
		{
			ps.execute();
			ps.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		_log.info(getClass().getSimpleName() + ": Removed " + ratingListSize + " rating entries");
	}
	
	private void rewardPlayer(RimKamalokaWorld world, L2Npc npc)
	{
		if (!world.isFinished || world.isRewarded)
		{
			return;
		}
		
		world.isRewarded = true;
		
		final int[] reward = REWARDS[world.index][world.grade];
		
		if (reward == null)
		{
			return;
		}
		
		for (int objectId : world.getAllowed())
		{
			final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
			if ((player != null) && player.isOnline())
			{
				player.sendMessage("Grade: " + world.grade);
				for (int i = 0; i < reward.length; i += 2)
				{
					player.addItem("Reward", reward[i], reward[i + 1], npc, true);
				}
				
				// Store data in database only if player was rewarded with S grade
				if (world.grade == 5)
				{
					storeRating(player, world.kanabionsCount + world.dopplersCount + world.voidersCount);
				}
			}
		}
	}
	
	/**
	 * Spawn all NPCs in kamaloka
	 * @param world instanceWorld
	 */
	private void spawnKamaloka(RimKamalokaWorld world)
	{
		final int[][] spawnlist;
		final int index = world.index;
		world.KANABION = KANABIONS[index][0];
		world.DOPPLER = KANABIONS[index][1];
		world.VOIDER = KANABIONS[index][2];
		
		try
		{
			final L2NpcTemplate mob1 = NpcData.getInstance().getTemplate(world.KANABION);
			
			spawnlist = SPAWNLIST[index];
			final int length = spawnlist.length;
			
			L2Spawn spawn;
			for (int i = 0; i < length; i++)
			{
				final int[] loc = spawnlist[i];
				spawn = new L2Spawn(mob1);
				spawn.setInstanceId(world.getInstanceId());
				spawn.setX(loc[0]);
				spawn.setY(loc[1]);
				spawn.setZ(loc[2]);
				spawn.setHeading(-1);
				spawn.setRespawnDelay(RESPAWN_DELAY[getRandom(RESPAWN_DELAY.length)]);
				spawn.setAmount(1);
				spawn.startRespawn();
				spawn.doSpawn();
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, getClass().getSimpleName() + ": error during spawn: ", e);
		}
	}
	
	private void spawnNextMob(RimKamalokaWorld world, L2Npc oldNpc, int npcId, L2PcInstance player)
	{
		if (world.isFinished)
		{
			return;
		}
		
		L2MonsterInstance monster = null;
		
		if (!world.spawnedMobs.isEmpty())
		{
			for (L2MonsterInstance mob : world.spawnedMobs)
			{
				if ((mob == null) || !mob.isDecayed() || (mob.getId() != npcId))
				{
					continue;
				}
				mob.setDecayed(false);
				mob.setIsDead(false);
				mob.overhitEnabled(false);
				mob.refreshID();
				monster = mob;
				break;
			}
		}
		
		if (monster == null)
		{
			final L2NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
			monster = new L2MonsterInstance(template);
			IdFactory.getInstance().getNextId();
			world.spawnedMobs.add(monster);
		}
		
		synchronized (world.lastAttack)
		{
			world.lastAttack.put(monster.getObjectId(), System.currentTimeMillis());
		}
		
		monster.setCurrentHpMp(monster.getMaxHp(), monster.getMaxMp());
		monster.setHeading(oldNpc.getHeading());
		monster.setInstanceId(oldNpc.getInstanceId());
		monster.spawnMe(oldNpc.getX(), oldNpc.getY(), oldNpc.getZ() + 20);
		monster.setRunning();
		monster.addDamageHate(player, 0, 9999);
		monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		world.spawnCount++;
	}
	
	/**
	 * Store the player score into database
	 * @param player
	 * @param score
	 */
	private void storeRating(L2PcInstance player, int score)
	{
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement(REPLACE))
		{
			ps.setString(1, player.getName());
			ps.setInt(2, score);
			ps.execute();
			ps.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
	}
	
	/**
	 * Teleport player and pet to/from instance
	 * @param player
	 * @param coords x, y, z
	 * @param instanceId
	 */
	private void teleportPlayer(L2PcInstance player, int[] coords, int instanceId)
	{
		player.setInstanceId(instanceId);
		player.teleToLocation(coords[0], coords[1], coords[2], true);
		final L2Summon pet = player.getSummon();
		if (pet != null)
		{
			pet.setInstanceId(instanceId);
			pet.teleToLocation(coords[0], coords[1], coords[2], true);
		}
	}
	
	private class DespawnTask implements Runnable
	{
		private final RimKamalokaWorld _world;
		
		DespawnTask(RimKamalokaWorld world)
		{
			_world = world;
		}
		
		@Override
		public void run()
		{
			if ((_world != null) && !_world.isFinished && !_world.lastAttack.isEmpty() && !_world.spawnedMobs.isEmpty())
			{
				final long time = System.currentTimeMillis();
				for (L2MonsterInstance mob : _world.spawnedMobs)
				{
					if ((mob == null) || mob.isDead() || !mob.isVisible())
					{
						continue;
					}
					if (_world.lastAttack.containsKey(mob.getObjectId()) && ((time - _world.lastAttack.get(mob.getObjectId())) > DESPAWN_DELAY))
					{
						mob.deleteMe();
						synchronized (_world.lastAttack)
						{
							_world.lastAttack.remove(mob.getObjectId());
						}
					}
				}
			}
		}
	}
	
	private class FinishTask implements Runnable
	{
		private final RimKamalokaWorld _world;
		
		FinishTask(RimKamalokaWorld world)
		{
			_world = world;
		}
		
		@Override
		public void run()
		{
			if (_world != null)
			{
				_world.isFinished = true;
				if (_world.despawnTask != null)
				{
					_world.despawnTask.cancel(false);
					_world.despawnTask = null;
				}
				_world.spawnedMobs.clear();
				_world.lastAttack.clear();
				// destroy instance after EXIT_TIME
				final Instance instance = InstanceManager.getInstance().getInstance(_world.getInstanceId());
				if (instance != null)
				{
					instance.removeNpcs();
					instance.setDuration(EXIT_TIME * 60000);
					if (instance.getPlayers().isEmpty())
					{
						instance.setDuration(EMPTY_DESTROY_TIME * 60000);
					}
					else
					{
						instance.setDuration(EXIT_TIME * 60000);
						instance.setEmptyDestroyTime(EMPTY_DESTROY_TIME * 60000);
					}
				}
				
				// calculate reward
				if (_world.kanabionsCount < 10)
				{
					_world.grade = 0;
				}
				else
				{
					final int rewardFormula = ((_world.kanabionsCount + _world.dopplersCount + (2 * _world.voidersCount)) * 100) / _world.spawnCount;
					if ((rewardFormula > 16) && (rewardFormula <= 33))
					{
						_world.grade = 1;
					}
					else if ((rewardFormula > 33) && (rewardFormula <= 50))
					{
						_world.grade = 2;
					}
					else if ((rewardFormula > 50) && (rewardFormula <= 66))
					{
						_world.grade = 3;
					}
					else if ((rewardFormula > 66) && (rewardFormula <= 83))
					{
						_world.grade = 4;
					}
					else if (rewardFormula > 83)
					{
						_world.grade = 5;
					}
				}
				
				final int index = _world.index;
				
				// spawn rewarder npc
				addSpawn(REWARDER, REWARDERS[index][0], REWARDERS[index][1], REWARDERS[index][2], 0, false, 0, false, _world.getInstanceId());
			}
		}
	}
	
	private class LockTask implements Runnable
	{
		private final RimKamalokaWorld _world;
		
		LockTask(RimKamalokaWorld world)
		{
			_world = world;
		}
		
		@Override
		public void run()
		{
			if (_world != null)
			{
				final Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MINUTE, RESET_MIN);
				// if time is >= RESET_HOUR - roll to the next day
				if (calendar.get(Calendar.HOUR_OF_DAY) >= RESET_HOUR)
				{
					calendar.roll(Calendar.DATE, true);
				}
				calendar.set(Calendar.HOUR_OF_DAY, RESET_HOUR);
				
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_FROM_HERE_S1_S_ENTRY_HAS_BEEN_RESTRICTED);
				sm.addInstanceName(_world.getTemplateId());
				
				// set instance reenter time for all allowed players
				boolean found = false;
				for (int objectId : _world.getAllowed())
				{
					final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
					if ((player != null) && player.isOnline())
					{
						found = true;
						InstanceManager.getInstance().setInstanceTime(objectId, _world.getTemplateId(), calendar.getTimeInMillis());
						player.sendPacket(sm);
					}
				}
				if (!found)
				{
					_world.isFinished = true;
					_world.spawnedMobs.clear();
					_world.lastAttack.clear();
					
					if (_world.lockTask != null)
					{
						_world.lockTask.cancel(false);
						_world.lockTask = null;
					}
					
					if (_world.finishTask != null)
					{
						_world.finishTask.cancel(false);
						_world.finishTask = null;
					}
					
					if (_world.despawnTask != null)
					{
						_world.despawnTask.cancel(false);
						_world.despawnTask = null;
					}
					InstanceManager.getInstance().destroyInstance(_world.getInstanceId());
				}
			}
		}
	}
	
	private class RimKamalokaWorld extends InstanceWorld
	{
		private ScheduledFuture<?> despawnTask = null;
		private ScheduledFuture<?> finishTask = null;
		private ScheduledFuture<?> lockTask = null;
		
		private final Map<Integer, Long> lastAttack = new HashMap<>();
		private final List<L2MonsterInstance> spawnedMobs = new ArrayList<>();
		
		private int index;
		private int grade = 0;
		private int KANABION;
		private int kanabionsCount = 0;
		private int DOPPLER;
		private int dopplersCount = 0;
		private int VOIDER;
		private int voidersCount = 0;
		private int spawnCount = 0;
		
		private boolean isFinished = false;
		private boolean isRewarded = false;
	}
}