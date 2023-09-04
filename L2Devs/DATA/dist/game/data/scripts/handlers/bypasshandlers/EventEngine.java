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
package handlers.bypasshandlers;

import java.util.logging.Level;

import org.l2jdevs.gameserver.handler.IBypassHandler;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.entity.L2Event;

public class EventEngine implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"event_participate",
		"event_unregister"
	};
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!target.isNpc())
		{
			return false;
		}
		
		try
		{
			if (command.equalsIgnoreCase("event_participate"))
			{
				L2Event.registerPlayer(activeChar);
				return true;
			}
			else if (command.equalsIgnoreCase("event_unregister"))
			{
				L2Event.removeAndResetPlayer(activeChar);
				return true;
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
}
