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
package com.l2jserver.gameserver.network.clientpackets;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.communitybbs.CommunityBoard;
import com.l2jserver.gameserver.communitybbs.Managers.FriendBBSManager;
import com.l2jserver.gameserver.datatables.AdminCommandAccessRights;
import com.l2jserver.gameserver.handler.AdminCommandHandler;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.handler.VoicedCommandHandler;
import com.l2jserver.gameserver.model.L2CharPosition;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantSummonInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.CTF;
import com.l2jserver.gameserver.model.entity.DM;
import com.l2jserver.gameserver.model.entity.Hero;
import com.l2jserver.gameserver.model.entity.L2Event;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.GMAudit;

/**
 * This class ...
 * @version $Revision: 1.12.4.5 $ $Date: 2005/04/11 10:06:11 $
 */
public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final String _C__23_REQUESTBYPASSTOSERVER = "[C] 23 RequestBypassToServer";
	private static Logger _log = Logger.getLogger(RequestBypassToServer.class.getName());
	
	// Config
	private static boolean DEBUG_COMMANDS = Config.DEBUG_REQUEST_BYPASS_TO_SERVER;
	// S
	private String _command;
	
	@Override
	protected void readImpl()
	{
		_command = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (DEBUG_COMMANDS)
		{
			_log.info("Player / GM request bypass to server: " + _command + " command.");
			if (activeChar.isGM())
			{
				activeChar.sendMessage("Your request for bypass to server: " + _command + " command.");
			}
		}
		if (!getClient().getFloodProtectors().getServerBypass().tryPerformAction(_command))
		{
			return;
		}
		
		if (_command.isEmpty())
		{
			_log.info(activeChar.getName() + " send empty requestbypass");
			activeChar.logout();
			return;
		}
		
		try
		{
			if (_command.startsWith("admin_")) // && activeChar.getAccessLevel() >= Config.GM_ACCESSLEVEL)
			{
				String command = _command.split(" ")[0];
				
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
				
				if (ach == null)
				{
					if (activeChar.isGM())
					{
						activeChar.sendMessage("The command " + command.substring(6) + " does not exist!");
					}
					
					_log.warning("No handler registered for admin command '" + command + "'");
					return;
				}
				
				if (!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access rights to use this command!");
					_log.warning("Character " + activeChar.getName() + " tried to use admin command " + command + ", without proper access level!");
					return;
				}
				
				if (AdminCommandAccessRights.getInstance().requireConfirm(command))
				{
					activeChar.setAdminConfirmCmd(_command);
					ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1);
					dlg.addString("Are you sure you want execute command " + _command.substring(6) + " ?");
					activeChar.sendPacket(dlg);
				}
				else
				{
					if (Config.GMAUDIT)
					{
						GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", _command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"));
					}
					
					ach.useAdminCommand(_command, activeChar);
				}
			}
			else if (_command.equals("come_here") && (activeChar.isGM()))
			{
				comeHere(activeChar);
			}
			else if (_command.startsWith("_friendlist_0_"))
			{
				FriendBBSManager.getInstance().parsecmd(_command, activeChar);
			}
			else if (_command.startsWith("npc_"))
			{
				if (!activeChar.validateBypass(_command))
				{
					return;
				}
				
				int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
				{
					id = _command.substring(4, endOfId);
				}
				else
				{
					id = _command.substring(4);
				}
				try
				{
					L2Object object = L2World.getInstance().findObject(Integer.parseInt(id));
					
					if (_command.substring(endOfId + 1).startsWith("event_participate"))
					{
						L2Event.inscribePlayer(activeChar);
					}
					else if ((object instanceof L2Npc) && (endOfId > 0) && activeChar.isInsideRadius(object, L2Npc.INTERACTION_DISTANCE, false, false))
					{
						((L2Npc) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					}
					
					// Add by CTF Event
					if (_command.substring(endOfId + 1).startsWith("ctf_player_join "))
					{
						final String teamName = _command.substring(endOfId + 1).substring(16);
						
						if (CTF._joining)
						{
							CTF.addPlayer(activeChar, teamName);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not join now!");
						}
					}
					else if (_command.substring(endOfId + 1).startsWith("ctf_player_leave"))
					{
						if (CTF._joining)
						{
							CTF.removePlayer(activeChar);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not leave now!");
						}
					}
					
					if (_command.substring(endOfId + 1).startsWith("dmevent_player_join"))
					{
						if (DM._joining)
						{
							DM.addPlayer(activeChar);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not join now!");
						}
					}
					else if (_command.substring(endOfId + 1).startsWith("dmevent_player_leave"))
					{
						if (DM._joining)
						{
							DM.removePlayer(activeChar);
						}
						else
						{
							activeChar.sendMessage("The event is already started. You can not leave now!");
						}
					}
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
				}
			}
			else if (_command.startsWith("."))
			{
				String command = _command.substring(1).split(" ")[0];
				String params = _command.substring(1).split(" ").length > 1 ? _command.substring(1).split(" ")[1] : "";
				IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
				if (vch == null)
				{
					_log.warning(activeChar + " requested not registered admin command '" + command + "'");
					return;
				}
				vch.useVoicedCommand(command, activeChar, params);
			}
			else if (_command.startsWith("summon_"))
			{
				if (!activeChar.validateBypass(_command))
				{
					return;
				}
				
				int endOfId = _command.indexOf('_', 8);
				String id;
				if (endOfId > 0)
				{
					id = _command.substring(7, endOfId);
				}
				else
				{
					id = _command.substring(7);
				}
				try
				{
					L2Object object = L2World.getInstance().findObject(Integer.parseInt(id));
					
					if ((object instanceof L2MerchantSummonInstance) && (endOfId > 0) && activeChar.isInsideRadius(object, L2Npc.INTERACTION_DISTANCE, false, false))
					{
						((L2MerchantSummonInstance) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					}
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
				}
			}
			// Navigate through Manor windows
			else if (_command.startsWith("manor_menu_select"))
			{
				final IBypassHandler manor = BypassHandler.getInstance().getBypassHandler("manor_menu_select");
				if (manor != null)
				{
					manor.useBypass(_command, activeChar, null);
				}
			}
			else if (_command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_maillist_0_1_0_"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("bbs"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("Quest "))
			{
				if (!activeChar.validateBypass(_command))
				{
					return;
				}
				
				L2PcInstance player = getClient().getActiveChar();
				if (player == null)
				{
					return;
				}
				
				String p = _command.substring(6).trim();
				int idx = p.indexOf(' ');
				if (idx < 0)
				{
					player.processQuestEvent(p, "");
				}
				else
				{
					player.processQuestEvent(p.substring(0, idx), p.substring(idx).trim());
				}
			}
			else if (_command.startsWith("_match"))
			{
				L2PcInstance player = getClient().getActiveChar();
				if (player == null)
				{
					return;
				}
				
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
				{
					Hero.getInstance().showHeroFights(player, heroclass, heroid, heropage);
				}
			}
			else if (_command.startsWith("_diary"))
			{
				L2PcInstance player = getClient().getActiveChar();
				if (player == null)
				{
					return;
				}
				
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
				{
					Hero.getInstance().showHeroDiary(player, heroclass, heroid, heropage);
				}
			}
			else
			{
				final IBypassHandler handler = BypassHandler.getInstance().getBypassHandler(_command);
				if (handler != null)
				{
					handler.useBypass(_command, activeChar, null);
				}
				else
				{
					_log.log(Level.WARNING, getClient() + " sent not handled RequestBypassToServer: [" + _command + "]");
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, getClient() + " sent bad RequestBypassToServer: \"" + _command + "\"", e);
			if (activeChar.isGM())
			{
				StringBuilder sb = new StringBuilder(200);
				sb.append("<html><body>");
				sb.append("Bypass error: " + e + "<br1>");
				sb.append("Bypass command: " + _command + "<br1>");
				sb.append("StackTrace:<br1>");
				for (StackTraceElement ste : e.getStackTrace())
				{
					sb.append(ste.toString() + "<br1>");
				}
				sb.append("</body></html>");
				// item html
				NpcHtmlMessage msg = new NpcHtmlMessage(0, 12807);
				msg.setHtml(sb.toString());
				msg.disableValidation();
				activeChar.sendPacket(msg);
			}
		}
	}
	
	/**
	 * @param client
	 */
	private static void comeHere(L2PcInstance activeChar)
	{
		L2Object obj = activeChar.getTarget();
		if (obj == null)
		{
			return;
		}
		if (obj instanceof L2Npc)
		{
			L2Npc temp = (L2Npc) obj;
			temp.setTarget(activeChar);
			temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 0));
		}
	}
	
	@Override
	public String getType()
	{
		return _C__23_REQUESTBYPASSTOSERVER;
	}
}
