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

import java.util.List;

import org.l2jmobius.gameserver.instancemanager.MailManager;
import org.l2jmobius.gameserver.model.Message;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Migi, DS
 */
public class ExShowReceivedPostList extends ServerPacket
{
	private final List<Message> _inbox;
	
	public ExShowReceivedPostList(int objectId)
	{
		_inbox = MailManager.getInstance().getInbox(objectId);
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_SHOW_RECEIVED_POST_LIST.writeId(this);
		writeInt((int) (System.currentTimeMillis() / 1000));
		if ((_inbox != null) && !_inbox.isEmpty())
		{
			writeInt(_inbox.size());
			for (Message msg : _inbox)
			{
				writeInt(msg.getId());
				writeString(msg.getSubject());
				writeString(msg.getSenderName());
				writeInt(msg.isLocked());
				writeInt(msg.getExpirationSeconds());
				writeInt(msg.isUnread());
				writeInt(1);
				writeInt(msg.hasAttachments());
				writeInt(msg.isReturned());
				writeInt(msg.getSendBySystem());
				writeInt(0);
			}
		}
		else
		{
			writeInt(0);
		}
	}
}
