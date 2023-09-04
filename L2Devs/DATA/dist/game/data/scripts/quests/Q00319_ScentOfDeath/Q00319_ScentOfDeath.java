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
package quests.Q00319_ScentOfDeath;

import org.l2jdevs.gameserver.enums.audio.Sound;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.holders.ItemHolder;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.model.quest.State;
import org.l2jdevs.gameserver.util.Util;

/**
 * Scent of Death (319)
 * @author Zoey76
 */
public class Q00319_ScentOfDeath extends Quest
{
	// NPC
	private static final int MINALESS = 30138;
	// Monsters
	private static final int MARSH_ZOMBIE = 20015;
	private static final int MARSH_ZOMBIE_LORD = 20020;
	// Item
	private static final int ZOMBIES_SKIN = 1045;
	private static final int LESSER_HEALING_POTION = 1060;
	// Misc
	private static final int MIN_LEVEL = 11;
	private static final int MIN_CHANCE = 2;
	private static final int REQUIRED_ITEM_COUNT = 5;
	
	public Q00319_ScentOfDeath()
	{
		super(319, Q00319_ScentOfDeath.class.getSimpleName(), "Scent of Death");
		addStartNpc(MINALESS);
		addTalkId(MINALESS);
		addKillId(MARSH_ZOMBIE, MARSH_ZOMBIE_LORD);
		registerQuestItems(ZOMBIES_SKIN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		if (player.getLevel() >= MIN_LEVEL)
		{
			switch (event)
			{
				case "30138-04.htm":
				{
					st.startQuest();
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
                final QuestState st = getRandomPartyMemberState(killer, 1, 3, npc);
		if (st != null //
                    && Util.checkIfInRange(1500, npc, st.getPlayer(), false) //
                    && getRandom(10) < MIN_CHANCE) {
                        st.giveItems(ZOMBIES_SKIN, 1);
                        st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
                        if (st.getQuestItemsCount(ZOMBIES_SKIN) >= REQUIRED_ITEM_COUNT)
                            st.setCond(2, true);
                    }
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = player.getLevel() >= MIN_LEVEL ? "30138-03.htm" : "30138-02.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30138-05.html";
						break;
					}
					case 2:
					{
                                            long n = st.getQuestItemsCount(ZOMBIES_SKIN) / REQUIRED_ITEM_COUNT;
                                            st.giveAdenaFuzzy(3350 * n, false);
                                            st.giveItems(LESSER_HEALING_POTION, n);
                                            st.takeItems(ZOMBIES_SKIN, -1);
						st.exitQuest(true, true);
						htmltext = "30138-06.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
