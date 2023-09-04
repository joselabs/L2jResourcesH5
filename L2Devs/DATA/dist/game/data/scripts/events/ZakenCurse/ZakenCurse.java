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
package events.ZakenCurse;

import org.l2jdevs.gameserver.data.xml.impl.NpcData;
import org.l2jdevs.gameserver.datatables.SpawnTable;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.L2Spawn;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2jdevs.gameserver.model.event.LongTimeEvent;
import org.l2jdevs.gameserver.model.holders.SkillHolder;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;
import org.l2jdevs.gameserver.util.Util;

/**
 * Zaken Curse Event.
 * @author xban1x, Sacrifice
 */
public final class ZakenCurse extends LongTimeEvent
{
	private static final int MAGIC_LABORATORY_WORKER = 32131;
	
	private static final int GOLDEN_APIGA = 9143;
	private static final int REDEMPTION_BOW = 9141;
	
	private static final SkillHolder FORGIVENESS = new SkillHolder(3260, 1);
	private static final SkillHolder PARDON = new SkillHolder(3262, 1);
	
	//@formatter:off
	private static final int[] CHANCE = {45, 5, 2};
	private static final int[] REWARD = {1, 7, 10};
	//@formatter:on
	
	private static final int MIN_LVL = 20;
	private static final int MAX_GOLDEN_APIGA = 10000000;
	private static final int BOW_REUSE_TIME = 86400000; // 24 hours
	
	private static final int[] PIGS =
	{
		13031, // Huge Pig
		13032, // Huge Pig
		13033, // Huge Pig
		13034, // Super Huge Pig
		13035 // Super Huge Pig
	};
	
	//@formatter:off
	private static final int[][] PIGS_SPAWNS =
	{
		{12739, 172819, -3415, 53544}, {13386, 172680, -3452, 63628}, {13304, 173135, -3449, 53988}, {13229, 173748, -3463, 19658},
		{14639, 174127, -3486, 65020}, {14924, 174953, -3602, 7150},  {15625, 175086, -3662, 59709}, {16243, 174928, -3644, 60588},
		{16217, 174024, -3691, 47810}, {16989, 173868, -3626, 64213}, {16919, 173152, -3598, 43726}, {16477, 172906, -3565, 35563},
		{15708, 173583, -3576, 25291}, {17796, 173812, -3645, 63328}, {18306, 174341, -3680, 8494 }, {18731, 174004, -3639, 55432},
		{19147, 174213, -3598, 64786}, {19715, 173364, -3578, 55248}, {20446, 173433, -3577, 5533},  {20738, 173844, -3577, 59844},
		{20235, 172255, -3582, 45099}, {21085, 172082, -3577, 63738}, {21857, 172346, -3567, 2650},  {21763, 173214, -3577, 16169},
		{21583, 171240, -3547, 59056}, {20920, 170514, -3556, 41043}, {20314, 169681, -3532, 49447}, {19792, 169950, -3560, 26862},
		{19419, 168903, -3480, 48761}, {18970, 169172, -3483, 29138}, {18014, 168372, -3524, 38624}, {18275, 167273, -3486, 50371},
		{17227, 167052, -3519, 34197}, {16007, 167490, -3546, 24702}, {14782, 167310, -3643, 36856}, {14296, 167874, -3636, 16980},
		{14201, 169242, -3616, 19444}, {14354, 170546, -3558, 13843}, {15016, 170893, -3485, 3419},  {15978, 171333,-3555, 11655},
		{21891, 170635, -3516, 49199}, {21252, 169570, -3443, 50277}, {18828, 168262, -3463, 34177}, {17409, 167654, -3460, 33168},
		{15513, 168057, -3512, 21964}, {12083, 172092, -3514, 29851}, {12720, 173616, -3463, 10111}, {14383, 173431, -3459, 53120},
		{14621, 173096, -3485, 57956}, {14555, 172248, -3479, 48166}, {15692, 171932, -3564, 62491}, {17146, 172578, -3546, 4609},
		{17973, 173254, -3580, 4655},  {19411, 173905, -3604, 2154},  {20159, 174128, -3577, 54788}, {23193, 172600, -3472, 554},
		{23850, 172349, -3396, 57840}, {24495, 171338, -3381, 54967}, {25261, 170669, -3385, 60908}, {26423, 170887, -3413, 943},
		{26357, 170026, -3348, 26605}, {25473, 169179, -3330, 40551}, {26009, 168673, -3295, 64740}, {26556, 168599, -3253, 2226},
		{27314, 168350, -3220, 47035}, {27086, 166750, -3365, 46402}, {27221, 165848, -3447, 59247}, {26110, 165447, -3437, 34146},
		{26548, 164573, -3496, 54428}, {23190, 166013, -3339, 23885}, {21682, 167176, -3370, 24088}, {20640, 167878, -3390, 27964},
		{20771, 168524, -3395, 13984}, {19174, 170072, -3558, 21523}, {18615, 170401, -3507, 54788}, {21504, 173649, -3577, 2177},
		{21986, 171263, -3567, 55609}, {23064, 170718, -3419, 19893}, {23737, 171751, -3408, 9846},  {24991, 172189, -3388, 7754},
		{25393, 173308, -3428, 15644}, {27313, 172418, -3388, 56835}, {28511, 172474, -3476, 14904}, {28403, 170634, -3390, 44315},
		{27401, 169594, -3271, 43576}, {26214, 169225, -3256, 38961}, {29224, 166327, -3548, 56697}, {28861, 164369, -3632, 38263},
		{15806, 164244, -3644, 48476}, {15933, 165069, -3556, 15252}, {15342, 165490, -3557, 26398}, {14679, 166316, -3611, 16307},
		{15156, 166694, -3551, 14326}, {13016, 166792, -3713, 3823},  {11674, 166816, -3719, 39015}, {11058, 167733, -3696, 19620},
		{9968, 167180, -3604, 43620},  {9112, 167076, -3641, 29917},  {9528, 168170, -3545, 12466},  {9690, 169159, -3535, 60733},
		{9969, 170049, -3538, 8305},   {11123, 171612, -3605, 9787},  {10489, 173775, -3660, 23256}, {9770, 175077, -3662, 19973},
		{10552, 175728, -3623, 8360},  {11806, 176327, -3583, 6701},  {13250, 172087, -3477, 36585}, {14915, 171874, -3504, 3516},
		{16333, 170029, -3558, 64040}, {17037, 168803, -3544, 54926}, {16409, 166252, -3494, 45158}, {14410, 163938, -3702, 41423},
		{13200, 165174, -3696, 19611}, {12473, 166578, -3675, 21933}, {13818, 168499, -3660, 14813}, {18012, 174804, -3673, 1808},
		{23437, 169290, -3436, 64996}, {25486, 167606, -3227, 23874}, {22918, 165340, -3340, 37225}, {20191, 166388, -3405, 31650},
		{19683, 167733, -3395,15523}
	};
	//@formatter:on
	
