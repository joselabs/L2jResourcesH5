/*
 * Copyright (C) L2J Sunrise
 * This file is part of L2J Sunrise.
 */
package l2r.gameserver.enums;

/**
 * @author vGodFather
 */
public enum AttackType
{
	MISSED(0x80),
	BLOCKED(0x40),
	CRITICAL(0x20),
	SHOT_USED(0x10);
	
	private final int _id;
	
	private AttackType(int id)
	{
		_id = id;
	}
	
	public int getId()
	{
		return _id;
	}
}
