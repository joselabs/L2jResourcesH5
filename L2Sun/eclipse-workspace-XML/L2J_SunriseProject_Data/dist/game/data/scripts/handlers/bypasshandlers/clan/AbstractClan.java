package handlers.bypasshandlers.clan;

import java.util.List;

import l2r.Config;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.enums.ClanUnitTypes;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.handler.IBypassHandler;
import l2r.gameserver.instancemanager.CastleManager;
import l2r.gameserver.instancemanager.FortManager;
import l2r.gameserver.instancemanager.FortSiegeManager;
import l2r.gameserver.instancemanager.SiegeManager;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.L2Clan.SubPledge;
import l2r.gameserver.model.L2ClanMember;
import l2r.gameserver.model.L2SkillLearn;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.AcquireSkillType;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.entity.Fort;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.AcquireSkillList;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.Util;

/**
 * @author vGodFather
 */
public abstract class AbstractClan implements IBypassHandler
{
	protected final void createSubPledge(L2PcInstance player, String clanName, String leaderName, int pledgeType, int minClanLvl)
	{
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		final L2Clan clan = player.getClan();
		if (clan.getLevel() < minClanLvl)
		{
			if (pledgeType == ClanUnitTypes.SUBUNIT_ACADEMY.getId())
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN_ACADEMY);
			}
			else
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_MILITARY_UNIT);
			}
			
			return;
		}
		if (!isValidName(clanName) || (clanName.length() < 2))
		{
			player.sendPacket(SystemMessageId.CLAN_NAME_INCORRECT);
			return;
		}
		if (clanName.length() > ClanTable.CLAN_NAME_MAX_LENGHT)
		{
			player.sendPacket(SystemMessageId.CLAN_NAME_TOO_LONG);
			return;
		}
		
		for (L2Clan tempClan : ClanTable.getInstance().getClans())
		{
			if (tempClan.getSubPledge(clanName) != null)
			{
				if (pledgeType == ClanUnitTypes.SUBUNIT_ACADEMY.getId())
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_EXISTS);
					sm.addString(clanName);
					player.sendPacket(sm);
				}
				else
				{
					player.sendPacket(SystemMessageId.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME);
				}
				
				return;
			}
		}
		
		if (pledgeType != ClanUnitTypes.SUBUNIT_ACADEMY.getId())
		{
			if ((clan.getClanMember(leaderName) == null) || (clan.getClanMember(leaderName).getPledgeType() != 0))
			{
				if (pledgeType >= ClanUnitTypes.SUBUNIT_KNIGHT1.getId())
				{
					player.sendPacket(SystemMessageId.CAPTAIN_OF_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED);
				}
				else if (pledgeType >= ClanUnitTypes.SUBUNIT_ROYAL1.getId())
				{
					player.sendPacket(SystemMessageId.CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED);
				}
				
				return;
			}
		}
		
		final int leaderId = pledgeType != ClanUnitTypes.SUBUNIT_ACADEMY.getId() ? clan.getClanMember(leaderName).getObjectId() : 0;
		
		if (clan.createSubPledge(player, pledgeType, leaderId, clanName) == null)
		{
			return;
		}
		
		SystemMessage sm;
		if (pledgeType == ClanUnitTypes.SUBUNIT_ACADEMY.getId())
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED);
			sm.addString(player.getClan().getName());
		}
		else if (pledgeType >= ClanUnitTypes.SUBUNIT_KNIGHT1.getId())
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED);
			sm.addString(player.getClan().getName());
		}
		else if (pledgeType >= ClanUnitTypes.SUBUNIT_ROYAL1.getId())
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED);
			sm.addString(player.getClan().getName());
		}
		else
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_CREATED);
		}
		player.sendPacket(sm);
		
		if (pledgeType != ClanUnitTypes.SUBUNIT_ACADEMY.getId())
		{
			final L2ClanMember leaderSubPledge = clan.getClanMember(leaderName);
			final L2PcInstance leaderPlayer = leaderSubPledge.getPlayerInstance();
			if (leaderPlayer != null)
			{
				leaderPlayer.setPledgeClass(L2ClanMember.calculatePledgeClass(leaderPlayer));
				leaderPlayer.sendUserInfo(true);
			}
		}
	}
	
	protected static final void renameSubPledge(L2PcInstance player, int pledgeType, String pledgeName)
	{
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		final L2Clan clan = player.getClan();
		final SubPledge subPledge = player.getClan().getSubPledge(pledgeType);
		
		if (subPledge == null)
		{
			player.sendMessage("Pledge don't exists.");
			return;
		}
		if (!Util.isAlphaNumeric(pledgeName) || (2 > pledgeName.length()))
		{
			player.sendPacket(SystemMessageId.CLAN_NAME_INCORRECT);
			return;
		}
		if (pledgeName.length() > 16)
		{
			player.sendPacket(SystemMessageId.CLAN_NAME_TOO_LONG);
			return;
		}
		
		subPledge.setName(pledgeName);
		clan.updateSubPledgeInDB(subPledge.getId());
		clan.broadcastClanStatus();
		player.sendMessage("Pledge name changed.");
	}
	
	protected final void dissolveClan(L2PcInstance player, int clanId)
	{
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		final L2Clan clan = player.getClan();
		if (clan.getAllyId() != 0)
		{
			player.sendPacket(SystemMessageId.CANNOT_DISPERSE_THE_CLANS_IN_ALLY);
			return;
		}
		if (clan.isAtWar())
		{
			player.sendPacket(SystemMessageId.CANNOT_DISSOLVE_WHILE_IN_WAR);
			return;
		}
		if ((clan.getCastleId() != 0) || (clan.getHideoutId() != 0) || (clan.getFortId() != 0))
		{
			player.sendPacket(SystemMessageId.CANNOT_DISSOLVE_WHILE_OWNING_CLAN_HALL_OR_CASTLE);
			return;
		}
		
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			if (SiegeManager.getInstance().checkIsRegistered(clan, castle.getResidenceId()))
			{
				player.sendPacket(SystemMessageId.CANNOT_DISSOLVE_WHILE_IN_SIEGE);
				return;
			}
		}
		for (Fort fort : FortManager.getInstance().getForts())
		{
			if (FortSiegeManager.getInstance().checkIsRegistered(clan, fort.getResidenceId()))
			{
				player.sendPacket(SystemMessageId.CANNOT_DISSOLVE_WHILE_IN_SIEGE);
				return;
			}
		}
		
		if (player.isInsideZone(ZoneIdType.SIEGE))
		{
			player.sendPacket(SystemMessageId.CANNOT_DISSOLVE_WHILE_IN_SIEGE);
			return;
		}
		if (clan.getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(SystemMessageId.DISSOLUTION_IN_PROGRESS);
			return;
		}
		
		// vGodFather Minimum time is 5 minutes
		long dissolutionTime = (Config.ALT_CLAN_DISSOLVE_DAYS * 86400000L) > 0 ? Config.ALT_CLAN_DISSOLVE_DAYS * 86400000L : 300000;
		clan.setDissolvingExpiryTime(System.currentTimeMillis() + dissolutionTime); // 24*60*60*1000 = 86400000
		clan.updateClanInDB();
		
		// The clan leader should take the XP penalty of a full death.
		player.calculateDeathExpPenalty(null, false);
		ClanTable.getInstance().scheduleRemoveClan(clan.getId());
	}
	
	protected final void recoverClan(L2PcInstance player, int clanId)
	{
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		final L2Clan clan = player.getClan();
		clan.setDissolvingExpiryTime(0);
		clan.updateClanInDB();
	}
	
	/**
	 * this displays PledgeSkillList to the player.
	 * @param player
	 */
	protected static final void showPledgeSkillList(L2PcInstance player)
	{
		if (!player.isClanLeader())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage();
			html.setFile(player, player.getHtmlPrefix(), "data/html/villagemaster/NotClanLeader.htm");
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final List<L2SkillLearn> skills = SkillTreesData.getInstance().getAvailablePledgeSkills(player.getClan());
		final AcquireSkillList asl = new AcquireSkillList(AcquireSkillType.PLEDGE);
		int counts = 0;
		
		for (L2SkillLearn s : skills)
		{
			asl.addSkill(s.getSkillId(), s.getSkillLevel(), s.getSkillLevel(), s.getLevelUpSp(), s.getSocialClass().ordinal());
			counts++;
		}
		
		if (counts == 0)
		{
			if (player.getClan().getLevel() < 8)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1);
				if (player.getClan().getLevel() < 5)
				{
					sm.addInt(5);
				}
				else
				{
					sm.addInt(player.getClan().getLevel() + 1);
				}
				player.sendPacket(sm);
			}
			else
			{
				final NpcHtmlMessage html = new NpcHtmlMessage();
				html.setFile(player, player.getHtmlPrefix(), "data/html/villagemaster/NoMoreSkills.htm");
				player.sendPacket(html);
			}
		}
		else
		{
			player.sendPacket(asl);
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	protected void changeClaLeader(L2PcInstance activeChar, String targetString)
	{
		if (activeChar.getClan() == null)
		{
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			sendMessage(activeChar, 0);
			return;
		}
		if (!activeChar.isClanLeader())
		{
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			sendMessage(activeChar, 1);
			return;
		}
		if (activeChar.getName().equalsIgnoreCase(targetString))
		{
			return;
		}
		
		if (activeChar.isFlying() && Config.ALT_CLAN_LEADER_INSTANT_ACTIVATION)
		{
			activeChar.dismount();
			return;
		}
		
		final L2Clan clan = activeChar.getClan();
		
		final L2ClanMember member = clan.getClanMember(targetString);
		if (member == null)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DOES_NOT_EXIST);
			sm.addString(targetString);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}
		if (!member.isOnline())
		{
			activeChar.sendPacket(SystemMessageId.INVITED_USER_NOT_ONLINE);
			return;
		}
		if (member.getPlayerInstance().isAcademyMember())
		{
			activeChar.sendPacket(SystemMessageId.RIGHT_CANT_TRANSFERRED_TO_ACADEMY_MEMBER);
			return;
		}
		if (activeChar.getSiegeState() != 0)
		{
			activeChar.sendMessage("You cannot change Clan Leader during a siege.");
			return;
		}
		
		if (Config.ALT_CLAN_LEADER_INSTANT_ACTIVATION)
		{
			clan.setNewLeader(member);
		}
		else
		{
			final NpcHtmlMessage msg = new NpcHtmlMessage();
			if (clan.getNewLeaderId() == 0)
			{
				clan.setNewLeaderId(member.getObjectId(), true);
				msg.setFile(activeChar, activeChar.getHtmlPrefix(), "data/scripts/ai/npc/VillageMasters/Clan/9000-07-success.htm");
			}
			else
			{
				msg.setFile(activeChar, activeChar.getHtmlPrefix(), "data/scripts/ai/npc/VillageMasters/Clan/9000-07-in-progress.htm");
			}
			activeChar.sendPacket(msg);
		}
	}
	
	protected void cancelClanLeaderChange(L2PcInstance player, int clanId)
	{
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		final L2Clan clan = player.getClan();
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		if (clan.getNewLeaderId() != 0)
		{
			clan.setNewLeaderId(0, true);
			msg.setFile(player, player.getHtmlPrefix(), "data/scripts/ai/npc/VillageMasters/Clan/9000-07-canceled.htm");
		}
		else
		{
			msg.setHtml("<html><body>You don't have clan leader delegation applications submitted yet!</body></html>");
		}
		
		player.sendPacket(msg);
	}
	
	protected void sendMessage(L2PcInstance player, int msgId)
	{
		if (msgId == 0)
		{
			NpcHtmlMessage msg = new NpcHtmlMessage();
			msg.setHtml("<html><body><br><br>You do not belong to any Clan.</body></html>");
			player.sendPacket(msg);
		}
		else if (msgId == 1)
		{
			NpcHtmlMessage msg = new NpcHtmlMessage();
			msg.setHtml("<html><body>You are not a Clan Leader.</body></html>");
			player.sendPacket(msg);
		}
	}
	
	private static boolean isValidName(String name)
	{
		return Config.CLAN_NAME_TEMPLATE.matcher(name).matches();
	}
}