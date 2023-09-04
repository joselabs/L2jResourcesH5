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

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Tempy
 */
public class GmViewQuestInfo extends ServerPacket
{
	private final Player _player;
	
	public GmViewQuestInfo(Player player)
	{
		_player = player;
	}
	
	@Override
	public void write()
	{
		ServerPackets.GM_VIEW_QUEST_INFO.writeId(this);
		writeString(_player.getName());
		final List<Quest> questList = _player.getAllActiveQuests();
		if (questList.isEmpty())
		{
			writeByte(0);
			writeShort(0);
			writeShort(0);
			return;
		}
		
		writeShort(questList.size()); // quest count
		for (Quest q : questList)
		{
			writeInt(q.getId());
			final QuestState qs = _player.getQuestState(q.getName());
			if (qs == null)
			{
				writeInt(0);
				continue;
			}
			writeInt(qs.getCond()); // stage of quest progress
		}
	}
}
