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

import org.l2jdevs.gameserver.enums.EffectCalculationType;
import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.stat.CharStat;
import org.l2jdevs.gameserver.model.conditions.Condition;
import org.l2jdevs.gameserver.model.effects.AbstractEffect;
import org.l2jdevs.gameserver.model.skills.BuffInfo;
import org.l2jdevs.gameserver.model.stats.Stats;
import org.l2jdevs.gameserver.model.stats.functions.FuncAdd;
import org.l2jdevs.gameserver.model.stats.functions.FuncMul;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;

/**
 * Max Mp effect implementation.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class MaxMp extends AbstractEffect
{
	private final double _power;
	private final EffectCalculationType _type;
	private final boolean _heal;
	
	public MaxMp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_type = params.getEnum("type", EffectCalculationType.class, EffectCalculationType.DIFF);
		switch (_type)
		{
			case DIFF:
			{
				_power = params.getDouble("power", 0);
				break;
			}
			default:
			{
				_power = 1 + (params.getDouble("power", 0) / 100.0);
			}
		}
		_heal = params.getBoolean("heal", false);
		
		if (params.isEmpty())
		{
			_log.warning(getClass().getSimpleName() + ": must have parameters.");
		}
	}
	
	@Override
	public void onExit(BuffInfo info)
	{
		final CharStat charStat = info.getEffected().getStat();
		synchronized (charStat)
		{
			charStat.getActiveChar().removeStatsOwner(this);
		}
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		final L2Character effected = info.getEffected();
		final CharStat charStat = effected.getStat();
		final double currentMp = effected.getCurrentMp();
		double amount = _power;
		
		synchronized (charStat)
		{
			switch (_type)
			{
				case DIFF:
				{
					charStat.getActiveChar().addStatFunc(new FuncAdd(Stats.MAX_MP, 1, this, _power, null));
					if (_heal)
					{
						effected.setCurrentMp((currentMp + _power));
					}
					break;
				}
				case PER:
				{
					final double maxMp = effected.getMaxMp();
					charStat.getActiveChar().addStatFunc(new FuncMul(Stats.MAX_MP, 1, this, _power, null));
					if (_heal)
					{
						amount = (_power - 1) * maxMp;
						effected.setCurrentMp(currentMp + amount);
					}
					break;
				}
			}
		}
		if (_heal)
		{
			effected.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_MP_HAS_BEEN_RESTORED).addInt((int) amount));
		}
	}
	
}
