/*
 * Copyright (C) 2004-2014 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.network.serverpackets;

import javolution.util.FastList;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;

public final class WareHouseDepositList extends L2GameServerPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 4;
	public static final int CASTLE = 3; // not sure
	public static final int FREIGHT = 1;
	private final long _playerAdena;
	private final FastList<L2ItemInstance> _items;
	/**
	 * <ul>
	 * <li>0x01-Private Warehouse</li>
	 * <li>0x02-Clan Warehouse</li>
	 * <li>0x03-Castle Warehouse</li>
	 * <li>0x04-Warehouse</li>
	 * </ul>
	 */
	private final int _whType;
	
	public WareHouseDepositList(L2PcInstance player, int type)
	{
		_whType = type;
		_playerAdena = player.getAdena();
		_items = new FastList<>();
		
		final boolean isPrivate = _whType == PRIVATE;
		for (L2ItemInstance temp : player.getInventory().getAvailableItems(true, isPrivate, false))
		{
			if ((temp != null) && temp.isDepositable(isPrivate))
			{
				_items.add(temp);
			}
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x41);
		writeH(_whType);
		writeQ(_playerAdena);
		final int count = _items.size();
		writeH(count);
		
		for (L2ItemInstance item : _items)
		{
			writeD(item.getObjectId());
			writeD(item.getDisplayId());
			writeD(item.getLocationSlot());
			writeQ(item.getCount());
			writeH(item.getItem().getType2());
			writeH(item.getCustomType1());
			writeH(item.isEquipped() ? 0x01 : 0x00);
			writeD(item.getItem().getBodyPart());
			writeH(item.getEnchantLevel());
			writeH(item.getCustomType2());
			if (item.isAugmented())
			{
				writeD(item.getAugmentation().getAugmentationId());
			}
			else
			{
				writeD(0x00);
			}
			writeD(item.getMana());
			writeD(item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) : -9999);
			
			writeH(item.getAttackElementType());
			writeH(item.getAttackElementPower());
			for (byte i = 0; i < 6; i++)
			{
				writeH(item.getElementDefAttr(i));
			}
			// Enchant Effects
			for (int op : item.getEnchantOptions())
			{
				writeH(op);
			}
			writeD(item.getObjectId());
		}
		_items.clear();
	}
}
