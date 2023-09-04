/*
 * Copyright (C) L2J Sunrise
 * This file is part of L2J Sunrise.
 */
package l2r.gameserver.enums;

/**
 * @author vGodFather
 */
public enum ClanUnitTypes
{
	/** Clan subunit type of Academy */
	SUBUNIT_ACADEMY(-1),
	
	/** Clan subunit type of Royal Guard A */
	SUBUNIT_ROYAL1(100),
	
	/** Clan subunit type of Royal Guard B */
	SUBUNIT_ROYAL2(200),
	
	/** Clan subunit type of Order of Knights A-1 */
	SUBUNIT_KNIGHT1(1001),
	
	/** Clan subunit type of Order of Knights A-2 */
	SUBUNIT_KNIGHT2(1002),
	
	/** Clan subunit type of Order of Knights B-1 */
	SUBUNIT_KNIGHT3(2001),
	
	/** Clan subunit type of Order of Knights B-2 */
	SUBUNIT_KNIGHT4(2002);
	
	private final int _id;
	
	private ClanUnitTypes(int id)
	{
		_id = id;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public static ClanUnitTypes getById(int id)
	{
		for (ClanUnitTypes crestType : values())
		{
			if (crestType.getId() == id)
			{
				return crestType;
			}
		}
		return null;
	}
}
