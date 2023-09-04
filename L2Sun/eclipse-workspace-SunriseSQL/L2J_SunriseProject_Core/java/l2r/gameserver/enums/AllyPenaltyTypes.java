/*
 * Copyright (C) L2J Sunrise
 * This file is part of L2J Sunrise.
 */
package l2r.gameserver.enums;

/**
 * @author vGodFather
 */
public enum AllyPenaltyTypes
{
	/** Clan leaved ally */
	PENALTY_TYPE_CLAN_LEAVED(1),
	
	/** Clan was dismissed from ally */
	PENALTY_TYPE_CLAN_DISMISSED(2),
	
	/** Leader clan dismiss clan from ally */
	PENALTY_TYPE_DISMISS_CLAN(3),
	
	/** Leader clan dissolve ally */
	PENALTY_TYPE_DISSOLVE_ALLY(4);
	
	private final int _id;
	
	private AllyPenaltyTypes(int id)
	{
		_id = id;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public static AllyPenaltyTypes getById(int id)
	{
		for (AllyPenaltyTypes crestType : values())
		{
			if (crestType.getId() == id)
			{
				return crestType;
			}
		}
		return null;
	}
}
