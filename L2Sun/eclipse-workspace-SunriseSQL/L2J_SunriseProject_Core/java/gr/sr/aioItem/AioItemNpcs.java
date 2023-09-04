package gr.sr.aioItem;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import l2r.Config;
import l2r.gameserver.GameTimeController;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.sql.CharNameTable;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.data.xml.impl.ClassListData;
import l2r.gameserver.data.xml.impl.HennaData;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.data.xml.impl.MultisellData;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.data.xml.impl.TransformData;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.enums.QuickVarType;
import l2r.gameserver.enums.Race;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.idfactory.IdFactory;
import l2r.gameserver.instancemanager.CastleManager;
import l2r.gameserver.instancemanager.SiegeManager;
import l2r.gameserver.instancemanager.TownManager;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.L2Augmentation;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.PlayerClass;
import l2r.gameserver.model.base.SubClass;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.entity.olympiad.OlympiadManager;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.L2Henna;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.CharInfo;
import l2r.gameserver.network.serverpackets.ExBrExtraUserInfo;
import l2r.gameserver.network.serverpackets.ExShowBaseAttributeCancelWindow;
import l2r.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import l2r.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import l2r.gameserver.network.serverpackets.HennaEquipList;
import l2r.gameserver.network.serverpackets.HennaRemoveList;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.PartySmallWindowAll;
import l2r.gameserver.network.serverpackets.PartySmallWindowDeleteAll;
import l2r.gameserver.network.serverpackets.SetupGauge;
import l2r.gameserver.network.serverpackets.SiegeInfo;
import l2r.gameserver.network.serverpackets.SortedWareHouseWithdrawalList;
import l2r.gameserver.network.serverpackets.SortedWareHouseWithdrawalList.WarehouseListType;
import l2r.gameserver.network.serverpackets.UserInfo;
import l2r.gameserver.network.serverpackets.WareHouseDepositList;
import l2r.gameserver.util.Broadcast;
import l2r.gameserver.util.Util;
import l2r.util.StringUtil;

import gr.sr.achievementEngine.AchievementsHandler;
import gr.sr.achievementEngine.AchievementsManager;
import gr.sr.aioItem.dymanicHtmls.GenerateHtmls;
import gr.sr.aioItem.runnable.TransformFinalizer;
import gr.sr.configsEngine.configs.impl.AioItemsConfigs;
import gr.sr.configsEngine.configs.impl.CustomServerConfigs;
import gr.sr.configsEngine.configs.impl.LeaderboardsConfigs;
import gr.sr.donateEngine.DonateHandler;
import gr.sr.imageGeneratorEngine.CaptchaImageGenerator;
import gr.sr.leaderboards.ArenaLeaderboard;
import gr.sr.leaderboards.CraftLeaderboard;
import gr.sr.leaderboards.FishermanLeaderboard;
import gr.sr.leaderboards.TvTLeaderboard;
import gr.sr.main.Conditions;
import gr.sr.main.TopListsLoader;
import gr.sr.securityEngine.SecurityActions;
import gr.sr.securityEngine.SecurityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vGodFather Version 3.3 Last Edit: 19-10-2012
 */
public class AioItemNpcs
{
	// Variable Section
	// Global Variable
	private static int itemIdToGet;
	private static int price;
	private static Logger _log = LoggerFactory.getLogger(AioItemNpcs.class);
	private static final String[] COMMANDS =
	{
		"withdrawp",
		"withdrawsortedp",
		"withdrawc",
		"withdrawsortedc"
	};
	
	private AioItemNpcs()
	{
		// Dummy default
	}
	
	protected static String getSubClassMenu(Race pRace)
	{
		if (Config.ALT_GAME_SUBCLASS_EVERYWHERE || (pRace != Race.KAMAEL))
		{
			return "data/html/sunrise/AioItemNpcs/subclass/SubClass.htm";
		}
		
		return "data/html/sunrise/AioItemNpcs/subclass/SubClass_NoOther.htm";
	}
	
	protected static String getSubClassFail()
	{
		return "data/html/sunrise/AioItemNpcs/subclass/SubClass_Fail.htm";
	}
	
	/**
	 * Method to manage all player bypasses
	 * @param player
	 * @param command
	 */
	public static void onBypassFeedback(L2PcInstance player, String command)
	{
		final String[] subCommand = command.split("_");
		
		// No null pointers
		if (player == null)
		{
			return;
		}
		
		// Restrictions Section
		if (!Conditions.checkPlayerConditions(player))
		{
			return;
		}
		
		// Page navigation, html command how to starts
		if (command.startsWith("Chat"))
		{
			if (subCommand[1].isEmpty() || (subCommand[1] == null))
			{
				return;
			}
			NpcHtmlMessage msg = new NpcHtmlMessage();
			msg.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/" + subCommand[1]);
			player.sendPacket(msg);
		}
		
		// Warehouse
		else if (command.toLowerCase().startsWith(COMMANDS[0])) // WithdrawP
		{
			if (Config.L2JMOD_ENABLE_WAREHOUSESORTING_PRIVATE)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage();
				msg.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/warehouse/WhSortedP.htm");
				player.sendPacket(msg);
			}
			else
			{
				GenerateHtmls.showPWithdrawWindow(player, null, (byte) 0);
			}
			
			return;
		}
		else if (command.toLowerCase().startsWith(COMMANDS[1])) // WithdrawSortedP
		{
			final String param[] = command.split(" ");
			
			if (param.length > 2)
			{
				GenerateHtmls.showPWithdrawWindow(player, WarehouseListType.valueOf(param[1]), SortedWareHouseWithdrawalList.getOrder(param[2]));
			}
			else if (param.length > 1)
			{
				GenerateHtmls.showPWithdrawWindow(player, WarehouseListType.valueOf(param[1]), SortedWareHouseWithdrawalList.A2Z);
			}
			else
			{
				GenerateHtmls.showPWithdrawWindow(player, WarehouseListType.ALL, SortedWareHouseWithdrawalList.A2Z);
			}
			
			return;
		}
		else if (command.toLowerCase().startsWith(COMMANDS[2])) // WithdrawC
		{
			if (Config.L2JMOD_ENABLE_WAREHOUSESORTING_CLAN)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage();
				msg.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/warehouse/WhSortedC.htm");
				player.sendPacket(msg);
			}
			else
			{
				GenerateHtmls.showCWithdrawWindow(player, null, (byte) 0);
			}
			
