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

import org.l2jdevs.gameserver.datatables.LanguageData;
import org.l2jdevs.gameserver.handler.IVoicedCommandHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.util.Util;

/**
 * @author Zoey76
 */
public class SetVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"set name",
		"set home",
		"set group"
	};
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (command.equals("set"))
		{
			final L2Object target = activeChar.getTarget();
			if ((target == null) || !target.isPlayer())
			{
				return false;
			}
			
			final L2PcInstance player = activeChar.getTarget().getActingPlayer();
			if ((activeChar.getClan() == null) || (player.getClan() == null) || (activeChar.getClan().getId() != player.getClan().getId()))
			{
				return false;
			}
			
			if (params.startsWith("privileges"))
			{
				final String val = params.substring(11);
				if (!Util.isDigit(val))
				{
					return false;
				}
				
				final int n = Integer.parseInt(val);
				if ((activeChar.getClanPrivileges().getBitmask() <= n) || !activeChar.isClanLeader())
				{
					return false;
				}
				
				player.getClanPrivileges().setBitmask(n);
				activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "dp_handler_setvcmd_clan_privileges").replace("%s%", n + "").replace("%i%", activeChar.getName() + ""));
			}
			else if (params.startsWith("title"))
			{
				// TODO why is this empty?
			}
		}
		return true;
	}
}
