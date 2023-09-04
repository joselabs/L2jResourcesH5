package gr.sr.configsEngine.configs.impl;

import gr.sr.configsEngine.AbstractConfigs;

public class OfflineTradeConfigs extends AbstractConfigs
{
	public static boolean OFFLINE_TRADE_ENABLE;
	public static boolean OFFLINE_CRAFT_ENABLE;
	public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
	public static boolean OFFLINE_MODE_NO_DAMAGE;
	public static boolean RESTORE_OFFLINERS;
	public static int OFFLINE_MAX_DAYS;
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	public static boolean OFFLINE_SET_NAME_COLOR;
	public static int OFFLINE_NAME_COLOR;
	public static boolean OFFLINE_FAME;
	
	@Override
	public void loadConfigs()
	{
		loadFile("./config/sunrise/OfflineTrade.ini");
		
		OFFLINE_TRADE_ENABLE = getBoolean(_settings, _override, "OfflineTradeEnable", false);
		OFFLINE_CRAFT_ENABLE = getBoolean(_settings, _override, "OfflineCraftEnable", false);
		OFFLINE_MODE_IN_PEACE_ZONE = getBoolean(_settings, _override, "OfflineModeInPaceZone", false);
		OFFLINE_MODE_NO_DAMAGE = getBoolean(_settings, _override, "OfflineModeNoDamage", false);
		OFFLINE_SET_NAME_COLOR = getBoolean(_settings, _override, "OfflineSetNameColor", false);
		OFFLINE_NAME_COLOR = Integer.decode("0x" + getString(_settings, _override, "OfflineNameColor", "808080"));
		OFFLINE_FAME = getBoolean(_settings, _override, "OfflineFame", true);
		RESTORE_OFFLINERS = getBoolean(_settings, _override, "RestoreOffliners", false);
		OFFLINE_MAX_DAYS = getInt(_settings, _override, "OfflineMaxDays", 10);
		OFFLINE_DISCONNECT_FINISHED = getBoolean(_settings, _override, "OfflineDisconnectFinished", true);
	}
	
	public static OfflineTradeConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final OfflineTradeConfigs _instance = new OfflineTradeConfigs();
	}
}