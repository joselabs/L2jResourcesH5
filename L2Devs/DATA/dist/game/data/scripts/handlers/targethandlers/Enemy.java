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

import static org.l2jdevs.gameserver.model.skills.targets.L2TargetType.ENEMY;
import static org.l2jdevs.gameserver.network.SystemMessageId.INCORRECT_TARGET;

import org.l2jdevs.gameserver.handler.ITargetTypeHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.model.skills.targets.L2TargetType;

/**
 * Enemy target type handler.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class Enemy implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		switch (skill.getAffectScope())
		{
			case SINGLE:
			{
				if (target == null)
				{
					return EMPTY_TARGET_LIST;
				}
				
				final L2PcInstance player = activeChar.getActingPlayer();
				if (target.isDead() || (!target.isAttackable() && //
					(player != null) && //
					!player.checkIfPvP(target) && //
					!player.getCurrentSkill().isCtrlPressed()))
				{
					activeChar.sendPacket(INCORRECT_TARGET);
					return EMPTY_TARGET_LIST;
				}
				
				return new L2Character[]
				{
					target
				};
			}
		}
		return EMPTY_TARGET_LIST;
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return ENEMY;
	}
}
