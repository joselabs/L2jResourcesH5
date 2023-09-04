package gr.sr.configsEngine.configs.impl;

import gr.sr.configsEngine.AbstractConfigs;

public class AntiFeedConfigs extends AbstractConfigs
{
	public static boolean ANTIFEED_ENABLE;
	public static boolean ANTIFEED_DUALBOX;
	public static boolean ANTIFEED_DISCONNECTED_AS_DUALBOX;
	public static int ANTIFEED_INTERVAL;
	
	@Override
	public void loadConfigs()
	{
		loadFile("./config/sunrise/AntiFeed.ini");
		
		ANTIFEED_ENABLE = getBoolean(_settings, _override, "AntiFeedEnable", false);
		ANTIFEED_DUALBOX = getBoolean(_settings, _override, "AntiFeedDualbox", true);
		ANTIFEED_DISCONNECTED_AS_DUALBOX = getBoolean(_settings, _override, "AntiFeedDisconnectedAsDualbox", true);
		ANTIFEED_INTERVAL = getInt(_settings, _override, "AntiFeedInterval", 120) * 1000;
	}
	
	public static AntiFeedConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AntiFeedConfigs _instance = new AntiFeedConfigs();
	}
}