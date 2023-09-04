package gr.sr.configsEngine.configs.impl;

import java.util.ArrayList;
import java.util.List;

import gr.sr.configsEngine.AbstractConfigs;

public class MultilingualConfigs extends AbstractConfigs
{
	public static boolean MULTILANG_ENABLE;
	public static List<String> MULTILANG_ALLOWED = new ArrayList<>();
	public static String MULTILANG_DEFAULT;
	public static boolean MULTILANG_VOICED_ALLOW;
	public static boolean MULTILANG_SM_ENABLE;
	public static List<String> MULTILANG_SM_ALLOWED = new ArrayList<>();
	public static boolean MULTILANG_NS_ENABLE;
	public static List<String> MULTILANG_NS_ALLOWED = new ArrayList<>();
	
	@Override
	public void loadConfigs()
	{
		loadFile("./config/sunrise/MultilingualSupport.ini");
		
		MULTILANG_DEFAULT = getString(_settings, _override, "MultiLangDefault", "en");
		MULTILANG_ENABLE = getBoolean(_settings, _override, "MultiLangEnable", false);
		String[] allowed = getString(_settings, _override, "MultiLangAllowed", MULTILANG_DEFAULT).split(";");
		MULTILANG_ALLOWED = new ArrayList<>(allowed.length);
		for (String lang : allowed)
		{
			MULTILANG_ALLOWED.add(lang);
		}
		
		if (!MULTILANG_ALLOWED.contains(MULTILANG_DEFAULT))
		{
			_log.warn("MultiLang[Config.load()]: default language: " + MULTILANG_DEFAULT + " is not in allowed list !");
		}
		
		MULTILANG_VOICED_ALLOW = getBoolean(_settings, _override, "MultiLangVoiceCommand", true);
		MULTILANG_SM_ENABLE = getBoolean(_settings, _override, "MultiLangSystemMessageEnable", false);
		allowed = getString(_settings, _override, "MultiLangSystemMessageAllowed", "").split(";");
		MULTILANG_SM_ALLOWED = new ArrayList<>(allowed.length);
		for (String lang : allowed)
		{
			if (!lang.isEmpty())
			{
				MULTILANG_SM_ALLOWED.add(lang);
			}
		}
		MULTILANG_NS_ENABLE = getBoolean(_settings, _override, "MultiLangNpcStringEnable", false);
		allowed = getString(_settings, _override, "MultiLangNpcStringAllowed", "").split(";");
		MULTILANG_NS_ALLOWED = new ArrayList<>(allowed.length);
		for (String lang : allowed)
		{
			if (!lang.isEmpty())
			{
				MULTILANG_NS_ALLOWED.add(lang);
			}
		}
	}
	
	public static MultilingualConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final MultilingualConfigs _instance = new MultilingualConfigs();
	}
}