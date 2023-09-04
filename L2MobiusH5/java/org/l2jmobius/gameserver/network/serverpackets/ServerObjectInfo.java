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

import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author devScarlet, mrTJO
 */
public class ServerObjectInfo extends ServerPacket
{
	private final Npc _activeChar;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	private final int _displayId;
	private final boolean _isAttackable;
	private final double _collisionHeight;
	private final double _collisionRadius;
	private final String _name;
	
	public ServerObjectInfo(Npc activeChar, Creature actor)
	{
		_activeChar = activeChar;
		_displayId = _activeChar.getTemplate().getDisplayId();
		_isAttackable = _activeChar.isAutoAttackable(actor);
		_collisionHeight = _activeChar.getCollisionHeight();
		_collisionRadius = _activeChar.getCollisionRadius();
		_x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		_name = _activeChar.getTemplate().isUsingServerSideName() ? _activeChar.getTemplate().getName() : "";
	}
	
	@Override
	public void write()
	{
		ServerPackets.SERVER_OBJECT_INFO.writeId(this);
		writeInt(_activeChar.getObjectId());
		writeInt(_displayId + 1000000);
		writeString(_name); // name
		writeInt(_isAttackable);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_heading);
		writeDouble(1.0); // movement multiplier
		writeDouble(1.0); // attack speed multiplier
		writeDouble(_collisionRadius);
		writeDouble(_collisionHeight);
		writeInt((int) (_isAttackable ? _activeChar.getCurrentHp() : 0));
		writeInt(_isAttackable ? _activeChar.getMaxHp() : 0);
		writeInt(1); // object type
		writeInt(0); // special effects
	}
}
