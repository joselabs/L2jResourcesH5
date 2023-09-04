package gr.sr.configsEngine.configs.impl;

import gr.sr.configsEngine.AbstractConfigs;

public class ScreenWelcomeMessageConfigs extends AbstractConfigs
{
	public static boolean WELCOME_MESSAGE_ENABLED;
	public static String WELCOME_MESSAGE_TEXT;
	public static int WELCOME_MESSAGE_TIME;
	
	@Override
	public void loadConfigs()
	{
		loadFile("./config/sunrise/ScreenWelcomeMessage.ini");
		
		WELCOME_MESSAGE_ENABLED = getBoolean(_settings, _override, "ScreenWelcomeMessageEnable", false);
		WELCOME_MESSAGE_TEXT = getString(_settings, _override, "ScreenWelcomeMessageText", "Welcome to L2JSunrise!");
		WELCOME_MESSAGE_TIME = getInt(_settings, _override, "ScreenWelcomeMessageTime", 10) * 1000;
	}
	
	public static ScreenWelcomeMessageConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ScreenWelcomeMessageConfigs _instance = new ScreenWelcomeMessageConfigs();
	}
}