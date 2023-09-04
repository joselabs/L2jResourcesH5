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

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.buylist.BuyListHolder;
import org.l2jmobius.gameserver.model.buylist.Product;
import org.l2jmobius.gameserver.network.ServerPackets;

public class BuyList extends ServerPacket
{
	private final int _listId;
	private final Collection<Product> _list;
	private final long _money;
	private double _taxRate = 0;
	
	public BuyList(BuyListHolder list, long currentMoney, double taxRate)
	{
		_listId = list.getListId();
		_list = list.getProducts();
		_money = currentMoney;
		_taxRate = taxRate;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_BUY_SELL_LIST.writeId(this);
		writeInt(0);
		writeLong(_money); // current money
		writeInt(_listId);
		writeShort(_list.size());
		for (Product product : _list)
		{
			if ((product.getCount() > 0) || !product.hasLimitedStock())
			{
				writeInt(product.getItemId());
				writeInt(product.getItemId());
				writeInt(0);
				writeLong(product.getCount() < 0 ? 0 : product.getCount());
				writeShort(product.getItem().getType2());
				writeShort(product.getItem().getType1()); // Custom Type 1
				writeShort(0); // isEquipped
				writeInt(product.getItem().getBodyPart()); // Body Part
				writeShort(product.getItem().getDefaultEnchantLevel()); // Enchant
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
				if ((product.getItemId() >= 3960) && (product.getItemId() <= 4026))
				{
					writeLong((long) (product.getPrice() * Config.RATE_SIEGE_GUARDS_PRICE * (1 + _taxRate)));
				}
				else
				{
					writeLong((long) (product.getPrice() * (1 + _taxRate)));
				}
			}
		}
	}
}
