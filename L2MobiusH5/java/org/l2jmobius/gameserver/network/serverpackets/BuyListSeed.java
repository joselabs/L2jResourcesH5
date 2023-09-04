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

import org.l2jmobius.gameserver.instancemanager.CastleManorManager;
import org.l2jmobius.gameserver.model.SeedProduction;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author l3x
 */
public class BuyListSeed extends ServerPacket
{
	private final int _manorId;
	private final long _money;
	private final List<SeedProduction> _list = new ArrayList<>();
	
	public BuyListSeed(long currentMoney, int castleId)
	{
		_money = currentMoney;
		_manorId = castleId;
		for (SeedProduction s : CastleManorManager.getInstance().getSeedProduction(castleId, false))
		{
			if ((s.getAmount() > 0) && (s.getPrice() > 0))
			{
				_list.add(s);
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.BUY_LIST_SEED.writeId(this);
		writeLong(_money); // current money
		writeInt(_manorId); // manor id
		if (!_list.isEmpty())
		{
			writeShort(_list.size()); // list length
			for (SeedProduction s : _list)
			{
				writeInt(s.getId());
				writeInt(s.getId());
				writeInt(0);
				writeLong(s.getAmount()); // item count
				writeShort(5); // Custom Type 2
				writeShort(0); // Custom Type 1
				writeShort(0); // Equipped
				writeInt(0); // Body Part
				writeShort(0); // Enchant
				writeShort(0); // Custom Type
				writeInt(0); // Augment
				writeInt(-1); // Mana
				writeInt(-9999); // Time
				writeShort(0); // Element Type
				writeShort(0); // Element Power
				for (byte i = 0; i < 6; i++)
				{
					writeShort(0);
				}
				// Enchant Effects
				writeShort(0);
				writeShort(0);
				writeShort(0);
				writeLong(s.getPrice()); // price
			}
			_list.clear();
		}
		else
		{
			writeShort(0);
		}
	}
}