package gr.sr.configsEngine.configs.impl;

import gr.sr.configsEngine.AbstractConfigs;

public class WeddingConfigs extends AbstractConfigs
{
	public static boolean L2JMOD_ALLOW_WEDDING;
	public static int L2JMOD_WEDDING_PRICE;
	public static boolean L2JMOD_WEDDING_PUNISH_INFIDELITY;
	public static boolean L2JMOD_WEDDING_TELEPORT;
	public static int L2JMOD_WEDDING_TELEPORT_PRICE;
	public static int L2JMOD_WEDDING_TELEPORT_DURATION;
	public static boolean L2JMOD_WEDDING_SAMESEX;
	public static boolean L2JMOD_WEDDING_FORMALWEAR;
	public static int L2JMOD_WEDDING_DIVORCE_COSTS;
	
	@Override
	public void loadConfigs()
	{
		loadFile("./config/sunrise/Wedding.ini");
		
		L2JMOD_ALLOW_WEDDING = getBoolean(_settings, _override, "AllowWedding", false);
		L2JMOD_WEDDING_PRICE = getInt(_settings, _override, "WeddingPrice", 250000000);
		L2JMOD_WEDDING_PUNISH_INFIDELITY = getBoolean(_settings, _override, "WeddingPunishInfidelity", true);
		L2JMOD_WEDDING_TELEPORT = getBoolean(_settings, _override, "WeddingTeleport", true);
		L2JMOD_WEDDING_TELEPORT_PRICE = getInt(_settings, _override, "WeddingTeleportPrice", 50000);
		L2JMOD_WEDDING_TELEPORT_DURATION = getInt(_settings, _override, "WeddingTeleportDuration", 60);
		L2JMOD_WEDDING_SAMESEX = getBoolean(_settings, _override, "WeddingAllowSameSex", false);
		L2JMOD_WEDDING_FORMALWEAR = getBoolean(_settings, _override, "WeddingFormalWear", true);
		L2JMOD_WEDDING_DIVORCE_COSTS = getInt(_settings, _override, "WeddingDivorceCosts", 20);
	}
	
	public static WeddingConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final WeddingConfigs _instance = new WeddingConfigs();
	}
}