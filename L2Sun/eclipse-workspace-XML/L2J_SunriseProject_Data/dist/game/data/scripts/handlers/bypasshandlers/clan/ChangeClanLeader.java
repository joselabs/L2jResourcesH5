package handlers.bypasshandlers.clan;

import java.util.StringTokenizer;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2VillageMasterInstance;

/**
 * @author vGodFather
 */
public class ChangeClanLeader extends AbstractClan
{
	private static final String[] _command =
	{
		"change_clan_leader",
		"cancel_clan_leader_change"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2VillageMasterInstance))
		{
			return false;
		}
		
		if (command.startsWith("change_clan_leader"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // _command
			if (!st.hasMoreTokens())
			{
				return false;
			}
			
			String targetString = st.nextToken();
			changeClaLeader(activeChar, targetString);
		}
		else if (command.startsWith("cancel_clan_leader_change"))
		{
			cancelClanLeaderChange(activeChar, activeChar.getClanId());
		}
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return _command;
	}
}
