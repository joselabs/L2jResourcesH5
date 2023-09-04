package com.l2jserver.gameserver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import com.l2jserver.Config;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class GameServerRestart
{
	private static GameServerRestart _instance = null;
	protected static final Logger _log = Logger.getLogger(GameServerRestart.class.getName());
	private Calendar NextRestart;
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	
	public static GameServerRestart getInstance()
	{
		if (_instance == null)
		{
			_instance = new GameServerRestart();
		}
		return _instance;
	}
	
	public String getRestartNextTime()
	{
		if (NextRestart.getTime() != null)
		{
			return format.format(NextRestart.getTime());
		}
		return "[Auto Restart]: Error on getRestartNextTime.";
	}
	
	public void StartCalculationOfNextRestartTime()
	{
		_log.info("[Auto Restart]: System activated.");
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0, timeL = 0;
			int count = 0;
			
			for (String timeOfDay : Config.AUTO_RESTART_INTERVAL)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(Calendar.SECOND, 00);
				
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				
				timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
				
				if (count == 0)
				{
					flush2 = timeL;
					NextRestart = testStartTime;
				}
				
				if (timeL < flush2)
				{
					flush2 = timeL;
					NextRestart = testStartTime;
				}
				
				count++;
			}
			
			_log.info("[Auto Restart]: Next restart time: " + NextRestart.getTime().toString());
			ThreadPoolManager.getInstance().scheduleGeneral(new StartRestartTask(), flush2);
		}
		catch (Exception e)
		{
			System.out.println("[Auto Restart]: The auto restart has problem with the config file, please, check and correct it.!");
		}
	}
	
	class StartRestartTask implements Runnable
	{
		@Override
		public void run()
		{
			_log.info("[Auto Restart]: Auto restart started.");
			Shutdown.getInstance().startAutoRestart(Config.AUTO_RESTART_TIME);
		}
	}
}
