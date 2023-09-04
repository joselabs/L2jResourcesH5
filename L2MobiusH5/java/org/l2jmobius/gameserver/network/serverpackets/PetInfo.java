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

import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.instance.Pet;
import org.l2jmobius.gameserver.model.actor.instance.Servitor;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.ServerPackets;

public class PetInfo extends ServerPacket
{
	private final Summon _summon;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	private final boolean _isSummoned;
	private final int _value;
	private final int _mAtkSpd;
	private final int _pAtkSpd;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final double _moveMultiplier;
	private final int _maxHp;
	private final int _maxMp;
	private int _maxFed;
	private int _curFed;
	
	public PetInfo(Summon summon, int value)
	{
		_summon = summon;
		_isSummoned = summon.isShowSummonAnimation();
		_x = summon.getX();
		_y = summon.getY();
		_z = summon.getZ();
		_heading = summon.getHeading();
		_mAtkSpd = summon.getMAtkSpd();
		_pAtkSpd = (int) summon.getPAtkSpd();
		_moveMultiplier = summon.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(summon.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(summon.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(summon.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(summon.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = summon.isFlying() ? _runSpd : 0;
		_flyWalkSpd = summon.isFlying() ? _walkSpd : 0;
		_maxHp = summon.getMaxHp();
		_maxMp = summon.getMaxMp();
		_value = value;
		if (summon.isPet())
		{
			final Pet pet = (Pet) _summon;
			_curFed = pet.getCurrentFed(); // how fed it is
			_maxFed = pet.getMaxFed(); // max fed it can be
		}
		else if (summon.isServitor())
		{
			final Servitor sum = (Servitor) _summon;
			_curFed = sum.getLifeTimeRemaining();
			_maxFed = sum.getLifeTime();
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.PET_INFO.writeId(this);
		writeInt(_summon.getSummonType());
		writeInt(_summon.getObjectId());
		writeInt(_summon.getTemplate().getDisplayId() + 1000000);
		writeInt(0); // 1=attackable
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(_heading);
		writeInt(0);
		writeInt(_mAtkSpd);
		writeInt(_pAtkSpd);
		writeInt(_runSpd);
		writeInt(_walkSpd);
		writeInt(_swimRunSpd);
		writeInt(_swimWalkSpd);
		writeInt(_flyRunSpd);
		writeInt(_flyWalkSpd);
		writeInt(_flyRunSpd);
		writeInt(_flyWalkSpd);
		writeDouble(_moveMultiplier);
		writeDouble(_summon.getAttackSpeedMultiplier()); // attack speed multiplier
		writeDouble(_summon.getTemplate().getFCollisionRadius());
		writeDouble(_summon.getTemplate().getFCollisionHeight());
		writeInt(_summon.getWeapon()); // right hand weapon
		writeInt(_summon.getArmor()); // body armor
		writeInt(0); // left hand weapon
		writeByte(_summon.getOwner() != null); // when pet is dead and player exit game, pet doesn't show master name
		writeByte(_summon.isRunning()); // running=1 (it is always 1, walking mode is calculated from multiplier)
		writeByte(_summon.isInCombat()); // attacking 1=true
		writeByte(_summon.isAlikeDead()); // dead 1=true
		writeByte(_isSummoned ? 2 : _value); // 0=teleported 1=default 2=summoned
		writeInt(-1); // High Five NPCString ID
		if (_summon.isPet())
		{
			writeString(_summon.getName()); // Pet name.
		}
		else
		{
			writeString(_summon.getTemplate().isUsingServerSideName() ? _summon.getName() : ""); // Summon name.
		}
		writeInt(-1); // High Five NPCString ID
		writeString(_summon.getTitle()); // owner name
		writeInt(1);
		writeInt(_summon.getPvpFlag()); // 0 = white,2= purpleblink, if its greater then karma = purple
		writeInt(_summon.getKarma()); // karma
		writeInt(_curFed); // how fed it is
		writeInt(_maxFed); // max fed it can be
		writeInt((int) _summon.getCurrentHp()); // current hp
		writeInt(_maxHp); // max hp
		writeInt((int) _summon.getCurrentMp()); // current mp
		writeInt(_maxMp); // max mp
		writeInt((int) _summon.getStat().getSp()); // sp
		writeInt(_summon.getLevel()); // level
		writeLong(_summon.getStat().getExp());
		if (_summon.getExpForThisLevel() > _summon.getStat().getExp())
		{
			writeLong(_summon.getStat().getExp()); // 0% absolute value
		}
		else
		{
			writeLong(_summon.getExpForThisLevel()); // 0% absolute value
		}
		writeLong(_summon.getExpForNextLevel()); // 100% absoulte value
		writeInt(_summon.isPet() ? _summon.getInventory().getTotalWeight() : 0); // weight
		writeInt(_summon.getMaxLoad()); // max weight it can carry
		writeInt((int) _summon.getPAtk(null)); // patk
		writeInt((int) _summon.getPDef(null)); // pdef
		writeInt((int) _summon.getMAtk(null, null)); // matk
		writeInt((int) _summon.getMDef(null, null)); // mdef
		writeInt(_summon.getAccuracy()); // accuracy
		writeInt(_summon.getEvasionRate(null)); // evasion
		writeInt(_summon.getCriticalHit(null, null)); // critical
		writeInt((int) _summon.getMoveSpeed()); // speed
		writeInt((int) _summon.getPAtkSpd()); // atkspeed
		writeInt(_summon.getMAtkSpd()); // casting speed
		writeInt(_summon.getAbnormalVisualEffects()); // c2 abnormal visual effect... bleed=1; poison=2; poison & bleed=3; flame=4;
		writeShort(_summon.isMountable()); // c2 ride button
		writeByte(_summon.isInsideZone(ZoneId.WATER) ? 1 : _summon.isFlying() ? 2 : 0); // c2
		// Following all added in C4.
		writeShort(0); // ??
		writeByte(_summon.getTeam().getId());
		writeInt(_summon.getSoulShotsPerHit()); // How many soulshots this servitor uses per hit
		writeInt(_summon.getSpiritShotsPerHit()); // How many spiritshots this servitor uses per hit
		writeInt(_summon.getFormId()); // CT1.5 Pet form and skills
		writeInt(_summon.getAbnormalVisualEffectSpecial());
	}
}
