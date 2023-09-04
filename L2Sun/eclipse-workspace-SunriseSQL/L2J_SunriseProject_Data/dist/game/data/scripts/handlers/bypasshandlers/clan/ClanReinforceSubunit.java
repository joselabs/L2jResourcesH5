package handlers.bypasshandlers.clan;

import java.util.StringTokenizer;

import l2r.gameserver.enums.ClanUnitTypes;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2VillageMasterInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author vGodFather
 */
public class ClanReinforceSubunit extends AbstractClan
{
	private static final String[] COMMANDS =
	{
		"clan_reinforce_subunit"
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
		if (st.countTokens() < 1)
		{
			return false;
		}
		
		if (activeChar.getClan() == null)
		{
			sendMessage(activeChar, 0);
			return false;
		}
		if (!activeChar.isClanLeader())
		{
			sendMessage(activeChar, 1);
			return false;
		}
		
		int pledgeType = Integer.parseInt(st.nextToken());
		int repCost = 0;
		if (pledgeType == -1)
		{
			_log.error("Player {} tried to reinforce his clans' academy! ban him!", activeChar.getName());
			return false;
		}
		if ((activeChar.getClan().getSubPledge(pledgeType) == null) || (activeChar.getClan().getSubPledge(pledgeType).getSize() > 20))
		{
			NpcHtmlMessage msg = new NpcHtmlMessage();
			msg.setFile(activeChar, activeChar.getHtmlPrefix(), "data/scripts/ai/npc/VillageMasters/Clan/reinforce-fail.htm");
			activeChar.sendPacket(msg);
			return false;
		}
		if (pledgeType >= ClanUnitTypes.SUBUNIT_KNIGHT1.getId())
		{
			if (activeChar.getClan().getLevel() < 9)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage();
				msg.setFile(activeChar, activeChar.getHtmlPrefix(), "data/scripts/ai/npc/VillageMasters/Clan/knights-lowlvl.htm");
				activeChar.sendPacket(msg);
				return false;
			}
			repCost = 5000;
		}
		else if (pledgeType >= ClanUnitTypes.SUBUNIT_ROYAL1.getId())
		{
			if (activeChar.getClan().getLevel() < 11)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage();
				msg.setFile(activeChar, activeChar.getHtmlPrefix(), "data/scripts/ai/npc/VillageMasters/Clan/royal-lowlvl.htm");
				activeChar.sendPacket(msg);
				return false;
			}
			repCost = 7500;
		}
		
		if ((repCost > 0) && (activeChar.getClan().getReputationScore() > repCost))
		{
			activeChar.getClan().takeReputationScore(repCost, true);
			activeChar.getClan().getSubPledge(pledgeType).setAsReinforced();
			activeChar.getClan().updateSubPledgeInDB(pledgeType);
			NpcHtmlMessage msg = new NpcHtmlMessage();
			msg.setFile(activeChar, activeChar.getHtmlPrefix(), "data/scripts/ai/npc/VillageMasters/Clan/reinforce-succ.htm");
			activeChar.sendPacket(msg);
			activeChar.getClan().broadcastClanStatus();
		}
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
