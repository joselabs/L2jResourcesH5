package gr.sr.configsEngine.configs.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import l2r.util.StringUtil;

import gr.sr.configsEngine.AbstractConfigs;

public class DualboxChecksConfigs extends AbstractConfigs
{
	public static int DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP;
	public static int DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP;
	public static Map<Integer, Integer> DUALBOX_CHECK_WHITELIST;
	
	@Override
	public void loadConfigs()
	{
		loadFile("./config/sunrise/DualboxCheck.ini");
		
		DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP = getInt(_settings, _override, "DualboxCheckMaxOlympiadParticipantsPerIP", 0);
		DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP = getInt(_settings, _override, "DualboxCheckMaxL2EventParticipantsPerIP", 0);
		String[] dualboxCheckWhiteList = getString(_settings, _override, "DualboxCheckWhitelist", "127.0.0.1,0").split(";");
		DUALBOX_CHECK_WHITELIST = new HashMap<>(dualboxCheckWhiteList.length);
		for (String entry : dualboxCheckWhiteList)
		{
			String[] entrySplit = entry.split(",");
			if (entrySplit.length != 2)
			{
				_log.warn(StringUtil.concat("DualboxCheck[Config.load()]: invalid config property -> DualboxCheckWhitelist \"", entry, "\""));
			}
			else
			{
				try
				{
					int num = Integer.parseInt(entrySplit[1]);
					num = (num == 0) ? -1 : num;
					DUALBOX_CHECK_WHITELIST.put(InetAddress.getByName(entrySplit[0]).hashCode(), num);
				}
				catch (UnknownHostException e)
				{
					_log.warn(StringUtil.concat("DualboxCheck[Config.load()]: invalid address -> DualboxCheckWhitelist \"", entrySplit[0], "\""));
				}
				catch (NumberFormatException e)
				{
					_log.warn(StringUtil.concat("DualboxCheck[Config.load()]: invalid number -> DualboxCheckWhitelist \"", entrySplit[1], "\""));
				}
			}
		}
	}
	
	public static DualboxChecksConfigs getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DualboxChecksConfigs _instance = new DualboxChecksConfigs();
	}
}