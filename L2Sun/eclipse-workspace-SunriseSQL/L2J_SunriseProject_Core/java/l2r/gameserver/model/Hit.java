/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.model;

import l2r.gameserver.enums.AttackType;
import l2r.gameserver.model.actor.L2Character;

/**
 * @author UnAfraid
 */
public class Hit
{
	private final int _targetId;
	private final int _damage;
	private final int _ssGrade;
	private int _flags = 0;
	
	public Hit(L2Object target, int damage, boolean miss, boolean crit, byte shld, boolean soulshot, int ssGrade)
	{
		_targetId = target.getObjectId();
		_damage = damage;
		_ssGrade = ssGrade;
		
		if (miss)
		{
			addMask(AttackType.MISSED);
			return;
		}
		
		if (crit)
		{
			addMask(AttackType.CRITICAL);
		}
		
		if (soulshot)
		{
			addMask(AttackType.SHOT_USED);
		}
		
		if ((target.isCharacter() && ((L2Character) target).isHpBlocked()) || (shld > 0))
		{
			addMask(AttackType.BLOCKED);
		}
	}
	
	private void addMask(AttackType type)
	{
		_flags |= type.getId();
	}
	
	public int getTargetId()
	{
		return _targetId;
	}
	
	public int getDamage()
	{
		return _damage;
	}
	
	public int getFlags()
	{
		return _flags;
	}
	
	public int getGrade()
	{
		return _ssGrade;
	}
	
	public boolean isMiss()
	{
		return (AttackType.MISSED.getId() & _flags) != 0;
	}
	
	public boolean isCritical()
	{
		return (AttackType.CRITICAL.getId() & _flags) != 0;
	}
	
	public boolean isShotUsed()
	{
		return (AttackType.SHOT_USED.getId() & _flags) != 0;
	}
	
	public boolean isBlocked()
	{
		return (AttackType.BLOCKED.getId() & _flags) != 0;
	}
}
