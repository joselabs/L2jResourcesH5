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
import org.l2jmobius.gameserver.data.xml.ExperienceData;
import org.l2jmobius.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jmobius.gameserver.instancemanager.TerritoryWarManager;
import org.l2jmobius.gameserver.model.Elementals;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.ServerPackets;

public class UserInfo extends ServerPacket
{
	private final Player _player;
	private int _relation;
	private int _airShipHelm;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final double _moveMultiplier;
	
	public UserInfo(Player player)
	{
		_player = player;
		final int _territoryId = TerritoryWarManager.getInstance().getRegisteredTerritoryId(player);
		_relation = _player.isClanLeader() ? 0x40 : 0;
		if (_player.getSiegeState() == 1)
		{
			if (_territoryId == 0)
			{
				_relation |= 0x180;
			}
			else
			{
				_relation |= 0x1000;
			}
		}
		if (_player.getSiegeState() == 2)
		{
			_relation |= 0x80;
		}
		// _isDisguised = TerritoryWarManager.getInstance().isDisguised(character.getObjectId());
		if (_player.isInAirShip() && _player.getAirShip().isCaptain(_player))
		{
			_airShipHelm = _player.getAirShip().getHelmItemId();
		}
		else
		{
			_airShipHelm = 0;
		}
		_moveMultiplier = player.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = player.isFlying() ? _runSpd : 0;
		_flyWalkSpd = player.isFlying() ? _walkSpd : 0;
	}
	
