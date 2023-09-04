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

import org.l2jmobius.gameserver.model.ActionKey;
import org.l2jmobius.gameserver.model.UIKeysSettings;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author mrTJO
 */
public class ExUISetting extends ServerPacket
{
	private final UIKeysSettings _uiSettings;
	private int buffsize;
	private int categories;
	
	public ExUISetting(Player player)
	{
		_uiSettings = player.getUISettings();
		calcSize();
	}
	
	private void calcSize()
	{
		int size = 16; // initial header and footer
		int category = 0;
		final int numKeyCt = _uiSettings.getKeys().size();
		for (int i = 0; i < numKeyCt; i++)
		{
			size++;
			if (_uiSettings.getCategories().containsKey(category))
			{
				final List<Integer> catElList1 = _uiSettings.getCategories().get(category);
				size += catElList1.size();
			}
			category++;
			size++;
			if (_uiSettings.getCategories().containsKey(category))
			{
				final List<Integer> catElList2 = _uiSettings.getCategories().get(category);
				size += catElList2.size();
			}
			category++;
			size += 4;
			if (_uiSettings.getKeys().containsKey(i))
			{
				final List<ActionKey> keyElList = _uiSettings.getKeys().get(i);
				size += (keyElList.size() * 20);
			}
		}
		buffsize = size;
		categories = category;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_UI_SETTING.writeId(this);
		writeInt(buffsize);
		writeInt(categories);
		int category = 0;
		final int numKeyCt = _uiSettings.getKeys().size();
		writeInt(numKeyCt);
		for (int i = 0; i < numKeyCt; i++)
		{
			if (_uiSettings.getCategories().containsKey(category))
			{
				final List<Integer> catElList1 = _uiSettings.getCategories().get(category);
				writeByte(catElList1.size());
				for (int cmd : catElList1)
				{
					writeByte(cmd);
				}
			}
			else
			{
				writeByte(0);
			}
			category++;
			if (_uiSettings.getCategories().containsKey(category))
			{
				final List<Integer> catElList2 = _uiSettings.getCategories().get(category);
				writeByte(catElList2.size());
				for (int cmd : catElList2)
				{
					writeByte(cmd);
				}
			}
			else
			{
				writeByte(0);
			}
			category++;
			if (_uiSettings.getKeys().containsKey(i))
			{
				final List<ActionKey> keyElList = _uiSettings.getKeys().get(i);
				writeInt(keyElList.size());
				for (ActionKey akey : keyElList)
				{
					writeInt(akey.getCommandId());
					writeInt(akey.getKeyId());
					writeInt(akey.getToogleKey1());
					writeInt(akey.getToogleKey2());
					writeInt(akey.getShowStatus());
				}
			}
			else
			{
				writeInt(0);
			}
		}
		writeInt(0x11);
		writeInt(16);
	}
}
