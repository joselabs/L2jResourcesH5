package handlers.bypasshandlers.clan;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2VillageMasterInstance;

/**
 * @author vGodFather
 */
public class LearnClanSkills extends AbstractClan
{
	private static final String[] _command =
	{
		"learn_clan_skills"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2VillageMasterInstance))
		{
			return false;
		}
		
		showPledgeSkillList(activeChar);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return _command;
	}
}
