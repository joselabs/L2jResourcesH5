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
package handlers.targethandlers;

import java.util.ArrayList;
import java.util.List;

import org.l2jdevs.gameserver.GeoData;
import org.l2jdevs.gameserver.handler.ITargetTypeHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2SiegeFlagInstance;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.model.skills.targets.L2TargetType;
import org.l2jdevs.gameserver.model.zone.ZoneId;

/**
 * Aura Friendly target handler implementation.
 * @author Sahar
 */
public class AuraFriendly implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		List<L2Character> targetList = new ArrayList<>();
		L2PcInstance player = activeChar.getActingPlayer();
		int maxTargets = skill.getAffectLimit();
		for (L2Character obj : player.getKnownList().getKnownCharactersInRadius(skill.getAffectRange()))
		{
			if ((obj == activeChar) || !checkTarget(player, obj))
			{
				continue;
			}
			
			if ((maxTargets > 0) && (targetList.size() >= maxTargets))
			{
				break;
			}
			
			targetList.add(obj);
		}
		
		if (targetList.isEmpty())
		{
			return EMPTY_TARGET_LIST;
		}
		
		return targetList.toArray(new L2Character[targetList.size()]);
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return L2TargetType.AURA_FRIENDLY;
	}
	
	private boolean checkTarget(L2PcInstance activeChar, L2Character target)
	{
		if ((target == null) || !GeoData.getInstance().canSeeTarget(activeChar, target))
		{
			return false;
		}
		
		if (target.isAlikeDead() || target.isDoor() || (target instanceof L2SiegeFlagInstance) || target.isMonster())
		{
			return false;
		}
		
		if (target.isPlayable())
		{
			L2PcInstance targetPlayer = target.getActingPlayer();
			
			if (activeChar.isInDuelWith(target))
			{
				return false;
			}
			
			if (activeChar.isInPartyWith(target))
			{
				return true;
			}
			
			if (target.isInsideZone(ZoneId.PVP))
			{
				return false;
			}
			
			if (activeChar.isInClanWith(target) || activeChar.isInAllyWith(target) || activeChar.isInCommandChannelWith(target))
			{
				return true;
			}
			
			if ((targetPlayer.getPvpFlag() > 0) || (targetPlayer.getKarma() > 0))
			{
				return false;
			}
		}
		
		return true;
	}
}