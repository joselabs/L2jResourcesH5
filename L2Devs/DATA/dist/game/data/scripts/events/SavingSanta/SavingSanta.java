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
package events.SavingSanta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jdevs.gameserver.ThreadPoolManager;
import org.l2jdevs.gameserver.ai.CtrlIntention;
import org.l2jdevs.gameserver.datatables.ItemTable;
import org.l2jdevs.gameserver.datatables.SkillData;
import org.l2jdevs.gameserver.enums.audio.Sound;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.L2World;
import org.l2jdevs.gameserver.model.Location;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.event.LongTimeEvent;
import org.l2jdevs.gameserver.model.holders.ItemHolder;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.model.zone.ZoneId;
import org.l2jdevs.gameserver.network.NpcStringId;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.serverpackets.ActionFailed;
import org.l2jdevs.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jdevs.gameserver.network.serverpackets.NpcSay;
import org.l2jdevs.gameserver.network.serverpackets.SocialAction;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;
import org.l2jdevs.gameserver.util.Broadcast;
import org.l2jdevs.gameserver.util.Util;

/**
 * Saving Santa Christmas Event.
 * @author Zoey76, U3Games, Sacrifice
 */
public final class SavingSanta extends LongTimeEvent
{
	private static final int SANTA_TRAINEE = 31863;
	private static final int THOMAS_D_TURKEY = 13183;
	private static final int SPECIAL_CHRISTMAS_TREE_NPC_ID = 13007;
	private static final int CHRISTMAS_SANTA_MERRY_CHRISTMAS = 104;
	private static final int CHRISTMAS_SLEED_MERRY_CHRISTMAS = 105;
	
	private static final int SPECIAL_TREE_RECOVERY_BONUS = 2139;
	private static final int TURKEYS_CHOICE_SCISSORS = 6100;
	private static final int ATTACK_TURKEY = 6116;
	private static final int CHRISTMAS_FESTIVAL = 23017;
	private static final int STUPID_TURKEYS_MISTAKE = 23018;
	private static final int SCISSORS = 23019;
	private static final int FIRST_WIN = 23022;
	private static final int RESET_CONSECUTIVE_WINS = 23023;
	
	private static final int[] SANTA_MAGE_BUFFS =
	{
		7051, // Master's Blessing - Shield
		7054, // Master's Blessing - Empower
		7055 // Master's Blessing - Wind Walk
	};
	
	private static final int[] SANTA_FIGHTER_BUFFS =
	{
		7043, // Master's Blessing - Haste
		7051, // Master's Blessing - Shield
		7057 // Master's Blessing - Greater Might
	};
	
	private static final ItemHolder[] REQUIRED_ITEMS =
	{
		new ItemHolder(5556, 4), // Star Ornament
		new ItemHolder(5557, 4), // Bead Ornament
		new ItemHolder(5558, 10), // Fir Tree Branch
		new ItemHolder(5559, 1) // Flower Pot
	};
	
	private static final int SANTA_CLAUS_WEAPON_EXCHANGE_TICKET_NORMAL = 20107;
	private static final int SANTA_CLAUS_WEAPON_EXCHANGE_TICKET_JACKPOT = 20108;
	
	private static final int SANTA_CLAUS_GIFT_SET_NORMAL = 20101;
	private static final int SANTA_CLAUS_GIFT_SET_JACKPOT = 20102;
	
	private static final int SAVING_SANTA_HAT = 20100;
	private static final int SANTAS_HAT = 7836;
	
	private static final int CHRISTMAS_TREE = 5560;
	private static final int SPECIAL_CHRISTMAS_TREE_ITEM_ID = 5561;
	
	// @formatter:off
	private static final List<Integer> RANDOM_A_PLUS_10_WEAPON =
	Arrays.asList(
		81, // Dragon Slayer
		151, // Sword of Miracles
		164, // Elysian
		213, // Branch of the Mother Tree 
		236, // Soul Separator
		270, // Dragon Grinder
		289, // Soul Bow
		2500, // Dark Legion's Edge
		5706, // Damascus*Damascus
		7895, // Flaming Dragon Skull
		7902 // Doom Crusher
	);
	//@formatter:on
	
	private static final Location THOMAS_D_TURKEY_SPAWN = new Location(117935, -126003, -2585, 54625);
	
