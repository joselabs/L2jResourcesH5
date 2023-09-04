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

import org.l2jdevs.Config;
import org.l2jdevs.gameserver.enums.MountType;
import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PetInstance;
import org.l2jdevs.gameserver.model.conditions.Condition;
import org.l2jdevs.gameserver.model.effects.AbstractEffect;
import org.l2jdevs.gameserver.model.skills.BuffInfo;

/**
 * Food For Pet effect implementation.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class FoodForPet extends AbstractEffect
{
	private final int _normal;
	private final int _ride;
	private final int _wyvern;
	
	public FoodForPet(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_normal = params.getInt("normal", 0);
		_ride = params.getInt("ride", 0);
		_wyvern = params.getInt("wyvern", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		final L2Character activeChar = info.getEffector();
		
		if (activeChar.isPet())
		{
			final L2PetInstance pet = (L2PetInstance) activeChar;
			pet.setCurrentFed(pet.getCurrentFed() + (_normal * Config.PET_FOOD_RATE));
		}
		else if (activeChar.isPlayer())
		{
			final L2PcInstance player = activeChar.getActingPlayer();
			if (player.getMountType() == MountType.WYVERN)
			{
				player.setCurrentFeed(player.getCurrentFeed() + _wyvern);
			}
			else
			{
				player.setCurrentFeed(player.getCurrentFeed() + _ride);
			}
		}
	}
}
