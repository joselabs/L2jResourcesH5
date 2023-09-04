package l2r;

import gr.sr.configsEngine.AbstractConfigs;
import gr.sr.configsEngine.configs.impl.AntiFeedConfigs;
import gr.sr.configsEngine.configs.impl.BankingConfigs;
import gr.sr.configsEngine.configs.impl.DualboxChecksConfigs;
import gr.sr.configsEngine.configs.impl.MultilingualConfigs;
import gr.sr.configsEngine.configs.impl.OfflineTradeConfigs;
import gr.sr.configsEngine.configs.impl.PvpAnnounceConfigs;
import gr.sr.configsEngine.configs.impl.ScreenWelcomeMessageConfigs;
import gr.sr.configsEngine.configs.impl.WeddingConfigs;

/**
 * @author vGodFather
 */
public final class ConfigSunrise extends AbstractConfigs
{
	public static void load()
	{
		AntiFeedConfigs.getInstance().loadConfigs();
		BankingConfigs.getInstance().loadConfigs();
		DualboxChecksConfigs.getInstance().loadConfigs();
		MultilingualConfigs.getInstance().loadConfigs();
		OfflineTradeConfigs.getInstance().loadConfigs();
		PvpAnnounceConfigs.getInstance().loadConfigs();
		ScreenWelcomeMessageConfigs.getInstance().loadConfigs();
		WeddingConfigs.getInstance().loadConfigs();
	}
	
	public static ConfigSunrise getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ConfigSunrise _instance = new ConfigSunrise();
	}
}
