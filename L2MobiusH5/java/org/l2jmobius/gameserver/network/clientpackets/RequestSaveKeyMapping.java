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
package org.l2jmobius.gameserver.network.clientpackets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.data.xml.UIData;
import org.l2jmobius.gameserver.model.ActionKey;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ConnectionState;
import org.l2jmobius.gameserver.network.GameClient;

/**
 * Request Save Key Mapping client packet.
 * @author mrTJO, Zoey76
 */
public class RequestSaveKeyMapping implements ClientPacket
{
	private final Map<Integer, List<ActionKey>> _keyMap = new HashMap<>();
	private final Map<Integer, List<Integer>> _catMap = new HashMap<>();
	
	@Override
	public void read(ReadablePacket packet)
	{
		int category = 0;
		packet.readInt(); // Unknown
		packet.readInt(); // Unknown
		final int _tabNum = packet.readInt();
		for (int i = 0; i < _tabNum; i++)
		{
			final int cmd1Size = packet.readByte();
			for (int j = 0; j < cmd1Size; j++)
			{
				UIData.addCategory(_catMap, category, packet.readByte());
			}
			category++;
			
			final int cmd2Size = packet.readByte();
			for (int j = 0; j < cmd2Size; j++)
			{
				UIData.addCategory(_catMap, category, packet.readByte());
			}
			category++;
			
			final int cmdSize = packet.readInt();
			for (int j = 0; j < cmdSize; j++)
			{
				final int cmd = packet.readInt();
				final int key = packet.readInt();
				final int tgKey1 = packet.readInt();
				final int tgKey2 = packet.readInt();
				final int show = packet.readInt();
				UIData.addKey(_keyMap, i, new ActionKey(i, cmd, key, tgKey1, tgKey2, show));
			}
		}
		packet.readInt();
		packet.readInt();
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (!Config.STORE_UI_SETTINGS || (player == null) || (client.getConnectionState() != ConnectionState.IN_GAME))
		{
			return;
		}
		player.getUISettings().storeAll(_catMap, _keyMap);
	}
}