	private static final Location[] SANTAS_HELPER_SPAWN =
	{
		new Location(147698, -56025, -2775),
		new Location(147443, 26942, -2205),
		new Location(82218, 148605, -3470),
		new Location(82754, 53573, -1496),
		new Location(15064, 143254, -2668),
		new Location(111067, 218933, -3543),
		new Location(-12965, 122914, -3117),
		new Location(87362, -143166, -1293),
		new Location(-81037, 150092, -3044),
		new Location(117412, 76642, -2695),
		new Location(43983, -47758, -797),
		new Location(-45907, 49387, -3060),
		new Location(12153, 16753, -4584),
		new Location(-84458, 244761, -3730),
		new Location(114750, -178692, -820),
		new Location(-45656, -113119, -240),
		new Location(-117195, 46837, 367)
	};
	
	private static final NpcStringId[] NPC_STRINGS =
	{
		NpcStringId.ITS_HURTING_IM_IN_PAIN_WHAT_CAN_I_DO_FOR_THE_PAIN,
		NpcStringId.NO_WHEN_I_LOSE_THAT_ONE_ILL_BE_IN_MORE_PAIN,
		NpcStringId.HAHAHAH_I_CAPTURED_SANTA_CLAUS_THERE_WILL_BE_NO_GIFTS_THIS_YEAR,
		NpcStringId.NOW_WHY_DONT_YOU_TAKE_UP_THE_CHALLENGE,
		NpcStringId.COME_ON_ILL_TAKE_ALL_OF_YOU_ON,
		NpcStringId.HOW_ABOUT_IT_I_THINK_I_WON,
		NpcStringId.NOW_THOSE_OF_YOU_WHO_LOST_GO_AWAY,
		NpcStringId.WHAT_A_BUNCH_OF_LOSERS,
		NpcStringId.I_GUESS_YOU_CAME_TO_RESCUE_SANTA_BUT_YOU_PICKED_THE_WRONG_PERSON,
		NpcStringId.AH_OKAY,
		NpcStringId.UAGH_I_WASNT_GOING_TO_DO_THAT,
		NpcStringId.YOURE_CURSED_OH_WHAT,
		NpcStringId.STOP_IT_NO_MORE_I_DID_IT_BECAUSE_I_WAS_TOO_LONELY,
		NpcStringId.I_HAVE_TO_RELEASE_SANTA_HOW_INFURIATING,
		NpcStringId.I_HATE_HAPPY_MERRY_CHRISTMAS,
		NpcStringId.OH_IM_BORED,
		NpcStringId.SHALL_I_GO_TO_TAKE_A_LOOK_IF_SANTA_IS_STILL_THERE_HEHE,
		NpcStringId.OH_HO_HO_MERRY_CHRISTMAS,
		NpcStringId.SANTA_COULD_GIVE_NICE_PRESENTS_ONLY_IF_HES_RELEASED_FROM_THE_TURKEY,
		NpcStringId.OH_HO_HO_OH_HO_HO_THANK_YOU_LADIES_AND_GENTLEMEN_I_WILL_REPAY_YOU_FOR_SURE,
		NpcStringId.UMERRY_CHRISTMAS_YOURE_DOING_A_GOOD_JOB,
		NpcStringId.MERRY_CHRISTMAS_THANK_YOU_FOR_RESCUING_ME_FROM_THAT_WRETCHED_TURKEY,
		NpcStringId.S1_I_HAVE_PREPARED_A_GIFT_FOR_YOU,
		NpcStringId.I_HAVE_A_GIFT_FOR_S1,
		NpcStringId.TAKE_A_LOOK_AT_THE_INVENTORY_I_HOPE_YOU_LIKE_THE_GIFT_I_GAVE_YOU,
		NpcStringId.TAKE_A_LOOK_AT_THE_INVENTORY_PERHAPS_THERE_MIGHT_BE_A_BIG_PRESENT,
		NpcStringId.IM_TIRED_OF_DEALING_WITH_YOU_IM_LEAVING,
		NpcStringId.WHEN_ARE_YOU_GOING_TO_STOP_I_SLOWLY_STARTED_TO_BE_TIRED_OF_IT,
		NpcStringId.MESSAGE_FROM_SANTA_CLAUS_MANY_BLESSINGS_TO_S1_WHO_SAVED_ME
	};
	
	private static final long MIN_TIME_BETWEEN_TWO_REWARDS = 43200000; // 12 Hours
	private static final long MIN_TIME_BETWEEN_TWO_BLESSINGS = 14400000; // 4 Hours
	
	// Is Saving Santa event used?
	private static boolean _savingSanta = true;
	// Use Santa's Helpers Auto Buff?
	private static boolean _santasHelperAutoBuff = true;
	
	private static boolean _christmasEvent = true;
	private static boolean _isSantaFree = true;
	private static boolean _isJackPot = false;
	private static boolean _isWaitingForPlayerSkill = false;
	
	private static List<L2Npc> _santaHelpers = new ArrayList<>();
	private static List<L2Npc> _specialTrees = new ArrayList<>();
	
