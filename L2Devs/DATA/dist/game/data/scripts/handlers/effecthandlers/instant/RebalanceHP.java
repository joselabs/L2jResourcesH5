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
package handlers.effecthandlers.instant;

import org.l2jdevs.gameserver.model.L2Party;
import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.L2Summon;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.conditions.Condition;
import org.l2jdevs.gameserver.model.effects.AbstractEffect;
import org.l2jdevs.gameserver.model.effects.L2EffectType;
import org.l2jdevs.gameserver.model.skills.BuffInfo;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.util.Util;

/**
 * Rebalance HP effect implementation.
 * @author Adry_85, earendil
 */
public final class RebalanceHP extends AbstractEffect
{
	public RebalanceHP(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.REBALANCE_HP;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		if (!info.getEffector().isPlayer() || !info.getEffector().isInParty())
		{
			return;
		}
		
		double fullHP = 0;
		double currentHPs = 0;
		final L2Party party = info.getEffector().getParty();
		final Skill skill = info.getSkill();
		final L2Character effector = info.getEffector();
		for (L2PcInstance member : party.getMembers())
		{
			if (!member.isDead() && Util.checkIfInRange(skill.getAffectRange(), effector, member, true))
			{
				fullHP += member.getMaxHp();
				currentHPs += member.getCurrentHp();
			}
			
			final L2Summon summon = member.getSummon();
			if ((summon != null) && (!summon.isDead() && Util.checkIfInRange(skill.getAffectRange(), effector, summon, true)))
			{
				fullHP += summon.getMaxHp();
				currentHPs += summon.getCurrentHp();
			}
		}
		
		double percentHP = currentHPs / fullHP;
		for (L2PcInstance member : party.getMembers())
		{
			if (!member.isDead() && Util.checkIfInRange(skill.getAffectRange(), effector, member, true))
			{
				double newHP = member.getMaxHp() * percentHP;
				if (newHP > member.getCurrentHp()) // The target gets healed
				{
					// The heal will be blocked if the current hp passes the limit
					if (member.getCurrentHp() > member.getMaxRecoverableHp())
					{
						newHP = member.getCurrentHp();
					}
					else if (newHP > member.getMaxRecoverableHp())
					{
						newHP = member.getMaxRecoverableHp();
					}
				}
				
				member.setCurrentHp(newHP);
			}
			
			final L2Summon summon = member.getSummon();
			if ((summon != null) && (!summon.isDead() && Util.checkIfInRange(skill.getAffectRange(), effector, summon, true)))
			{
				double newHP = summon.getMaxHp() * percentHP;
				if (newHP > summon.getCurrentHp()) // The target gets healed
				{
					// The heal will be blocked if the current hp passes the limit
					if (summon.getCurrentHp() > summon.getMaxRecoverableHp())
					{
						newHP = summon.getCurrentHp();
					}
					else if (newHP > summon.getMaxRecoverableHp())
					{
						newHP = summon.getMaxRecoverableHp();
					}
				}
				summon.setCurrentHp(newHP);
			}
		}
	}
}
