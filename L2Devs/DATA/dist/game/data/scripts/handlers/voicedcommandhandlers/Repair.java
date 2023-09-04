/*
 * Copyright © 2004-2019 L2JDevs
 * 
 * This file is part of L2JDevs.
 * 
 * L2JDevs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2JDevs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.voicedcommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.l2jdevs.commons.database.pool.impl.ConnectionFactory;
import org.l2jdevs.gameserver.cache.HtmCache;
import org.l2jdevs.gameserver.handler.IVoicedCommandHandler;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Repair Character Voiced Command
 * @author Sacrifice
 */
public final class Repair implements IVoicedCommandHandler
{
	private static final String SELECT_1 = "SELECT `account_name` FROM `characters` WHERE `char_name` = ?";
	private static final String SELECT_2 = "SELECT `key`, `expiration` FROM `punishments` WHERE `key` = ? AND `type` = 'JAIL'";
	private static final String SELECT_4 = "SELECT `charId` FROM `characters` WHERE `char_name` = ?";
	
	private static final String UPDATE_1 = "UPDATE `characters` SET `x` = 17867, `y` = 170259, `z` = -3503 WHERE `charId` = ?";
	private static final String UPDATE_2 = "UPDATE `items` SET `loc`= 'WAREHOUSE' WHERE `owner_id` = ? AND `loc` = 'PAPERDOLL'";
	
	private static final String DELETE_1 = "DELETE FROM `character_shortcuts` WHERE `charId` = ?";
	
	private static final String[] VOICED_COMMANDS =
	{
		"repair",
	};
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		String repairChar = null;
		
		try
		{
			if (params != null)
			{
				if (params.length() > 1)
				{
					final String[] cmdParams = params.split(" ");
					repairChar = cmdParams[0];
				}
			}
		}
		catch (Exception e)
		{
			repairChar = null;
		}
		
		if (command.startsWith("repair") && (repairChar != null))
		{
			_log.info("Repair Attempt for character: " + repairChar);
			if (checkAccount(activeChar, repairChar))
			{
				if (checkChar(activeChar, repairChar))
				{
					final String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/Repair/repair-self.html");
					final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
					npcHtmlMessage.setHtml(htmContent);
					activeChar.sendPacket(npcHtmlMessage);
					return false;
				}
				else if (checkJail(activeChar))
				{
					final String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/Repair/repair-jail.html");
					final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
					npcHtmlMessage.setHtml(htmContent);
					activeChar.sendPacket(npcHtmlMessage);
					return false;
				}
				else
				{
					repairBadCharacter(repairChar);
					final String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/Repair/repair-done.html");
					final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
					npcHtmlMessage.setHtml(htmContent);
					activeChar.sendPacket(npcHtmlMessage);
					return true;
				}
			}
			final String htmContent = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/mods/Repair/repair-error.html");
			final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
			npcHtmlMessage.setHtml(htmContent);
			activeChar.sendPacket(npcHtmlMessage);
			return false;
		}
		activeChar.sendMessage("The format is .repair <player name>");
		return true;
	}
	
	private boolean checkAccount(L2PcInstance activeChar, String repairChar)
	{
		boolean result = false;
		String repairCharAccount = "";
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_1))
		{
			ps.setString(1, repairChar);
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					repairCharAccount = rs.getString(1);
				}
				rs.close();
			}
			ps.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
			return result;
		}
		
		if (activeChar.getAccountName().compareTo(repairCharAccount) == 0)
		{
			result = true;
		}
		return result;
	}
	
	private boolean checkChar(L2PcInstance activeChar, String repairChar)
	{
		boolean result = false;
		if (activeChar.getName().compareTo(repairChar) == 0)
		{
			result = true;
		}
		return result;
	}
	
	private boolean checkJail(L2PcInstance activeChar)
	{
		boolean result = false;
		int key = 0;
		long expiration = 0;
		
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_2))
		{
			ps.setInt(1, key);
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					key = rs.getInt(1);
					expiration = rs.getLong(2);
				}
				rs.close();
			}
			ps.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
			return result;
		}
		
		if ((activeChar.getId() == key) && (expiration >= System.currentTimeMillis()))
		{
			result = true;
		}
		return result;
	}
	
	private void repairBadCharacter(String charName)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_4);
			PreparedStatement ps2 = con.prepareStatement(UPDATE_1);
			PreparedStatement ps3 = con.prepareStatement(DELETE_1);
			PreparedStatement ps4 = con.prepareStatement(UPDATE_2))
		{
			ps.setString(1, charName);
			
			try (ResultSet rs = ps.executeQuery())
			{
				int objId = 0;
				if (rs.next())
				{
					objId = rs.getInt(1);
				}
				
				if (objId == 0)
				{
					rs.close();
					ps.close();
					con.close();
					return;
				}
				
				ps2.setInt(1, objId);
				ps2.execute();
				
				ps3.setInt(1, objId);
				ps3.execute();
				
				ps4.setInt(1, objId);
				ps4.execute();
				
				rs.close();
			}
			ps.close();
			ps2.close();
			ps3.close();
			ps4.close();
		}
		catch (SQLException sqle)
		{
			_log.warning("Could not repair character: " + charName + " " + sqle);
		}
	}
}