	private final Map<String, Long> _rewardedPlayers = new HashMap<>();
	private final Map<String, Long> _blessedPlayers = new HashMap<>();
	
	private SavingSanta()
	{
		super(SavingSanta.class.getSimpleName(), "events");
		addStartNpc(SANTA_TRAINEE);
		addAggroRangeEnterId(THOMAS_D_TURKEY);
		addFirstTalkId(THOMAS_D_TURKEY, CHRISTMAS_SANTA_MERRY_CHRISTMAS, CHRISTMAS_SLEED_MERRY_CHRISTMAS, SANTA_TRAINEE);
		addSkillSeeId(THOMAS_D_TURKEY);
		addSpawnId(SPECIAL_CHRISTMAS_TREE_NPC_ID);
		addTalkId(SANTA_TRAINEE);
		
		if (isEventPeriod())
		{
			_christmasEvent = true;
			startQuestTimer("SpecialTreeHeal", 5000, null, null);
			
			for (Location santasHelperSpawn : SANTAS_HELPER_SPAWN)
			{
				_santaHelpers.add(addSpawn(SANTA_TRAINEE, santasHelperSpawn.getX(), santasHelperSpawn.getY(), santasHelperSpawn.getZ(), 0, false, 0));
			}
			
			if (_santasHelperAutoBuff)
			{
				startQuestTimer("SantaBlessings", 5000, null, null);
			}
			
			if (_savingSanta)
			{
				startQuestTimer("ThomasQuest", 1000, null, null);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new SavingSanta();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		
		switch (event)
		{
			case "ThomasQuest":
			{
				startQuestTimer("ThomasQuest", 14400000, null, null);
				final L2Npc ThomasDTurkey = addSpawn(THOMAS_D_TURKEY, THOMAS_D_TURKEY_SPAWN.getX(), THOMAS_D_TURKEY_SPAWN.getY(), THOMAS_D_TURKEY_SPAWN.getZ(), THOMAS_D_TURKEY_SPAWN.getHeading(), false, 1800000);
				Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.THOMAS_D_TURKEY_APPEARED));
				startQuestTimer("ThomasCast1", 15000, ThomasDTurkey, null);
				_isSantaFree = false;
				break;
			}
			case "SantaSpawn":
			{
				if (_isSantaFree)
				{
					startQuestTimer("SantaSpawn", 120000, null, null);
					for (L2PcInstance players : L2World.getInstance().getPlayers())
					{
						if ((players != null) && players.isOnline() && (players.getLevel() >= 20) && players.isInCombat() && !players.isInsideZone(ZoneId.PEACE) && !players.isFlyingMounted())
						{
							if (_rewardedPlayers.containsKey(players.getAccountName()))
							{
								final long elapsedTimeSinceLastRewarded = System.currentTimeMillis() - _rewardedPlayers.get(players.getAccountName());
								if (elapsedTimeSinceLastRewarded < MIN_TIME_BETWEEN_TWO_REWARDS)
								{
									continue;
								}
							}
							else
							{
								final String data = loadGlobalQuestVar(players.getAccountName());
								if (!data.isEmpty() && ((System.currentTimeMillis() - Long.parseLong(data)) < MIN_TIME_BETWEEN_TWO_REWARDS))
								{
									_rewardedPlayers.put(players.getAccountName(), Long.parseLong(data));
									continue;
								}
							}
							final int locx = (int) (players.getX() + (Math.pow(-1, getRandom(1, 2)) * 50));
							final int locy = (int) (players.getY() + (Math.pow(-1, getRandom(1, 2)) * 50));
							final int heading = Util.calculateHeadingFrom(locx, locy, players.getX(), players.getY());
							final L2Npc santa = addSpawn(CHRISTMAS_SANTA_MERRY_CHRISTMAS, locx, locy, players.getZ(), heading, false, 30000);
							_rewardedPlayers.put(players.getAccountName(), System.currentTimeMillis());
							saveGlobalQuestVar(players.getAccountName(), String.valueOf(System.currentTimeMillis()));
							startQuestTimer("SantaRewarding0", 500, santa, players);
						}
					}
				}
				break;
			}
			case "ThomasCast1":
			{
				if (!npc.isDecayed())
				{
					_isWaitingForPlayerSkill = true;
					startQuestTimer("ThomasCast2", 4000, npc, null);
					npc.doCast(SkillData.getInstance().getSkill(ATTACK_TURKEY, 1));
					// It's hurting... I'm in pain... What can I do for the pain...
					// No... When I lose that one... I'll be in more pain...
					// Hahahah!!! I captured Santa Claus!! There will be no gifts this year!!!
					// Now! Why don't you take up the challenge?
					// Come on, I'll take all of you on!
					// How about it? I think I won?
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[getRandom(6)]));
				}
				else
				{
					if (!_isSantaFree)
					{
						Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.THOMAS_D_TURKEY_DISAPPEARED));
						_isWaitingForPlayerSkill = false;
					}
				}
				break;
			}
			case "ThomasCast2":
			{
				if (!npc.isDecayed())
				{
					final Skill skill = SkillData.getInstance().getSkill(TURKEYS_CHOICE_SCISSORS, getRandom(1, 3));
					startQuestTimer("ThomasCast1", 13000, npc, null);
					npc.doCast(skill);
					// It's hurting... I'm in pain... What can I do for the pain...
					// No... When I lose that one... I'll be in more pain...
					// Hahahah!!! I captured Santa Claus!! There will be no gifts this year!!!
					// Now! Why don't you take up the challenge?
					// Come on, I'll take all of you on!
					// How about it? I think I won?
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[getRandom(6)]));
					turkeysChoiceScissorsFinished(npc, skill);
				}
				else
				{
					if (!_isSantaFree)
					{
						Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.THOMAS_D_TURKEY_DISAPPEARED));
						_isWaitingForPlayerSkill = false;
					}
				}
				break;
			}
			case "SantaRewarding0":
			{
				startQuestTimer("SantaRewarding1", 9500, npc, player);
				npc.broadcastPacket(new SocialAction(npc.getObjectId(), 3));
				break;
			}
			case "SantaRewarding1":
			{
				startQuestTimer("SantaRewarding2", 5000, npc, player);
				npc.broadcastPacket(new SocialAction(npc.getObjectId(), 1));
				// Merry Christmas~ Thank you for rescuing me from that wretched Turkey.
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[21]));
				break;
			}
			case "SantaRewarding2":
			{
				startQuestTimer("SantaRewarding3", 5000, npc, player);
				// I have a gift for $s1.
				final NpcSay iHaveAGift = new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[23]);
				iHaveAGift.addStringParameter(player.getName());
				npc.broadcastPacket(iHaveAGift);
				break;
			}
			case "SantaRewarding3":
			{
				npc.broadcastPacket(new SocialAction(npc.getObjectId(), 2));
				
				if (_isJackPot)
				{
					// Take a look at the inventory. Perhaps there might be a big present~
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[25]));
					player.getInventory().addItem("SavingSantaPresent", SANTA_CLAUS_GIFT_SET_JACKPOT, 1, player, npc);
					_isJackPot = false;
				}
				else
				{
					// Take a look at the inventory. I hope you like the gift I gave you.
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[24]));
					player.getInventory().addItem("SavingSantaPresent", SANTA_CLAUS_GIFT_SET_NORMAL, 1, player, npc);
				}
				break;
			}
			case "SantaBlessings":
			{
				if (_christmasEvent)
				{
					startQuestTimer("SantaBlessings", 15000, null, null);
					
					for (L2Npc santaHelper : _santaHelpers)
					{
						final Collection<L2PcInstance> blessList = santaHelper.getKnownList().getKnownPlayers().values();
						for (L2PcInstance playersAlreadyBlessed : blessList)
						{
							if ((playersAlreadyBlessed.getLevel() >= 20) && !playersAlreadyBlessed.isFlyingMounted())
							{
								if (_blessedPlayers.containsKey(playersAlreadyBlessed.getAccountName()))
								{
									final long elapsedTimeSinceLastBlessed = System.currentTimeMillis() - _blessedPlayers.get(playersAlreadyBlessed.getAccountName());
									if (elapsedTimeSinceLastBlessed < MIN_TIME_BETWEEN_TWO_BLESSINGS)
									{
										continue;
									}
								}
								else
								{
									final String data = loadGlobalQuestVar(playersAlreadyBlessed.getAccountName());
									if (!data.isEmpty() && ((System.currentTimeMillis() - Long.parseLong(data)) < MIN_TIME_BETWEEN_TWO_BLESSINGS))
									{
										_blessedPlayers.put(playersAlreadyBlessed.getAccountName(), Long.parseLong(data));
										continue;
									}
								}
								
								for (L2Npc santaHelper1 : _santaHelpers)
								{
									final Collection<L2PcInstance> playerList = santaHelper1.getKnownList().getKnownPlayers().values();
									for (L2PcInstance playersNotBlessed : playerList)
									{
										if (playersNotBlessed.getClassId().isMage())
										{
											for (int buffId : SANTA_MAGE_BUFFS)
											{
												if (playersNotBlessed.getEffectList().getBuffInfoBySkillId(buffId) == null)
												{
													playersNotBlessed.broadcastPacket(new MagicSkillUse(santaHelper1, playersNotBlessed, buffId, 1, 2000, 1));
													SkillData.getInstance().getSkill(buffId, 1).applyEffects(playersNotBlessed, playersNotBlessed);
													_blessedPlayers.put(playersNotBlessed.getAccountName(), System.currentTimeMillis());
													saveGlobalQuestVar(playersNotBlessed.getAccountName(), String.valueOf(System.currentTimeMillis()));
												}
											}
										}
										else
										{
											for (int buffId : SANTA_FIGHTER_BUFFS)
											{
												if (playersNotBlessed.getEffectList().getBuffInfoBySkillId(buffId) == null)
												{
													playersNotBlessed.broadcastPacket(new MagicSkillUse(santaHelper1, playersNotBlessed, buffId, 1, 2000, 1));
													SkillData.getInstance().getSkill(buffId, 1).applyEffects(playersNotBlessed, playersNotBlessed);
													_blessedPlayers.put(playersNotBlessed.getAccountName(), System.currentTimeMillis());
													saveGlobalQuestVar(playersNotBlessed.getAccountName(), String.valueOf(System.currentTimeMillis()));
												}
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			}
			case "SpecialTreeHeal":
			{
				startQuestTimer("SpecialTreeHeal", 9000, null, null);
				
				for (L2Npc tree : _specialTrees)
				{
					final Collection<L2PcInstance> playerList = tree.getKnownList().getKnownPlayers().values();
					for (L2PcInstance players : playerList)
					{
						final int xxMin = tree.getX() - 60;
						final int yyMin = tree.getY() - 60;
						final int xxMax = tree.getX() + 60;
						final int yyMax = tree.getY() + 60;
						final int playerX = players.getX();
						final int playerY = players.getY();
						
						if ((playerX > xxMin) && (playerX < xxMax) && (playerY > yyMin) && (playerY < yyMax))
						{
							SkillData.getInstance().getSkill(SPECIAL_TREE_RECOVERY_BONUS, 1).applyEffects(tree, players);
						}
					}
				}
				break;
			}
			case "Tree":
			{
				if (player != null)
				{
					int itemsOk = 0;
					htmltext = "<html><title>Christmas Event</title><body><br><br><table width=260><tr><td></td><td width=40></td><td width=40></td></tr><tr><td><font color=LEVEL>Christmas Tree</font></td><td width=40><img src=\"Icon.etc_x_mas_tree_i00\" width=32 height=32></td><td width=40></td></tr></table><br><br><table width=260>";
					
					for (ItemHolder item : REQUIRED_ITEMS)
					{
						final long pieceCount = player.getInventory().getInventoryItemCount(item.getId(), -1);
						if (pieceCount >= item.getCount())
						{
							itemsOk = itemsOk + 1;
							htmltext = htmltext + "<tr><td>" + ItemTable.getInstance().getTemplate(item.getId()).getName() + "</td><td width=40>" + pieceCount + "</td><td width=40><font color=0FF000>OK</font></td></tr>";
						}
						else
						{
							htmltext = htmltext + "<tr><td>" + ItemTable.getInstance().getTemplate(item.getId()).getName() + "</td><td width=40>" + pieceCount + "</td><td width=40><font color=8ae2ffb>NO</font></td></tr>";
						}
					}
					
					if (itemsOk == 4)
					{
						htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
						htmltext = htmltext + "<tr><td><center><button value=\"Get the tree\" action=\"bypass -h Quest SavingSanta buyTree\" width=110 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td></tr></table></body></html>";
					}
					else if (itemsOk < 4)
					{
						htmltext = htmltext + "</table><br><br>You do not have enough items.</center></body></html>";
					}
					return htmltext;
				}
				break;
			}
			case "buyTree":
			{
				if (player != null)
				{
					playSound(player, Sound.ITEMSOUND_QUEST_MIDDLE);
					
					for (ItemHolder item : REQUIRED_ITEMS)
					{
						if (player.getInventory().getInventoryItemCount(item.getId(), -1) < item.getCount())
						{
							return "";
						}
					}
					
					for (ItemHolder item : REQUIRED_ITEMS)
					{
						player.getInventory().destroyItemByItemId(event, item.getId(), item.getCount(), player, npc);
					}
					player.getInventory().addItem(event, CHRISTMAS_TREE, 1, player, npc);
				}
				break;
			}
			case "SpecialTree":
			{
				if (!_savingSanta && (player != null))
				{
					htmltext = "<html><title>Christmas Event</title><body><br><br><table width=260><tr><td></td><td width=40></td><td width=40></td></tr><tr><td><font color=LEVEL>Special Christmas Tree</font></td><td width=40><img src=\"Icon.etc_x_mas_tree_i00\" width=32 height=32></td><td width=40></td></tr></table><br><br><table width=260>";
					final long pieceCount = player.getInventory().getInventoryItemCount(CHRISTMAS_TREE, -1);
					int itemsOk = 0;
					
					if (pieceCount >= 10)
					{
						itemsOk = 1;
						htmltext = htmltext + "<tr><td>Christmas Tree</td><td width=40>" + pieceCount + "</td><td width=40><font color=0FF000>OK</font></td></tr>";
					}
					else
					{
						htmltext = htmltext + "<tr><td>Christmas Tree</td><td width=40>" + pieceCount + "</td><td width=40><font color=8ae2ffb>NO</font></td></tr>";
					}
					
					if (itemsOk == 1)
					{
						htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
						htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
						htmltext = htmltext + "<tr><td><center><button value=\"Get the tree\" action=\"bypass -h Quest SavingSanta buySpecialTree\" width=110 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td></tr></table></body></html>";
					}
					else if (itemsOk == 0)
					{
						htmltext = htmltext + "</table><br><br>You do not have enough items.</center></body></html>";
					}
					return htmltext;
				}
				break;
			}
			case "buySpecialTree":
			{
				if (!_savingSanta && (player != null))
				{
					playSound(player, Sound.ITEMSOUND_QUEST_MIDDLE);
					
					if (player.getInventory().getInventoryItemCount(CHRISTMAS_TREE, -1) < 10)
					{
						return "";
					}
					player.getInventory().destroyItemByItemId(event, CHRISTMAS_TREE, 10, player, npc);
					player.getInventory().addItem(event, SPECIAL_CHRISTMAS_TREE_ITEM_ID, 1, player, npc);
				}
				break;
			}
			case "SantaHat":
			{
				if (player != null)
				{
					htmltext = "<html><title>Christmas Event</title><body><br><br><table width=260><tr><td></td><td width=40></td><td width=40></td></tr><tr><td><font color=LEVEL>Santa's Hat</font></td><td width=40><img src=\"Icon.Accessory_santas_cap_i00\" width=32 height=32></td><td width=40></td></tr></table><br><br><table width=260>";
					final long pieceCount = player.getInventory().getInventoryItemCount(CHRISTMAS_TREE, -1);
					int itemsOk = 0;
					
					if (pieceCount >= 10)
					{
						itemsOk = 1;
						htmltext = htmltext + "<tr><td>Christmas Tree</td><td width=40>" + pieceCount + "</td><td width=40><font color=0FF000>OK</font></td></tr>";
					}
					else
					{
						htmltext = htmltext + "<tr><td>Christmas Tree</td><td width=40>" + pieceCount + "</td><td width=40><font color=8ae2ffb>NO</font></td></tr>";
					}
					
					if (itemsOk == 1)
					{
						htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
						htmltext = htmltext + "<tr><td><center><button value=\"Get the hat\" action=\"bypass -h Quest SavingSanta buyHat\" width=110 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td></tr></table></body></html>";
					}
					else if (itemsOk == 0)
					{
						htmltext = htmltext + "</table><br><br>You do not have enough items.</center></body></html>";
					}
					return htmltext;
				}
				break;
			}
			case "buyHat":
			{
				if (player != null)
				{
					playSound(player, Sound.ITEMSOUND_QUEST_MIDDLE);
					
					if (player.getInventory().getInventoryItemCount(CHRISTMAS_TREE, -1) < 10)
					{
						return "";
					}
					player.getInventory().destroyItemByItemId(event, CHRISTMAS_TREE, 10, player, npc);
					player.getInventory().addItem(event, SANTAS_HAT, 1, player, npc);
				}
				break;
			}
			case "SavingSantaHat":
			{
				if (_savingSanta && (player != null))
				{
					htmltext = "<html><title>Christmas Event</title><body><br><br><table width=260><tr><td></td><td width=40></td><td width=40></td></tr><tr><td><font color=LEVEL>Saving Santa's Hat</font></td><td width=40><img src=\"Icon.Accessory_santas_cap_i00\" width=32 height=32></td><td width=40></td></tr></table><br><br><table width=260>";
					final long adenaCount = player.getInventory().getAdena();
					int itemsOk = 0;
					
					if (adenaCount >= 50000)
					{
						itemsOk = 1;
						htmltext = htmltext + "<tr><td>Adena</td><td width=40>" + adenaCount + "</td><td width=40><font color=0FF000>OK</font></td></tr>";
					}
					else
					{
						htmltext = htmltext + "<tr><td>Adena</td><td width=40>" + adenaCount + "</td><td width=40><font color=8ae2ffb>NO</font></td></tr>";
					}
					
					if (itemsOk == 1)
					{
						htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
						htmltext = htmltext + "<tr><td><center><button value=\"Get the hat\" action=\"bypass -h Quest SavingSanta buySavingHat\" width=110 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td></tr></table></body></html>";
					}
					else if (itemsOk == 0)
					{
						htmltext = htmltext + "</table><br><br>You do not have enough Adena.</center></body></html>";
					}
					return htmltext;
				}
				break;
			}
			case "buySavingHat":
			{
				if (_savingSanta && (player != null))
				{
					playSound(player, Sound.ITEMSOUND_QUEST_MIDDLE);
					
					if (player.getInventory().getAdena() < 50000)
					{
						break;
					}
					player.getInventory().reduceAdena(event, 50000, player, npc);
					player.getInventory().addItem(event, SAVING_SANTA_HAT, 1, player, npc);
				}
				break;
			}
			case "HolidayFestival":
			{
				if (_savingSanta && (player != null))
				{
					if (_isSantaFree)
					{
						npc.broadcastPacket(new MagicSkillUse(npc, player, CHRISTMAS_FESTIVAL, 1, 2000, 1));
						SkillData.getInstance().getSkill(CHRISTMAS_FESTIVAL, 1).applyEffects(player, player);
					}
					else
					{
						return "savingsanta-nobuff.htm";
					}
				}
				break;
			}
			case "getWeapon":
			{
				if (_savingSanta && (player != null))
				{
					if (player.getInventory().getInventoryItemCount(SANTA_CLAUS_WEAPON_EXCHANGE_TICKET_JACKPOT, -1) > 0)
					{
						return "savingsanta-weapon.htm";
					}
					
					if (player.getInventory().getInventoryItemCount(SANTA_CLAUS_WEAPON_EXCHANGE_TICKET_NORMAL, -1) > 0)
					{
						player.getInventory().destroyItemByItemId(event, SANTA_CLAUS_WEAPON_EXCHANGE_TICKET_NORMAL, 1, player, npc);
						player.getInventory().addItem(event, RANDOM_A_PLUS_10_WEAPON.get(getRandom(RANDOM_A_PLUS_10_WEAPON.size())), 1, player, npc).setEnchantLevel(10);
						break;
					}
					return "savingsanta-noweapon.htm";
				}
				break;
			}
		}
		
		if (event.startsWith("weapon_") && (player != null) && _savingSanta)
		{
			if ((player.getInventory().getInventoryItemCount(SANTA_CLAUS_WEAPON_EXCHANGE_TICKET_JACKPOT, -1) <= 0) || (player.getLevel() < 20))
			{
				return null;
			}
			
			int grade = player.getExpertiseLevel() - 1;
			if (grade < -1)
			{
				return null;
			}
			
			int itemId = Integer.parseInt(event.replace("weapon_", ""));
			if ((itemId < 1) || (itemId > 14))
			{
				return null;
			}
			
			if (grade > 4)
			{
				grade = 4;
			}
			itemId += SANTA_CLAUS_WEAPON_EXCHANGE_TICKET_JACKPOT + (grade * 14);
			player.getInventory().destroyItemByItemId(event, SANTA_CLAUS_WEAPON_EXCHANGE_TICKET_JACKPOT, 1, player, npc);
			player.getInventory().addItem(event, itemId, 1, player, npc).setEnchantLevel(getRandom(4, 16));
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (!player.isInsideRadius(THOMAS_D_TURKEY_SPAWN, 600, true, true))
		{
			// I guess you came to rescue Santa. But you picked the wrong person.
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[8]));
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		
		switch (npc.getId())
		{
			case THOMAS_D_TURKEY:
			case CHRISTMAS_SANTA_MERRY_CHRISTMAS:
			case CHRISTMAS_SLEED_MERRY_CHRISTMAS:
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				htmltext = null;
				break;
			}
			case SANTA_TRAINEE:
			{
				if (_savingSanta)
				{
					htmltext = "savingsanta.htm";
				}
				else
				{
					htmltext = "santa.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, L2Object[] targets, boolean isSummon)
	{
		if (_isWaitingForPlayerSkill && (skill.getId() >= 21014) && (skill.getId() <= 21016))
		{
			caster.broadcastPacket(new MagicSkillUse(caster, caster, SCISSORS, skill.getId() - 21013, 3000, 1));
			SkillData.getInstance().getSkill(SCISSORS, skill.getId() - 21013).applyEffects(caster, caster);
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		_specialTrees.add(npc);
		return super.onSpawn(npc);
	}
	
	@Override
	public boolean unload()
	{
		for (L2Npc santaHelper : _santaHelpers)
		{
			santaHelper.deleteMe();
		}
		
		for (L2Npc specialTree : _specialTrees)
		{
			specialTree.deleteMe();
		}
		return super.unload();
	}
	
	private void turkeysChoiceScissorsFinished(L2Npc npc, Skill skill)
	{
		// Turkey's Choice
		// Level 1: Scissors
		// Level 2: Rock
		// Level 3: Paper
		if (skill.getId() == TURKEYS_CHOICE_SCISSORS)
		{
			_isWaitingForPlayerSkill = false;
			for (L2PcInstance playersInRadius : npc.getKnownList().getKnownPlayersInRadius(600))
			{
				// Level 1: Scissors
				// Level 2: Rock
				// Level 3: Paper
				if (playersInRadius.getEffectList().getBuffInfoBySkillId(SCISSORS) == null)
				{
					continue;
				}
				
				final int result = playersInRadius.getEffectList().getBuffInfoBySkillId(SCISSORS).getSkill().getLevel() - skill.getLevel();
				switch (result)
				{
					case 0: // TIE
					{
						// Oh. I'm bored.
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[15]));
						break;
					}
					case -2: // player WINS
					case 1: // player WINS
					{
						final int level = (playersInRadius.getEffectList().getBuffInfoBySkillId(FIRST_WIN) != null ? (playersInRadius.getEffectList().getBuffInfoBySkillId(FIRST_WIN).getSkill().getLevel() + 1) : 1);
						playersInRadius.broadcastPacket(new MagicSkillUse(playersInRadius, playersInRadius, FIRST_WIN, level, 3000, 1));
						SkillData.getInstance().getSkill(FIRST_WIN, level).applyEffects(playersInRadius, playersInRadius);
						
						switch (level)
						{
							case 1:
							case 2:
							{
								// Ah, okay...
								// Agh!! I wasn't going to do that!
								// You're cursed!! Oh.. What?
								// Have you done nothing but rock-paper-scissors??
								npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[10 + getRandom(4)]));
								break;
							}
							case 3:
							{
								SkillData.getInstance().getSkill(STUPID_TURKEYS_MISTAKE, 1).applyEffects(playersInRadius, playersInRadius);
								// Stop it, no more... I did it because I was too lonely...
								npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[13]));
								break;
							}
							case 4:
							{
								Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.THOMAS_D_TURKEY_DEFETED));
								// I have to release Santa... How infuriating!!!
								// I hate happy Merry Christmas!!!
								npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[14 + getRandom(2)]));
								
								startQuestTimer("SantaSpawn", 120000, null, null);
								final L2Npc holidaySled = addSpawn(CHRISTMAS_SLEED_MERRY_CHRISTMAS, 117935, -126003, -2585, 54625, false, 12000);
								// Message from Santa Claus: Many blessings to $s1, who saved me~
								final NpcSay santaSaved = new NpcSay(holidaySled.getObjectId(), 10, holidaySled.getId(), NPC_STRINGS[28]);
								santaSaved.addStringParameter(playersInRadius.getName());
								holidaySled.broadcastPacket(santaSaved);
								// Oh ho ho.... Merry Christmas!!
								holidaySled.broadcastPacket(new NpcSay(holidaySled.getObjectId(), 0, holidaySled.getId(), NPC_STRINGS[17]));
								
								if (getRandom(100) > 90)
								{
									_isJackPot = true;
									playersInRadius.getInventory().addItem("SavingSantaPresent", SANTA_CLAUS_GIFT_SET_JACKPOT, 1, playersInRadius, holidaySled);
								}
								else
								{
									playersInRadius.getInventory().addItem("SavingSantaPresent", SANTA_CLAUS_GIFT_SET_NORMAL, 1, playersInRadius, holidaySled);
								}
								ThreadPoolManager.getInstance().scheduleEvent(new SledAnimation(holidaySled), 7000);
								npc.decayMe();
								_isSantaFree = true;
								break;
							}
						}
						break;
					}
					case -1: // Player LOOSE
					case 2: // Player LOOSE
					{
						// Now!! Those of you who lost, go away!
						// What a bunch of losers.
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), NPC_STRINGS[6 + getRandom(2)]));
						playersInRadius.broadcastPacket(new MagicSkillUse(playersInRadius, playersInRadius, RESET_CONSECUTIVE_WINS, 1, 3000, 1));
						playersInRadius.stopSkillEffects(false, FIRST_WIN);
						break;
					}
				}
			}
		}
	}
	
	private static class SledAnimation implements Runnable
	{
		private final L2Npc _sled;
		
		public SledAnimation(L2Npc sled)
		{
			_sled = sled;
		}
		
		@Override
		public void run()
		{
			try
			{
				_sled.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				_sled.broadcastPacket(new SocialAction(_sled.getObjectId(), 1));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}