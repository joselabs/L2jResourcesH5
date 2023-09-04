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
package events.Halloween;

import org.l2jdevs.gameserver.ai.CtrlIntention;
import org.l2jdevs.gameserver.data.xml.impl.MultisellData;
import org.l2jdevs.gameserver.model.actor.L2Attackable;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.event.LongTimeEvent;
import org.l2jdevs.gameserver.model.quest.QuestState;

/**
 * Halloween event AI.
 * @author U3Games, Sacrifice
 */
public final class Halloween extends LongTimeEvent
{
	private static final int HALLOWEN_NPC = 125;
	
	private static final int HALLOWEEN_CANDY = 15430;
	
	private static final int PUMPKIN_GHOST = 13135;
	
	//@formatter:off
	private static final int[] FOREST_OF_DEATH_NIGHT =
	{
		18119, 21547, 21553, 21557, 21559, 21561, 21563,
		21565, 21567, 21570, 21572, 21574, 21578, 21580,
		21581, 21583, 21587, 21590, 21593, 21596, 21599
	};
	
	private static final int[] THE_CEMENTARY =
	{
		20666, 20668, 20669, 20678,
		20997, 20998, 20999, 21000
	};
	
	private static final int[] IMPERIAL_TOMB =
	{
		21396, 21397, 21398, 21399, 21400, 21401, 21402,
		21403, 21404, 21405, 21406, 21407, 21408, 21409,
		21410, 21411, 21412, 21413, 21414, 21415, 21416,
		21417, 21418, 21419, 21420, 21421, 21422, 21423,
		21424, 21425, 21426, 21427, 21428, 21429, 21430,
		21431
	};
	//@formatter:on
	
	private Halloween()
	{
		super(Halloween.class.getSimpleName(), "events");
		addStartNpc(HALLOWEN_NPC);
		addFirstTalkId(HALLOWEN_NPC);
		addTalkId(HALLOWEN_NPC);
		addKillId(PUMPKIN_GHOST);
		
		for (int id : FOREST_OF_DEATH_NIGHT)
		{
			addKillId(id);
		}
		
		for (int id : THE_CEMENTARY)
		{
			addKillId(id);
		}
		
		for (int id : IMPERIAL_TOMB)
		{
			addKillId(id);
		}
	}
	
	public static void main(String[] args)
	{
		new Halloween();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		String htmltext = event;
		switch (event)
		{
			case "main":
			{
				htmltext = "125.htm";
				break;
			}
			case "info":
			{
				htmltext = "125-info.htm";
				break;
			}
			case "rewards":
			{
				MultisellData.getInstance().separateAndSend(7104001, player, npc, false);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getQuestState(getName()) == null)
		{
			newQuestState(player);
		}
		return "125.htm";
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		QuestState st = player.getQuestState(getName());
		
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		for (int Id : FOREST_OF_DEATH_NIGHT)
		{
			if (npc.getId() == Id)
			{
				spawnGhost(npc, player);
			}
		}
		
		for (int Id : THE_CEMENTARY)
		{
			if (npc.getId() == Id)
			{
				spawnGhost(npc, player);
			}
		}
		
		for (int Id : IMPERIAL_TOMB)
		{
			if (npc.getId() == Id)
			{
				spawnGhost(npc, player);
			}
		}
		
		if (npc.getId() == PUMPKIN_GHOST)
		{
			npc.dropItem(player, HALLOWEEN_CANDY, 3);
		}
		return super.onKill(npc, player, isPet);
	}
	
	private void spawnGhost(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		final L2Attackable pumpkinGhost = (L2Attackable) st.addSpawn(PUMPKIN_GHOST, 60000);
		if (pumpkinGhost != null)
		{
			boolean isPet = false;
			
			if (player.getSummon() != null)
			{
				isPet = true;
			}
			final L2Character originalAttacker = isPet ? player.getSummon() : player;
			pumpkinGhost.setRunning();
			pumpkinGhost.addDamageHate(originalAttacker, 0, 500);
			pumpkinGhost.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, originalAttacker);
		}
	}
}