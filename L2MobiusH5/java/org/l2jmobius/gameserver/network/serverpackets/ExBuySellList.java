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
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author ShanSoft
 */
public class ExBuySellList extends AbstractItemPacket
{
	private final Collection<Item> _sellList;
	private Collection<Item> _refundList = null;
	private final boolean _done;
	
	public ExBuySellList(Player player, boolean done)
	{
		_sellList = player.getInventory().getAvailableItems(false, false, false);
		if (player.hasRefund())
		{
			_refundList = player.getRefund().getItems();
		}
		_done = done;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_BUY_SELL_LIST.writeId(this);
		writeInt(1);
		if ((_sellList != null))
		{
			writeShort(_sellList.size());
			for (Item item : _sellList)
			{
				writeItem(item);
				writeLong(Config.MERCHANT_ZERO_SELL_PRICE ? 0 : item.getTemplate().getReferencePrice() / 2);
			}
		}
		else
		{
			writeShort(0);
		}
		if ((_refundList != null) && !_refundList.isEmpty())
		{
			writeShort(_refundList.size());
			int i = 0;
			for (Item item : _refundList)
			{
				writeItem(item);
				writeInt(i++);
				writeLong(Config.MERCHANT_ZERO_SELL_PRICE ? 0 : (item.getTemplate().getReferencePrice() / 2) * item.getCount());
			}
		}
		else
		{
			writeShort(0);
		}
		writeByte(_done);
	}
}