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

import org.l2jmobius.gameserver.data.xml.PrimeShopData;
import org.l2jmobius.gameserver.model.holders.PrimeShopProductHolder;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class ExBrProductInfo extends ServerPacket
{
	private final PrimeShopProductHolder _product;
	
	public ExBrProductInfo(int id)
	{
		_product = PrimeShopData.getInstance().getProduct(id);
	}
	
	@Override
	public void write()
	{
		if (_product == null)
		{
			return;
		}
		
		ServerPackets.EX_BR_PRODUCT_INFO.writeId(this);
		writeInt(_product.getProductId()); // product id
		writeInt(_product.getPrice()); // points
		writeInt(1); // components size
		writeInt(_product.getItemId()); // item id
		writeInt(_product.getItemCount()); // quality
		writeInt(_product.getItemWeight()); // weight
		writeInt(_product.isTradable()); // 0 - dont drop/trade
	}
}
