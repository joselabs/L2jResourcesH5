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

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Decoy;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.ServerPackets;

public class CharInfo extends ServerPacket
{
	private static final int[] PAPERDOLL_ORDER = new int[]
	{
		Inventory.PAPERDOLL_UNDER,
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
	
	private final Player _player;
	private final Clan _clan;
	private int _objId;
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	private final int _mAtkSpd;
	private final int _pAtkSpd;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final double _moveMultiplier;
	private int _vehicleId = 0;
	private final boolean _gmSeeInvis;
	
	public CharInfo(Player player, boolean gmSeeInvis)
	{
		super(256);
		
		_player = player;
		_objId = player.getObjectId();
		_clan = player.getClan();
		if ((_player.getVehicle() != null) && (_player.getInVehiclePosition() != null))
		{
			_x = _player.getInVehiclePosition().getX();
			_y = _player.getInVehiclePosition().getY();
			_z = _player.getInVehiclePosition().getZ();
			_vehicleId = _player.getVehicle().getObjectId();
		}
		else
		{
			_x = _player.getX();
			_y = _player.getY();
			_z = _player.getZ();
		}
		_heading = _player.getHeading();
		_mAtkSpd = _player.getMAtkSpd();
		_pAtkSpd = (int) _player.getPAtkSpd();
		_moveMultiplier = player.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = player.isFlying() ? _runSpd : 0;
		_flyWalkSpd = player.isFlying() ? _walkSpd : 0;
		_gmSeeInvis = gmSeeInvis;
	}
	
	public CharInfo(Decoy decoy, boolean gmSeeInvis)
	{
		this(decoy.getActingPlayer(), gmSeeInvis); // init
		_objId = decoy.getObjectId();
		_x = decoy.getX();
		_y = decoy.getY();
		_z = decoy.getZ();
		_heading = decoy.getHeading();
	}
	
	@Override
	public void write()
	{
		ServerPackets.CHAR_INFO.writeId(this);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_vehicleId);
		writeInt(_objId);
		writeString(_player.getAppearance().getVisibleName());
		writeInt(_player.getRace().ordinal());
		writeInt(_player.getAppearance().isFemale());
		writeInt(_player.getBaseClass());
		
		for (int slot : getPaperdollOrder())
		{
			writeInt(_player.getInventory().getPaperdollItemDisplayId(slot));
		}
		
		for (int slot : getPaperdollOrder())
		{
			writeInt(_player.getInventory().getPaperdollAugmentationId(slot));
		}
		
		writeInt(_player.getInventory().getTalismanSlots());
		writeInt(_player.getInventory().canEquipCloak());
		writeInt(_player.getPvpFlag());
		writeInt(_player.getKarma());
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
		writeDouble(_player.getAttackSpeedMultiplier());
		writeDouble(_player.getCollisionRadius());
		writeDouble(_player.getCollisionHeight());
		writeInt(_player.getAppearance().getHairStyle());
		writeInt(_player.getAppearance().getHairColor());
		writeInt(_player.getAppearance().getFace());
		writeString(_gmSeeInvis ? "Invisible" : _player.getAppearance().getVisibleTitle());
		if (!_player.isCursedWeaponEquipped())
		{
			writeInt(_player.getClanId());
			writeInt(_player.getClanCrestId());
			writeInt(_player.getAllyId());
			writeInt(_player.getAllyCrestId());
		}
		else
		{
			writeInt(0);
			writeInt(0);
			writeInt(0);
			writeInt(0);
		}
		writeByte(!_player.isSitting()); // standing = 1 sitting = 0
		writeByte(_player.isRunning()); // running = 1 walking = 0
		writeByte(_player.isInCombat());
		writeByte(!_player.isInOlympiadMode() && _player.isAlikeDead());
		writeByte(!_gmSeeInvis && _player.isInvisible()); // invisible = 1 visible =0
		writeByte(_player.getMountType().ordinal()); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		writeByte(_player.getPrivateStoreType().getId());
		
		writeShort(_player.getCubics().size());
		for (int cubicId : _player.getCubics().keySet())
		{
			writeShort(cubicId);
		}
		
		writeByte(_player.isInPartyMatchRoom());
		writeInt(_gmSeeInvis ? (_player.getAbnormalVisualEffects() | AbnormalVisualEffect.STEALTH.getMask()) : _player.getAbnormalVisualEffects());
		writeByte(_player.isInsideZone(ZoneId.WATER) ? 1 : _player.isFlyingMounted() ? 2 : 0);
		writeShort(_player.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
		writeInt(_player.getMountNpcId() + 1000000);
		writeInt(_player.getClassId().getId());
		writeInt(0); // ?
		writeByte(_player.isMounted() ? 0 : _player.getEnchantEffect());
		writeByte(_player.getTeam().getId());
		writeInt(_player.getClanCrestLargeId());
		writeByte(_player.isNoble()); // Symbol on char menu ctrl+I
		writeByte(_player.isHero() || (_player.isGM() && Config.GM_HERO_AURA)); // Hero Aura
		
		writeByte(_player.isFishing()); // 1: Fishing Mode (Cant be undone by setting back to 0)
		writeInt(_player.getFishX());
		writeInt(_player.getFishY());
		writeInt(_player.getFishZ());
		
		writeInt(_player.getAppearance().getNameColor());
		writeInt(_heading);
		writeInt(_player.getPledgeClass());
		writeInt(_player.getPledgeType());
		writeInt(_player.getAppearance().getTitleColor());
		writeInt(_player.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_player.getCursedWeaponEquippedId()) : 0);
		writeInt(_clan != null ? _clan.getReputationScore() : 0);
		// T1
		writeInt(_player.getTransformationDisplayId());
		writeInt(_player.getAgathionId());
		// T2
		writeInt(1);
		// T2.3
		writeInt(_player.getAbnormalVisualEffectSpecial());
	}
	
	@Override
	public int[] getPaperdollOrder()
	{
		return PAPERDOLL_ORDER;
	}
}
