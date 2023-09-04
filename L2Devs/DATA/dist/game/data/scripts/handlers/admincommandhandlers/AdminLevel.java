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
package handlers.admincommandhandlers;

import java.util.StringTokenizer;

import org.l2jdevs.gameserver.data.json.ExperienceData;
import org.l2jdevs.gameserver.handler.IAdminCommandHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.network.SystemMessageId;

public class AdminLevel implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_level",
		"admin_set_level"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		L2Object targetChar = activeChar.getTarget();
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		String val = "";
		if (st.countTokens() >= 1)
		{
			val = st.nextToken();
		}
		
		if (actualCommand.equalsIgnoreCase("admin_add_level"))
		{
			try
			{
				if (targetChar.isPlayer())
				{
					L2PcInstance targetPlayer = (L2PcInstance) targetChar;
					targetPlayer.addLevel(Integer.parseInt(val));
				}
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Wrong Number Format");
			}
		}
		else if (actualCommand.equalsIgnoreCase("admin_set_level"))
		{
			try
			{
				if (!(targetChar instanceof L2PcInstance))
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					return false;
				}
				L2PcInstance targetPlayer = (L2PcInstance) targetChar;
				int oldLevel = targetPlayer.getLevel();
				int newLevel = Integer.parseInt(val);
				
				if (newLevel < 1)
				{
					newLevel = 1;
				}
				targetPlayer.setLevel(newLevel);
				targetPlayer.setExp(ExperienceData.getInstance().getExpForLevel(Math.min(newLevel, targetPlayer.getMaxExpLevel())));
				targetPlayer.onLevelChange(newLevel > oldLevel);
				targetPlayer.broadcastInfo();
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Level require number as value!");
				return false;
			}
		}
		return true;
	}
}
