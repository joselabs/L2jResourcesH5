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

import org.l2jmobius.gameserver.model.Macro;
import org.l2jmobius.gameserver.model.MacroCmd;
import org.l2jmobius.gameserver.network.ServerPackets;

public class SendMacroList extends ServerPacket
{
	private final int _rev;
	private final int _count;
	private final Macro _macro;
	
	public SendMacroList(int rev, int count, Macro macro)
	{
		_rev = rev;
		_count = count;
		_macro = macro;
	}
	
	@Override
	public void write()
	{
		ServerPackets.MACRO_LIST.writeId(this);
		writeInt(_rev); // macro change revision (changes after each macro edition)
		writeByte(0); // unknown
		writeByte(_count); // count of Macros
		writeByte(_macro != null); // unknown
		if (_macro != null)
		{
			writeInt(_macro.getId()); // Macro ID
			writeString(_macro.getName()); // Macro Name
			writeString(_macro.getDescr()); // Desc
			writeString(_macro.getAcronym()); // acronym
			writeByte(_macro.getIcon()); // icon
			writeByte(_macro.getCommands().size()); // count
			int i = 1;
			for (MacroCmd cmd : _macro.getCommands())
			{
				writeByte(i++); // command count
				writeByte(cmd.getType().ordinal()); // type 1 = skill, 3 = action, 4 = shortcut
				writeInt(cmd.getD1()); // skill id
				writeByte(cmd.getD2()); // shortcut id
				writeString(cmd.getCmd()); // command name
			}
		}
	}
}
