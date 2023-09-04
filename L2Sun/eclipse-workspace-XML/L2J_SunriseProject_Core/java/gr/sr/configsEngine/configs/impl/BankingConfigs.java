package gr.sr.configsEngine.configs.impl;

import gr.sr.configsEngine.AbstractConfigs;

public class BankingConfigs extends AbstractConfigs
{
	public static boolean BANKING_SYSTEM_ENABLED;
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int BANKING_SYSTEM_ADENA;
	
	@Override
	public void loadConfigs()
	{
		loadFile("./config/sunrise/Banking.ini");
		
		BANKING_SYSTEM_ENABLED = getBoolean(_settings, _override, "BankingEnabled", false);
		BANKING_SYSTEM_GOLDBARS = getInt(_settings, _override, "BankingGoldbarCount", 1);
		BANKING_SYSTEM_ADENA = getInt(_settings, _override, "BankingAdenaCount", 500000000);
	}
	
	public static BankingConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final BankingConfigs _instance = new BankingConfigs();
	}
}