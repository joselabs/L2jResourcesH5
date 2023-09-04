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
package quests.Q00380_BringOutTheFlavorOfIngredients;

import java.util.HashMap;
import java.util.Map;

import org.l2jdevs.Config;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.holders.ItemChanceHolder;
import org.l2jdevs.gameserver.model.holders.ItemHolder;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.model.quest.State;

/**
 * Bring Out the Flavor of Ingredients! (380)
 * @author Pandragon
 */
public final class Q00380_BringOutTheFlavorOfIngredients extends Quest
{
	// NPC
	private static final int ROLLAND = 30069;
	// Items
	private static final int ANTIDOTE = 1831;
	private static final int RITRON_FRUIT = 5895;
	private static final int MOON_FLOWER = 5896;
	private static final int LEECH_FLUIDS = 5897;

    private static final int SRC_ANTIDOTE = 0x0,
        SRC_RITRON_FRUIT = 20205,
        SRC_MOON_FLOWER = 20206,
        SRC_LEECH_FLUIDS = 20225,
        NUMBER_ANTIDOTE = 2,
        NUMBER_RITRON_FRUIT = 4,
        NUMBER_MOON_FLOWER = 20,
        NUMBER_LEECH_FLUIDS = 10;

	// Monsters
	private static final Map<Integer, ItemChanceHolder> MONSTER_CHANCES = new HashMap<>();
	// Rewards
	private static final int RITRON_RECIPE = 5959;
	private static final int RITRON_DESSERT = 5960;
	// Misc
	private static final int MIN_LVL = 24;
	{
		MONSTER_CHANCES.put(SRC_RITRON_FRUIT, new ItemChanceHolder(RITRON_FRUIT, 0.1, NUMBER_RITRON_FRUIT)); // Dire Wolf
		MONSTER_CHANCES.put(SRC_MOON_FLOWER, new ItemChanceHolder(MOON_FLOWER, 0.5, NUMBER_MOON_FLOWER)); // Kadif Werewolf
		MONSTER_CHANCES.put(SRC_LEECH_FLUIDS, new ItemChanceHolder(LEECH_FLUIDS, 0.5, NUMBER_LEECH_FLUIDS)); // Giant Mist Leech
	}

	public Q00380_BringOutTheFlavorOfIngredients()
	{
		super(380, Q00380_BringOutTheFlavorOfIngredients.class.getSimpleName(), "Bring Out the Flavor of Ingredients!");
		addStartNpc(ROLLAND);
		addTalkId(ROLLAND);
		addKillId(MONSTER_CHANCES.keySet());
		registerQuestItems(RITRON_FRUIT, MOON_FLOWER, LEECH_FLUIDS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs != null)
		{
			switch (event)
			{
				case "30069-03.htm":
				case "30069-04.htm":
				case "30069-06.html":
				{
					htmltext = event;
					break;
				}
				case "30069-05.htm":
				{
					if (qs.isCreated())
					{
						qs.startQuest();
						htmltext = event;
					}
					break;
				}
				case "30069-13.html":
				{
					if (qs.isCond(9))
					{
						rewardItems(player, RITRON_RECIPE, 1);
						qs.exitQuest(true, true);
						htmltext = event;
					}
					break;
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if ((qs != null) && (qs.getCond() < 4))
		{
			final ItemChanceHolder item = MONSTER_CHANCES.get(npc.getId());
			if (hasItem(qs.getPlayer(), new ItemHolder(RITRON_FRUIT, 4)) && hasItem(qs.getPlayer(), new ItemHolder(MOON_FLOWER, 20)) && hasItem(qs.getPlayer(), new ItemHolder(LEECH_FLUIDS, 10)) && hasItem(qs.getPlayer(), new ItemHolder(ANTIDOTE, 2)))
			{
				qs.setCond(qs.getCond() + 1, true);
			}
			else
			{
				giveItemRandomly(qs.getPlayer(), npc, item.getId(), 1, item.getCount(), getEffectiveChance(item.getChance()), true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = talker.getLevel() >= MIN_LVL ? "30069-02.htm" : "30069-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					case 2:
					case 3:
					case 4: {
						if ((getQuestItemsCount(talker, ANTIDOTE) >= 2)
                                                    && (getQuestItemsCount(talker, RITRON_FRUIT) >= NUMBER_RITRON_FRUIT)
                                                    && (getQuestItemsCount(talker, MOON_FLOWER) >= NUMBER_MOON_FLOWER)
                                                    && (getQuestItemsCount(talker, LEECH_FLUIDS) >= NUMBER_LEECH_FLUIDS)) {
							takeItems(talker, ANTIDOTE, NUMBER_ANTIDOTE);
							takeItems(talker, -1, RITRON_FRUIT, MOON_FLOWER, LEECH_FLUIDS);
							qs.setCond(5, true);
							htmltext = "30069-08.html";
						}
						else
						{
							htmltext = "30069-07.html";
						}
						break;
					}
					case 5:
					{
						qs.setCond(6, true);
						htmltext = "30069-09.html";
						break;
					}
					case 6:
					{
						qs.setCond(7, true);
						htmltext = "30069-10.html";
						break;
					}
					case 7:
					{
						qs.setCond(8, true);
						htmltext = "30069-11.html";
						break;
					}
					case 8:
					{
						rewardItems(talker, RITRON_DESSERT, 1);
						if (getRandom(100) < 56)
						{
							htmltext = "30069-15.html";
							qs.exitQuest(true, true);
						}
						else
						{
							qs.setCond(9, true);
							htmltext = "30069-12.html";
						}
						break;
					}
					case 9:
					{
						htmltext = "30069-12.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(talker);
				break;
			}
		}
		return htmltext;
	}
}
