/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.Clan.SubPledge;
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.network.ServerPackets;

public class PledgeShowMemberListAll extends ServerPacket
{
	private final Clan _clan;
	private final Player _player;
	private final ClanMember[] _members;
	private int _pledgeType;
	
	public PledgeShowMemberListAll(Clan clan, Player player)
	{
		_clan = clan;
		_player = player;
		_members = _clan.getMembers();
	}
	
	@Override
	public void write()
	{
		_pledgeType = 0;
		// FIXME: That's wrong on retail sends this whole packet few times (depending how much sub pledges it has)
		writePledge(0);
		for (SubPledge subPledge : _clan.getAllSubPledges())
		{
			_player.sendPacket(new PledgeReceiveSubPledgeCreated(subPledge, _clan));
		}
		for (ClanMember m : _members)
		{
			if (m.getPledgeType() == 0)
			{
				continue;
			}
			_player.sendPacket(new PledgeShowMemberListAdd(m));
		}
		// unless this is sent sometimes, the client doesn't recognise the player as the leader
		_player.sendPacket(new UserInfo(_player));
		_player.sendPacket(new ExBrExtraUserInfo(_player));
	}
	
	private void writePledge(int mainOrSubpledge)
	{
		ServerPackets.PLEDGE_SHOW_MEMBER_LIST_ALL.writeId(this);
		writeInt(mainOrSubpledge);
		writeInt(_clan.getId());
		writeInt(_pledgeType);
		writeString(_clan.getName());
		writeString(_clan.getLeaderName());
		writeInt(_clan.getCrestId()); // crest id .. is used again
		writeInt(_clan.getLevel());
		writeInt(_clan.getCastleId());
		writeInt(_clan.getHideoutId());
		writeInt(_clan.getFortId());
		writeInt(_clan.getRank());
		writeInt(_clan.getReputationScore());
		writeInt(0); // 0
		writeInt(0); // 0
		writeInt(_clan.getAllyId());
		writeString(_clan.getAllyName());
		writeInt(_clan.getAllyCrestId());
		writeInt(_clan.isAtWar()); // new c3
		writeInt(0); // Territory castle ID
		writeInt(_clan.getSubPledgeMembersCount(_pledgeType));
		for (ClanMember m : _members)
		{
			if (m.getPledgeType() != _pledgeType)
			{
				continue;
			}
			writeString(m.getName());
			writeInt(m.getLevel());
			writeInt(m.getClassId());
			final Player player = m.getPlayer();
			if (player != null)
			{
				writeInt(player.getAppearance().isFemale()); // no visible effect
				writeInt(player.getRace().ordinal()); // writeInt(1);
			}
			else
			{
				writeInt(1); // no visible effect
				writeInt(1); // writeInt(1);
			}
			writeInt(m.isOnline() ? m.getObjectId() : 0); // objectId = online 0 = offline
			writeInt(m.getSponsor() != 0);
		}
	}
}
