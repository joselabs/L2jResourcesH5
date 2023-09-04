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

import java.util.Arrays;
import java.util.List;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.ServerPackets;

public class ExSendUIEvent extends ServerPacket
{
	private final int _objectId;
	private final boolean _type;
	private final boolean _countUp;
	private final int _startTime;
	private final int _endTime;
	private final int _npcstringId;
	private List<String> _params = null;
	
	/**
	 * @param player
	 * @param hide
	 * @param countUp
	 * @param startTime
	 * @param endTime
	 * @param text
	 */
	public ExSendUIEvent(Player player, boolean hide, boolean countUp, int startTime, int endTime, String text)
	{
		this(player, hide, countUp, startTime, endTime, -1, text);
	}
	
	/**
	 * @param player
	 * @param hide
	 * @param countUp
	 * @param startTime
	 * @param endTime
	 * @param npcString
	 * @param params
	 */
	public ExSendUIEvent(Player player, boolean hide, boolean countUp, int startTime, int endTime, NpcStringId npcString, String... params)
	{
		this(player, hide, countUp, startTime, endTime, npcString.getId(), params);
	}
	
	/**
	 * @param player
	 * @param hide
	 * @param countUp
	 * @param startTime
	 * @param endTime
	 * @param npcstringId
	 * @param params
	 */
	public ExSendUIEvent(Player player, boolean hide, boolean countUp, int startTime, int endTime, int npcstringId, String... params)
	{
		_objectId = player.getObjectId();
		_type = hide;
		_countUp = countUp;
		_startTime = startTime;
		_endTime = endTime;
		_npcstringId = npcstringId;
		_params = Arrays.asList(params);
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_SEND_UI_EVENT.writeId(this);
		writeInt(_objectId);
		writeInt(_type); // 0 = show, 1 = hide (there is 2 = pause and 3 = resume also but they don't work well you can only pause count down and you cannot resume it because resume hides the counter).
		writeInt(0); // unknown
		writeInt(0); // unknown
		writeString(_countUp ? "1" : "0"); // 0 = count down, 1 = count up
		// timer always disappears 10 seconds before end
		writeString(String.valueOf(_startTime / 60));
		writeString(String.valueOf(_startTime % 60));
		writeString(String.valueOf(_endTime / 60));
		writeString(String.valueOf(_endTime % 60));
		writeInt(_npcstringId);
		if (_params != null)
		{
			for (String param : _params)
			{
				writeString(param);
			}
		}
	}
}