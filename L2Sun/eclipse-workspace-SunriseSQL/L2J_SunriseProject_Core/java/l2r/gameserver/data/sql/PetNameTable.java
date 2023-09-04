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
package l2r.gameserver.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import l2r.Config;
import l2r.L2DatabaseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetNameTable
{
	private static Logger _log = LoggerFactory.getLogger(PetNameTable.class);
	
	public static PetNameTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public boolean doesPetNameExist(String name)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT name FROM pets WHERE name=?"))
		{
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery())
			{
				return rs.next();
			}
		}
		catch (SQLException e)
		{
			_log.warn(getClass().getSimpleName() + ": Could not check existing petname:" + e.getMessage(), e);
		}
		return false;
	}
	
	public boolean isValidPetName(String name)
	{
		return Config.PET_NAME_TEMPLATE.matcher(name).matches();
	}
	
	private static class SingletonHolder
	{
		protected static final PetNameTable _instance = new PetNameTable();
	}
}
