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
package handlers.usercommandhandlers;

import org.l2jdevs.gameserver.enums.Race;
import org.l2jdevs.gameserver.handler.IUserCommandHandler;
import org.l2jdevs.gameserver.instancemanager.MapRegionManager;
import org.l2jdevs.gameserver.instancemanager.ZoneManager;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.zone.type.L2RespawnZone;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;

/**
 * Loc user command.
 */
public class Loc implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		int region;
		L2RespawnZone zone = ZoneManager.getInstance().getZone(activeChar, L2RespawnZone.class);
		if (zone != null)
		{
			region = MapRegionManager.getInstance().getRestartRegion(activeChar, zone.getAllRespawnPoints().get(Race.HUMAN)).getLocId();
		}
		else
		{
			region = MapRegionManager.getInstance().getMapRegionLocId(activeChar);
		}
		
		SystemMessage sm;
		if (region > 0)
		{
			sm = SystemMessage.getSystemMessage(region);
			if (sm.getSystemMessageId().getParamCount() == 3)
			{
				sm.addInt(activeChar.getX());
				sm.addInt(activeChar.getY());
				sm.addInt(activeChar.getZ());
			}
		}
		else
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.CURRENT_LOCATION_S1);
			sm.addString(activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ());
		}
		activeChar.sendPacket(sm);
		return true;
	}
}
