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
 * this program. If not, see <http://l2jeternity.com/>.
 */
package services.ColorNameNPC;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import l2e.gameserver.data.parser.ItemsParser;
import l2e.gameserver.data.parser.NpcsParser;
import l2e.gameserver.instancemanager.QuestManager;
import l2e.gameserver.model.Location;
import l2e.gameserver.model.actor.Npc;
import l2e.gameserver.model.actor.Player;
import l2e.gameserver.model.holders.SpawnsHolder;
import l2e.gameserver.model.quest.Quest;
import l2e.gameserver.model.quest.QuestState;
import l2e.gameserver.model.strings.server.ServerStorage;

/**
 * Based on L2J Eternity-World
 */
public class ColorNameNPC extends Quest
{
	private final static int NPC = 50023;
	private static String COLORNAME_NPC_COLORS = "009900;FF99FF;BF00FF;FFFFFF;00FF00;000000;80FF80;AAAAAA";
	private static String COLORNAME_NPC_SETTINGS_LINE = "5;[];10;[];15;[];30;[]";
	
	private static ColorNameNpcSettings COLORNAME_NPC_SETTINGS = new ColorNameNpcSettings(COLORNAME_NPC_SETTINGS_LINE);
	private final List<SpawnsHolder> _spawnList = new ArrayList<>();

