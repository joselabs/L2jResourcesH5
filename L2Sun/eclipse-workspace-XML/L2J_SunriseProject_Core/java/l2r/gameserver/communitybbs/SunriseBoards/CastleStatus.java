package l2r.gameserver.communitybbs.SunriseBoards;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import l2r.L2DatabaseFactory;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class CastleStatus extends AbstractSunriseBoards
{
	private final StringBuilder _list = new StringBuilder();
	
	@Override
	public void load()
	{
		_list.setLength(0);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			for (int i = 1; i < 9; i++)
			{
				try (PreparedStatement statement = con.prepareStatement("SELECT c.name, c.siegeDate, c.taxPercent, cd.clan_name FROM castle AS c JOIN clan_data AS cd ON cd.hasCastle = c.id WHERE c.id = " + i + ";"))
				{
					try (ResultSet result = statement.executeQuery())
					{
						while (result.next())
						{
							final String owner = result.getString("clan_name");
							final String name = result.getString("name");
							final long someLong = result.getLong("siegeDate");
							final int tax = result.getInt("taxPercent");
							final Date anotherDate = new Date(someLong);
							final String DATE_FORMAT = "dd-MMM-yyyy HH:mm";
							final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
							
							addCastleToList(name, owner, tax, sdf.format(anotherDate));
						}
					}
				}
			}
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void addCastleToList(String name, String owner, int tax, String siegeDate)
	{
		_list.append("<table border=0 cellspacing=0 cellpadding=2 width=480>");
		_list.append("<tr>");
		_list.append("<td align=center FIXWIDTH=120>" + name + "</td>");
		_list.append("<td align=center FIXWIDTH=60>" + tax + "</td>");
		_list.append("<td align=center FIXWIDTH=120>" + owner + "</td>");
		_list.append("<td align=center FIXWIDTH=155>" + siegeDate + "</td>");
		_list.append("</tr>");
		_list.append("</table>");
		_list.append("<img src=\"L2UI.Squaregray\" width=\"480\" height=\"1\">");
	}
	
	@Override
	public String getList()
	{
		return _list.toString();
	}
	
	public static CastleStatus getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final CastleStatus _instance = new CastleStatus();
	}
}
