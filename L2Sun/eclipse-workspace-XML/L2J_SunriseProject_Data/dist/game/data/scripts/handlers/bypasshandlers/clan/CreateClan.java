package handlers.bypasshandlers.clan;

import java.util.StringTokenizer;

import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.handler.IBypassHandler;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2VillageMasterInstance;

/**
 * @author vGodFather
 */
public class CreateClan implements IBypassHandler
{
	private static final String[] _command =
	{
		"create_clan"
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
		if (!st.hasMoreTokens())
		{
			return false;
		}
		
		ClanTable.getInstance().createClan(activeChar, st.nextToken());
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return _command;
	}
}
