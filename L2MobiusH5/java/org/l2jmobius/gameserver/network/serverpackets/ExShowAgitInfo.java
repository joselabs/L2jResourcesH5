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

import java.util.Map;

import org.l2jmobius.gameserver.data.sql.ClanHallTable;
import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.model.residences.AuctionableHall;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author KenM
 */
public class ExShowAgitInfo extends ServerPacket
{
	@Override
	public void write()
	{
		ServerPackets.EX_SHOW_AGIT_INFO.writeId(this);
		final Map<Integer, AuctionableHall> clannhalls = ClanHallTable.getInstance().getAllAuctionableClanHalls();
		writeInt(clannhalls.size());
		for (AuctionableHall ch : clannhalls.values())
		{
			writeInt(ch.getId());
			writeString(ch.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(ch.getOwnerId()).getName()); // owner clan name
			writeString(ch.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(ch.getOwnerId()).getLeaderName()); // leader name
			writeInt(ch.getGrade() < 1); // 0 - auction 1 - war clanhall 2 - ETC (rainbow spring clanhall)
		}
	}
}
