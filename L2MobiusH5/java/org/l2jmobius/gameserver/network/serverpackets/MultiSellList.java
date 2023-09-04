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

import static org.l2jmobius.gameserver.data.xml.MultisellData.PAGE_SIZE;

import org.l2jmobius.gameserver.model.multisell.Entry;
import org.l2jmobius.gameserver.model.multisell.Ingredient;
import org.l2jmobius.gameserver.model.multisell.ListContainer;
import org.l2jmobius.gameserver.network.ServerPackets;

public class MultiSellList extends ServerPacket
{
	private int _size;
	private int _index;
	private final ListContainer _list;
	private final boolean _finished;
	
	public MultiSellList(ListContainer list, int index)
	{
		_list = list;
		_index = index;
		_size = list.getEntries().size() - index;
		if (_size > PAGE_SIZE)
		{
			_finished = false;
			_size = PAGE_SIZE;
		}
		else
		{
			_finished = true;
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.MULTI_SELL_LIST.writeId(this);
		writeInt(_list.getListId()); // list id
		writeInt(1 + (_index / PAGE_SIZE)); // page started from 1
		writeInt(_finished); // finished
		writeInt(PAGE_SIZE); // size of pages
		writeInt(_size); // list length
		Entry ent;
		while (_size-- > 0)
		{
			ent = _list.getEntries().get(_index++);
			writeInt(ent.getEntryId());
			writeByte(ent.isStackable());
			writeShort(0); // C6
			writeInt(0); // C6
			writeInt(0); // T1
			writeShort(65534); // T1
			writeShort(0); // T1
			writeShort(0); // T1
			writeShort(0); // T1
			writeShort(0); // T1
			writeShort(0); // T1
			writeShort(0); // T1
			writeShort(0); // T1
			writeShort(ent.getProducts().size());
			writeShort(ent.getIngredients().size());
			for (Ingredient ing : ent.getProducts())
			{
				if (ing.getTemplate() != null)
				{
					writeInt(ing.getTemplate().getDisplayId());
					writeInt(ing.getTemplate().getBodyPart());
					writeShort(ing.getTemplate().getType2());
				}
				else
				{
					writeInt(ing.getItemId());
					writeInt(0);
					writeShort(65535);
				}
				writeLong(ing.getItemCount());
				if (ing.getItemInfo() != null)
				{
					writeShort(ing.getItemInfo().getEnchantLevel()); // enchant level
					writeInt(ing.getItemInfo().getAugmentId()); // augment id
					writeInt(0); // mana
					writeShort(ing.getItemInfo().getElementId()); // attack element
					writeShort(ing.getItemInfo().getElementPower()); // element power
					writeShort(ing.getItemInfo().getElementals()[0]); // fire
					writeShort(ing.getItemInfo().getElementals()[1]); // water
					writeShort(ing.getItemInfo().getElementals()[2]); // wind
					writeShort(ing.getItemInfo().getElementals()[3]); // earth
					writeShort(ing.getItemInfo().getElementals()[4]); // holy
					writeShort(ing.getItemInfo().getElementals()[5]); // dark
				}
				else
				{
					writeShort(ing.getEnchantLevel()); // enchant level
					writeInt(0); // augment id
					writeInt(0); // mana
					writeShort(0); // attack element
					writeShort(0); // element power
					writeShort(0); // fire
					writeShort(0); // water
					writeShort(0); // wind
					writeShort(0); // earth
					writeShort(0); // holy
					writeShort(0); // dark
				}
			}
			for (Ingredient ing : ent.getIngredients())
			{
				writeInt(ing.getTemplate() != null ? ing.getTemplate().getDisplayId() : ing.getItemId());
				writeShort(ing.getTemplate() != null ? ing.getTemplate().getType2() : 65535);
				writeLong(ing.getItemCount());
				if (ing.getItemInfo() != null)
				{
					writeShort(ing.getItemInfo().getEnchantLevel()); // enchant level
					writeInt(ing.getItemInfo().getAugmentId()); // augment id
					writeInt(0); // mana
					writeShort(ing.getItemInfo().getElementId()); // attack element
					writeShort(ing.getItemInfo().getElementPower()); // element power
					writeShort(ing.getItemInfo().getElementals()[0]); // fire
					writeShort(ing.getItemInfo().getElementals()[1]); // water
					writeShort(ing.getItemInfo().getElementals()[2]); // wind
					writeShort(ing.getItemInfo().getElementals()[3]); // earth
					writeShort(ing.getItemInfo().getElementals()[4]); // holy
					writeShort(ing.getItemInfo().getElementals()[5]); // dark
				}
				else
				{
					writeShort(ing.getEnchantLevel()); // enchant level
					writeInt(0); // augment id
					writeInt(0); // mana
					writeShort(0); // attack element
					writeShort(0); // element power
					writeShort(0); // fire
					writeShort(0); // water
					writeShort(0); // wind
					writeShort(0); // earth
					writeShort(0); // holy
					writeShort(0); // dark
				}
			}
		}
	}
}
