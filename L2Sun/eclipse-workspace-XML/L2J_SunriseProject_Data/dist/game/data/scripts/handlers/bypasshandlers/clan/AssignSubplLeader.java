package handlers.bypasshandlers.clan;

import java.util.StringTokenizer;

import l2r.gameserver.enums.ClanUnitTypes;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.L2Clan.SubPledge;
import l2r.gameserver.model.L2ClanMember;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2VillageMasterInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ExBrExtraUserInfo;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.network.serverpackets.UserInfo;

/**
 * @author vGodFather
 */
public class AssignSubplLeader extends AbstractClan
{
	private static final String[] _command =
	{
		"assign_subpl_leader"
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
		
		if (st.countTokens() < 2)
		{
			return false;
		}
		
		String clanName = st.nextToken();
		String leaderName = st.nextToken();
		
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
		if (leaderName.length() > 16)
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_CHARACTER_NAME_TRY_AGAIN);
			return false;
		}
		if (activeChar.getName().equals(leaderName))
		{
			activeChar.sendPacket(SystemMessageId.CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED);
			return false;
		}
		final L2Clan clan = activeChar.getClan();
		final SubPledge subPledge = activeChar.getClan().getSubPledge(clanName);
		
		if ((null == subPledge) || (subPledge.getId() == ClanUnitTypes.SUBUNIT_ACADEMY.getId()))
		{
			activeChar.sendPacket(SystemMessageId.CLAN_NAME_INCORRECT);
			return false;
		}
		if ((clan.getClanMember(leaderName) == null) || (clan.getClanMember(leaderName).getPledgeType() != 0))
		{
			if (subPledge.getId() >= ClanUnitTypes.SUBUNIT_KNIGHT1.getId())
			{
				activeChar.sendPacket(SystemMessageId.CAPTAIN_OF_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED);
			}
			else if (subPledge.getId() >= ClanUnitTypes.SUBUNIT_ROYAL1.getId())
			{
				activeChar.sendPacket(SystemMessageId.CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED);
			}
			return false;
		}
		
		subPledge.setLeaderId(clan.getClanMember(leaderName).getObjectId());
		clan.updateSubPledgeInDB(subPledge.getId());
		final L2ClanMember leaderSubPledge = clan.getClanMember(leaderName);
		final L2PcInstance leaderPlayer = leaderSubPledge.getPlayerInstance();
		if (leaderPlayer != null)
		{
			leaderPlayer.setPledgeClass(L2ClanMember.calculatePledgeClass(leaderPlayer));
			leaderPlayer.sendPacket(new UserInfo(leaderPlayer));
			leaderPlayer.sendPacket(new ExBrExtraUserInfo(leaderPlayer));
		}
		
		clan.broadcastClanStatus();
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_SELECTED_AS_CAPTAIN_OF_S2);
		sm.addString(leaderName);
		sm.addString(clanName);
		clan.broadcastToOnlineMembers(sm);
		sm = null;
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return _command;
	}
}
