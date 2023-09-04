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
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.ServerPackets;

public class ItemList extends AbstractItemPacket
{
	private final Player _player;
	private final List<Item> _items = new ArrayList<>();
	private final boolean _showWindow;
	
	public ItemList(Player player, boolean showWindow)
	{
		_player = player;
		_showWindow = showWindow;
		for (Item item : player.getInventory().getItems())
		{
			if (!item.isQuestItem())
			{
				_items.add(item);
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.ITEM_LIST.writeId(this);
		writeShort(_showWindow);
		writeShort(_items.size());
		for (Item item : _items)
		{
			writeItem(item);
		}
		writeInventoryBlock(_player.getInventory());
	}
	
	@Override
	public void run()
	{
		final Player player = getPlayer();
		if (player != null)
		{
			player.sendPacket(new ExQuestItemList(_player));
		}
	}
}
