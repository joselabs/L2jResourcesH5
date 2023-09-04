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

import org.l2jmobius.gameserver.data.xml.PrimeShopData;
import org.l2jmobius.gameserver.model.holders.PrimeShopProductHolder;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class ExBrProductList extends ServerPacket
{
	private final Collection<PrimeShopProductHolder> _itemList = PrimeShopData.getInstance().getAllItems();
	
	@Override
	public void write()
	{
		ServerPackets.EX_BR_PRODUCT_LIST.writeId(this);
		writeInt(_itemList.size());
		for (PrimeShopProductHolder product : _itemList)
		{
			final int category = product.getCategory();
			writeInt(product.getProductId()); // product id
			writeShort(category); // category id
			writeInt(product.getPrice()); // points
			switch (category)
			{
				case 6:
				{
					writeInt(1); // event
					break;
				}
				case 7:
				{
					writeInt(2); // best
					break;
				}
				case 8:
				{
					writeInt(3); // event & best
					break;
				}
				default:
				{
					writeInt(0); // normal
					break;
				}
			}
			writeInt(0); // start sale
			writeInt(0); // end sale
			writeByte(0); // day week
			writeByte(0); // start hour
			writeByte(0); // start min
			writeByte(0); // end hour
			writeByte(0); // end min
			writeInt(0); // current stock
			writeInt(0); // max stock
		}
	}
}
