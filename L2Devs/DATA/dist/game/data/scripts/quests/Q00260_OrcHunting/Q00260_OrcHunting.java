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
package quests.Q00260_OrcHunting;

import java.util.HashMap;
import java.util.Map;

import org.l2jdevs.gameserver.enums.Race;
import org.l2jdevs.gameserver.enums.audio.Sound;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.model.quest.State;
import org.l2jdevs.Config;

/**
 * Orc Hunting (260)
 * @author xban1x
 */
public final class Q00260_OrcHunting extends Quest
{
	// NPC
	private static final int RAYEN = 30221;
	// Items
	private static final int ORC_AMULET = 1114;
	private static final int ORC_NECKLACE = 1115;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	static
	{
		MONSTERS.put(20468, ORC_AMULET); // Kaboo Orc
		MONSTERS.put(20469, ORC_AMULET); // Kaboo Orc Archer
		MONSTERS.put(20470, ORC_AMULET); // Kaboo Orc Grunt
		MONSTERS.put(20471, ORC_NECKLACE); // Kaboo Orc Fighter
		MONSTERS.put(20472, ORC_NECKLACE); // Kaboo Orc Fighter Leader
		MONSTERS.put(20473, ORC_NECKLACE); // Kaboo Orc Fighter Lieutenant
		MONSTERS.put(20372, ORC_NECKLACE); // Baraq Orc Fighter
		MONSTERS.put(20373, ORC_NECKLACE); // Baraq Orc Fighter Leader
	}
	// Misc
	private static final int MIN_LVL = 6;
	
	public Q00260_OrcHunting()
	{
		super(260, Q00260_OrcHunting.class.getSimpleName(), "Orc Hunting");
		addStartNpc(RAYEN);
		addTalkId(RAYEN);
		addKillId(MONSTERS.keySet());
		registerQuestItems(ORC_AMULET, ORC_NECKLACE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30221-04.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "30221-07.html":
			{
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30221-08.html":
			{
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		QuestState st = getQuestState(killer, false);
                if(st == null)
                    st = getRandomPartyMemberState(killer, 1, 3, npc);
		if ((st != null) && (Config.L2JMOD_QUEST_ITEM_ALWAYS_DROPS || getRandom(10) > 4))
		{
			st.giveItems(MONSTERS.get(npc.getId()), 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
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
				htmltext = (player.getRace() == Race.ELF) ? (player.getLevel() >= MIN_LVL) ? "30221-03.htm" : "30221-02.html" : "30221-01.html";
				break;
			}
			case State.STARTED:
			{
				if (hasAtLeastOneQuestItem(player, getRegisteredItemIds()))
				{
					final long amulets = st.getQuestItemsCount(ORC_AMULET);
					final long necklaces = st.getQuestItemsCount(ORC_NECKLACE);
                                        final long oan10 = ((amulets + necklaces) / 10) * 1000;
					st.giveAdenaFuzzy(((amulets * 12) + (necklaces * 30) + oan10), true);
					takeItems(player, -1, getRegisteredItemIds());
					giveNewbieReward(player);
					htmltext = "30221-06.html";
				}
				else
				{
					htmltext = "30221-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
