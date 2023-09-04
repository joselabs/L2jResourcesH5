/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.Collection;

import org.l2jmobius.gameserver.instancemanager.TerritoryWarManager;
import org.l2jmobius.gameserver.model.TerritoryWard;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author -Gigiikun-
 */
public class ExShowOwnthingPos extends ServerPacket
{
	public static final ExShowOwnthingPos STATIC_PACKET = new ExShowOwnthingPos();
	
	private ExShowOwnthingPos()
	{
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_SHOW_OWNTHING_POS.writeId(this);
		if (TerritoryWarManager.getInstance().isTWInProgress())
		{
			final Collection<TerritoryWard> territoryWardList = TerritoryWarManager.getInstance().getAllTerritoryWards();
			writeInt(territoryWardList.size());
			for (TerritoryWard ward : territoryWardList)
			{
				writeInt(ward.getTerritoryId());
				if (ward.getNpc() != null)
				{
					writeInt(ward.getNpc().getX());
					writeInt(ward.getNpc().getY());
					writeInt(ward.getNpc().getZ());
				}
				else if (ward.getPlayer() != null)
				{
					writeInt(ward.getPlayer().getX());
					writeInt(ward.getPlayer().getY());
					writeInt(ward.getPlayer().getZ());
				}
				else
				{
					writeInt(0);
					writeInt(0);
					writeInt(0);
				}
			}
		}
		else
		{
			writeInt(0);
		}
	}
}
