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
package ai.npc.NpcBuffers;

import org.l2jdevs.gameserver.ThreadPoolManager;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

/**
 * @author UnAfraid
 */
public final class NpcBuffers extends AbstractNpcAI
{
	private final NpcBuffersData _npcBuffers = new NpcBuffersData();
	
	private NpcBuffers()
	{
		super(NpcBuffers.class.getSimpleName(), "ai/npc");
		
		for (int npcId : _npcBuffers.getNpcBufferIds())
		{
			// TODO: Cleanup once npc rework is finished and default html is configurable.
			addFirstTalkId(npcId);
			addSpawnId(npcId);
		}
	}
	
	public static void main(String[] args)
	{
		new NpcBuffers();
	}
	
	// TODO: Cleanup once npc rework is finished and default html is configurable.
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		final NpcBufferData data = _npcBuffers.getNpcBuffer(npc.getId());
		for (NpcBufferSkillData skill : data.getSkills())
		{
			ThreadPoolManager.getInstance().scheduleAi(new NpcBufferAI(npc, skill), skill.getInitialDelay());
		}
		return super.onSpawn(npc);
	}
}
