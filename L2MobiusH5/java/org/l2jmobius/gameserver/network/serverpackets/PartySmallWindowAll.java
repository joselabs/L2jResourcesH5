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

import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;

public class PartySmallWindowAll extends ServerPacket
{
	private final Party _party;
	private final Player _exclude;
	
	public PartySmallWindowAll(Player exclude, Party party)
	{
		_exclude = exclude;
		_party = party;
	}
	
	@Override
	public void write()
	{
		ServerPackets.PARTY_SMALL_WINDOW_ALL.writeId(this);
		writeInt(_party.getLeaderObjectId());
		writeInt(_party.getDistributionType().getId());
		writeInt(_party.getMemberCount() - 1);
		for (Player member : _party.getMembers())
		{
			if ((member != null) && (member != _exclude))
			{
				writeInt(member.getObjectId());
				writeString(member.getName());
				writeInt((int) member.getCurrentCp()); // c4
				writeInt(member.getMaxCp()); // c4
				writeInt((int) member.getCurrentHp());
				writeInt(member.getMaxHp());
				writeInt((int) member.getCurrentMp());
				writeInt(member.getMaxMp());
				writeInt(member.getLevel());
				writeInt(member.getClassId().getId());
				writeInt(0); // writeInt(1); ??
				writeInt(member.getRace().ordinal());
				writeInt(0); // T2.3
				writeInt(0); // T2.3
				if (member.hasSummon())
				{
					writeInt(member.getSummon().getObjectId());
					writeInt(member.getSummon().getId() + 1000000);
					writeInt(member.getSummon().getSummonType());
					writeString(member.getSummon().getName());
					writeInt((int) member.getSummon().getCurrentHp());
					writeInt(member.getSummon().getMaxHp());
					writeInt((int) member.getSummon().getCurrentMp());
					writeInt(member.getSummon().getMaxMp());
					writeInt(member.getSummon().getLevel());
				}
				else
				{
					writeInt(0);
				}
			}
		}
	}
}
