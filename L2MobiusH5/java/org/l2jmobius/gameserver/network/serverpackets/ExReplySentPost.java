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

import org.l2jmobius.gameserver.model.Message;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.itemcontainer.ItemContainer;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * ExReplySentPost packet implementation.
 * @author Migi, DS
 */
public class ExReplySentPost extends AbstractItemPacket
{
	private final Message _msg;
	private Collection<Item> _items = null;
	
	public ExReplySentPost(Message msg)
	{
		_msg = msg;
		if (msg.hasAttachments())
		{
			final ItemContainer attachments = msg.getAttachments();
			if ((attachments != null) && (attachments.getSize() > 0))
			{
				_items = attachments.getItems();
			}
			else
			{
				PacketLogger.warning("Message " + msg.getId() + " has attachments but itemcontainer is empty.");
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_REPLY_SENT_POST.writeId(this);
		writeInt(_msg.getId());
		writeInt(_msg.isLocked());
		writeString(_msg.getReceiverName());
		writeString(_msg.getSubject());
		writeString(_msg.getContent());
		if ((_items != null) && !_items.isEmpty())
		{
			writeInt(_items.size());
			for (Item item : _items)
			{
				writeItem(item);
				writeInt(item.getObjectId());
			}
			writeLong(_msg.getReqAdena());
			writeInt(_msg.getSendBySystem());
		}
		else
		{
			writeInt(0);
			writeLong(_msg.getReqAdena());
		}
	}
}
