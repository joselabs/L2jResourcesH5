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

import org.l2jmobius.gameserver.instancemanager.SoDManager;
import org.l2jmobius.gameserver.instancemanager.SoIManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.network.ServerPackets;

public class ExShowSeedMapInfo extends ServerPacket
{
	public static final ExShowSeedMapInfo STATIC_PACKET = new ExShowSeedMapInfo();
	
	private static final Location[] ENTRANCES =
	{
		new Location(-246857, 251960, 4331, 1),
		new Location(-213770, 210760, 4400, 2),
	};
	
	private ExShowSeedMapInfo()
	{
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_SHOW_SEED_MAP_INFO.writeId(this);
		writeInt(ENTRANCES.length);
		for (Location loc : ENTRANCES)
		{
			writeInt(loc.getX());
			writeInt(loc.getY());
			writeInt(loc.getZ());
			switch (loc.getHeading())
			{
				case 1: // Seed of Destruction
				{
					writeInt(2770 + SoDManager.getInstance().getSoDState());
					break;
				}
				case 2: // Seed of Immortality
				{
					writeInt(SoIManager.getCurrentStage() + 2765);
					break;
				}
			}
		}
	}
}
