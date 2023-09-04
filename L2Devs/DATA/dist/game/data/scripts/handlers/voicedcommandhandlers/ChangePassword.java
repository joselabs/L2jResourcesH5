/*
 * Copyright © 2004-2019 L2JDevs
 * 
 * This file is part of L2JDevs.
 * 
 * L2JDevs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2JDevs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.voicedcommandhandlers;

import java.util.StringTokenizer;
import java.util.logging.Level;

import org.l2jdevs.gameserver.LoginServerThread;
import org.l2jdevs.gameserver.cache.HtmCache;
import org.l2jdevs.gameserver.datatables.LanguageData;
import org.l2jdevs.gameserver.handler.IVoicedCommandHandler;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Nik
 */
public class ChangePassword implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"changepassword"
	};
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (target != null)
		{
			final StringTokenizer st = new StringTokenizer(target);
			try
			{
				String curpass = null, newpass = null, repeatnewpass = null;
				if (st.hasMoreTokens())
				{
					curpass = st.nextToken();
				}
				if (st.hasMoreTokens())
				{
					newpass = st.nextToken();
				}
				if (st.hasMoreTokens())
				{
					repeatnewpass = st.nextToken();
				}
				
				if (!((curpass == null) || (newpass == null) || (repeatnewpass == null)))
				{
					if (!newpass.equals(repeatnewpass))
					{
						activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "password_no_repeated"));
						return false;
					}
					if (newpass.length() < 3)
					{
						activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "password_shorter"));
						return false;
					}
					if (newpass.length() > 30)
					{
						activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "password_longer"));
						return false;
					}
					
					LoginServerThread.getInstance().sendChangePassword(activeChar.getAccountName(), activeChar.getName(), curpass, newpass);
				}
				else
				{
					activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "password_invalid"));
					return false;
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "password_problem"));
				_log.log(Level.WARNING, "", e);
			}
		}
		else
		{
			// showHTML(activeChar);
			String html = HtmCache.getInstance().getHtm("en", "data/html/mods/ChangePassword.htm");
			if (html == null)
			{
				html = "<html><body><br><br><center><font color=LEVEL>404:</font> File Not Found</center></body></html>";
			}
			activeChar.sendPacket(new NpcHtmlMessage(html));
			return true;
		}
		return true;
	}
}
