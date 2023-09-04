/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
/**
	coded by Balancer
	balancer@balancer.ru
	http://balancer.ru

	version 0.1, 2005-06-06
*/

package net.sf.l2j.gameserver.lib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public class Log
{
	private static final Logger _log = Logger.getLogger(Log.class.getName());
	
	public static final void add(String text, String cat)
	{
		
		/*
		 * Logger _log = logs.get(cat); if(_log == null) { _log =
		 * Logger.getLogger(cat); logs.put(cat, _log); }
		 */

		String date = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date());
		
		new File("log/game").mkdirs();
		FileWriter save = null;
		
		try
		{
			File file = new File("log/game/" + (cat != null ? cat : "_all")
			        + ".txt");
			// file.getAbsolutePath().mkdirs();
			save = new FileWriter(file, true);
			String out = "[" + date + "] '---': " + text + "\n"; // "+char_name()+"
			save.write(out);
		}
		catch (IOException e)
		{
			_log.warning("saving chat log failed: " + e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				save.close();
			}
			catch (Exception e)
			{
			}
		}
		
		if (cat != null)
			add(text, null);
		
	}
	
	@Deprecated
	public static final void addEvent(L2PcInstance pc, String text)
	{
		String date = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date());
		String filedate = (new SimpleDateFormat("yyMMdd_H")).format(new Date());
		
		new File("log/game").mkdirs();
		File file = new File("log/game/actions_" + filedate + ".txt");
		FileWriter save = null;
		
		try
		{
			save = new FileWriter(file, true);
			String out = "[" + date + "] '<" + pc.getName() + ">': " + text + "\n"; // "+char_name()+"
			save.write(out);
		}
		catch (IOException e)
		{
			_log.warning("saving actions log failed: " + e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				save.close();
			}
			catch (Exception e1)
			{
			}
		}
	}
	
	@Deprecated
	public static final void Assert(boolean exp)
	{
		Assert(exp, "");
	}
	
	public static final void Assert(boolean exp, String cmt)
	{
		if (exp || !Config.ASSERT)
			return;
		
		_log.warning("Assertion error [" + cmt + "]");
		Thread.dumpStack();
	}
}
