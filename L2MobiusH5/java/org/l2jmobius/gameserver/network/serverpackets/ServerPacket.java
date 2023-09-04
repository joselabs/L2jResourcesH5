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

import org.l2jmobius.commons.network.WritablePacket;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;

/**
 * @author Mobius
 */
public abstract class ServerPacket extends WritablePacket
{
	protected static final int[] PAPERDOLL_ORDER =
	{
		Inventory.PAPERDOLL_UNDER,
		Inventory.PAPERDOLL_REAR,
		Inventory.PAPERDOLL_LEAR,
		Inventory.PAPERDOLL_NECK,
		Inventory.PAPERDOLL_RFINGER,
		Inventory.PAPERDOLL_LFINGER,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_CLOAK,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_HAIR,
		Inventory.PAPERDOLL_HAIR2,
		Inventory.PAPERDOLL_RBRACELET,
		Inventory.PAPERDOLL_LBRACELET,
		Inventory.PAPERDOLL_DECO1,
		Inventory.PAPERDOLL_DECO2,
		Inventory.PAPERDOLL_DECO3,
		Inventory.PAPERDOLL_DECO4,
		Inventory.PAPERDOLL_DECO5,
		Inventory.PAPERDOLL_DECO6,
		Inventory.PAPERDOLL_BELT
	};
	
	protected int[] getPaperdollOrder()
	{
		return PAPERDOLL_ORDER;
	}
	
	/**
	 * Construct a ServerPacket with an initial data size of 32 bytes.
	 */
	protected ServerPacket()
	{
		super(32);
	}
	
	/**
	 * Construct a ServerPacket with a given initial data size.
	 * @param initialSize
	 */
	protected ServerPacket(int initialSize)
	{
		super(initialSize);
	}
	
	private Player _player;
	
	/**
	 * @return the Player
	 */
	public Player getPlayer()
	{
		return _player;
	}
	
	/**
	 * @param player the Player to set.
	 */
	public void setPlayer(Player player)
	{
		_player = player;
	}
}
