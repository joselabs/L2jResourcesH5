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

import java.util.List;

import org.l2jmobius.gameserver.instancemanager.FortManager;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.siege.Fort;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author KenM
 */
public class ExShowFortressInfo extends ServerPacket
{
	public static final ExShowFortressInfo STATIC_PACKET = new ExShowFortressInfo();
	
	private ExShowFortressInfo()
	{
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_SHOW_FORTRESS_INFO.writeId(this);
		final List<Fort> forts = FortManager.getInstance().getForts();
		writeInt(forts.size());
		for (Fort fort : forts)
		{
			final Clan clan = fort.getOwnerClan();
			writeInt(fort.getResidenceId());
			writeString(clan != null ? clan.getName() : "");
			writeInt(fort.getSiege().isInProgress());
			// Time of possession
			writeInt(fort.getOwnedTime());
		}
	}
}
