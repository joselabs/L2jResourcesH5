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
package services.ServerInfoNPC;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import l2e.gameserver.Config;
import l2e.gameserver.data.parser.NpcsParser;
import l2e.gameserver.instancemanager.ChampionManager;
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
public class ServerInfoNPC extends Quest
{
	private final static int NPC = 50026;
	private static String[] SERVERINFO_NPC_ADM = "AdmServer".split("\\;");
	private static String[] SERVERINFO_NPC_GM = "GmServer 01;GmServer 02".split("\\;");
	private static String SERVERINFO_NPC_DESCRIPTION = "Server description.";
	private static String SERVERINFO_NPC_EMAIL = "user@user.com";
	private static String SERVERINFO_NPC_PHONE = "0";
	private static String[] SERVERINFO_NPC_CUSTOM = "ame 01;Name 02;Name 03".split("\\;");
	private static String[] SERVERINFO_NPC_DISABLE_PAGE = "0".split("\\;");

	private final List<SpawnsHolder> _spawnList = new ArrayList<>();
	
	public ServerInfoNPC(int questId, String name, String descr)
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
		String htmltext = "";
		final String lang = player.getLang();
		final String eventSplit[] = event.split(" ");
		if (eventSplit[0].equalsIgnoreCase("redirect"))
		{
			if (eventSplit[1].equalsIgnoreCase("main"))
			{
				htmltext = pageIndex(player, "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.WELCOME") + " " + player.getName() + ".");
			}
			if (eventSplit[1].equalsIgnoreCase("page"))
			{
				htmltext = pageSub(player, Integer.valueOf(eventSplit[2]));
			}
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext;
		final String lang = player.getLang();
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			final Quest q = QuestManager.getInstance().getQuest(getName());
			st = q.newQuestState(player);
		}
		htmltext = pageIndex(player, "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.WELCOME") + " " + player.getName() + ".");
		return htmltext;
	}

	public String pageIndex(Player player, String msg)
	{
		String htmltext = "";
		final String lang = player.getLang();
		htmltext += htmlPage(player, "Title");
		htmltext += msg;
		htmltext += "<center><table width=230>";
		if (disablePage(1) == 0)
		{
			htmltext += "<tr><td align=center>" + button("" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.RATE") + "", "redirect page 1 0", 150, 20) + "</td></tr>";
		}
		if (disablePage(2) == 0)
		{
			htmltext += "<tr><td align=center>" + button("" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.CONTACT") + "", "redirect page 2 0", 150, 20) + "</td></tr>";
		}
		if (disablePage(3) == 0)
		{
			htmltext += "<tr><td align=center>" + button("" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.CUSTOM_NPC") + "", "redirect page 3 0", 150, 20) + "</td></tr>";
		}
		if (disablePage(4) == 0)
		{
			htmltext += "<tr><td align=center>" + button("" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.MODS") + "", "redirect page 4 0", 150, 20) + "</td></tr>";
		}
		htmltext += "</table></center>";
		htmltext += htmlPage(player, "Footer");
		return htmltext;
	}

	public String pageSub(Player player, int pIndex)
	{
		String htmltext = "", title = "";
		final String lang = player.getLang();
		switch (pIndex)
		{
			case 1 :
				title = "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.SERVER_RATE") + "";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.RATE_XP") + " <font color=\"LEVEL\">" + String.valueOf(Config.RATE_XP_BY_LVL[player.getLevel()]) + "x</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.GROUP_RATE_XP") + " <font color=\"LEVEL\">" + String.valueOf(Config.RATE_PARTY_XP) + "x</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.RATE_SP") + " <font color=\"LEVEL\">" + String.valueOf(Config.RATE_SP_BY_LVL[player.getLevel()]) + "x</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.GROUP_RATE_SP") + " <font color=\"LEVEL\">" + String.valueOf(Config.RATE_PARTY_SP) + "x</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.RATE_DROP") + " <font color=\"LEVEL\">" + String.valueOf(Config.RATE_DROP_ITEMS) + "x</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.RATE_DROP_RB") + " <font color=\"LEVEL\">" + String.valueOf(Config.RATE_DROP_RAIDBOSS) + "x</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.RATE_SPOIL") + " <font color=\"LEVEL\">" + String.valueOf(Config.RATE_DROP_SPOIL) + "x</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.RATE_QUESTS") + " <font color=\"LEVEL\">" + String.valueOf(Config.RATE_QUEST_DROP) + "x</font><br>";
				break;
			case 2 :
				title = "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.CONTACT_INFO") + "";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.SERVER") + " <font color=\"LEVEL\">" + Config.SERVER_NAME + "</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.INFO") + " <font color=\"LEVEL\">" + SERVERINFO_NPC_DESCRIPTION + "</font><br>";
				htmltext += "<br>" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.ADMIN") + "<br1>";
				for (final String adm : SERVERINFO_NPC_ADM)
				{
					htmltext += "* <font color=\"LEVEL\">" + adm + "</font><br1>";
				}
				htmltext += "<br>" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.GM") + "<br1>";
				for (final String gm : SERVERINFO_NPC_GM)
				{
					htmltext += "* <font color=\"LEVEL\">" + gm + "</font><br1>";
				}
				htmltext += "<br>E-mail: <font color=\"LEVEL\">" + SERVERINFO_NPC_EMAIL + "</font><br>";
				if (!SERVERINFO_NPC_PHONE.equalsIgnoreCase("0"))
				{
					htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.PHONE") + " <font color=\"LEVEL\">" + SERVERINFO_NPC_PHONE + "</font><br>";
				}
				break;
			case 3 :
				title = "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.CUST_NPC") + "";
				for (final String npcCustom : SERVERINFO_NPC_CUSTOM)
				{
					htmltext += "* <font color=\"LEVEL\">[" + npcCustom + "]</font><br>";
				}
				break;
			case 4 :
				title = "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.SERVER_MODS") + "";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.BANK_SYSTEM") + " <font color=\"LEVEL\">" + convBoolean(player, Config.BANKING_SYSTEM_ENABLED) + "</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.VITALITY") + " <font color=\"LEVEL\">" + convBoolean(player, Config.ENABLE_VITALITY) + "</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.CHAMPIONS") + " <font color=\"LEVEL\">" + convBoolean(player, ChampionManager.getInstance().ENABLE_EXT_CHAMPION_MODE) + "</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.WEDDING") + " <font color=\"LEVEL\">" + convBoolean(player, Config.ALLOW_WEDDING) + "</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.OFFLINE_TRADE") + " <font color=\"LEVEL\">" + convBoolean(player, Config.OFFLINE_TRADE_ENABLE) + "</font><br>";
				htmltext += "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.OFFLINE_CRAFT") + " <font color=\"LEVEL\">" + convBoolean(player, Config.OFFLINE_CRAFT_ENABLE) + "</font><br>";
				break;
			default :
				title = "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.NOT_FOUND") + "";
				htmltext = "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.NOT_FOUND") + "";
				break;
		}
		return showText(player, title, htmltext);
	}

	public int disablePage(int page)
	{
		int p = 0;
		for (final String pIndex : SERVERINFO_NPC_DISABLE_PAGE)
		{
			if (pIndex.equalsIgnoreCase(String.valueOf(page)))
			{
				p = 1;
			}
		}
		return p;
	}

	public String convBoolean(Player player, Boolean b)
	{
		final String lang = player.getLang();
		String text = "<null>";
		if (b)
		{
			text = "<font color=\"00FF00\">" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.ONLINE") + "</font>";
		}
		else
		{
			text = "<font color=\"FF0000\">" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.OFFLINE") + "</font>";
		}
		return text;
	}

	public String convEnchantMax(Player player, int i)
	{
		final String lang = player.getLang();
		String text = "<null>";
		if (i == 0)
		{
			text = "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.NO_LIMIT") + "";
		}
		else
		{
			text = "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.UP_TO") + " +" + String.valueOf(i);
		}
		return text;
	}

	public String htmlPage(Player player, String op)
	{
		final String lang = player.getLang();
		String texto = "";
		if (op.equals("Title"))
		{
			texto += "<html><body><title>" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.SERVER_INFO") + "</title><center><br>" + "<b><font color=ffcc00>" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.SERVER_INFO") + "</font></b>" + "<br><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\"><br></center>";
		}
		else if (op.equals("Footer"))
		{
			texto += "<br><center><img src=\"L2UI_CH3.herotower_deco\" width=\"256\" height=\"32\"><br>" + "<br><font color=\"303030\">---</font></center></body></html>";
		}
		else
		{
			texto = "" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.NOT_FOUND") + "";
		}
		return texto;
	}

	public String button(String name, String event, int w, int h)
	{
		return "<button value=\"" + name + "\" action=\"bypass -h Quest ServerInfoNPC " + event + "\" " + "width=\"" + Integer.toString(w) + "\" height=\"" + Integer.toString(h) + "\" " + "back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
	}

	public String link(String name, String event, String color)
	{
		return "<a action=\"bypass -h Quest ServerInfoNPC " + event + "\">" + "<font color=\"" + color + "\">" + name + "</font></a>";
	}

	public String showText(Player player, String title, String text)
	{
		final String lang = player.getLang();
		String htmltext = "";
		htmltext += htmlPage(player, "Title");
		htmltext += "<center><font color=\"LEVEL\">" + title + "</font></center><br>";
		htmltext += text + "<br><br>";
		htmltext += "<center>" + button("" + ServerStorage.getInstance().getString(lang, "ServerInfoNPC.BACK") + "", "redirect main 1 0", 120, 20) + "</center>";
		htmltext += htmlPage(player, "Footer");
		return htmltext;
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

	public static void main(String[] args)
	{
		new ServerInfoNPC(-1, "ServerInfoNPC", "services");
	}
}
