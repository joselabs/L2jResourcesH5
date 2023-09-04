package handlers.bypasshandlers.clan;

import java.util.StringTokenizer;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2VillageMasterInstance;

/**
 * @author vGodFather
 */
public class ClanRenameSubunit extends AbstractClan
{
	private static final String[] _command =
	{
		"clan_rename_subunit"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2VillageMasterInstance))
		{
			return false;
		}
		
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken(); // _command
		
		String clanName = "";
		
		if (st.countTokens() < 1)
		{
			return false;
		}
		String cmdParams = st.nextToken();
		if (st.countTokens() == 1)
		{
			clanName = st.nextToken();
		}
		
		renameSubPledge(activeChar, Integer.parseInt(cmdParams), clanName);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return _command;
	}
}
