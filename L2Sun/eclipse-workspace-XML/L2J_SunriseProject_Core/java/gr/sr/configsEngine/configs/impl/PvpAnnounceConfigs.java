package gr.sr.configsEngine.configs.impl;

import gr.sr.configsEngine.AbstractConfigs;

public class PvpAnnounceConfigs extends AbstractConfigs
{
	public static boolean ANNOUNCE_PK_PVP;
	public static boolean ANNOUNCE_PK_PVP_NORMAL_MESSAGE;
	public static String ANNOUNCE_PK_MSG;
	public static String ANNOUNCE_PVP_MSG;
	
	@Override
	public void loadConfigs()
	{
		loadFile("./config/sunrise/PvpAnnounce.ini");
		
		ANNOUNCE_PK_PVP = getBoolean(_settings, _override, "AnnouncePkPvP", false);
		ANNOUNCE_PK_PVP_NORMAL_MESSAGE = getBoolean(_settings, _override, "AnnouncePkPvPNormalMessage", true);
		ANNOUNCE_PK_MSG = getString(_settings, _override, "AnnouncePkMsg", "$killer has slaughtered $target");
		ANNOUNCE_PVP_MSG = getString(_settings, _override, "AnnouncePvpMsg", "$killer has defeated $target");
	}
	
	public static PvpAnnounceConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PvpAnnounceConfigs _instance = new PvpAnnounceConfigs();
	}
}