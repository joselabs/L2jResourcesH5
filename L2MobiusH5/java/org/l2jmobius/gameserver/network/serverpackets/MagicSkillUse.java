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

import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * MagicSkillUse server packet implementation.
 * @author UnAfraid, NosBit, Mobius
 */
public class MagicSkillUse extends ServerPacket
{
	private final int _skillId;
	private final int _skillLevel;
	private final int _hitTime;
	private final int _reuseDelay;
	private final Creature _creature;
	private final Creature _target;
	private final Location _groundLocation;
	
	public MagicSkillUse(Creature creature, Creature target, int skillId, int skillLevel, int hitTime, int reuseDelay)
	{
		super(67);
		_creature = creature;
		_target = target;
		_skillId = skillId;
		_skillLevel = skillLevel;
		_hitTime = hitTime;
		_reuseDelay = reuseDelay;
		_groundLocation = creature.isPlayer() ? creature.getActingPlayer().getCurrentSkillWorldPosition() : null;
	}
	
	public MagicSkillUse(Creature creature, int skillId, int skillLevel, int hitTime, int reuseDelay)
	{
		this(creature, creature, skillId, skillLevel, hitTime, reuseDelay);
	}
	
	@Override
	public void write()
	{
		ServerPackets.MAGIC_SKILL_USE.writeId(this);
		writeInt(_creature.getObjectId());
		writeInt(_target.getObjectId());
		writeInt(_skillId);
		writeInt(_skillLevel);
		writeInt(_hitTime);
		writeInt(_reuseDelay);
		writeInt(_creature.getX());
		writeInt(_creature.getY());
		writeInt(_creature.getZ());
		writeShort(0); // isGroundTargetSkill ? 65535 : 0
		if (_groundLocation == null)
		{
			writeShort(0);
		}
		else
		{
			writeShort(1);
			writeInt(_groundLocation.getX());
			writeInt(_groundLocation.getY());
			writeInt(_groundLocation.getZ());
		}
		writeInt(_target.getX());
		writeInt(_target.getY());
		writeInt(_target.getZ());
	}
}