	private ZakenCurse()
	{
		super(ZakenCurse.class.getSimpleName(), "events");
		
		addStartNpc(MAGIC_LABORATORY_WORKER);
		addFirstTalkId(MAGIC_LABORATORY_WORKER);
		addTalkId(MAGIC_LABORATORY_WORKER);
		
		if (isEventPeriod())
		{
			spawnNpc(PIGS[0], PIGS_SPAWNS);
			
			for (int npcId : PIGS)
			{
				addSkillSeeId(npcId);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new ZakenCurse();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "continue":
			{
				htmltext = "32131-01.htm";
				break;
			}
			case "accept":
			{
				final long bowReuseTime = player.getVariables().getLong("BOW_REUSE_TIME", 0);
				if (bowReuseTime > System.currentTimeMillis())
				{
					final long remainingTime = (bowReuseTime - System.currentTimeMillis()) / 1000;
					final int hours = (int) (remainingTime / 3600);
					final int minutes = (int) ((remainingTime % 3600) / 60);
					final SystemMessage systemMessage = SystemMessage.getSystemMessage(SystemMessageId.AVAILABLE_AFTER_S1_S2_HOURS_S3_MINUTES);
					systemMessage.addItemName(REDEMPTION_BOW);
					systemMessage.addInt(hours);
					systemMessage.addInt(minutes);
					player.sendPacket(systemMessage);
				}
				else
				{
					if (player.getLevel() >= MIN_LVL)
					{
						giveItems(player, REDEMPTION_BOW, 1);
						player.getVariables().set("BOW_REUSE_TIME", System.currentTimeMillis() + BOW_REUSE_TIME);
						htmltext = "32131-02.htm";
					}
				}
				break;
			}
			case "stop":
			{
				if (player.getInventory().getInventoryItemCount(REDEMPTION_BOW, -1) > 0)
				{
					takeItems(player, REDEMPTION_BOW, player.getInventory().getInventoryItemCount(REDEMPTION_BOW, -1));
				}
				else
				{
					player.sendMessage("Can't see the bow in your inventory");
					break;
				}
				
				if (player.getVariables().hasVariable("BOW_REUSE_TIME"))
				{
					player.getVariables().remove("BOW_REUSE_TIME");
				}
				htmltext = "32131-03.htm";
				break;
			}
			case "isBest":
			{
				if (player.getInventory().getInventoryItemCount(GOLDEN_APIGA, -1) >= MAX_GOLDEN_APIGA)
				{
					htmltext = "32131-04.htm";
					giveItems(player, 8919, 1); // First Mate's Hat
					takeItems(player, GOLDEN_APIGA, MAX_GOLDEN_APIGA);
					break;
				}
				
				if (player.getInventory().getInventoryItemCount(GOLDEN_APIGA, -1) <= 50)
				{
					htmltext = "32131-05.htm";
				}
				else
				{
					final int chance = getRandom(0, 100);
					if (chance < 1)
					{
						giveItems(player, 8743, 1); // High-Grade Life Stone - Level 46
						giveItems(player, 955, 1); // Scroll: Enchant Weapon (D-Grade)
					}
					else if (chance < 2)
					{
						giveItems(player, 8744, 1); // High-Grade Life Stone - Level 49
						giveItems(player, 956, 1); // Scroll: Enchant Armor (D-Grade)
					}
					else if (chance < 5)
					{
						giveItems(player, 8745, 1); // High-Grade Life Stone - Level 52
						giveItems(player, 951, 1); // Scroll: Enchant Weapon (C-Grade)
					}
					else if (chance < 34)
					{
						giveItems(player, 8746, 1); // High-Grade Life Stone - Level 55
						giveItems(player, 952, 1); // Scroll: Enchant Armor (C-Grade)
					}
					else if (chance < 40)
					{
						giveItems(player, 8747, 1); // High-Grade Life Stone - Level 58
						giveItems(player, 947, 1); // Scroll: Enchant Weapon (B-Grade)
					}
					else if (chance < 45)
					{
						giveItems(player, 8748, 1); // High-Grade Life Stone - Level 61
						giveItems(player, 948, 1); // Scroll: Enchant Armor (B-Grade)
					}
					else if (chance < 49)
					{
						giveItems(player, 8749, 1); // High-Grade Life Stone - Level 64
						giveItems(player, 729, 1); // Scroll: Enchant Weapon (A-Grade)
					}
					else if (chance < 52)
					{
						giveItems(player, 20583, 2); // Blessed Scroll of Escape (Event)
						giveItems(player, 8750, 1); // High-Grade Life Stone - Level 67
					}
					else if (chance < 54)
					{
						giveItems(player, 730, 1); // Scroll: Enchant Armor (A-Grade)
						giveItems(player, 20584, 2); // Blessed Scroll of Resurrection (Event)
					}
					else if (chance < 55)
					{
						giveItems(player, 8751, 1); // High-Grade Life Stone - Level 70
						giveItems(player, 959, 1); // Scroll: Enchant Weapon (S-Grade)
					}
					else if (chance < 75)
					{
						giveItems(player, 9159, 1); // Silver Circlet of Salvation
						giveItems(player, 8752, 1); // High-Grade Life Stone - Level 76
					}
					else if (chance < 90)
					{
						giveItems(player, 9158, 1); // Gold Circlet of Redemption
						giveItems(player, 9160, 1); // Pig Wrangler's Cap
					}
					else
					{
						giveItems(player, 10632, 1); // Wondrous Cubic
					}
					takeItems(player, GOLDEN_APIGA, 50);
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "32131.htm";
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, L2Object[] targets, boolean isSummon)
	{
		if (!Util.contains(PIGS, npc.getId()))
		{
			return null;
		}
		
		npc.onDecay();
		
		if (skill.getId() == FORGIVENESS.getSkillId())
		{
			final int chance = getRandom(0, 100);
			
			if (npc.getId() < 13033)
			{
				if (chance <= CHANCE[0])
				{
					addSpawn(npc.getId() + 1, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 60000);
				}
				else
				{
					giveItems(caster, GOLDEN_APIGA, REWARD[0]);
				}
				return null;
			}
			else if (npc.getId() == 13034)
			{
				giveItems(caster, GOLDEN_APIGA, REWARD[1]);
			}
			else if (npc.getId() == 13035)
			{
				giveItems(caster, GOLDEN_APIGA, REWARD[2]);
			}
			return null;
		}
		
		if (skill.getId() == PARDON.getSkillId())
		{
			final int chance = getRandom(0, 100);
			
			if (npc.getId() < 13033)
			{
				if (chance <= CHANCE[2])
				{
					addSpawn(13035, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 60000);
				}
				else if (chance <= CHANCE[1])
				{
					addSpawn(13034, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 60000);
				}
				else if (chance <= CHANCE[0])
				{
					if (npc.getId() < 13033)
					{
						addSpawn(npc.getId() + 1, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 60000);
					}
				}
				else
				{
					giveItems(caster, GOLDEN_APIGA, REWARD[0]);
				}
			}
			else
			{
				if (npc.getId() == 13034)
				{
					giveItems(caster, GOLDEN_APIGA, REWARD[1]);
				}
				else if (npc.getId() == 13035)
				{
					giveItems(caster, GOLDEN_APIGA, REWARD[2]);
				}
				return null;
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	private void spawnNpc(int npcId, int[][] spawnList)
	{
		final L2NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcId);
		
		for (int[] spawnListData : spawnList)
		{
			try
			{
				final L2Spawn spawn = new L2Spawn(npcTemplate);
				spawn.setX(spawnListData[0]);
				spawn.setY(spawnListData[1]);
				spawn.setZ(spawnListData[2]);
				spawn.setHeading(spawnListData[3]);
				spawn.setRespawnDelay(60);
				spawn.setAmount(1);
				SpawnTable.getInstance().addNewSpawn(spawn, false);
				spawn.init();
			}
			catch (Exception e)
			{
				_log.warning(getClass().getSimpleName() + ": Could not spawn Npc: " + npcId);
				e.printStackTrace();
			}
		}
	}
}