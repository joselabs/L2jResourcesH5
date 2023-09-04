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

import org.l2jdevs.gameserver.ai.CtrlEvent;
import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2jdevs.gameserver.model.conditions.Condition;
import org.l2jdevs.gameserver.model.effects.AbstractEffect;
import org.l2jdevs.gameserver.model.skills.BuffInfo;
import org.l2jdevs.gameserver.model.stats.Formulas;
import org.l2jdevs.gameserver.network.SystemMessageId;

/**
 * Spoil effect implementation.
 * @author _drunk_, Ahmed, Zoey76
 */
public final class Spoil extends AbstractEffect
{
	public Spoil(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean calcSuccess(BuffInfo info)
	{
            // not the best place, but, afaiu, it needed only here -- and it called once
		final boolean f = Formulas.calcMagicSuccess(info.getEffector(), info.getEffected(), info.getSkill());
                if(!f) info.getEffector().sendPacket(SystemMessageId.GETTING_READY_TO_SHOOT_AN_ARROW);
                return f;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		if (!info.getEffected().isMonster() || info.getEffected().isDead())
		{
			info.getEffector().sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		final L2MonsterInstance target = (L2MonsterInstance) info.getEffected();
		if (target.isSpoiled())
		{
			info.getEffector().sendPacket(SystemMessageId.ALREADY_SPOILED);
			return;
		}
		
		target.setSpoilerObjectId(info.getEffector().getObjectId());
		info.getEffector().sendPacket(SystemMessageId.SPOIL_SUCCESS);
		target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, info.getEffector());
	}
}
