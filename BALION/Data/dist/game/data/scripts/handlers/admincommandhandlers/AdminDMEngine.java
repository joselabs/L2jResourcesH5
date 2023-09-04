package handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.DM;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

import javolution.text.TextBuilder;

public class AdminDMEngine implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_dmevent",
		"admin_dmevent_name",
		"admin_dmevent_desc",
		"admin_dmevent_join_loc",
		"admin_dmevent_minlvl",
		"admin_dmevent_maxlvl",
		"admin_dmevent_npc",
		"admin_dmevent_npc_pos",
		"admin_dmevent_reward",
		"admin_dmevent_reward_amount",
		"admin_dmevent_spawnpos",
		"admin_dmevent_color",
		"admin_dmevent_join",
		"admin_dmevent_teleport",
		"admin_dmevent_start",
		"admin_dmevent_abort",
		"admin_dmevent_finish",
		"admin_dmevent_sit",
		"admin_dmevent_dump",
		"admin_dmevent_save",
		"admin_dmevent_load"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		try
		{
			if (command.equals("admin_dmevent"))
			{
				showMainPage(activeChar);
			}
			else if (command.startsWith("admin_dmevent_name "))
			{
				DM._eventName = command.substring(19);
				showMainPage(activeChar);
			}
			else if (command.startsWith("admin_dmevent_desc "))
			{
				DM._eventDesc = command.substring(19);
				showMainPage(activeChar);
			}
			else if (command.startsWith("admin_dmevent_minlvl "))
			{
				if (!DM.checkMinLevel(Integer.valueOf(command.substring(21))))
				{
					return false;
				}
				DM._minlvl = Integer.valueOf(command.substring(21));
				showMainPage(activeChar);
			}
			else if (command.startsWith("admin_dmevent_maxlvl "))
			{
				if (!DM.checkMaxLevel(Integer.valueOf(command.substring(21))))
				{
					return false;
				}
				DM._maxlvl = Integer.valueOf(command.substring(21));
				showMainPage(activeChar);
			}
			else if (command.startsWith("admin_dmevent_join_loc "))
			{
				DM._joiningLocationName = command.substring(23);
				showMainPage(activeChar);
			}
			else if (command.startsWith("admin_dmevent_npc "))
			{
				DM._npcId = Integer.valueOf(command.substring(18));
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_npc_pos"))
			{
				DM.setNpcPos(activeChar);
				showMainPage(activeChar);
			}
			else if (command.startsWith("admin_dmevent_reward "))
			{
				DM._rewardId = Integer.valueOf(command.substring(21));
				showMainPage(activeChar);
			}
			else if (command.startsWith("admin_dmevent_reward_amount "))
			{
				DM._rewardAmount = Integer.valueOf(command.substring(28));
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_spawnpos"))
			{
				DM.setPlayersPos(activeChar);
				showMainPage(activeChar);
			}
			else if (command.startsWith("admin_dmevent_color "))
			{
				String[] params;
				
				params = command.split(" ");
				
				if (params.length != 3)
				{
					activeChar.sendMessage("Wrong usege: //admin_dmevent_color <colorHex> <teamName>");
					return false;
				}
				
				// name/title color in client is BGR, not RGB
				
			}
			else if (command.equals("admin_dmevent_join"))
			{
				DM.startJoin(activeChar);
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_teleport"))
			{
				DM.teleportStart();
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_start"))
			{
				DM.startEvent(activeChar);
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_abort"))
			{
				activeChar.sendMessage("Aborting event");
				DM.abortEvent();
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_finish"))
			{
				DM.finishEvent(activeChar);
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_sit"))
			{
				DM.sit();
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_load"))
			{
				DM.loadData();
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_save"))
			{
				DM.saveData();
				showMainPage(activeChar);
			}
			else if (command.equals("admin_dmevent_dump"))
			{
				DM.dumpData();
			}
			
			return true;
			
		}
		catch (Exception e)
		{
			activeChar.sendMessage("The command was not used correctly");
			return false;
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	public void showMainPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		TextBuilder replyMSG = new TextBuilder("<html><body>");
		
		replyMSG.append("<center><font name=hs12 color=\"LEVEL\">Deathmatch</font></center><br>");
		replyMSG.append("<table><tr><td><edit var=\"input1\" width=\"125\"></td><td><edit var=\"input2\" width=\"125\"></td></tr></table><br1>");
		replyMSG.append("<table border=\"0\"><tr>");
		replyMSG.append("<td width=\"100\"><button value=\"Name\" action=\"bypass -h admin_dmevent_name $input1\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"Description\" action=\"bypass -h admin_dmevent_desc $input1\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"Join Location\" action=\"bypass -h admin_dmevent_join_loc $input1\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table><br1><table><tr>");
		replyMSG.append("</tr></table><br1><table><tr>");
		replyMSG.append("<td width=\"100\"><button value=\"Maximum lvl\" action=\"bypass -h admin_dmevent_maxlvl $input1\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"Minimum lvl\" action=\"bypass -h admin_dmevent_minlvl $input1\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table><br1><table><tr>");
		replyMSG.append("<td width=\"100\"><button value=\"NPC ID\" action=\"bypass -h admin_dmevent_npc $input1\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"NPC Location - Your loc.\" action=\"bypass -h admin_dmevent_npc_pos\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table><br1><table><tr>");
		replyMSG.append("<td width=\"100\"><button value=\"Reward ID\" action=\"bypass -h admin_dmevent_reward $input1\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"Reward Amount\" action=\"bypass -h admin_dmevent_reward_amount $input1\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table><br1><table><tr>");
		replyMSG.append("<td width=\"100\"><button value=\"DM Color\" action=\"bypass -h admin_dmevent_color $input1\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"DM SpawnPos\" action=\"bypass -h admin_dmevent_spawnpos\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table><table><br><br><tr>");
		replyMSG.append("<td width=\"100\"><button value=\"Join\" action=\"bypass -h admin_dmevent_join\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"Teleport\" action=\"bypass -h admin_dmevent_teleport\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"Start\" action=\"bypass -h admin_dmevent_start\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table><table><tr>");
		replyMSG.append("<td width=\"100\"><button value=\"Abort\" action=\"bypass -h admin_dmevent_abort\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"Finish\" action=\"bypass -h admin_dmevent_finish\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table><br1><table><tr>");
		replyMSG.append("<td width=\"100\"><button value=\"Sit Force\" action=\"bypass -h admin_dmevent_sit\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"Dump\" action=\"bypass -h admin_dmevent_dump\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table><br1><br><table><tr>");
		replyMSG.append("<td width=\"100\"><button value=\"Save\" action=\"bypass -h admin_dmevent_save\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td width=\"100\"><button value=\"Load\" action=\"bypass -h admin_dmevent_load\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table><br><br>");
		replyMSG.append("<center><font name=hs12 color=\"LEVEL\">Current Setting (Loaded)</font></center><br1>");
		replyMSG.append("Name:&nbsp;<font color=\"00FF00\">" + DM._eventName + "</font><br1>");
		replyMSG.append("Description:&nbsp;<font color=\"00FF00\">" + DM._eventDesc + "</font><br1>");
		replyMSG.append("Joining location name:&nbsp;<font color=\"00FF00\">" + DM._joiningLocationName + "</font><br1>");
		replyMSG.append("Joining NPC ID:&nbsp;<font color=\"00FF00\">" + DM._npcId + " on pos " + DM._npcX + "," + DM._npcY + "," + DM._npcZ + "</font><br1>");
		replyMSG.append("Reward ID:&nbsp;<font color=\"00FF00\">" + DM._rewardId + "</font><br1>");
		replyMSG.append("Reward Amount:&nbsp;<font color=\"00FF00\">" + DM._rewardAmount + "</font><br1>");
		replyMSG.append("Min lvl:&nbsp;<font color=\"00FF00\">" + DM._minlvl + "</font><br1>");
		replyMSG.append("Max lvl:&nbsp;<font color=\"00FF00\">" + DM._maxlvl + "</font><br1>");
		replyMSG.append("Death Match Color:&nbsp;<font color=\"00FF00\">" + DM._playerColors + "</font><br1>");
		replyMSG.append("Death Match Spawn Pos:&nbsp;<font color=\"00FF00\">" + DM._playerX + "," + DM._playerY + "," + DM._playerZ + "</font><br1>");
		replyMSG.append("<font name=hs12>Current players:</font><br1>");
		
		if (!DM._started)
		{
			replyMSG.append("<br1>");
			replyMSG.append("<font color=\"LEVEL\">" + DM._players.size() + "</font> players participating.");
			replyMSG.append("<br><br>");
		}
		else if (DM._started)
		{
			replyMSG.append("<br1>");
			replyMSG.append("<font color=\"LEVEL\">" + DM._players.size() + "</font> players in fighting event.");
			replyMSG.append("<br><br>");
		}
		
		replyMSG.append("</body></html>");
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
}