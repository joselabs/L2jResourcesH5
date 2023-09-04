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
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author mrTJO
 */
public class ExCubeGameAddPlayer extends ServerPacket
{
	private final Player _player;
	private final boolean _isRedTeam;
	
	/**
	 * Add Player To Minigame Waiting List
	 * @param player Player Instance
	 * @param isRedTeam Is Player from Red Team?
	 */
	public ExCubeGameAddPlayer(Player player, boolean isRedTeam)
	{
		_player = player;
		_isRedTeam = isRedTeam;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_BLOCK_UP_SET_LIST.writeId(this);
		writeInt(1);
		writeInt(0xffffffff);
		writeInt(_isRedTeam);
		writeInt(_player.getObjectId());
		writeString(_player.getName());
	}
}