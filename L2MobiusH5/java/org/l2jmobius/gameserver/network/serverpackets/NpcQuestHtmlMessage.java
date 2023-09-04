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

import org.l2jmobius.gameserver.enums.HtmlActionScope;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * NpcQuestHtmlMessage server packet implementation.
 * @author HorridoJoho
 */
public class NpcQuestHtmlMessage extends AbstractHtmlPacket
{
	private final int _questId;
	
	public NpcQuestHtmlMessage(int npcObjId, int questId)
	{
		super(npcObjId);
		_questId = questId;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_NPC_QUEST_HTML_MESSAGE.writeId(this);
		writeInt(getNpcObjId());
		writeString(getHtml());
		writeInt(_questId);
	}
	
	@Override
	public HtmlActionScope getScope()
	{
		return HtmlActionScope.NPC_QUEST_HTML;
	}
}
