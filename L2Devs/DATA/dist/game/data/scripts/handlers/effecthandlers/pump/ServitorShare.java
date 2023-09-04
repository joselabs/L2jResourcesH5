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

import java.util.HashMap;
import java.util.Map;

import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.conditions.Condition;
import org.l2jdevs.gameserver.model.effects.AbstractEffect;
import org.l2jdevs.gameserver.model.effects.EffectFlag;
import org.l2jdevs.gameserver.model.effects.L2EffectType;
import org.l2jdevs.gameserver.model.skills.BuffInfo;
import org.l2jdevs.gameserver.model.stats.Stats;

/**
 * Servitor Share effect implementation. Have effect only on servitor's but not on pets Important: Only one effect can be used on char per time.
 * @author Zealar
 */
public final class ServitorShare extends AbstractEffect
{
	private final Map<Stats, Double> stats = new HashMap<>(9);
	
	public ServitorShare(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		for (String key : params.getSet().keySet())
		{
			stats.put(Stats.valueOfXml(key), params.getDouble(key, 1.));
		}
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.SERVITOR_SHARE.getMask();
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.BUFF;
	}
	
	@Override
	public void onExit(BuffInfo info)
	{
		info.getEffected().getActingPlayer().setServitorShare(null);
		if (info.getEffected().getSummon() != null)
		{
			if (info.getEffected().getSummon().getCurrentHp() > info.getEffected().getSummon().getMaxHp())
			{
				info.getEffected().getSummon().setCurrentHp(info.getEffected().getSummon().getMaxHp());
			}
			if (info.getEffected().getSummon().getCurrentMp() > info.getEffected().getSummon().getMaxMp())
			{
				info.getEffected().getSummon().setCurrentMp(info.getEffected().getSummon().getMaxMp());
			}
			info.getEffected().getSummon().broadcastInfo();
		}
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		super.onStart(info);
		info.getEffected().getActingPlayer().setServitorShare(stats);
		if (info.getEffected().getActingPlayer().getSummon() != null)
		{
			info.getEffected().getActingPlayer().getSummon().broadcastInfo();
			info.getEffected().getActingPlayer().getSummon().getStatus().startHpMpRegeneration();
		}
	}
}
