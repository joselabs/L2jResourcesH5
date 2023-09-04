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
package hellbound.AI;

import org.l2jdevs.gameserver.instancemanager.RaidBossSpawnManager;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2RaidBossInstance;
import org.l2jdevs.gameserver.model.holders.SkillHolder;

import ai.npc.AbstractNpcAI;

/**
 * Typhoon's AI.
 * @author GKR
 */
public final class Typhoon extends AbstractNpcAI
{
	// NPCs
	private static final int TYPHOON = 25539;
	// Skills
	private static final SkillHolder STORM = new SkillHolder(5434); // Gust
	
	public Typhoon()
	{
		super(Typhoon.class.getSimpleName(), "hellbound/AI");
		addAggroRangeEnterId(TYPHOON);
		addSpawnId(TYPHOON);
		
		final L2RaidBossInstance boss = RaidBossSpawnManager.getInstance().getBosses().get(TYPHOON);
		if (boss != null)
		{
			onSpawn(boss);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("CAST") && (npc != null) && !npc.isDead())
		{
			npc.doSimultaneousCast(STORM);
			startQuestTimer("CAST", 5000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		npc.doSimultaneousCast(STORM);
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("CAST", 5000, npc, null);
		return super.onSpawn(npc);
	}
}