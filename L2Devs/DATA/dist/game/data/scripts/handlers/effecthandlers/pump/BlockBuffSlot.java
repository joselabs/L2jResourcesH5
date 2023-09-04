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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.conditions.Condition;
import org.l2jdevs.gameserver.model.effects.AbstractEffect;
import org.l2jdevs.gameserver.model.skills.AbnormalType;
import org.l2jdevs.gameserver.model.skills.BuffInfo;

/**
 * Block Buff Slot effect implementation.
 * @author Zoey76
 */
public final class BlockBuffSlot extends AbstractEffect
{
	private final Set<AbnormalType> _blockBuffSlots;
	
	public BlockBuffSlot(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		String blockBuffSlots = params.getString("slot", null);
		if ((blockBuffSlots != null) && !blockBuffSlots.isEmpty())
		{
			_blockBuffSlots = new HashSet<>();
			for (String slot : blockBuffSlots.split(";"))
			{
				_blockBuffSlots.add(AbnormalType.valueOf(slot));
			}
		}
		else
		{
			_blockBuffSlots = Collections.<AbnormalType> emptySet();
		}
	}
	
	@Override
	public void onExit(BuffInfo info)
	{
		info.getEffected().getEffectList().removeBlockedBuffSlots(_blockBuffSlots);
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		info.getEffected().getEffectList().addBlockedBuffSlots(_blockBuffSlots);
	}
}
