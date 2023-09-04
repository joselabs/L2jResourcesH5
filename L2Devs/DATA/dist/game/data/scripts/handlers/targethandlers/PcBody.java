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

import org.l2jdevs.gameserver.datatables.LanguageData;
import org.l2jdevs.gameserver.handler.ITargetTypeHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PetInstance;
import org.l2jdevs.gameserver.model.effects.L2EffectType;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.model.skills.targets.L2TargetType;
import org.l2jdevs.gameserver.model.zone.ZoneId;
import org.l2jdevs.gameserver.network.SystemMessageId;

/**
 * @author UnAfraid
 */
public class PcBody implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		List<L2Character> targetList = new ArrayList<>();
		if ((target != null) && target.isDead())
		{
			final L2PcInstance player;
			if (activeChar.isPlayer())
			{
				player = activeChar.getActingPlayer();
			}
			else
			{
				player = null;
			}
			
			final L2PcInstance targetPlayer;
			if (target.isPlayer())
			{
				targetPlayer = target.getActingPlayer();
			}
			else
			{
				targetPlayer = null;
			}
			
			final L2PetInstance targetPet;
			if (target.isPet())
			{
				targetPet = (L2PetInstance) target;
			}
			else
			{
				targetPet = null;
			}
			
			if ((player != null) && ((targetPlayer != null) || (targetPet != null)))
			{
				boolean condGood = true;
				
				if (skill.hasEffectType(L2EffectType.RESURRECTION))
				{
					if (targetPlayer != null)
					{
						// check target is not in a active siege zone
						if (targetPlayer.isInsideZone(ZoneId.SIEGE) && !targetPlayer.isInSiege())
						{
							condGood = false;
							activeChar.sendPacket(SystemMessageId.CANNOT_BE_RESURRECTED_DURING_SIEGE);
						}
						
						if (targetPlayer.isFestivalParticipant()) // Check to see if the current player target is in a festival.
						{
							condGood = false;
							activeChar.sendMessage(LanguageData.getInstance().getMsg(player, "ss_no_resurrect_player"));
						}
					}
				}
				
				if (condGood)
				{
					if (!onlyFirst)
					{
						targetList.add(target);
						return targetList.toArray(new L2Object[targetList.size()]);
					}
					return new L2Character[]
					{
						target
					};
				}
			}
		}
		activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
		return EMPTY_TARGET_LIST;
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return L2TargetType.PC_BODY;
	}
}
