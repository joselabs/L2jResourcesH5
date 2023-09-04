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
package quests.Q00258_BringWolfPelts;

import java.util.HashMap;
import java.util.Map;

import org.l2jdevs.Config;
import org.l2jdevs.gameserver.enums.audio.Sound;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.model.quest.State;

/**
 * Bring Wolf Pelts (258)
 * @author xban1x
 */
public final class Q00258_BringWolfPelts extends Quest
{
	// Npc
	private static final int LECTOR = 30001;
	// Item
	private static final int WOLF_PELT = 702;
	// Monsters
	private static final int[] MONSTERS = {
                20120, // 4 : Wolf
                20442, // 5 : Elder Wolf
                //20317, // 4 : Black Wolf -- Redbonet overtakes
                20456, // 4 : Ashen Wolf
                20475, // 4 : Kasha Wolf
                20525, // 4 : Gray Wolf
                20527, // 4 : Great Snow Wolf
                22232, // 4 : Mimir Black Wolf
                22233, // 5 : Dominant Black Wolf
                20318, // 6 : Black Timber Wolf
                20477, // 6 : Kasha Timber Wolf
                20524, // 7 : Grey Wolf Elder
                21121, // 23 : Kasha Dire Wolf
                20205, // 24 : Dire Wolf
                22001, // 25 : Grim Wolf
                20929, // 32 : Hatu Dire Wolf
                20783, // 35 : Dread Wolf
                21638 // 35 : Dread Wolf
	};
	// Rewards
	private static final int REWARD_COST_UPRANGE = 9900;
        private static final int[] REWARDS = {
            // 1119, 1100, 1103, 1122
            // price, itemId
            28, 36, // Leather Sandals
            60, 18, // Leather Shield
            90, 48, // Short Gloves
            470, 1129, // Crude Leather Shoes
            530, 1119, // Short Leather Gloves
            600, 1122, // Cotton Shoes
            700, 42, // Leather Cap
            1100, 463, // Feriotic Stockings
            1300, 29, // Leather Pants
            1518, 426, // Tunic
            1870, 428, // Feriotic Tunic
            1950, 22, // Leather Shirt
            2040, 37, // Leather Shoes
            2540, 49, // Gloves
            3820, 30, // Hard Leather Pants
            4300, 464, // Leather Stockings
            5210, 50, // Leather Gloves
            5460, 38, // Low Boots
            6120, 429, // Leather Tunic
            7820, 44, // Leather Helmet
            9700, 412, // Cotton Pants
            9840, 390, // Cotton Shirt 15600
        };
	// Misc
	private static final int MIN_LVL = 3;
	private static final int WOLF_PELT_COUNT = 40;
	
	public Q00258_BringWolfPelts()
	{
		super(258, Q00258_BringWolfPelts.class.getSimpleName(), "Bring Wolf Pelts");
		addStartNpc(LECTOR);
		addTalkId(LECTOR);
		addKillId(MONSTERS);
		registerQuestItems(WOLF_PELT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30001-03.html"))
		{
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
                final QuestState st = getRandomPartyMemberState(killer, 1, 3, npc);
		if ((st != null) && st.isCond(1))
		{
			st.giveItems(WOLF_PELT, 1);
			if (st.getQuestItemsCount(WOLF_PELT) >= WOLF_PELT_COUNT)
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
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() >= MIN_LVL) ? "30001-02.htm" : "30001-01.html";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30001-04.html";
						break;
					}
					case 2:
					{
						if (st.getQuestItemsCount(WOLF_PELT) >= WOLF_PELT_COUNT)
						{
							final int rng = getRandom(REWARD_COST_UPRANGE);
                                                        for(int i = 0; i < REWARDS.length; i += 2)
                                                            if(REWARDS[i] > rng) {
                                                                st.giveItems(REWARDS[i+1], 1);
                                                                break;
                                                            }
							st.exitQuest(true, true);
							htmltext = "30001-05.html";
							break;
						}
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