	public ColorNameNPC(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addFirstTalkId(NPC);
		addStartNpc(NPC);
		addTalkId(NPC);
		loadSpawnList();
		
		if (_spawnList != null)
		{
			for (final SpawnsHolder spawn : _spawnList)
			{
				addSpawn(NPC, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading(), false, 0, false);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final String paramEvent[] = event.split(" ");
		final String action = paramEvent[0];
		final String value1 = paramEvent[1];
		final String value2 = paramEvent[2];
		String htmltext = "";
		final QuestState st = player.getQuestState(getName());
		if (action.equalsIgnoreCase("viewColor"))
		{
			htmltext = viewColor(player, Integer.valueOf(value1), player.getName());
		}
		else if (action.equalsIgnoreCase("buyColor"))
		{
			htmltext = buyColor(player, value1, value2, st);
		}
		else if (action.equalsIgnoreCase("page"))
		{
			htmltext = page(player, player.getLevel());
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			final Quest q = QuestManager.getInstance().getQuest(getName());
			st = q.newQuestState(player);
		}
		return page(player, player.getLevel());
	}
	
	public String page(Player player, int lvl)
	{
		final String lang = player.getLang();
		String htmltext = "";
		final String _days[] = COLORNAME_NPC_SETTINGS.getDays();
		htmltext += htmlPage(player, "Title");
		if (lvl < 40)
		{
			htmltext += "" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.MIN_LVL") + " 40";
		}
		else
		{
			htmltext += "" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.WELCOME") + "<br>" + "" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.SEARCH_COLOR") + "" + "" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.NEED_ITEM") + "<br>";
			htmltext += "<center>";
			htmltext += "<table width=\"260\" align=\"center\">";
			htmltext += "<tr>";
			htmltext += "<td width=\"165\" align=\"center\">" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.COUNT") + "</td>";
			htmltext += "<td width=\"95\" align=\"center\">" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.DAY") + "</td>";
			htmltext += "</tr>";
			for (final String _day : _days)
			{
				final int days = Integer.valueOf(_day);
				htmltext += "<tr>";
				htmltext += "<td align=\"left\">";
				for (final Integer _itemId : COLORNAME_NPC_SETTINGS.getFeeItems(days).keySet())
				{
					final int _count = COLORNAME_NPC_SETTINGS.getFeeItems(days).get(_itemId);
					htmltext += "<font color=\"LEVEL\">" + _count + "</font> " + player.getItemName(ItemsParser.getInstance().getTemplate(_itemId)) + "<br1>";
				}
				htmltext += "<br><br></td>";
				htmltext += "<td align=\"center\">" + button(_day + " " + ServerStorage.getInstance().getString(lang, "ColorNameNPC.DAYS") + "", "viewColor " + _day + " 0", 80, 30) + "<br><br></td>";
				htmltext += "</tr>";
			}
			htmltext += "</center>";
		}
		htmltext += htmlPage(player, "Footer");
		return htmltext;
	}
	
	public String viewColor(Player player, int d, String name)
	{
		final String lang = player.getLang();
		String htmltext = "";
		htmltext += htmlPage(player, "Title");
		htmltext += "" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.COLOR_LIST") + "  " + d + " " + ServerStorage.getInstance().getString(lang, "ColorNameNPC.DAYS") + ".<br1>" + "" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.COLOR_BUY") + "<br>" + "" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.THX") + "<br>";
		htmltext += "<font color=\"LEVEL\">" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.COUNT") + ":</font><br1>";
		for (final Integer _itemId : COLORNAME_NPC_SETTINGS.getFeeItems(d).keySet())
		{
			final int _count = COLORNAME_NPC_SETTINGS.getFeeItems(d).get(_itemId);
			htmltext += "<font color=\"LEVEL\">" + _count + "</font> " + player.getItemName(ItemsParser.getInstance().getTemplate(_itemId)) + "<br1>";
		}
		htmltext += "<br><center>";
		htmltext += "<table>";
		htmltext += "<tr><td align=\"center\"><img src=\"L2UI.SquareWhite\" width=\"250\" height=\"1\"></td></tr>";
		htmltext += "<tr><td>";
		htmltext += "<table>";
		htmltext += "<tr>";
		htmltext += "<td width=\"200\" align=\"center\"> </td>";
		htmltext += "<td width=\"50\" align=\"center\"></td>";
		htmltext += "</tr>";
		final String[] colors = COLORNAME_NPC_COLORS.split("\\;");
		for (final String element : colors)
		{
			htmltext += "<tr><td align=\"left\">";
			htmltext += "" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.COLOR") + " - " + link(name, "buyColor " + d + " " + element, element) + "<br1>";
			htmltext += "</td>";
			htmltext += "<td align=\"center\">";
			htmltext += button("" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.SELECT") + "", "buyColor " + d + " " + element, 60, 30);
			htmltext += "</td></tr>";
		}
		htmltext += "<tr><td height=\"3\" align=\"center\"> </td><td height=\"3\" align=\"center\"> </td></tr>";
		htmltext += "</table>";
		htmltext += "</td></tr>";
		htmltext += "<tr><td align=\"center\"><img src=\"L2UI.SquareWhite\" width=\"250\" height=\"1\"></td></tr>";
		htmltext += "</table><br>";
		htmltext += button("" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.BACK") + "", "page 0 0", 60, 30);
		htmltext += "</center>";
		htmltext += htmlPage(player, "Footer");
		return htmltext;
	}
	
	public String buyColor(Player player, String d, String c, QuestState st)
	{
		String htmltext = "";
		htmltext += htmlPage(player, "Title");
		for (final Integer _itemId : COLORNAME_NPC_SETTINGS.getFeeItems(Integer.valueOf(d)).keySet())
		{
			final Long _count = Long.valueOf(COLORNAME_NPC_SETTINGS.getFeeItems(Integer.valueOf(d)).get(_itemId));
			if (st.getQuestItemsCount(Integer.valueOf(_itemId)) < _count)
			{
				htmltext += "" + ServerStorage.getInstance().getString(player.getLang(), "ColorNameNPC.NO_ITEM") + "<br>";
				htmltext += "<br><center>" + button("" + ServerStorage.getInstance().getString(player.getLang(), "ColorNameNPC.BACK") + "", "page 0 0", 60, 30) + "</center>";
				htmltext += htmlPage(player, "Footer");
				return htmltext;
			}
		}
		if (player.getKarma() > 0)
		{
			htmltext += "" + ServerStorage.getInstance().getString(player.getLang(), "ColorNameNPC.KARMA") + "<br>";
		}
		else if (player.getPvpFlag() != 0)
		{
			htmltext += "" + ServerStorage.getInstance().getString(player.getLang(), "ColorNameNPC.COMBAT") + "<br>";
		}
		else if (player.isAttackingNow() == true)
		{
			htmltext += "" + ServerStorage.getInstance().getString(player.getLang(), "ColorNameNPC.NO_SAY") + "<br>";
		}
		else
		{
			for (final Integer _itemId : COLORNAME_NPC_SETTINGS.getFeeItems(Integer.valueOf(d)).keySet())
			{
				final Long _count = Long.valueOf(COLORNAME_NPC_SETTINGS.getFeeItems(Integer.valueOf(d)).get(_itemId));
				st.takeItems(_itemId, _count);
			}
			final int color = Integer.decode("0x" + c);
			final long time = (System.currentTimeMillis() + (Long.valueOf(d) * 24 * 60 * 60 * 1000L));
			player.setVar("namecolor", Integer.toString(color), time);
			player.getAppearance().setNameColor(color);
			player.broadcastUserInfo(true);
			htmltext += "" + ServerStorage.getInstance().getString(player.getLang(), "ColorNameNPC.COLOR_CHANGE") + "<br>" + ServerStorage.getInstance().getString(player.getLang(), "ColorNameNPC.COME_STILL") + "<br>";
		}
		htmltext += "<br><center>" + button("" + ServerStorage.getInstance().getString(player.getLang(), "ColorNameNPC.BACK") + "", "page 0 0", 60, 30) + "</center>";
		htmltext += htmlPage(player, "Footer");
		return htmltext;
	}
	
	public String htmlPage(Player player, String op)
	{
		final String lang = player.getLang();
		String texto = "";
		if (op == "Title")
		{
			texto += "<html><body><title>" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.TITLE") + "</title><center><br>" + "<b><font color=ffcc00>" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.INFO") + "</font></b>" + "<br><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\"><br></center>";
		}
		else if (op == "Footer")
		{
			texto += "<br><center><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\"><br>" + "<br><font color=\"303030\">---</font></center></body></html>";
		}
		else
		{
			texto = "" + ServerStorage.getInstance().getString(lang, "ColorNameNPC.NO_SEARCH") + "";
		}
		return texto;
	}
	
	public String button(String name, String event, int w, int h)
	{
		return "<button value=\"" + name + "\" action=\"bypass -h Quest ColorNameNPC " + event + "\" " + "width=\"" + Integer.toString(w) + "\" height=\"" + Integer.toString(h) + "\" " + "back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
	}
	
	public String link(String name, String event, String color)
	{
		return "<a action=\"bypass -h Quest ColorNameNPC " + event + "\">" + "<font color=\"" + color + "\">" + name + "</font></a>";
	}
	
	private void loadSpawnList()
	{
		final File configFile = new File("data/scripts/services/" + getScriptName() + "/spawnList.xml");
		try
		{
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			final Document doc = db.parse(configFile);
			final Node first = doc.getDocumentElement().getFirstChild();
			for (Node n = first; n != null; n = n.getNextSibling())
			{
				if (n.getNodeName().equalsIgnoreCase("spawnlist"))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if (d.getNodeName().equalsIgnoreCase("loc"))
						{
							try
							{
								final int xPos = Integer.parseInt(d.getAttributes().getNamedItem("x").getNodeValue());
								final int yPos = Integer.parseInt(d.getAttributes().getNamedItem("y").getNodeValue());
								final int zPos = Integer.parseInt(d.getAttributes().getNamedItem("z").getNodeValue());
								final int heading = d.getAttributes().getNamedItem("heading").getNodeValue() != null ? Integer.parseInt(d.getAttributes().getNamedItem("heading").getNodeValue()) : 0;
								
								if (NpcsParser.getInstance().getTemplate(NPC) == null)
								{
									_log.warning(getScriptName() + " script: " + NPC + " is wrong NPC id, NPC was not added in spawnlist");
									continue;
								}
								
								_spawnList.add(new SpawnsHolder(NPC, new Location(xPos, yPos, zPos, heading)));
							}
							catch (final NumberFormatException nfe)
							{
								_log.warning("Wrong number format in config.xml spawnlist block for " + getScriptName() + " script.");
							}
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			_log.log(Level.WARNING, getScriptName() + " script: error reading " + configFile.getAbsolutePath() + " ! " + e.getMessage(), e);
		}
	}
	
	private static class ColorNameNpcSettings
	{
		private final Map<Integer, Map<Integer, Integer>> _feeItems;
		private final Map<Integer, Boolean> _allowedColorName;
		
		public ColorNameNpcSettings(String _configLine)
		{
			_feeItems = new HashMap<>();
			_allowedColorName = new HashMap<>();
			if (_configLine != null)
			{
				parseConfigLine(_configLine.trim());
			}
		}
		
		private void parseConfigLine(String _configLine)
		{
			final StringTokenizer st = new StringTokenizer(_configLine, ";");
			while (st.hasMoreTokens())
			{
				final int days = Integer.parseInt(st.nextToken());
				_allowedColorName.put(days, true);
				final Map<Integer, Integer> _items = new HashMap<>();
				
				if (st.hasMoreTokens())
				{
					final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						final StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						final int _itemId = Integer.parseInt(st3.nextToken());
						final int _quantity = Integer.parseInt(st3.nextToken());
						_items.put(_itemId, _quantity);
					}
				}
				_feeItems.put(days, _items);
			}
		}
		
		public String[] getDays()
		{
			String _day = "";
			String _days[];
			for (final int key : _allowedColorName.keySet())
			{
				_day += key + ";";
			}
			_days = _day.split(";");
			return _days;
		}
		
		public Map<Integer, Integer> getFeeItems(int days)
		{
			if (_feeItems.containsKey(days))
			{
				return _feeItems.get(days);
			}
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		new ColorNameNPC(-1, "ColorNameNPC", "services");
	}
}
