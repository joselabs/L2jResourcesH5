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
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;

import hellbound.HellboundEngine;

/**
 * Hellbound voiced command.
 * @author DS
 */
public class Hellbound implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"hellbound"
	};
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (HellboundEngine.getInstance().isLocked())
		{
			activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "dp_handler_hellbound_locked"));
			return true;
		}
		
		final int maxTrust = HellboundEngine.getInstance().getMaxTrust();
		activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "dp_handler_hellbound_level").replace("%s%", HellboundEngine.getInstance().getLevel() + "") + " | Trust: " + HellboundEngine.getInstance().getTrust() + (maxTrust > 0 ? "/" + maxTrust : ""));
		return true;
	}
}
