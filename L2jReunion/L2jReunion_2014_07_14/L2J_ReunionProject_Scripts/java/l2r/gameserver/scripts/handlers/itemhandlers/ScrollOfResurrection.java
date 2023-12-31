/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.scripts.handlers.itemhandlers;

import l2r.gameserver.handler.IItemHandler;
import l2r.gameserver.instancemanager.CastleManager;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2PetInstance;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

public class ScrollOfResurrection implements IItemHandler
{
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		if (activeChar.isSitting())
		{
			activeChar.sendPacket(SystemMessageId.CANT_MOVE_SITTING);
			return false;
		}
		
		if (activeChar.isMovementDisabled())
		{
			return false;
		}
		
		final int itemId = item.getId();
		final boolean petScroll = (itemId == 6387);
		final SkillHolder[] skills = item.getItem().getSkills();
		
		if (skills == null)
		{
			_log.warn(getClass().getSimpleName() + ": is missing skills!");
			return false;
		}
		
		// SoR Animation section
		final L2Character target = (L2Character) activeChar.getTarget();
		if ((target == null) || !target.isDead())
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		
		L2PcInstance targetPlayer = null;
		if (target instanceof L2PcInstance)
		{
			targetPlayer = (L2PcInstance) target;
		}
		
		L2PetInstance targetPet = null;
		if (target instanceof L2PetInstance)
		{
			targetPet = (L2PetInstance) target;
		}
		
		if ((targetPlayer != null) || (targetPet != null))
		{
			boolean condGood = true;
			
			// check target is not in a active siege zone
			Castle castle = null;
			
			if (targetPlayer != null)
			{
				castle = CastleManager.getInstance().getCastle(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ());
			}
			else if (targetPet != null)
			{
				castle = CastleManager.getInstance().getCastle(targetPet.getOwner().getX(), targetPet.getOwner().getY(), targetPet.getOwner().getZ());
			}
			
			if ((castle != null) && castle.getSiege().isInProgress())
			{
				condGood = false;
				activeChar.sendPacket(SystemMessageId.CANNOT_BE_RESURRECTED_DURING_SIEGE);
			}
			
			if (targetPet != null)
			{
				if (targetPet.getOwner() != activeChar)
				{
					if (targetPet.getOwner().isReviveRequested())
					{
						if (targetPet.getOwner().isRevivingPet())
						{
							activeChar.sendPacket(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.CANNOT_RES_PET2); // A pet cannot be resurrected while it's owner is in the process of resurrecting.
						}
						condGood = false;
					}
				}
			}
			else if (targetPlayer != null)
			{
				if (targetPlayer.isFestivalParticipant()) // Check to see if the current player target is in a festival.
				{
					condGood = false;
					activeChar.sendMessage("You may not resurrect participants in a festival.");
				}
				if (targetPlayer.isReviveRequested())
				{
					if (targetPlayer.isRevivingPet())
					{
						activeChar.sendPacket(SystemMessageId.MASTER_CANNOT_RES); // While a pet is attempting to resurrect, it cannot help in resurrecting its master.
					}
					else
					{
						activeChar.sendPacket(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
					}
					condGood = false;
				}
				else if (petScroll)
				{
					condGood = false;
					activeChar.sendMessage("You do not have the correct scroll");
				}
			}
			
			if (condGood)
			{
				if (!activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false))
				{
					return false;
				}
				
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
				sm.addItemName(item);
				activeChar.sendPacket(sm);
				
				for (SkillHolder sk : skills)
				{
					activeChar.useMagic(sk.getSkill(), true, true);
				}
				return true;
			}
		}
		return false;
	}
}
