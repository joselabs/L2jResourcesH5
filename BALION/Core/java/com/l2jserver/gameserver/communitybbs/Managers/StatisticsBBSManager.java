/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://l2jpsproject.eu/>.
 */
package com.l2jserver.gameserver.communitybbs.Managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.l2jserver.Config;
import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.util.Rnd;

import javolution.text.TextBuilder;

public class StatisticsBBSManager extends BaseBBSManager
{
	public class CBStatMan
	{
		public int PlayerId = 0;
		public String ChName = "";
		public int ChGameTime = 0;
		public int ChPk = 0;
		public int ChPvP = 0;
		public int ChPcBangPoint = 0;
		public String ChClanName = "";
		public int ChClanLevel = 0;
		public int ChClanRep = 0;
		public String ChClanAlly = "";
		public int ChOnOff = 0;
		public int ChSex = 0;
	}
	
	public static StatisticsBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance player)
	{
		if (command.equals("_bbsstat;"))
		{
			showPvp(player);
		}
		else if (command.startsWith("_bbsstat;pk"))
		{
			showPK(player);
		}
		else if (command.startsWith("_bbsstat;clan"))
		{
			showClan(player);
		}
		else if (command.startsWith("_bbsstat;pcbang"))
		{
			showPcBang(player);
		}
		else
		{
			separateAndSend("<html><body><br><br><center>In bbsstat function: " + command + " is not implemented yet.</center><br><br></body></html>", player);
		}
	}
	
	private void showPvp(L2PcInstance player)
	{
		
		CBStatMan tp;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pvpkills DESC LIMIT 20;");
			rs = statement.executeQuery();
			
			TextBuilder html = new TextBuilder();
			html.append("<center><font name=hs12>Top 20 PvP Kills</font></center>");
			html.append("<table width=700 bgcolor=CCCCCC>");
			html.append("<tr>");
			html.append("<td width=350><font name=hs12 color=LEVEL>Name</font></td>");
			html.append("<td width=100><font name=hs12 color=LEVEL>Sex</font></td>");
			html.append("<td width=200><font name=hs12 color=LEVEL>Online Time</font></td>");
			html.append("<td width=100><font name=hs12 color=LEVEL>PK kills</font></td>");
			html.append("<td width=100><font name=hs12 color=00CC00>PvP kills</font></td>");
			html.append("<td width=200><font name=hs12 color=LEVEL>Status</font></td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700>");
			while (rs.next())
			{
				tp = new CBStatMan();
				tp.PlayerId = rs.getInt("charId");
				tp.ChName = rs.getString("char_name");
				tp.ChSex = rs.getInt("sex");
				tp.ChGameTime = rs.getInt("onlinetime");
				tp.ChPk = rs.getInt("pkkills");
				tp.ChPvP = rs.getInt("pvpkills");
				tp.ChOnOff = rs.getInt("online");
				String OnOff;
				String color;
				String sex;
				sex = tp.ChSex == 1 ? "Female" : "Male";
				if (tp.ChOnOff == 1)
				{
					OnOff = "ONLINE";
					color = "00CC00";
				}
				else
				{
					OnOff = "OFFLINE";
					color = "D70000";
				}
				
				html.append("<tr>");
				html.append("<td width=350>" + tp.ChName + "</td>");
				html.append("<td width=100>" + sex + "</td>");
				html.append("<td width=200>" + OnlineTime(tp.ChGameTime) + "</td>");
				html.append("<td width=100>" + tp.ChPk + "</td>");
				html.append("<td width=100><font color=00CC00>" + tp.ChPvP + "</font></td>");
				html.append("<td width=200><font color=" + color + ">" + OnOff + "</font></td>");
				html.append("</tr>");
			}
			html.append("</table>");
			
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(player.getLang(), "data/html/CommunityBoard/11.htm");
			adminReply.replace("%stat%", html.toString());
			adminReply.replace("%playername%", player.getName());
			adminReply.replace("%servername%", Config.SERVER_NAME);
			adminReply.replace("%online%", String.valueOf(TopBBSManager.getInstance().onlineplayers + Config.FAKE_PLAYERS + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS)));
			separateAndSend(adminReply.getHtm(), player);
			
			statement.close();
			rs.close();
			
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void showPK(L2PcInstance player)
	{
		
		CBStatMan tp;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pkkills DESC LIMIT 20;");
			rs = statement.executeQuery();
			
			TextBuilder html = new TextBuilder();
			html.append("<center><font name=hs12>Top 20 PK Kills</font></center>");
			html.append("<table width=700 bgcolor=CCCCCC>");
			html.append("<tr>");
			html.append("<td width=350><font name=hs12 color=LEVEL>Name</font></td>");
			html.append("<td width=100><font name=hs12 color=LEVEL>Sex</font></td>");
			html.append("<td width=200><font name=hs12 color=LEVEL>Online Time</font></td>");
			html.append("<td width=100><font name=hs12 color=00CC00>PK kills</font></td>");
			html.append("<td width=100><font name=hs12 color=LEVEL>PvP kills</font></td>");
			html.append("<td width=200><font name=hs12 color=LEVEL>Status</font></td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700>");
			while (rs.next())
			{
				tp = new CBStatMan();
				tp.PlayerId = rs.getInt("charId");
				tp.ChName = rs.getString("char_name");
				tp.ChSex = rs.getInt("sex");
				tp.ChGameTime = rs.getInt("onlinetime");
				tp.ChPk = rs.getInt("pkkills");
				tp.ChPvP = rs.getInt("pvpkills");
				tp.ChOnOff = rs.getInt("online");
				String OnOff;
				String color;
				String sex;
				sex = tp.ChSex == 1 ? "Female" : "Male";
				if (tp.ChOnOff == 1)
				{
					OnOff = "ONLINE";
					color = "00CC00";
				}
				else
				{
					OnOff = "OFFLINE";
					color = "D70000";
				}
				html.append("<tr>");
				html.append("<td width=350>" + tp.ChName + "</td>");
				html.append("<td width=100>" + sex + "</td>");
				html.append("<td width=200>" + OnlineTime(tp.ChGameTime) + "</td>");
				html.append("<td width=100><font color=00CC00>" + tp.ChPk + "</font></td>");
				html.append("<td width=100>" + tp.ChPvP + "</td>");
				html.append("<td width=200><font color=" + color + ">" + OnOff + "</font></td>");
				html.append("</tr>");
			}
			html.append("</table>");
			
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(player.getLang(), "data/html/CommunityBoard/11.htm");
			adminReply.replace("%stat%", html.toString());
			adminReply.replace("%playername%", player.getName());
			adminReply.replace("%servername%", Config.SERVER_NAME);
			adminReply.replace("%online%", String.valueOf(TopBBSManager.getInstance().onlineplayers + Config.FAKE_PLAYERS + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS)));
			separateAndSend(adminReply.getHtm(), player);
			
			statement.close();
			rs.close();
			
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void showPcBang(L2PcInstance player)
	{
		if (Config.PC_BANG_ENABLED)
		{
			CBStatMan tp;
			PreparedStatement statement = null;
			ResultSet rs = null;
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pccafe_points DESC LIMIT 20;");
				rs = statement.executeQuery();
				
				TextBuilder html = new TextBuilder();
				html.append("<center><font name=hs12>Top 20 PC Points</font></center>");
				html.append("<table width=700 bgcolor=CCCCCC>");
				html.append("<tr>");
				html.append("<td width=350><font name=hs12 color=LEVEL>Name</font></td>");
				html.append("<td width=100><font name=hs12 color=LEVEL>Sex</font></td>");
				html.append("<td width=200><font name=hs12 color=LEVEL>Online Time</font></td>");
				html.append("<td width=100><font name=hs12 color=00CC00>Pc Points</font></td>");
				html.append("<td width=200><font name=hs12 color=LEVEL>Status</font></td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("<img src=L2UI.SquareWhite width=700 height=1>");
				html.append("<table width=700>");
				while (rs.next())
				{
					tp = new CBStatMan();
					tp.PlayerId = rs.getInt("charId");
					tp.ChName = rs.getString("char_name");
					tp.ChSex = rs.getInt("sex");
					tp.ChGameTime = rs.getInt("onlinetime");
					tp.ChPcBangPoint = rs.getInt("pccafe_points");
					tp.ChOnOff = rs.getInt("online");
					String OnOff;
					String color;
					String sex;
					sex = tp.ChSex == 1 ? "Female" : "Male";
					if (tp.ChOnOff == 1)
					{
						OnOff = "ONLINE";
						color = "00CC00";
					}
					else
					{
						OnOff = "OFFLINE";
						color = "D70000";
					}
					html.append("<tr>");
					html.append("<td width=350>" + tp.ChName + "</td>");
					html.append("<td width=100>" + sex + "</td>");
					html.append("<td width=200>" + OnlineTime(tp.ChGameTime) + "</td>");
					html.append("<td width=100><font color=00CC00>" + tp.ChPcBangPoint + "</font></td>");
					html.append("<td width=200><font color=" + color + ">" + OnOff + "</font></td>");
					html.append("</tr>");
				}
				html.append("</table>");
				
				NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
				adminReply.setFile(player.getLang(), "data/html/CommunityBoard/11.htm");
				adminReply.replace("%stat%", html.toString());
				adminReply.replace("%playername%", player.getName());
				adminReply.replace("%servername%", Config.SERVER_NAME);
				adminReply.replace("%online%", String.valueOf(TopBBSManager.getInstance().onlineplayers + Config.FAKE_PLAYERS + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS)));
				separateAndSend(adminReply.getHtm(), player);
				
				statement.close();
				rs.close();
				
				return;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			player.sendMessage("PC Bang System is Deactivated");
			return;
		}
	}
	
	private void showClan(L2PcInstance player)
	{
		
		CBStatMan tp;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT clan_name,clan_level,reputation_score,ally_name FROM clan_data WHERE clan_level>0 order by clan_level desc limit 20;");
			rs = statement.executeQuery();
			
			TextBuilder html = new TextBuilder();
			html.append("<center><font name=hs12>Top 20 Clans by clan level.</font></center>");
			html.append("<table width=700 bgcolor=CCCCCC>");
			html.append("<tr>");
			html.append("<td width=350><font name=hs12 color=LEVEL>Clan Name</font></td>");
			html.append("<td width=100><font name=hs12 color=LEVEL>Alliance</font></td>");
			html.append("<td width=100><font name=hs12 color=LEVEL>Reps Sc.</font></td>");
			html.append("<td width=200><font name=hs12 color=LEVEL>Clan Level</font></td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=L2UI.SquareWhite width=700 height=1>");
			html.append("<table width=700>");
			while (rs.next())
			{
				tp = new CBStatMan();
				tp.ChClanName = rs.getString("clan_name");
				tp.ChClanAlly = rs.getString("ally_name");
				tp.ChClanRep = rs.getInt("reputation_score");
				tp.ChClanLevel = rs.getInt("clan_level");
				String ClanAlly;
				if (tp.ChClanAlly == null)
				{
					ClanAlly = "---";
				}
				else
				{
					ClanAlly = tp.ChClanAlly;
				}
				
				html.append("<tr>");
				html.append("<td width=350>" + tp.ChClanName + "</td>");
				html.append("<td width=100>" + ClanAlly + "</td>");
				html.append("<td width=100>" + tp.ChClanRep + "</td>");
				html.append("<td width=200>" + tp.ChClanLevel + "</td>");
				html.append("</tr>");
			}
			html.append("</table>");
			
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(player.getLang(), "data/html/CommunityBoard/11.htm");
			adminReply.replace("%stat%", html.toString());
			adminReply.replace("%playername%", player.getName());
			adminReply.replace("%servername%", Config.SERVER_NAME);
			adminReply.replace("%online%", String.valueOf(TopBBSManager.getInstance().onlineplayers + Config.FAKE_PLAYERS + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS)));
			separateAndSend(adminReply.getHtm(), player);
			
			statement.close();
			rs.close();
			
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	String OnlineTime(int time)
	{
		long onlinetimeH;
		if (((time / 60 / 60) - 0.5) <= 0)
		{
			onlinetimeH = 0;
		}
		else
		{
			onlinetimeH = Math.round((time / 60 / 60) - 0.5);
		}
		return "" + onlinetimeH + " h. ";
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance player)
	{
		
	}
	
	private static class SingletonHolder
	{
		protected static final StatisticsBBSManager _instance = new StatisticsBBSManager();
	}
}