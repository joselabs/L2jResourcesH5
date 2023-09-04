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
import java.util.Collection;
import java.util.List;

import org.l2jdevs.gameserver.handler.ITargetTypeHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.model.skills.targets.L2TargetType;
import org.l2jdevs.gameserver.util.Util;

/**
 * @author UnAfraid
 */
public class ClanMember implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		List<L2Character> targetList = new ArrayList<>();
		if (activeChar.isNpc())
		{
			// for buff purposes, returns friendly mobs nearby and mob itself
			final L2Npc npc = (L2Npc) activeChar;
			if ((npc.getTemplate().getClans() == null) || npc.getTemplate().getClans().isEmpty())
			{
				return new L2Character[]
				{
					activeChar
				};
			}
			final Collection<L2Object> objs = activeChar.getKnownList().getKnownObjects().values();
			for (L2Object newTarget : objs)
			{
				if (newTarget.isNpc() && npc.isInMyClan((L2Npc) newTarget))
				{
					if (!Util.checkIfInRange(skill.getCastRange(), activeChar, newTarget, true))
					{
						continue;
					}
					if (((L2Npc) newTarget).isAffectedBySkill(skill.getId()))
					{
						continue;
					}
					targetList.add((L2Npc) newTarget);
					break;
				}
			}
			if (targetList.isEmpty())
			{
				targetList.add(npc);
			}
		}
		else
		{
			return EMPTY_TARGET_LIST;
		}
		return targetList.toArray(new L2Character[targetList.size()]);
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return L2TargetType.CLAN_MEMBER;
	}
}
