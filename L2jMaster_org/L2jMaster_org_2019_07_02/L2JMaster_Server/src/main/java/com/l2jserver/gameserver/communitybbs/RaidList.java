/*
 * Copyright (C) 2004-2019 L2J Server
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
 package com.l2jserver.gameserver.communitybbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.l2jserver.Config;
import com.l2jserver.commons.database.pool.impl.ConnectionFactory;

public class RaidList
{
	private final StringBuilder _raidList = new StringBuilder();
	
	public RaidList(String rfid)
	{
		loadFromDB(rfid);
	}
	
	@SuppressWarnings("deprecation")
	private void loadFromDB(String rfid)
	{
		int type = Integer.parseInt(rfid);
		Connection con = null;
		int stpoint = 0;
		int pos = 0;
		String sort = "";
		if (Config.RAID_LIST_SORT_ASC)
		{
			sort = "ASC";
		}
		else
		{
			sort = "DESC";
		}
		for (int count = 1; count != type; count++)
		{
			stpoint += Config.RAID_LIST_RESULTS;
		}
		
		try
		{
			con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT id, name, level FROM npc WHERE type='L2RaidBoss' AND EXISTS (SELECT * FROM raidboss_spawnlist WHERE raidboss_spawnlist.boss_id = npc.id) ORDER BY `level` " + sort + " Limit " + stpoint + ", " + Config.RAID_LIST_RESULTS);
			ResultSet result = statement.executeQuery();
			pos = stpoint;
			
			while (result.next())
			{
				int npcid = result.getInt("id");
				String npcname = result.getString("name");
				int rlevel = result.getInt("level");
				PreparedStatement statement2 = con.prepareStatement("SELECT respawn_time, respawn_min_delay, respawn_max_delay FROM raidboss_spawnlist WHERE boss_id=" + npcid);
				ResultSet result2 = statement2.executeQuery();
				
				while (result2.next())
				{
					pos++;
					boolean rstatus = false;
					long respawn = result2.getLong("respawn_time");
					if (respawn == 0)
					{
						rstatus = true;
					}
					int mindelay = result2.getInt("respawn_min_delay");
					int maxdelay = result2.getInt("respawn_max_delay");
					mindelay = mindelay / 60 / 60;
					maxdelay = maxdelay / 60 / 60;
					addRaidToList(pos, npcname, rlevel, mindelay, maxdelay, rstatus);
				}
				result2.close();
				statement2.close();
			}
			
			result.close();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectionFactory.close(con);
		}
	}
	
	private void addRaidToList(int pos, String npcname, int rlevel, int mindelay, int maxdelay, boolean rstatus)
	{
		_raidList.append("<table border=0 cellspacing=0 cellpadding=2 width=750 height=" + Config.RAID_LIST_ROW_HEIGHT + ">");
		_raidList.append("<tr>");
		_raidList.append("<td FIXWIDTH=5></td>");
		_raidList.append("<td FIXWIDTH=20>" + pos + "</td>");
		_raidList.append("<td FIXWIDTH=270>" + npcname + "</td>");
		_raidList.append("<td FIXWIDTH=50>" + rlevel + "</td>");
		_raidList.append("<td FIXWIDTH=120 align=center>" + mindelay + " - " + maxdelay + "</td>");
		_raidList.append("<td FIXWIDTH=50 align=center>" + ((rstatus) ? "<font color=99FF00>Alive</font>" : "<font color=CC0000>Dead</font>") + "</td>");
		_raidList.append("<td FIXWIDTH=5></td>");
		_raidList.append("</tr>");
		_raidList.append("</table>");
		_raidList.append("<img src=\"L2UI.Squaregray\" width=\"740\" height=\"1\">");
	}
	
	public String loadRaidList()
	{
		return _raidList.toString();
	}
}