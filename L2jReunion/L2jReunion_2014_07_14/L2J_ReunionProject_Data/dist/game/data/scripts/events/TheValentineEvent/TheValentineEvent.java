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
package events.TheValentineEvent;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.event.LongTimeEvent;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * The Valentine Event event AI.
 * @author Gnacik
 */
public class TheValentineEvent extends LongTimeEvent
{
	// NPC
	private static final int NPC = 4301;
	// Item
	private static final int RECIPE = 20191;
	
	public TheValentineEvent(String name, String descr)
	{
		super(name, descr);
		addStartNpc(NPC);
		addFirstTalkId(NPC);
		addTalkId(NPC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		String htmltext = event;
		if (event.equalsIgnoreCase("4301-3.htm"))
		{
			if (st.isCompleted())
			{
				htmltext = "4301-4.htm";
			}
			else
			{
				st.giveItems(RECIPE, 1);
				st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
				st.setState(State.COMPLETED);
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
		return npc.getId() + ".htm";
	}
	
	public static void main(String[] args)
	{
		new TheValentineEvent(TheValentineEvent.class.getSimpleName(), "events");
	}
}
