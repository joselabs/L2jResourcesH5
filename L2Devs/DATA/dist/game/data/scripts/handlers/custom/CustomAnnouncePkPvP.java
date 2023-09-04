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
package handlers.custom;

import org.l2jdevs.Config;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.events.Containers;
import org.l2jdevs.gameserver.model.events.EventType;
import org.l2jdevs.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import org.l2jdevs.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;
import org.l2jdevs.gameserver.util.Broadcast;

/**
 * @author Zealar
 */
public class CustomAnnouncePkPvP
{
	
	public CustomAnnouncePkPvP()
	{
		if (Config.ANNOUNCE_PK_PVP)
		{
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PVP_KILL, (OnPlayerPvPKill event) -> OnPlayerPvPKill(event), this));
		}
	}
	
	/**
	 * @param event
	 * @return
	 */
	private Object OnPlayerPvPKill(OnPlayerPvPKill event)
	{
		L2PcInstance pk = event.getActiveChar();
		if (pk.isGM())
		{
			return null;
		}
		L2PcInstance player = event.getTarget();
		
		String msg = Config.ANNOUNCE_PVP_MSG;
		if (player.getPvpFlag() == 0)
		{
			msg = Config.ANNOUNCE_PK_MSG;
		}
		msg = msg.replace("$killer", pk.getName()).replace("$target", player.getName());
		if (Config.ANNOUNCE_PK_PVP_NORMAL_MESSAGE)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1);
			sm.addString(msg);
			Broadcast.toAllOnlinePlayers(sm);
		}
		else
		{
			Broadcast.toAllOnlinePlayers(msg, false);
		}
		return null;
	}
}
