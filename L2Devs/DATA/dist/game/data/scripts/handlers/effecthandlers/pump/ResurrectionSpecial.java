/*
 * Copyright © 2004-2019 L2JDevs
 *
 * This file is part of L2JDevs.
 *
 * L2JDevs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JDevs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers.pump;

import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PetInstance;
import org.l2jdevs.gameserver.model.conditions.Condition;
import org.l2jdevs.gameserver.model.effects.AbstractEffect;
import org.l2jdevs.gameserver.model.effects.EffectFlag;
import org.l2jdevs.gameserver.model.effects.L2EffectType;
import org.l2jdevs.gameserver.model.skills.BuffInfo;
import org.l2jdevs.gameserver.model.skills.Skill;

/**
 * Resurrection Special effect implementation.
 * @author Zealar
 */
public final class ResurrectionSpecial extends AbstractEffect
{
	private final int _resPower;
	private final int _resRecovery;
	
	public ResurrectionSpecial(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_resPower = params.getInt("resPower", 0);
		_resRecovery = params.getInt("resRecovery", 0);
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.RESURRECTION_SPECIAL.getMask();
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.RESURRECTION_SPECIAL;
	}
	
	@Override
	public void onExit(BuffInfo info)
	{
		if (!info.getEffected().isPlayer() && !info.getEffected().isPet())
		{
			return;
		}
		L2PcInstance caster = info.getEffector().getActingPlayer();
		
		Skill skill = info.getSkill();
		
		if (info.getEffected().isPlayer())
		{
			info.getEffected().getActingPlayer().reviveRequest(caster, skill, false, _resPower, _resRecovery);
			return;
		}
		if (info.getEffected().isPet())
		{
			L2PetInstance pet = (L2PetInstance) info.getEffected();
			info.getEffected().getActingPlayer().reviveRequest(pet.getActingPlayer(), skill, true, _resPower, _resRecovery);
		}
	}
}