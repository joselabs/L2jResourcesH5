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

import org.l2jmobius.gameserver.model.Elementals;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Kerberos
 */
public class ExChooseInventoryAttributeItem extends ServerPacket
{
	private final int _itemId;
	private final byte _atribute;
	private final int _level;
	
	public ExChooseInventoryAttributeItem(Item item)
	{
		_itemId = item.getDisplayId();
		_atribute = Elementals.getItemElement(_itemId);
		if (_atribute == Elementals.NONE)
		{
			throw new IllegalArgumentException("Undefined Atribute item: " + item);
		}
		_level = Elementals.getMaxElementLevel(_itemId);
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM.writeId(this);
		writeInt(_itemId);
		// Structure for now
		// Must be 0x01 for stone/crystal attribute type
		writeInt(_atribute == Elementals.FIRE); // Fire
		writeInt(_atribute == Elementals.WATER); // Water
		writeInt(_atribute == Elementals.WIND); // Wind
		writeInt(_atribute == Elementals.EARTH); // Earth
		writeInt(_atribute == Elementals.HOLY); // Holy
		writeInt(_atribute == Elementals.DARK); // Unholy
		writeInt(_level); // Item max attribute level
	}
}
