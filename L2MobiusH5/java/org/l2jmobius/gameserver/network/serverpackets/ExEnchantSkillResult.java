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

import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author JIV
 */
public class ExEnchantSkillResult extends ServerPacket
{
	private static final ExEnchantSkillResult STATIC_PACKET_TRUE = new ExEnchantSkillResult(true);
	private static final ExEnchantSkillResult STATIC_PACKET_FALSE = new ExEnchantSkillResult(false);
	
	public static ExEnchantSkillResult valueOf(boolean result)
	{
		return result ? STATIC_PACKET_TRUE : STATIC_PACKET_FALSE;
	}
	
	private final boolean _enchanted;
	
	public ExEnchantSkillResult(boolean enchanted)
	{
		_enchanted = enchanted;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_ENCHANT_SKILL_RESULT.writeId(this);
		writeInt(_enchanted);
	}
}
