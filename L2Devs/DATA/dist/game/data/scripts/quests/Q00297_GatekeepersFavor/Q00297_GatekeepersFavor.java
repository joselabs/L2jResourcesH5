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
package quests.Q00297_GatekeepersFavor;

import org.l2jdevs.gameserver.enums.audio.Sound;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.model.quest.State;

/**
 * Gatekeeper's Favor (297)
 * @author malyelfik
 */
public class Q00297_GatekeepersFavor extends Quest
{
	// NPC
	private static final int WIRPHY = 30540;
	// Monster
        private static final int[] GOLEMS = {
            20521, // whinstone golem
            20526, // obsidian golem
            20346, // darkstone golem
            20511, // pitchstone golem
            21128, // spine golem
            21131 // enchanted spine golem
        };
	// Items
	private static final int STARSTONE = 1573;
	private static final int GATEKEEPER_TOKEN = 1659;
	// Misc
	private static final int MIN_LEVEL = 15;
	private static final int STARSTONE_COUT = 20;
        private static final boolean __UNLIMITED = false;
	
	public Q00297_GatekeepersFavor()
	{
		super(297, Q00297_GatekeepersFavor.class.getSimpleName(), "Gatekeeper's Favor");
		addStartNpc(WIRPHY);
		addTalkId(WIRPHY);
		addKillId(GOLEMS);
		registerQuestItems(STARSTONE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30540-03.htm"))
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				return "30540-01.htm";
			}
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
                final QuestState st = getRandomPartyMemberState(killer, 1, 3, npc);
                if(__UNLIMITED) {
                    if ((st != null) && st.isStarted()) {
			st.giveItems(STARSTONE, 1);
			if (!st.isCond(2) && st.getQuestItemsCount(STARSTONE) >= STARSTONE_COUT)
                            st.setCond(2, true);
			else
                            st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
                    }
                }
                else
		if ((st != null) && st.isStarted() && (st.getQuestItemsCount(STARSTONE) < STARSTONE_COUT))
		{
			st.giveItems(STARSTONE, 1);
			if (st.getQuestItemsCount(STARSTONE) >= STARSTONE_COUT)
			{
				st.setCond(2, true);
			}
			else
			{
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState())
		{
			case State.CREATED:
				htmltext = "30540-02.htm";
				break;
			case State.STARTED:
				if (st.isCond(1))
				{
					htmltext = "30540-04.html";
				}
				else if (st.isCond(2) && (st.getQuestItemsCount(STARSTONE) >= STARSTONE_COUT))
				{
					st.giveItems(GATEKEEPER_TOKEN, st.getQuestItemsCount(STARSTONE) / 10);
					st.exitQuest(true, true);
					htmltext = "30540-05.html";
				}
				break;
		}
		return htmltext;
	}
}
