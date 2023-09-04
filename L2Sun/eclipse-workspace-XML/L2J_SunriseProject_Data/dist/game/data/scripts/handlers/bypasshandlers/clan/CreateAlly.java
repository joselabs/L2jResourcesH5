package handlers.bypasshandlers.clan;

import java.util.StringTokenizer;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2VillageMasterInstance;
import l2r.gameserver.network.SystemMessageId;

/**
 * @author vGodFather
 */
public class CreateAlly extends AbstractClan
{
	private static final String[] _command =
	{
		"create_ally"
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
		
		if (activeChar.getClan() == null)
		{
			activeChar.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CREATE_ALLIANCE);
		}
		else
		{
			activeChar.getClan().createAlly(activeChar, st.nextToken());
		}
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return _command;
	}
}
