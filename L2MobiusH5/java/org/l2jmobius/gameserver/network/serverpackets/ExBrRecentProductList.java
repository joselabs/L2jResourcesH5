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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.data.xml.PrimeShopData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.PrimeShopProductHolder;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class ExBrRecentProductList extends ServerPacket
{
	private final List<PrimeShopProductHolder> _itemList = new ArrayList<>();
	
	public ExBrRecentProductList(Player player)
	{
		final int playerObj = player.getObjectId();
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT productId FROM prime_shop_transactions WHERE charId=? ORDER BY transactionTime DESC"))
		{
			statement.setInt(1, playerObj);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final PrimeShopProductHolder product = PrimeShopData.getInstance().getProduct(rset.getInt("productId"));
					if ((product != null) && !_itemList.contains(product))
					{
						_itemList.add(product);
					}
				}
			}
		}
		catch (Exception e)
		{
			PacketLogger.warning("Could not restore Item Mall transaction: " + e.getMessage());
		}
	}
	
	@Override
	public void write()
	{
		if (_itemList.isEmpty())
		{
			return;
		}
		
		ServerPackets.EX_BR_RECENT_PRODUCT_LIST.writeId(this);
		writeInt(_itemList.size());
		for (PrimeShopProductHolder product : _itemList)
		{
			writeInt(product.getProductId());
			writeShort(product.getCategory());
			writeInt(product.getPrice());
			writeInt(0); // category
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
