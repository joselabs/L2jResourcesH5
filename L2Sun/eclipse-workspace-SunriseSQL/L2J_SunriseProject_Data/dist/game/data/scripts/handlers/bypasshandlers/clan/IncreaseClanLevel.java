package handlers.bypasshandlers.clan;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2VillageMasterInstance;
import l2r.gameserver.network.serverpackets.MagicSkillLaunched;
import l2r.gameserver.network.serverpackets.MagicSkillUse;

/**
 * @author vGodFather
 */
public class IncreaseClanLevel extends AbstractClan
{
	private static final String[] _command =
	{
		"increase_clan_level"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2VillageMasterInstance))
		{
			return false;
		}
		
		if (activeChar.getClan().levelUpClan(activeChar))
		{
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, 5103, 1, 0, 0));
			activeChar.broadcastPacket(new MagicSkillLaunched(activeChar, 5103, 1));
		}
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return _command;
	}
}
