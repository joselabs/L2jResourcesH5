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
import org.l2jdevs.gameserver.model.conditions.Condition;
import org.l2jdevs.gameserver.model.effects.AbstractEffect;
import org.l2jdevs.gameserver.model.skills.BuffInfo;

/**
 * Crystal Grade Modify effect implementation.
 * @author Zoey76
 */
public final class CrystalGradeModify extends AbstractEffect
{
	private final int _grade;
	
	public CrystalGradeModify(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_grade = params.getInt("grade", 0);
	}
	
	@Override
	public boolean canStart(BuffInfo info)
	{
		return info.getEffected().isPlayer();
	}
	
	@Override
	public void onExit(BuffInfo info)
	{
		final L2PcInstance player = info.getEffected().getActingPlayer();
		if (player != null)
		{
			player.setExpertisePenaltyBonus(0);
			player.refreshExpertisePenalty();
		}
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		final L2PcInstance player = info.getEffected().getActingPlayer();
		if (player != null)
		{
			player.setExpertisePenaltyBonus(_grade);
			player.refreshExpertisePenalty();
		}
	}
}