	@Override
	public void write()
	{
		ServerPackets.USER_INFO.writeId(this);
		writeInt(_player.getX());
		writeInt(_player.getY());
		writeInt(_player.getZ());
		writeInt(_player.getVehicle() != null ? _player.getVehicle().getObjectId() : 0);
		writeInt(_player.getObjectId());
		writeString(_player.getAppearance().getVisibleName());
		writeInt(_player.getRace().ordinal());
		writeInt(_player.getAppearance().isFemale());
		writeInt(_player.getBaseClass());
		writeInt(_player.getLevel());
		writeLong(_player.getExp());
		writeDouble((float) (_player.getExp() - ExperienceData.getInstance().getExpForLevel(_player.getLevel())) / (ExperienceData.getInstance().getExpForLevel(_player.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(_player.getLevel()))); // High Five exp %
		writeInt(_player.getSTR());
		writeInt(_player.getDEX());
		writeInt(_player.getCON());
		writeInt(_player.getINT());
		writeInt(_player.getWIT());
		writeInt(_player.getMEN());
		writeInt(_player.getMaxHp());
		writeInt((int) Math.round(_player.getCurrentHp()));
		writeInt(_player.getMaxMp());
		writeInt((int) Math.round(_player.getCurrentMp()));
		writeInt((int) _player.getSp());
		writeInt(_player.getCurrentLoad());
		writeInt(_player.getMaxLoad());
		writeInt(_player.getActiveWeaponItem() != null ? 40 : 20); // 20 no weapon, 40 weapon equipped
		
		for (int slot : getPaperdollOrder())
		{
			writeInt(_player.getInventory().getPaperdollObjectId(slot));
		}
		
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
		writeInt((int) _player.getPAtk(null));
		writeInt((int) _player.getPAtkSpd());
		writeInt((int) _player.getPDef(null));
		writeInt(_player.getEvasionRate(null));
		writeInt(_player.getAccuracy());
		writeInt(_player.getCriticalHit(null, null));
		writeInt((int) _player.getMAtk(null, null));
		writeInt(_player.getMAtkSpd());
		writeInt((int) _player.getPAtkSpd());
		writeInt((int) _player.getMDef(null, null));
		writeInt(_player.getPvpFlag());
		writeInt(_player.getKarma());
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
		writeInt(_player.isGM()); // builder level
		
		String title = _player.getTitle();
		if (_player.isGM() && _player.isInvisible())
		{
			title = "[Invisible]";
		}
		writeString(title);
		
		writeInt(_player.getClanId());
		writeInt(_player.getClanCrestId());
		writeInt(_player.getAllyId());
		writeInt(_player.getAllyCrestId()); // ally crest id
		// 0x40 leader rights
		// siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
		writeInt(_relation);
		writeByte(_player.getMountType().ordinal()); // mount type
		writeByte(_player.getPrivateStoreType().getId());
		writeByte(_player.hasDwarvenCraft());
		writeInt(_player.getPkKills());
		writeInt(_player.getPvpKills());
		
		writeShort(_player.getCubics().size());
		for (int cubicId : _player.getCubics().keySet())
		{
			writeShort(cubicId);
		}
		
		writeByte(_player.isInPartyMatchRoom());
		writeInt(_player.isInvisible() ? _player.getAbnormalVisualEffects() | AbnormalVisualEffect.STEALTH.getMask() : _player.getAbnormalVisualEffects());
		writeByte(_player.isInsideZone(ZoneId.WATER) ? 1 : _player.isFlyingMounted() ? 2 : 0);
		writeInt(_player.getClanPrivileges().getBitmask());
		writeShort(_player.getRecomLeft()); // c2 recommendations remaining
		writeShort(_player.getRecomHave()); // c2 recommendations received
		writeInt(_player.getMountNpcId() > 0 ? _player.getMountNpcId() + 1000000 : 0);
		writeShort(_player.getInventoryLimit());
		writeInt(_player.getClassId().getId());
		writeInt(0); // special effects? circles around player...
		writeInt(_player.getMaxCp());
		writeInt((int) _player.getCurrentCp());
		writeByte(_player.isMounted() || (_airShipHelm != 0) ? 0 : _player.getEnchantEffect());
		writeByte(_player.getTeam().getId());
		writeInt(_player.getClanCrestLargeId());
		writeByte(_player.isNoble()); // 1: symbol on char menu ctrl+I
		writeByte(_player.isHero() || (_player.isGM() && Config.GM_HERO_AURA)); // 1: Hero Aura
		
		writeByte(_player.isFishing()); // Fishing Mode
		writeInt(_player.getFishX()); // fishing x
		writeInt(_player.getFishY()); // fishing y
		writeInt(_player.getFishZ()); // fishing z
		
		writeInt(_player.getAppearance().getNameColor());
		// new c5
		writeByte(_player.isRunning()); // changes the Speed display on Status Window
		writeInt(_player.getPledgeClass()); // changes the text above CP on Status Window
		writeInt(_player.getPledgeType());
		writeInt(_player.getAppearance().getTitleColor());
		writeInt(_player.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_player.getCursedWeaponEquippedId()) : 0);
		// T1 Starts
		writeInt(_player.getTransformationDisplayId());
		final byte attackAttribute = _player.getAttackElement();
		writeShort(attackAttribute);
		writeShort(_player.getAttackElementValue(attackAttribute));
		writeShort(_player.getDefenseElementValue(Elementals.FIRE));
		writeShort(_player.getDefenseElementValue(Elementals.WATER));
		writeShort(_player.getDefenseElementValue(Elementals.WIND));
		writeShort(_player.getDefenseElementValue(Elementals.EARTH));
		writeShort(_player.getDefenseElementValue(Elementals.HOLY));
		writeShort(_player.getDefenseElementValue(Elementals.DARK));
		writeInt(_player.getAgathionId());
		// T2 Starts
		writeInt(_player.getFame()); // Fame
		writeInt(_player.isMinimapAllowed()); // Minimap on Hellbound
		writeInt(_player.getVitalityPoints()); // Vitality Points
		writeInt(_player.getAbnormalVisualEffectSpecial());
		// writeInt(_territoryId); // CT2.3
		// writeInt((_isDisguised ? 1: 0)); // CT2.3
		// writeInt(_territoryId); // CT2.3
	}
}
