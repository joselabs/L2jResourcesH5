/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import l2r.gameserver.enums.InstanceType;
import l2r.gameserver.handler.ITargetTypeHandler;
import l2r.gameserver.handler.TargetHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.events.EventType;
import l2r.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import l2r.gameserver.model.events.listeners.ConsumerEventListener;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.targets.L2TargetType;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.util.Util;
import l2r.util.Rnd;

/**
 * Trigger Skill By Damage effect implementation.
 * @author UnAfraid
 */
public final class TriggerSkillByDamage extends L2Effect
{
	private final int _minAttackerLevel;
	private final int _maxAttackerLevel;
	private final int _minDamage;
	private final int _chance;
	private final SkillHolder _skill;
	private final L2TargetType _targetType;
	private final InstanceType _attackerType;
	
	public TriggerSkillByDamage(Env env, EffectTemplate template)
	{
		super(env, template);
		
		_minAttackerLevel = template.getParameters().getInt("minAttackerLevel", 1);
		_maxAttackerLevel = template.getParameters().getInt("maxAttackerLevel", 100);
		_minDamage = template.getParameters().getInt("minDamage", 1);
		_chance = template.getParameters().getInt("chance", 100);
		_skill = new SkillHolder(template.getParameters().getInt("skillId"), template.getParameters().getInt("skillLevel", 1));
		_targetType = template.getParameters().getEnum("targetType", L2TargetType.class, L2TargetType.SELF);
		_attackerType = template.getParameters().getEnum("attackerType", InstanceType.class, InstanceType.L2Character);
	}
	
	public void onDamageReceivedEvent(OnCreatureDamageReceived event)
	{
		if (event.isDamageOverTime() || (_chance == 0) || (_skill.getSkillLvl() == 0))
		{
			return;
		}
		
		if ((((_targetType == L2TargetType.SELF) || (_targetType == L2TargetType.ONE)) && (_skill.getSkill().getCastRange() > 0)) && (Util.calculateDistance(event.getAttacker(), event.getTarget(), true, false) > _skill.getSkill().getCastRange()))
		{
			return;
		}
		
		final ITargetTypeHandler targetHandler = TargetHandler.getInstance().getHandler(_targetType);
		if (targetHandler == null)
		{
			_log.warn("Handler for target type: " + _targetType + " does not exist.");
			return;
		}
		
		if (event.getAttacker() == event.getTarget())
		{
			return;
		}
		
		if ((event.getAttacker().getLevel() < _minAttackerLevel) || (event.getAttacker().getLevel() > _maxAttackerLevel))
		{
			return;
		}
		
		if ((event.getDamage() < _minDamage) || (Rnd.get(1000) > (_chance * 10)) || !event.getAttacker().getInstanceType().isType(_attackerType))
		{
			return;
		}
		
		final L2Skill triggerSkill = _skill.getSkill();
		final L2Object[] targets = targetHandler.getTargetList(triggerSkill, event.getTarget(), false, event.getAttacker());
		for (L2Object triggerTarget : targets)
		{
			if ((triggerTarget == null) || !triggerTarget.isCharacter())
			{
				continue;
			}
			
			final L2Character targetChar = (L2Character) triggerTarget;
			if (!targetChar.isInvul())
			{
				event.getTarget().makeTriggerCast(triggerSkill, targetChar);
			}
		}
	}
	
	@Override
	public void onExit()
	{
		getEffected().removeListenerIf(EventType.ON_CREATURE_DAMAGE_RECEIVED, listener -> listener.getOwner() == this);
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().addListener(new ConsumerEventListener(getEffected(), EventType.ON_CREATURE_DAMAGE_RECEIVED, (OnCreatureDamageReceived event) -> onDamageReceivedEvent(event), this));
		return true;
	}
}
