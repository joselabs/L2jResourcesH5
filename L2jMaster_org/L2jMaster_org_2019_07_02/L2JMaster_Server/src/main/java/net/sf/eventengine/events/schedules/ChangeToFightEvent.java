/*
 * Copyright (C) 2004-2019 L2J Server
 *
 * This file is part of L2J EventEngine.
 *
 * L2J EventEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J EventEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.eventengine.events.schedules;

import net.sf.eventengine.EventEngineManager;
import net.sf.eventengine.enums.EventState;
import net.sf.eventengine.events.handler.AbstractEvent;
import net.sf.eventengine.events.holders.PlayerHolder;
import net.sf.eventengine.events.schedules.interfaces.EventScheduled;
import net.sf.eventengine.util.EventUtil;

/**
 * @author Zephyr
 */

public class ChangeToFightEvent implements EventScheduled
{
	int _time;
	
	public ChangeToFightEvent(int time)
	{
		_time = time;
	}
	
	@Override
	public int getTime()
	{
		return _time;
	}
	
	@Override
	public void run()
	{
		AbstractEvent currentEvent = EventEngineManager.getInstance().getCurrentEvent();
		
		currentEvent.runEventState(EventState.FIGHT);
		
		// Send a special message to the participants
		for (PlayerHolder player : currentEvent.getPlayerEventManager().getAllEventPlayers())
		{
			EventUtil.sendEventSpecialMessage(player, 2, "status_started");
		}
		
		// Start antiAfk task
		if (currentEvent.getAntiAfkManager() != null)
		{
			currentEvent.getAntiAfkManager().startTask();
		}
	}
}