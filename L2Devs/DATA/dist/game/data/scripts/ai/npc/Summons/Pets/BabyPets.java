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
package ai.npc.Summons.Pets;

import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.L2Summon;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PetInstance;
import org.l2jdevs.gameserver.model.events.EventType;
import org.l2jdevs.gameserver.model.events.ListenerRegisterType;
import org.l2jdevs.gameserver.model.events.annotations.RegisterEvent;
import org.l2jdevs.gameserver.model.events.annotations.RegisterType;
import org.l2jdevs.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2jdevs.gameserver.model.holders.SkillHolder;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;
import org.l2jdevs.gameserver.util.Util;

import ai.npc.AbstractNpcAI;

/**
 * Baby Pets AI.
 * @author St3eT
 * @since 2.6.0.0
 */
public final class BabyPets extends AbstractNpcAI
{
	// NPCs
	private static final int[] BABY_PETS =
	{
		12780, // Baby Buffalo
		12781, // Baby Kookaburra
		12782, // Baby Cougar
	};
	
	// Skills
	private static final int HEAL_TRICK = 4717;
	private static final int GREATER_HEAL_TRICK = 4718;
	
	private BabyPets()
	{
		super(BabyPets.class.getSimpleName(), "ai/npc/Summons/Pets");
		addSummonSpawnId(BABY_PETS);
	}
	
	public static void main(String[] args)
	{
		new BabyPets();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("CAST_HEAL") && (player != null))
		{
			final L2PetInstance pet = (L2PetInstance) player.getSummon();
			
			if (pet != null)
			{
				if (getRandom(100) <= 25)
				{
					castHealSkill(pet, new SkillHolder(HEAL_TRICK, getHealLevel(pet)), 80);
				}
				
				if (getRandom(100) <= 75)
				{
					castHealSkill(pet, new SkillHolder(GREATER_HEAL_TRICK, getHealLevel(pet)), 15);
				}
			}
			else
			{
				cancelQuestTimer("CAST_HEAL", null, player);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void OnPlayerLogout(OnPlayerLogout event)
	{
		cancelQuestTimer("CAST_HEAL", null, event.getActiveChar());
	}
	
	@Override
	public void onSummonSpawn(L2Summon summon)
	{
		startQuestTimer("CAST_HEAL", 1000, null, summon.getOwner(), true);
	}
	
	private void castHealSkill(L2Summon summon, SkillHolder skill, int maxHpPer)
	{
		final L2PcInstance owner = summon.getOwner();
		if (!owner.isDead() && !summon.isHungry() && (((owner.getCurrentHp() / owner.getMaxHp()) * 100) < maxHpPer) && summon.checkDoCastConditions(skill.getSkill()))
		{
			final boolean previousFollowStatus = summon.getFollowStatus();
			
			if (!previousFollowStatus && !summon.isInsideRadius(owner, skill.getSkill().getCastRange(), true, true))
			{
				return;
			}
			
			summon.setTarget(owner);
			summon.doCast(skill.getSkill());
			summon.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addSkillName(skill.getSkill()));
			
			if (previousFollowStatus != summon.getFollowStatus())
			{
				summon.setFollowStatus(previousFollowStatus);
			}
		}
	}
	
	private int getHealLevel(L2Summon summon)
	{
		final int summonLevel = summon.getLevel();
		return Util.constrain(summonLevel < 70 ? (summonLevel / 10) : (7 + ((summonLevel - 70) / 5)), 1, 12);
	}
}