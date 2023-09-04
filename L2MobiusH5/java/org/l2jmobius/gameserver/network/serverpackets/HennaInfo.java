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

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.Henna;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * This server packet sends the player's henna information.
 * @author Zoey76
 */
public class HennaInfo extends ServerPacket
{
	private final Player _player;
	private final List<Henna> _hennas = new ArrayList<>();
	
	public HennaInfo(Player player)
	{
		_player = player;
		for (Henna henna : _player.getHennaList())
		{
			if (henna != null)
			{
				_hennas.add(henna);
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.HENNA_INFO.writeId(this);
		writeByte(_player.getHennaStatINT()); // equip INT
		writeByte(_player.getHennaStatSTR()); // equip STR
		writeByte(_player.getHennaStatCON()); // equip CON
		writeByte(_player.getHennaStatMEN()); // equip MEN
		writeByte(_player.getHennaStatDEX()); // equip DEX
		writeByte(_player.getHennaStatWIT()); // equip WIT
		writeInt(3); // Slots
		writeInt(_hennas.size()); // Size
		for (Henna henna : _hennas)
		{
			writeInt(henna.getDyeId());
			writeInt(1);
		}
	}
}
