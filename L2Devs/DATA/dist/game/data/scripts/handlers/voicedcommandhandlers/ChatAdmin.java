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
package handlers.voicedcommandhandlers;

import java.util.StringTokenizer;

import org.l2jdevs.gameserver.data.sql.impl.CharNameTable;
import org.l2jdevs.gameserver.data.xml.impl.AdminData;
import org.l2jdevs.gameserver.handler.IVoicedCommandHandler;
import org.l2jdevs.gameserver.instancemanager.PunishmentManager;
import org.l2jdevs.gameserver.model.L2World;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.punishment.PunishmentAffect;
import org.l2jdevs.gameserver.model.punishment.PunishmentTask;
import org.l2jdevs.gameserver.model.punishment.PunishmentType;
import org.l2jdevs.gameserver.util.Util;

public class ChatAdmin implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"banchat",
		"unbanchat"
	};
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (!AdminData.getInstance().hasAccess(command, activeChar.getAccessLevel()))
		{
			return false;
		}
		
		if (command.equals(VOICED_COMMANDS[0])) // banchat
		{
			if (params == null)
			{
				activeChar.sendMessage("Usage: .banchat name [minutes]");
				return true;
			}
			StringTokenizer st = new StringTokenizer(params);
			if (st.hasMoreTokens())
			{
				String name = st.nextToken();
				long expirationTime = 0;
				if (st.hasMoreTokens())
				{
					String token = st.nextToken();
					if (Util.isDigit(token))
					{
						expirationTime = System.currentTimeMillis() + (Integer.parseInt(st.nextToken()) * 60 * 1000);
					}
				}
				
				int objId = CharNameTable.getInstance().getIdByName(name);
				if (objId > 0)
				{
					L2PcInstance player = L2World.getInstance().getPlayer(objId);
					if ((player == null) || !player.isOnline())
					{
						activeChar.sendMessage("Player not online !");
						return false;
					}
					if (player.isChatBanned())
					{
						activeChar.sendMessage("Player is already punished !");
						return false;
					}
					if (player == activeChar)
					{
						activeChar.sendMessage("You can't ban yourself !");
						return false;
					}
					if (player.isGM())
					{
						activeChar.sendMessage("You can't ban GM !");
						return false;
					}
					if (AdminData.getInstance().hasAccess(command, player.getAccessLevel()))
					{
						activeChar.sendMessage("You can't ban moderator !");
						return false;
					}
					
					PunishmentManager.getInstance().startPunishment(new PunishmentTask(objId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, expirationTime, "Chat banned by moderator", activeChar.getName()));
					player.sendMessage("Chat banned by moderator " + activeChar.getName());
					
					if (expirationTime > 0)
					{
						activeChar.sendMessage("Player " + player.getName() + " chat banned for " + expirationTime + " minutes.");
					}
					else
					{
						activeChar.sendMessage("Player " + player.getName() + " chat banned forever.");
					}
				}
				else
				{
					activeChar.sendMessage("Player not found !");
					return false;
				}
			}
		}
		else if (command.equals(VOICED_COMMANDS[1])) // unbanchat
		{
			if (params == null)
			{
				activeChar.sendMessage("Usage: .unbanchat name");
				return true;
			}
			StringTokenizer st = new StringTokenizer(params);
			if (st.hasMoreTokens())
			{
				String name = st.nextToken();
				
				int objId = CharNameTable.getInstance().getIdByName(name);
				if (objId > 0)
				{
					L2PcInstance player = L2World.getInstance().getPlayer(objId);
					if ((player == null) || !player.isOnline())
					{
						activeChar.sendMessage("Player not online !");
						return false;
					}
					if (!player.isChatBanned())
					{
						activeChar.sendMessage("Player is not chat banned !");
						return false;
					}
					
					PunishmentManager.getInstance().stopPunishment(objId, PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
					
					activeChar.sendMessage("Player " + player.getName() + " chat unbanned.");
					player.sendMessage("Chat unbanned by moderator " + activeChar.getName());
				}
				else
				{
					activeChar.sendMessage("Player not found !");
					return false;
				}
			}
		}
		return true;
	}
}
