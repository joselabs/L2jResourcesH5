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

import org.l2jmobius.gameserver.instancemanager.TerritoryWarManager;
import org.l2jmobius.gameserver.instancemanager.TerritoryWarManager.Territory;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author GodKratos
 */
public class ExShowDominionRegistry extends ServerPacket
{
	private static final int MINID = 80;
	
	private final int _castleId;
	private int _clanReq = 0;
	private int _mercReq = 0;
	private boolean _isMercRegistered = false;
	private boolean _isClanRegistered = false;
	private int _warTime = (int) (System.currentTimeMillis() / 1000);
	private final int _currentTime = (int) (System.currentTimeMillis() / 1000);
	
	public ExShowDominionRegistry(int castleId, Player player)
	{
		_castleId = castleId;
		if (TerritoryWarManager.getInstance().getRegisteredClans(castleId) != null)
		{
			_clanReq = TerritoryWarManager.getInstance().getRegisteredClans(castleId).size();
			if (player.getClan() != null)
			{
				_isClanRegistered = (TerritoryWarManager.getInstance().getRegisteredClans(castleId).contains(player.getClan()));
			}
		}
		if (TerritoryWarManager.getInstance().getRegisteredMercenaries(castleId) != null)
		{
			_mercReq = TerritoryWarManager.getInstance().getRegisteredMercenaries(castleId).size();
			_isMercRegistered = (TerritoryWarManager.getInstance().getRegisteredMercenaries(castleId).contains(player.getObjectId()));
		}
		_warTime = (int) (TerritoryWarManager.getInstance().getTWStartTimeInMillis() / 1000);
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_SHOW_DOMINION_REGISTRY.writeId(this);
		writeInt(MINID + _castleId); // Current Territory Id
		if (TerritoryWarManager.getInstance().getTerritory(_castleId) == null)
		{
			// something is wrong
			writeString("No Owner"); // Owners Clan
			writeString("No Owner"); // Owner Clan Leader
			writeString("No Ally"); // Owner Alliance
		}
		else
		{
			final Clan clan = TerritoryWarManager.getInstance().getTerritory(_castleId).getOwnerClan();
			if (clan == null)
			{
				// something is wrong
				writeString("No Owner"); // Owners Clan
				writeString("No Owner"); // Owner Clan Leader
				writeString("No Ally"); // Owner Alliance
			}
			else
			{
				writeString(clan.getName()); // Owners Clan
				writeString(clan.getLeaderName()); // Owner Clan Leader
				writeString(clan.getAllyName()); // Owner Alliance
			}
		}
		writeInt(_clanReq); // Clan Request
		writeInt(_mercReq); // Merc Request
		writeInt(_warTime); // War Time
		writeInt(_currentTime); // Current Time
		writeInt(_isClanRegistered); // is Cancel clan registration
		writeInt(_isMercRegistered); // is Cancel mercenaries registration
		writeInt(1); // unknown
		final List<Territory> territoryList = TerritoryWarManager.getInstance().getAllTerritories();
		writeInt(territoryList.size()); // Territory Count
		for (Territory t : territoryList)
		{
			writeInt(t.getTerritoryId()); // Territory Id
			writeInt(t.getOwnedWardIds().size()); // Emblem Count
			for (int i : t.getOwnedWardIds())
			{
				writeInt(i); // Emblem ID - should be in for loop for emblem count
			}
		}
	}
}
