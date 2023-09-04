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
package quests.Q00257_TheGuardIsBusy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jdevs.gameserver.enums.audio.Sound;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.holders.ItemHolder;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.model.quest.State;

/**
 * The Guard is Busy (257)
 * @author xban1x
 */
public final class Q00257_TheGuardIsBusy extends Quest
{
	// NPC
	private static final int GILBERT = 30039;
	
	// Misc
	private static final int MIN_LVL = 6;
	// Monsters
	private static final Map<Integer, List<MobDrop>> MONSTERS = new HashMap<>();
	// Items
	private static final int GLUDIO_LORDS_MARK = 1084;
	private static final int ORC_AMULET = 752;
	private static final int ORC_NECKLACE = 1085;
	private static final int WEREWOLF_FANG = 1086;
	static
	{
		MONSTERS.put(20006, Arrays.asList(new MobDrop(10, 2, ORC_AMULET, 2), new MobDrop(10, 10, ORC_AMULET, 1))); // Orc Archer
		MONSTERS.put(20093, Arrays.asList(new MobDrop(100, 85, ORC_NECKLACE, 1))); // Orc Fighter
		MONSTERS.put(20096, Arrays.asList(new MobDrop(100, 95, ORC_NECKLACE, 1))); // Orc Fighter Sub Leader
		MONSTERS.put(20098, Arrays.asList(new MobDrop(100, 100, ORC_NECKLACE, 1)));// Orc Fighter Leader
		MONSTERS.put(20130, Arrays.asList(new MobDrop(10, 7, ORC_AMULET, 1))); // Orc
		MONSTERS.put(20131, Arrays.asList(new MobDrop(10, 9, ORC_AMULET, 1))); // Orc Grunt
		MONSTERS.put(20132, Arrays.asList(new MobDrop(10, 7, WEREWOLF_FANG, 1))); // Werewolf
		MONSTERS.put(20342, Arrays.asList(new MobDrop(0, 1, WEREWOLF_FANG, 1))); // Werewolf Chieftain
		MONSTERS.put(20343, Arrays.asList(new MobDrop(100, 85, WEREWOLF_FANG, 1))); // Werewolf Hunter
		MONSTERS.put(20450, Arrays.asList(new MobDrop(10, 7, WEREWOLF_FANG, 1))); // relic Werewolf
		MONSTERS.put(20361, Arrays.asList(new MobDrop(10, 2, ORC_AMULET, 2), new MobDrop(10, 10, ORC_AMULET, 1))); // tunath Orc Archer
		MONSTERS.put(20362, Arrays.asList(new MobDrop(100, 85, ORC_NECKLACE, 1))); // tunath Orc warrior
	}
	
	public Q00257_TheGuardIsBusy()
	{
		super(257, Q00257_TheGuardIsBusy.class.getSimpleName(), "The Guard is Busy");
		addStartNpc(GILBERT);
		addTalkId(GILBERT);
		addKillId(MONSTERS.keySet());
		registerQuestItems(ORC_AMULET, GLUDIO_LORDS_MARK, ORC_NECKLACE, WEREWOLF_FANG);
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
			case "30039-03.htm":
			{
				st.startQuest();
				st.giveItems(GLUDIO_LORDS_MARK, 1);
				htmltext = event;
				break;
			}
			case "30039-05.html":
			{
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30039-06.html":
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
		final QuestState st = getQuestState(killer, false);
		if (st == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		for (MobDrop drop : MONSTERS.get(npc.getId()))
		{
			if (drop.getDrop())
			{
				st.giveItems(drop);
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				break;
			}
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
				htmltext = (player.getLevel() >= MIN_LVL) ? "30039-02.htm" : "30039-01.html";
				break;
			}
			case State.STARTED:
			{
				if (hasAtLeastOneQuestItem(player, ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG))
				{
					final long amulets = st.getQuestItemsCount(ORC_AMULET);
					final long common = getQuestItemsCount(player, ORC_NECKLACE, WEREWOLF_FANG);
                                        final long anw10 = ((amulets + common) / 10) * 1000;
					st.giveAdenaFuzzy((amulets * 10) + (common * 20) + anw10, true);
					takeItems(player, -1, ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG);
					giveNewbieReward(player);
					htmltext = "30039-07.html";
				}
				else
				{
					htmltext = "30039-04.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	public final static class MobDrop extends ItemHolder
	{
		private final int _chance;
		private final int _random;
		
		public MobDrop(int random, int chance, int id, long count)
		{
			super(id, count);
			_random = random;
			_chance = chance;
		}
		
		public boolean getDrop()
		{
			return (getRandom(_random) < _chance);
		}
	}
}
