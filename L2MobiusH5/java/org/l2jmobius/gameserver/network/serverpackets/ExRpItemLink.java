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

import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author KenM
 */
public class ExRpItemLink extends ServerPacket
{
	private final Item _item;
	
	public ExRpItemLink(Item item)
	{
		_item = item;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_RP_ITEM_LINK.writeId(this);
		writeInt(_item.getObjectId());
		writeInt(_item.getDisplayId());
		writeInt(_item.getLocationSlot());
		writeLong(_item.getCount());
		writeShort(_item.getTemplate().getType2());
		writeShort(_item.getCustomType1());
		writeShort(_item.isEquipped());
		writeInt(_item.getTemplate().getBodyPart());
		writeShort(_item.getEnchantLevel());
		writeShort(_item.getCustomType2());
		if (_item.isAugmented())
		{
			writeInt(_item.getAugmentation().getAugmentationId());
		}
		else
		{
			writeInt(0);
		}
		writeInt(_item.getMana());
		writeInt(_item.isTimeLimitedItem() ? (int) (_item.getRemainingTime() / 1000) : -9999);
		writeShort(_item.getAttackElementType());
		writeShort(_item.getAttackElementPower());
		for (byte i = 0; i < 6; i++)
		{
			writeShort(_item.getElementDefAttr(i));
		}
		// Enchant Effects
		for (int op : _item.getEnchantOptions())
		{
			writeShort(op);
		}
	}
}