			return;
		}
		else if (command.toLowerCase().startsWith(COMMANDS[3])) // WithdrawSortedC
		{
			final String param[] = command.split(" ");
			
			if (param.length > 2)
			{
				GenerateHtmls.showCWithdrawWindow(player, WarehouseListType.valueOf(param[1]), SortedWareHouseWithdrawalList.getOrder(param[2]));
			}
			else if (param.length > 1)
			{
				GenerateHtmls.showCWithdrawWindow(player, WarehouseListType.valueOf(param[1]), SortedWareHouseWithdrawalList.A2Z);
			}
			else
			{
				GenerateHtmls.showCWithdrawWindow(player, WarehouseListType.ALL, SortedWareHouseWithdrawalList.A2Z);
			}
			
			return;
		}
		else if (command.startsWith("ndeposit"))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.setActiveWarehouse(player.getWarehouse());
			if (player.getWarehouse().getSize() == player.getWareHouseLimit())
			{
				player.sendPacket(SystemMessageId.WAREHOUSE_FULL);
				return;
			}
			player.setQuickVar(QuickVarType.PORTAL_WH.getCommand(), true);
			player.tempInventoryDisable();
			player.sendPacket(new WareHouseDepositList(player, WareHouseDepositList.PRIVATE));
		}
		else if (command.startsWith("clandeposit"))
		{
			if (player.getClan() == null)
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
				return;
			}
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.setActiveWarehouse(player.getClan().getWarehouse());
			if (player.getClan().getLevel() == 0)
			{
				player.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
				return;
			}
			
			player.setQuickVar(QuickVarType.PORTAL_WH.getCommand(), true);
			player.setActiveWarehouse(player.getClan().getWarehouse());
			player.tempInventoryDisable();
			player.sendPacket(new WareHouseDepositList(player, WareHouseDepositList.CLAN));
		}
		
		else if (command.startsWith("showMyAchievements"))
		{
			AchievementsHandler.getAchievemntData(player);
			GenerateHtmls.showMyAchievements(player);
		}
		else if (command.startsWith("showAchievementInfo"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				int id = Integer.parseInt(st.nextToken());
				
				GenerateHtmls.showAchievementInfo(id, player);
			}
			catch (Exception e)
			{
				SecurityActions.startSecurity(player, SecurityType.AIO_ITEM);
			}
		}
		else if (command.startsWith("achievementGetReward"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				int id = Integer.parseInt(st.nextToken());
				
				if (!AchievementsManager.checkConditions(id, player))
				{
					return;
				}
				
				AchievementsManager.getInstance().rewardForAchievement(id, player);
				AchievementsHandler.saveAchievementData(player, id);
				GenerateHtmls.showMyAchievements(player);
			}
			catch (Exception e)
			{
				SecurityActions.startSecurity(player, SecurityType.AIO_ITEM);
			}
		}
		else if (command.startsWith("showAchievementStats"))
		{
			GenerateHtmls.showAchievementStats(player);
		}
		else if (command.startsWith("showAchievementHelp"))
		{
			GenerateHtmls.showAchievementHelp(player);
		}
		else if (command.startsWith("showAchievementMain"))
		{
			GenerateHtmls.showAchievementMain(player, 0);
		}
		else if (command.startsWith("siege_"))
		{
			int castleId = 0;
			
			if (command.startsWith("siege_gludio"))
			{
				castleId = 1;
			}
			else if (command.startsWith("siege_dion"))
			{
				castleId = 2;
			}
			else if (command.startsWith("siege_giran"))
			{
				castleId = 3;
			}
			else if (command.startsWith("siege_oren"))
			{
				castleId = 4;
			}
			else if (command.startsWith("siege_aden"))
			{
				castleId = 5;
			}
			else if (command.startsWith("siege_innadril"))
			{
				castleId = 6;
			}
			else if (command.startsWith("siege_goddard"))
			{
				castleId = 7;
			}
			else if (command.startsWith("siege_rune"))
			{
				castleId = 8;
			}
			else if (command.startsWith("siege_schuttgart"))
			{
				castleId = 9;
			}
			
			Castle castle = CastleManager.getInstance().getCastleById(castleId);
			if ((castle != null) && (castleId != 0))
			{
				player.sendPacket(new SiegeInfo(castle));
			}
		}
		
		// Subclass system
		else if (command.startsWith("Subclass"))
		{
			if ((player.getPvpFlag() != 0) && !player.isInsideZone(ZoneIdType.PEACE))
			{
				player.sendMessage("Cannot use while have PvP flag.");
				return;
			}
			
			// Subclasses may not be changed while a skill is in use.
			if (player.isCastingNow() || player.isAllSkillsDisabled())
			{
				player.sendPacket(SystemMessageId.SUBCLASS_NO_CHANGE_OR_CREATE_WHILE_SKILL_IN_USE);
				return;
			}
			else if (player.isInCombat())
			{
				player.sendMessage("Sub classes may not be created or changed while being in combat.");
				return;
			}
			else if (OlympiadManager.getInstance().isRegistered(player))
			{
				player.sendMessage("You can not change subclass when registered for Olympiad.");
				return;
			}
			else if (player.isInParty())
			{
				player.sendMessage("Sub classes may not be created or changed while being in party.");
				return;
			}
			else if (player.isCursedWeaponEquipped())
			{
				player.sendMessage("You can`t change Subclass while Cursed weapon equiped!");
				return;
			}
			final NpcHtmlMessage html = new NpcHtmlMessage();
			// Subclasses may not be changed while a transformated state.
			if (player.getTransformation() != null)
			{
				html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_NoTransformed.htm");
				player.sendPacket(html);
				return;
			}
			// Subclasses may not be changed while a summon is active.
			if (player.getSummon() != null)
			{
				html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_NoSummon.htm");
				player.sendPacket(html);
				return;
			}
			// Subclasses may not be changed while you have exceeded your inventory limit.
			if (!player.isInventoryUnder90(true))
			{
				player.sendPacket(SystemMessageId.NOT_SUBCLASS_WHILE_INVENTORY_FULL);
				return;
			}
			// Subclasses may not be changed while a you are over your weight limit.
			if (player.getWeightPenalty() >= 2)
			{
				player.sendPacket(SystemMessageId.NOT_SUBCLASS_WHILE_OVERWEIGHT);
				return;
			}
			
			int cmdChoice = 0;
			int paramOne = 0;
			int paramTwo = 0;
			try
			{
				cmdChoice = Integer.parseInt(command.substring(9, 10).trim());
				
				int endIndex = command.indexOf(' ', 11);
				if (endIndex == -1)
				{
					endIndex = command.length();
				}
				
				if (command.length() > 11)
				{
					paramOne = Integer.parseInt(command.substring(11, endIndex).trim());
					if (command.length() > endIndex)
					{
						paramTwo = Integer.parseInt(command.substring(endIndex).trim());
					}
				}
			}
			catch (Exception NumberFormatException)
			{
				_log.warn(AioItemNpcs.class.getName() + ": Wrong numeric values for command " + command);
			}
			
			Set<PlayerClass> subsAvailable = null;
			switch (cmdChoice)
			{
				case 0: // Subclass change menu
					html.setFile(player, player.getHtmlPrefix(), getSubClassMenu(player.getRace()));
					break;
				case 1: // Add Subclass - Initial
					// Avoid giving player an option to add a new sub class, if they have max sub-classes already.
					if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS)
					{
						html.setFile(player, player.getHtmlPrefix(), getSubClassFail());
						break;
					}
					
					subsAvailable = Conditions.getAvailableSubClasses(player);
					if ((subsAvailable != null) && !subsAvailable.isEmpty())
					{
						html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Add.htm");
						final StringBuilder content1 = StringUtil.startAppend(200);
						for (PlayerClass subClass : subsAvailable)
						{
							StringUtil.append(content1, "<a action=\"bypass -h Aioitem_Subclass 4 ", String.valueOf(subClass.ordinal()), "\" msg=\"1268;", ClassListData.getInstance().getClass(subClass.ordinal()).getClassName(), "\">", ClassListData.getInstance().getClass(subClass.ordinal()).getClientCode(), "</a><br>");
						}
						html.replace("%list%", content1.toString());
					}
					else
					{
						if ((player.getRace() == Race.ELF) || (player.getRace() == Race.DARK_ELF))
						{
							html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Fail_Elves.htm");
							player.sendPacket(html);
						}
						else if (player.getRace() == Race.KAMAEL)
						{
							html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Fail_Kamael.htm");
							player.sendPacket(html);
						}
						else
						{
							player.sendMessage("There are no sub classes available at this time.");
						}
						
						return;
					}
					break;
				case 2: // Change Class - Initial
					if (player.getSubClasses().isEmpty())
					{
						html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ChangeNo.htm");
					}
					else
					{
						final StringBuilder content2 = StringUtil.startAppend(200);
						if (Conditions.checkVillageMaster(player.getBaseClass()))
						{
							StringUtil.append(content2, "<a action=\"bypass -h Aioitem_Subclass 5 0\">", ClassListData.getInstance().getClass(player.getBaseClass()).getClientCode(), "</a><br>");
						}
						
						for (Iterator<SubClass> subList = Conditions.iterSubClasses(player); subList.hasNext();)
						{
							SubClass subClass = subList.next();
							if (Conditions.checkVillageMaster(subClass.getClassDefinition()))
							{
								StringUtil.append(content2, "<a action=\"bypass -h Aioitem_Subclass 5 ", String.valueOf(subClass.getClassIndex()), "\">", ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode(), "</a><br>");
							}
						}
						
						if (content2.length() > 0)
						{
							html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Change.htm");
							html.replace("%list%", content2.toString());
						}
						else
						{
							html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ChangeNotFound.htm");
						}
					}
					break;
				case 3: // Change/Cancel Subclass - Initial
					if ((player.getSubClasses() == null) || player.getSubClasses().isEmpty())
					{
						html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyEmpty.htm");
						break;
					}
					
					// custom value
					if ((player.getTotalSubClasses() > 3) || (Config.MAX_SUBCLASS > 3))
					{
						html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyCustom.htm");
						final StringBuilder content3 = StringUtil.startAppend(200);
						int classIndex = 1;
						
						for (Iterator<SubClass> subList = Conditions.iterSubClasses(player); subList.hasNext();)
						{
							SubClass subClass = subList.next();
							
							StringUtil.append(content3, "Sub-class ", String.valueOf(classIndex++), "<br>", "<a action=\"bypass -h Aioitem_Subclass 6 ", String.valueOf(subClass.getClassIndex()), "\">", ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode(), "</a><br>");
						}
						html.replace("%list%", content3.toString());
					}
					else
					{
						// retail html contain only 3 subclasses
						html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Modify.htm");
						if (player.getSubClasses().containsKey(1))
						{
							html.replace("%sub1%", ClassListData.getInstance().getClass(player.getSubClasses().get(1).getClassId()).getClientCode());
						}
						else
						{
							html.replace("<a action=\"bypass -h Aioitem_Subclass 6 1\">%sub1%</a><br>", "");
						}
						
						if (player.getSubClasses().containsKey(2))
						{
							html.replace("%sub2%", ClassListData.getInstance().getClass(player.getSubClasses().get(2).getClassId()).getClientCode());
						}
						else
						{
							html.replace("<a action=\"bypass -h Aioitem_Subclass 6 2\">%sub2%</a><br>", "");
						}
						
						if (player.getSubClasses().containsKey(3))
						{
							html.replace("%sub3%", ClassListData.getInstance().getClass(player.getSubClasses().get(3).getClassId()).getClientCode());
						}
						else
						{
							html.replace("<a action=\"bypass -h Aioitem_Subclass 6 3\">%sub3%</a><br>", "");
						}
					}
					break;
				case 4: // Add Subclass - Action (Subclass 4 x[x])
					/**
					 * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice.
					 */
					if (!player.getFloodProtectors().getSubclass().tryPerformAction("add subclass"))
					{
						_log.warn(AioItemNpcs.class.getName() + ": Player " + player.getName() + " has performed a subclass change too fast");
						return;
					}
					
					boolean allowAddition = true;
					
					if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS)
					{
						allowAddition = false;
					}
					
					if (player.getLevel() < 75)
					{
						allowAddition = false;
					}
					
					if (allowAddition)
					{
						if (!player.getSubClasses().isEmpty())
						{
							for (Iterator<SubClass> subList = Conditions.iterSubClasses(player); subList.hasNext();)
							{
								SubClass subClass = subList.next();
								
								if (subClass.getLevel() < 75)
								{
									allowAddition = false;
									break;
								}
							}
						}
					}
					
					/**
					 * If quest checking is enabled, verify if the character has completed the Mimir's Elixir (Path to Subclass) and Fate's Whisper (A Grade Weapon) quests by checking for instances of their unique reward items. If they both exist, remove both unique items and continue with adding
					 * the sub-class.
					 */
					if (allowAddition && !Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS)
					{
						allowAddition = Conditions.checkQuests(player);
					}
					
					if (allowAddition && Conditions.isValidNewSubClass(player, paramOne))
					{
						if (!player.addSubClass(paramOne, player.getTotalSubClasses() + 1))
						{
							return;
						}
						
						player.setActiveClass(player.getTotalSubClasses());
						
						html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_AddOk.htm");
						
						player.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS); // Subclass added.
					}
					else
					{
						html.setFile(player, player.getHtmlPrefix(), getSubClassFail());
					}
					break;
				case 5: // Change Class - Action
					/**
					 * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice. Note: paramOne = classIndex
					 */
					if (!player.getFloodProtectors().getSubclass().tryPerformAction("change class"))
					{
						_log.warn(AioItemNpcs.class.getName() + ": Player " + player.getName() + " has performed a subclass change too fast");
						return;
					}
					
					if (player.getClassIndex() == paramOne)
					{
						html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Current.htm");
						break;
					}
					
					if (paramOne == 0)
					{
						if (!Conditions.checkVillageMaster(player.getBaseClass()))
						{
							return;
						}
					}
					else
					{
						try
						{
							if (!Conditions.checkVillageMaster(player.getSubClasses().get(paramOne).getClassDefinition()))
							{
								return;
							}
						}
						catch (NullPointerException e)
						{
							return;
						}
					}
					
					player.setActiveClass(paramOne);
					player.sendPacket(SystemMessageId.SUBCLASS_TRANSFER_COMPLETED); // Transfer completed.
					return;
				case 6: // Change/Cancel Subclass - Choice
					// validity check
					if ((paramOne < 1) || (paramOne > Config.MAX_SUBCLASS))
					{
						return;
					}
					
					subsAvailable = Conditions.getAvailableSubClasses(player);
					// another validity check
					if ((subsAvailable == null) || subsAvailable.isEmpty())
					{
						player.sendMessage("There are no sub classes available at this time.");
						return;
					}
					
					final StringBuilder content6 = StringUtil.startAppend(200);
					for (PlayerClass subClass : subsAvailable)
					{
						StringUtil.append(content6, "<a action=\"bypass -h Aioitem_Subclass 7 ", String.valueOf(paramOne), " ", String.valueOf(subClass.ordinal()), "\" msg=\"1445;", "\">", ClassListData.getInstance().getClass(subClass.ordinal()).getClientCode(), "</a><br>");
					}
					
					switch (paramOne)
					{
						case 1:
							html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyChoice1.htm");
							break;
						case 2:
							html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyChoice2.htm");
							break;
						case 3:
							html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyChoice3.htm");
							break;
						default:
							html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyChoice.htm");
					}
					html.replace("%list%", content6.toString());
					break;
				case 7: // Change Subclass - Action
					/**
					 * Warning: the information about this subclass will be removed from the subclass list even if false!
					 */
					if (!player.getFloodProtectors().getSubclass().tryPerformAction("change class"))
					{
						_log.warn(AioItemNpcs.class.getName() + ": Player " + player.getName() + " has performed a subclass change too fast");
						return;
					}
					
					if (!Conditions.isValidNewSubClass(player, paramTwo))
					{
						return;
					}
					
					if (player.modifySubClass(paramOne, paramTwo))
					{
						player.abortCast();
						player.stopAllEffectsExceptThoseThatLastThroughDeath(); // all effects from old subclass stopped!
						player.stopAllEffectsNotStayOnSubclassChange();
						player.stopCubics();
						player.setActiveClass(paramOne);
						
						html.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyOk.htm");
						html.replace("%name%", ClassListData.getInstance().getClass(paramTwo).getClientCode());
						
						player.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS); // Subclass added.
					}
					else
					{
						/**
						 * This isn't good! modifySubClass() removed subclass from memory we must update _classIndex! Else IndexOutOfBoundsException can turn up some place down the line along with other seemingly unrelated problems.
						 */
						player.setActiveClass(0); // Also updates _classIndex plus switching _classid to baseclass.
						
						player.sendMessage("The sub class could not be added, you have been reverted to your base class.");
						return;
					}
					break;
			}
			player.sendPacket(html);
		}
		// Create new clan
		else if (command.startsWith("createclan"))
		{
			String[] commandStr = command.split(" ");
			String cmdParams = "";
			
			if (commandStr.length >= 2)
			{
				cmdParams = commandStr[1];
			}
			if (cmdParams.isEmpty())
			{
				return;
			}
			
			if (!Conditions.isValidName(cmdParams))
			{
				player.sendPacket(SystemMessageId.CLAN_NAME_INCORRECT);
				return;
			}
			
			ClanTable.getInstance().createClan(player, cmdParams);
		}
		// Add all clan skills
		else if (command.startsWith("clanskills"))
		{
			itemIdToGet = AioItemsConfigs.GET_FULL_CLAN_COIN;
			price = AioItemsConfigs.GET_FULL_CLAN_PRICE;
			if ((player.getClan() == null) || !player.isClanLeader())
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			if (!Conditions.checkPlayerItemCount(player, itemIdToGet, price))
			{
				return;
			}
			
			player.destroyItemByItemId("Clan donate", itemIdToGet, price, player, true);
			player.getClan().changeLevel(11);
			player.sendMessage("Clan level set to 11");
			player.getClan().addReputationScore(500000, true);
			player.getClan().addNewSkill(SkillData.getInstance().getInfo(391, 1));
			
			//@formatter:off
			int[] squad =
			{
				611, 612, 613, 614, 615, 616
			};
			
			int[] normal =
			{
				370, 371, 372, 373, 374, 375, 376,
				377, 378, 379, 380, 381, 382, 383,
				384, 385, 386, 387, 388, 389, 390
			};
			//@formatter:on
			
			for (int ids : normal)
			{
				player.getClan().addNewSkill(SkillData.getInstance().getInfo(ids, 3));
			}
			for (int ids : squad)
			{
				player.getClan().addNewSkill(SkillData.getInstance().getInfo(ids, 3), 0);
			}
			
			player.sendMessage("You have successfully perform this action");
		}
		// Change player name
		else if (command.startsWith("changename"))
		{
			try
			{
				itemIdToGet = AioItemsConfigs.CHANGE_NAME_COIN;
				price = AioItemsConfigs.CHANGE_NAME_PRICE;
				
				String val = command.substring(11);
				if (!Util.isAlphaNumeric(val))
				{
					player.sendMessage("Invalid character name.");
					return;
				}
				
				if (!Conditions.checkPlayerItemCount(player, itemIdToGet, price))
				{
					return;
				}
				
				if (CharNameTable.getInstance().getIdByName(val) > 0)
				{
					player.sendMessage("Warning, name " + val + " already exists.");
					return;
				}
				player.destroyItemByItemId("Name Change", itemIdToGet, price, player, true);
				player.setName(val);
				player.getAppearance().setVisibleName(val);
				player.store();
				player.sendMessage("Your name has been changed to " + val);
				player.broadcastUserInfo();
				
				if (player.isInParty())
				{
					// Delete party window for other party members
					player.getParty().broadcastToPartyMembers(player, new PartySmallWindowDeleteAll());
					for (L2PcInstance member : player.getParty().getMembers())
					{
						// And re-add
						if (member != player)
						{
							member.sendPacket(new PartySmallWindowAll(member, player.getParty()));
						}
					}
				}
				if (player.getClan() != null)
				{
					player.getClan().broadcastClanStatus();
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty character name
				player.sendMessage("Player name box cannot be empty.");
			}
		}
		// Change clan name
		else if (command.startsWith("changeclanname"))
		{
			try
			{
				itemIdToGet = AioItemsConfigs.CHANGE_CNAME_COIN;
				price = AioItemsConfigs.CHANGE_CNAME_PRICE;
				
				String val = command.substring(15);
				if ((player.getClan() == null) || !player.isClanLeader())
				{
					player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
					return;
				}
				if (!Util.isAlphaNumeric(val))
				{
					player.sendPacket(SystemMessageId.CLAN_NAME_INCORRECT);
					return;
				}
				
				if (!Conditions.checkPlayerItemCount(player, itemIdToGet, price))
				{
					return;
				}
				
				if (ClanTable.getInstance().getClanByName(val) != null)
				{
					player.sendMessage("Warning, clan name " + val + " already exists.");
					return;
				}
				
				player.destroyItemByItemId("Clan Name Change", itemIdToGet, price, player, true);
				player.getClan().setName(val);
				player.getClan().updateClanNameInDB();
				player.sendMessage("Your clan name has been changed to " + val);
				player.broadcastUserInfo();
				
				if (player.isInParty())
				{
					// Delete party window for other party members
					player.getParty().broadcastToPartyMembers(player, new PartySmallWindowDeleteAll());
					for (L2PcInstance member : player.getParty().getMembers())
					{
						// And re-add
						if (member != player)
						{
							member.sendPacket(new PartySmallWindowAll(member, player.getParty()));
						}
					}
				}
				if (player.getClan() != null)
				{
					player.getClan().broadcastClanStatus();
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty character name
				player.sendMessage("Clan name box cannot be empty.");
			}
		}
		// Attribute system
		else if (command.startsWith("element"))
		{
			int armorType = -1;
			
			if (command.startsWith("elementhead"))
			{
				armorType = Inventory.PAPERDOLL_HEAD;
			}
			else if (command.startsWith("elementchest"))
			{
				armorType = Inventory.PAPERDOLL_CHEST;
			}
			else if (command.startsWith("elementgloves"))
			{
				armorType = Inventory.PAPERDOLL_GLOVES;
			}
			else if (command.startsWith("elementboots"))
			{
				armorType = Inventory.PAPERDOLL_FEET;
			}
			else if (command.startsWith("elementlegs"))
			{
				armorType = Inventory.PAPERDOLL_LEGS;
			}
			else if (command.startsWith("elementwep"))
			{
				armorType = Inventory.PAPERDOLL_RHAND;
			}
			
			itemIdToGet = AioItemsConfigs.ELEMENT_COIN;
			price = AioItemsConfigs.ELEMENT_PRICE;
			int elementWeaponValue = AioItemsConfigs.ELEMENT_VALUE_WEAPON;
			int elementArmorValue = AioItemsConfigs.ELEMENT_VALUE_ARMOR;
			
			if (Conditions.checkPlayerItemCount(player, itemIdToGet, price))
			{
				L2ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
				if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == armorType))
				{
					String[] types = command.split(" ");
					byte elementtoAdd = Elementals.getElementId(types[1]);
					byte opositeElement = Elementals.getOppositeElement(elementtoAdd);
					Elementals oldElement = parmorInstance.getElemental(elementtoAdd);
					
					switch (parmorInstance.getItem().getCrystalType())
					{
						case NONE:
						case A:
						case B:
						case C:
						case D:
							player.sendMessage("Invalid item grade.");
							return;
						default:
							break;
					}
					
					if (!AioItemsConfigs.ELEMENT_ALLOW_MORE_ATT_FOR_WEAPONS)
					{
						if ((parmorInstance.isWeapon() && (parmorInstance.getElementals() != null)) || (parmorInstance.isArmor() && (oldElement != null) && (parmorInstance.getElementals() != null) && (parmorInstance.getElementals().length >= 3)))
						{
							player.sendPacket(SystemMessageId.ANOTHER_ELEMENTAL_POWER_ALREADY_ADDED);
							return;
						}
					}
					
					if (parmorInstance.isWeapon())
					{
						if ((oldElement != null) && (oldElement.getValue() >= elementWeaponValue))
						{
							player.sendMessage("You cannot add same attribute to item!");
							return;
						}
						
						if (parmorInstance.getElementals() != null)
						{
							for (Elementals elm : parmorInstance.getElementals())
							{
								if (parmorInstance.isEquipped())
								{
									parmorInstance.getElemental(elm.getElement()).removeBonus(player);
								}
								parmorInstance.clearElementAttr(elm.getElement());
							}
						}
					}
					else if (parmorInstance.isArmor())
					{
						if (parmorInstance.getElementals() != null)
						{
							for (Elementals elm : parmorInstance.getElementals())
							{
								if (elm.getElement() == opositeElement)
								{
									player.sendMessage("You cannot add opposite attribute to item!");
									return;
								}
								if ((elm.getElement() == elementtoAdd) && (elm.getValue() >= elementArmorValue))
								{
									player.sendMessage("You cannot add same attribute to item!");
									return;
								}
							}
						}
					}
					
					player.destroyItemByItemId("element", itemIdToGet, price, player, true);
					player.getInventory().unEquipItemInSlot(armorType);
					parmorInstance.setElementAttr(elementtoAdd, parmorInstance.isWeapon() ? elementWeaponValue : elementArmorValue);
					player.getInventory().equipItem(parmorInstance);
					player.sendMessage("Successfully added " + types[1] + " attribute to your item.");
					
					InventoryUpdate iu = new InventoryUpdate();
					iu.addModifiedItem(parmorInstance);
					player.sendPacket(iu);
				}
				else
				{
					player.sendMessage("You cannot attribute items that are not equipped!");
				}
			}
		}
		// Method to add specific augment to a weapon
		else if (command.startsWith("addaugment"))
		{
			int value = 0;
			int pskill = 0;
			
			String[] stats = command.split(" "); // str, int, men, con
			
			switch (stats[1])
			{
				case "STR+1":
					value = 16341;
					break;
				case "INT+1":
					value = 16343;
					break;
				case "MEN+1":
					value = 16344;
					break;
				case "CON+1":
					value = 16342;
					break;
			}
			
			switch (stats[2])
			{
				case "Heal_Empower":
					pskill = 16279;
					break;
				case "Wild_Magic":
					pskill = 16336;
					break;
				case "Empower":
					pskill = 16281;
					break;
				case "Magic_Barrier":
					pskill = 16282;
					break;
				case "Might":
					pskill = 16283;
					break;
				case "Shield":
					pskill = 16284;
					break;
				case "Duel_Might":
					pskill = 16285;
					break;
			}
			
			pskill = pskill + 8358;
			int armorType = Inventory.PAPERDOLL_RHAND;
			itemIdToGet = AioItemsConfigs.AUGMENT_COIN;
			price = AioItemsConfigs.AUGMENT_PRICE;
			
			if (!Conditions.checkPlayerItemCount(player, itemIdToGet, price))
			{
				return;
			}
			
			L2ItemInstance itemInstance = null;
			L2ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
			if (parmorInstance == null)
			{
				player.sendMessage("Equip the weapon for augmentation.");
				return;
			}
			if (parmorInstance.isAugmented())
			{
				player.sendPacket(SystemMessageId.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
				return;
			}
			if (parmorInstance.isHeroItem() || (parmorInstance.isShadowItem() || (parmorInstance.isCommonItem() || (parmorInstance.isEtcItem() || (parmorInstance.isTimeLimitedItem())))))
			{
				player.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
				return;
			}
			if (parmorInstance.getLocationSlot() == armorType)
			{
				itemInstance = parmorInstance;
			}
			
			if (itemInstance != null)
			{
				// set augment skill
				player.destroyItemByItemId("augment", itemIdToGet, price, player, true);
				player.getInventory().unEquipItemInSlot(armorType);
				itemInstance.setAugmentation(new L2Augmentation(((pskill << 16) + value)));
				player.getInventory().equipItem(itemInstance);
				player.sendPacket(SystemMessageId.THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED);
				
				// send packets
				InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(itemInstance);
				player.sendPacket(iu);
				player.broadcastPacket(new CharInfo(player));
				player.sendPacket(new UserInfo(player));
				player.broadcastPacket(new ExBrExtraUserInfo(player));
			}
		}
		// Teleport
		else if (command.startsWith("teleportTo"))
		{
			if (AioItemsConfigs.AIO_ENABLE_TP_DELAY && AioItemDelay._delayers.contains(player))
			{
				if (AioItemsConfigs.AIO_DELAY_SENDMESSAGE)
				{
					player.sendMessage("In order to use Aio Item teleport function again, you will have to wait " + AioItemsConfigs.AIO_DELAY + "!");
				}
				return;
			}
			
			try
			{
				Integer[] c = new Integer[3];
				c[0] = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(subCommand[1]))[0];
				c[1] = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(subCommand[1]))[1];
				c[2] = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(subCommand[1]))[2];
				boolean onlyForNobless = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(subCommand[1]))[3] == 1;
				itemIdToGet = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(subCommand[1]))[4];
				price = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(subCommand[1]))[5];
				
				if (!AioItemsConfigs.ALLOW_TELEPORT_DURING_SIEGE)
				{
					if (SiegeManager.getInstance().getSiege(c[0], c[1], c[2]) != null)
					{
						player.sendPacket(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE);
						return;
					}
					else if (TownManager.townHasCastleInSiege(c[0], c[1]) && player.isInsideZone(ZoneIdType.TOWN))
					{
						player.sendPacket(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE);
						return;
					}
				}
				
				if (!Conditions.checkPlayerItemCount(player, itemIdToGet, price))
				{
					return;
				}
				
				if (onlyForNobless && !player.isNoble() && !player.isGM())
				{
					player.sendMessage("Only noble chars can teleport there.");
					return;
				}
				
				if (player.isTransformed())
				{
					if ((player.getTransformationId() == 9) || (player.getTransformationId() == 8))
					{
						player.untransform();
					}
				}
				
				if (player.isInsideZone(ZoneIdType.PEACE) || player.isGM())
				{
					player.teleToLocation(c[0], c[1], c[2]);
				}
				else
				{
					if (AioItemsConfigs.TELEPORT_DELAY > 0)
					{
						doTeleportLong(player, c[0], c[1], c[2]);
					}
					else
					{
						player.teleToLocation(c[0], c[1], c[2]);
					}
					
					if (AioItemsConfigs.AIO_ENABLE_TP_DELAY)
					{
						ThreadPoolManager.getInstance().executeGeneral(new AioItemDelay(player));
					}
				}
				player.destroyItemByItemId("AIO Teleport", itemIdToGet, price, player, true);
			}
			catch (Exception e)
			{
				SecurityActions.startSecurity(player, SecurityType.AIO_ITEM);
			}
		}
		// Leaderboard commands
		else if (command.startsWith("rankarenainfo"))
		{
			if (LeaderboardsConfigs.RANK_ARENA_ENABLED)
			{
				NpcHtmlMessage html = new NpcHtmlMessage();
				html.setHtml(ArenaLeaderboard.getInstance().showHtm(player.getObjectId()));
				player.sendPacket(html);
			}
			else
			{
				player.sendMessage("This service is currently disabled.");
			}
		}
		else if (command.startsWith("rankfishermaninfo"))
		{
			if (LeaderboardsConfigs.RANK_FISHERMAN_ENABLED)
			{
				NpcHtmlMessage html = new NpcHtmlMessage();
				html.setHtml(FishermanLeaderboard.getInstance().showHtm(player.getObjectId()));
				player.sendPacket(html);
			}
			else
			{
				player.sendMessage("This service is currently disabled.");
			}
		}
		else if (command.startsWith("rankcraftinfo"))
		{
			if (LeaderboardsConfigs.RANK_CRAFT_ENABLED)
			{
				NpcHtmlMessage html = new NpcHtmlMessage();
				html.setHtml(CraftLeaderboard.getInstance().showHtm(player.getObjectId()));
				player.sendPacket(html);
			}
			else
			{
				player.sendMessage("This service is currently disabled.");
			}
		}
		else if (command.startsWith("ranktvtinfo"))
		{
			if (LeaderboardsConfigs.RANK_TVT_ENABLED)
			{
				NpcHtmlMessage html = new NpcHtmlMessage();
				html.setHtml(TvTLeaderboard.getInstance().showHtm(player.getObjectId()));
				player.sendPacket(html);
			}
			else
			{
				player.sendMessage("This service is currently disabled.");
			}
		}
		// Change gender options
		else if (command.startsWith("changeGender"))
		{
			if (command.startsWith("changeGenderDonate"))
			{
				itemIdToGet = AioItemsConfigs.CHANGE_GENDER_DONATE_COIN;
				price = AioItemsConfigs.CHANGE_GENDER_DONATE_PRICE;
			}
			else if (command.startsWith("changeGenderNormal"))
			{
				itemIdToGet = AioItemsConfigs.CHANGE_GENDER_NORMAL_COIN;
				price = AioItemsConfigs.CHANGE_GENDER_NORMAL_PRICE;
			}
			
			if (!Conditions.checkPlayerItemCount(player, itemIdToGet, price))
			{
				return;
			}
			
			player.destroyItemByItemId("changeGender", itemIdToGet, price, player, true);
			player.getAppearance().setSex(player.getAppearance().getSex() ? false : true);
			player.sendMessage("Your gender has been changed.");
			player.broadcastUserInfo();
			// Transform-untransorm player quickly to force the client to reload the character textures
			TransformData.getInstance().transformPlayer(105, player);
			player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(new TransformFinalizer(player), 200));
		}
		// GM Shop
		else if (command.startsWith("showMultiSellWindow"))
		{
			try
			{
				int multi = Integer.valueOf(subCommand[1]);
				if (AioItemsConfigs.MULTISELL_LIST.contains(multi))
				{
					MultisellData.getInstance().separateAndSend(multi, player, null, false);
				}
				else
				{
					SecurityActions.startSecurity(player, SecurityType.AIO_ITEM);
				}
			}
			catch (Exception e)
			{
				SecurityActions.startSecurity(player, SecurityType.AIO_ITEM);
			}
		}
		// Donate generate captcha code
		else if (command.startsWith("donateFormMain"))
		{
			NpcHtmlMessage playerReply = new NpcHtmlMessage();
			
			// Random image file name
			int imgId = IdFactory.getInstance().getNextId();
			// Conversion from .png to .dds, and crest packed send
			CaptchaImageGenerator.getInstance().captchaLogo(player, imgId);
			playerReply.setHtml("<html><title>Donate Captcha System</title>" + "<body>" + "<center>Enter the 5-digits code below and click Confirm." + "<br><img src=\"Crest.crest_" + Config.SERVER_ID + "_" + imgId + "\" width=256 height=64>" + "<br><font color=\"888888\">(There are only english uppercase letters.)</font>" + "<br><edit var=\"captcha\" width=110><br><button value=\"Confirm\" action=\"bypass -h Aioitem_confirmDonateCode $captcha\" width=80 height=26 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center></body></html>");
			player.sendPacket(playerReply);
			player.setDonateCode(CaptchaImageGenerator.getInstance().getFinalString());
			CaptchaImageGenerator.getInstance().getFinalString().replace(0, 5, "");
			return;
		}
		// Donate captcha code
		else if (command.startsWith("confirmDonateCode"))
		{
			String value = command.substring(17);
			StringTokenizer st = new StringTokenizer(value, " ");
			try
			{
				String newpass = null, repeatnewpass = null;
				if (st.hasMoreTokens())
				{
					newpass = st.nextToken();
				}
				repeatnewpass = player.getDonateCode();
				
				if (!((newpass == null) || (repeatnewpass == null)))
				{
					if (newpass.equals(repeatnewpass)) // Right:)
					{
						GenerateHtmls.sendPacket(player, "donate/donateform.htm");
						return;
					}
				}
				if ((newpass == null) || !newpass.equals(repeatnewpass))
				{
					player.sendMessage("Incorrect captcha code try again.");
				}
			}
			catch (Exception e)
			{
				player.sendMessage("A problem occured while adding captcha!");
				_log.warn(String.valueOf(e));
			}
		}
		// Donate form
		else if (command.startsWith("sendDonateForm"))
		{
			DonateHandler.sendDonateForm(player, command);
		}
		else if (command.startsWith("topFa"))
		{
			if (CustomServerConfigs.TOP_LISTS_RELOAD_TIME > 0)
			{
				GenerateHtmls.showTopFa(player);
			}
			else
			{
				player.sendMessage("Top Lists: Disabled.");
			}
		}
		// Top PvP info
		else if (command.startsWith("TopPvp"))
		{
			if (CustomServerConfigs.TOP_LISTS_RELOAD_TIME > 0)
			{
				GenerateHtmls.showTopPvp(player);
			}
			else
			{
				player.sendMessage("Top Lists: Disabled.");
			}
		}
		// Top pk info
		else if (command.startsWith("TopPk"))
		{
			if (CustomServerConfigs.TOP_LISTS_RELOAD_TIME > 0)
			{
				GenerateHtmls.showTopPk(player);
			}
			else
			{
				player.sendMessage("Top Lists: Disabled.");
			}
		}
		// Top clan info
		else if (command.startsWith("TopClan"))
		{
			if (CustomServerConfigs.TOP_LISTS_RELOAD_TIME > 0)
			{
				GenerateHtmls.showTopClan(player);
			}
			else
			{
				player.sendMessage("Top Lists: Disabled.");
			}
		}
		// Grand boss info
		else if (command.startsWith("rbinfo"))
		{
			GenerateHtmls.showRbInfo(player);
		}
		else if (command.startsWith("addAugment"))
		{
			player.sendPacket(new ExShowVariationMakeWindow());
		}
		else if (command.startsWith("delAugment"))
		{
			player.sendPacket(new ExShowVariationCancelWindow());
		}
		else if (command.startsWith("removeAtt"))
		{
			player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
		}
		else if (command.startsWith("drawSymbol"))
		{
			List<L2Henna> tato = HennaData.getInstance().getHennaList(player.getClassId());
			player.sendPacket(new HennaEquipList(player, tato));
		}
		else if (command.startsWith("removeSymbol"))
		{
			boolean hasHennas = false;
			
			for (int i = 1; i <= 3; i++)
			{
				final L2Henna henna = player.getHennaEx().getHenna(i);
				if (henna != null)
				{
					hasHennas = true;
				}
			}
			if (hasHennas)
			{
				player.sendPacket(new HennaRemoveList(player));
			}
			else
			{
				player.sendMessage("You do not have dyes.");
			}
		}
		else if (command.startsWith("adenaToItem"))
		{
			if (player.getAdena() > AioItemsConfigs.AIO_EXCHANGE_PRICE)
			{
				player.destroyItemByItemId("Aio Item", 57, AioItemsConfigs.AIO_EXCHANGE_PRICE, player, true);
				player.addItem("Aio Item", AioItemsConfigs.AIO_EXCHANGE_ID, 1, player, true);
			}
			else
			{
				player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			}
			
			GenerateHtmls.sendPacket(player, "service/exchange.htm");
		}
		else if (command.startsWith("itemToAdena"))
		{
			itemIdToGet = AioItemsConfigs.AIO_EXCHANGE_ID;
			price = 1;
			
			if (Conditions.checkPlayerItemCount(player, itemIdToGet, price))
			{
				player.destroyItemByItemId("Aio Item", AioItemsConfigs.AIO_EXCHANGE_ID, 1, player, true);
				player.addItem("Aio Item", 57, AioItemsConfigs.AIO_EXCHANGE_PRICE, player, true);
			}
			else
			{
				player.sendMessage("Not enough " + ItemData.getInstance().getTemplate(AioItemsConfigs.AIO_EXCHANGE_ID).getName() + ".");
			}
			
			GenerateHtmls.sendPacket(player, "service/exchange.htm");
		}
	}
	
	public static void doTeleportLong(L2PcInstance player, int x, int y, int z)
	{
		player.abortCast();
		player.abortAttack();
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.setTarget(player);
		player.disableAllSkills();
		Broadcast.toSelfAndKnownPlayers(player, new MagicSkillUse(player, 1050, 1, 30000, 0));
		player.sendPacket(new SetupGauge(SetupGauge.BLUE, 30000));
		player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(new Teleport(player, x, y, z), 30000));
		player.forceIsCasting(10 + GameTimeController.getInstance().getGameTicks() + (30000 / GameTimeController.MILLIS_IN_TICK));
	}
	
	private static class Teleport implements Runnable
	{
		L2PcInstance _activeChar;
		private final int _x, _y, _z;
		
		Teleport(L2PcInstance activeChar, int x, int y, int z)
		{
			_activeChar = activeChar;
			_x = x;
			_y = y;
			_z = z;
		}
		
		@Override
		public void run()
		{
			_activeChar.teleToLocation(_x, _y, _z, true);
			_activeChar.setIsCastingNow(false);
			_activeChar.enableAllSkills();
		}
	}
}