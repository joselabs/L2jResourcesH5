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

import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.data.xml.FakePlayerData;
import org.l2jmobius.gameserver.enums.Sex;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.holders.FakePlayerHolder;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class FakePlayerInfo extends ServerPacket
{
	private final Npc _npc;
	private final int _objId;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	private final int _mAtkSpd;
	private final int _pAtkSpd;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final double _moveMultiplier;
	private final float _attackSpeedMultiplier;
	private final FakePlayerHolder _fpcHolder;
	private final Clan _clan;
	
	public FakePlayerInfo(Npc npc)
	{
		super(256);
		
		_npc = npc;
		_objId = npc.getObjectId();
		_x = npc.getX();
		_y = npc.getY();
		_z = npc.getZ();
		_heading = npc.getHeading();
		_mAtkSpd = npc.getMAtkSpd();
		_pAtkSpd = (int) npc.getPAtkSpd();
		_attackSpeedMultiplier = npc.getAttackSpeedMultiplier();
		_moveMultiplier = npc.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(npc.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(npc.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(npc.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(npc.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = npc.isFlying() ? _runSpd : 0;
		_flyWalkSpd = npc.isFlying() ? _walkSpd : 0;
		_fpcHolder = FakePlayerData.getInstance().getInfo(npc.getId());
		_clan = ClanTable.getInstance().getClan(_fpcHolder.getClanId());
	}
	
	@Override
	public void write()
	{
		ServerPackets.CHAR_INFO.writeId(this);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(0); // vehicleId
		writeInt(_objId);
		writeString(_npc.getName());
		writeInt(_npc.getRace().ordinal());
		writeInt(_npc.getTemplate().getSex() == Sex.FEMALE);
		writeInt(_fpcHolder.getClassId());
		writeInt(0); // Inventory.PAPERDOLL_UNDER
		writeInt(_fpcHolder.getEquipHead());
		writeInt(_fpcHolder.getEquipRHand());
		writeInt(_fpcHolder.getEquipLHand());
		writeInt(_fpcHolder.getEquipGloves());
		writeInt(_fpcHolder.getEquipChest());
		writeInt(_fpcHolder.getEquipLegs());
		writeInt(_fpcHolder.getEquipFeet());
		writeInt(_fpcHolder.getEquipCloak());
		writeInt(_fpcHolder.getEquipRHand()); // dual hand
		writeInt(_fpcHolder.getEquipHair());
		writeInt(_fpcHolder.getEquipHair2());
		writeInt(0); // Inventory.PAPERDOLL_RBRACELET
		writeInt(0); // Inventory.PAPERDOLL_LBRACELET
		writeInt(0); // Inventory.PAPERDOLL_DECO1
		writeInt(0); // Inventory.PAPERDOLL_DECO2
		writeInt(0); // Inventory.PAPERDOLL_DECO3
		writeInt(0); // Inventory.PAPERDOLL_DECO4
		writeInt(0); // Inventory.PAPERDOLL_DECO5
		writeInt(0); // Inventory.PAPERDOLL_DECO6
		writeInt(0); // Inventory.PAPERDOLL_BELT
		for (int i = 0; i < 21; i++)
		{
			writeInt(0);
		}
		writeInt(0); // getTalismanSlots
		writeInt(1); // canEquipCloak
		writeInt(_npc.getScriptValue()); // getPvpFlag()
		writeInt(_npc.getKarma());
		writeInt(_mAtkSpd);
		writeInt(_pAtkSpd);
		writeInt(0); // ?
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeInt(_swimRunSpd);
		writeInt(_swimWalkSpd);
		writeInt(_flyRunSpd);
		writeInt(_flyWalkSpd);
		writeInt(_flyRunSpd);
		writeInt(_flyWalkSpd);
		writeDouble(_moveMultiplier);
		writeDouble(_attackSpeedMultiplier);
		writeDouble(_npc.getCollisionRadius());
		writeDouble(_npc.getCollisionHeight());
		writeInt(_fpcHolder.getHair());
		writeInt(_fpcHolder.getHairColor());
		writeInt(_fpcHolder.getFace());
		writeString(_npc.getTemplate().getTitle());
		if (_clan != null)
		{
			writeInt(_clan.getId());
			writeInt(_clan.getCrestId());
			writeInt(_clan.getAllyId());
			writeInt(_clan.getAllyCrestId());
		}
		else
		{
			writeInt(0);
			writeInt(0);
			writeInt(0);
			writeInt(0);
		}
		writeByte(1); // !isSitting() (at some initial tests it worked)
		writeByte(_npc.isRunning());
		writeByte(_npc.isInCombat());
		writeByte(_npc.isAlikeDead());
		writeByte(_npc.isInvisible());
		writeByte(0); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		writeByte(0); // getPrivateStoreType().getId()
		writeShort(0); // getCubics().size()
		// getCubics().keySet().forEach(packet::writeH);
		writeByte(0); // isInPartyMatchRoom
		writeInt(_npc.getAbnormalVisualEffects());
		writeByte(_npc.isInsideZone(ZoneId.WATER));
		writeShort(_fpcHolder.getRecommends()); // Blue value for name (0 = white, 255 = pure blue)
		writeInt(0); // getMountNpcId() == 0 ? 0 : getMountNpcId() + 1000000
		writeInt(_fpcHolder.getClassId());
		writeInt(0); // ?
		writeByte(_fpcHolder.getWeaponEnchantLevel()); // isMounted() ? 0 : _enchantLevel
		writeByte(_npc.getTeam().getId());
		writeInt(_clan != null ? _clan.getCrestLargeId() : 0);
		writeByte(_fpcHolder.getNobleLevel());
		writeByte(_fpcHolder.isHero());
		writeByte(_fpcHolder.isFishing());
		writeInt(_fpcHolder.getBaitLocationX());
		writeInt(_fpcHolder.getBaitLocationY());
		writeInt(_fpcHolder.getBaitLocationZ());
		writeInt(_fpcHolder.getNameColor());
		writeInt(_heading);
		writeInt(_fpcHolder.getPledgeStatus());
		writeInt(0); // getPledgeType()
		writeInt(_fpcHolder.getTitleColor());
		writeInt(0); // isCursedWeaponEquipped
		writeInt(0); // getClanId() > 0 ? getClan().getReputationScore() : 0
		// T1
		writeInt(0); // getTransformationDisplayId()
		writeInt(_fpcHolder.getAgathionId());
		// T2
		writeInt(1);
		// T2.3
		writeInt(_npc.getAbnormalVisualEffectSpecial());
	}
}
