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
package quests.Q00640_TheZeroHour;

import org.l2jdevs.Config;
import org.l2jdevs.gameserver.enums.audio.Sound;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.model.quest.State;

import quests.Q00109_InSearchOfTheNest.Q00109_InSearchOfTheNest;

/**
 * The Zero Hour (640)<br>
 * @author Sacrifice
 */
public class Q00640_TheZeroHour extends Quest
{
	private static final int FANG_OF_STAKATO = 8085;
	
	private static final int KAHMAN = 31554;
	
	private static final int MIN_LEVEL = 66;
	
	private static final int[] MONSTERS_TO_HUNT =
	{
		22617, // Spiked Stakato
		22618, // Spiked Stakato Worker
		22619, // Spiked Stakato Guard
		22620, // Female Spiked Stakato
		22621, // Male Spiked Stakato
		22622, // Male Spiked Stakato
		22623, // Spiked Stakato Sorcerer
		22625, // Cannibalistic Stakato Leader
		22626, // Cannibalistic Stakato Leader
		22627, // Spiked Stakato Soldier
		22628, // Spiked Stakato Drone
		22629, // Spiked Stakato Captain
		22630, // Spike Stakato Nurse
		22631, // Spike Stakato Nurse
		22633 // Spiked Stakato Shaman
	};
	
	public Q00640_TheZeroHour()
	{
		super(640, Q00640_TheZeroHour.class.getSimpleName(), "The Zero Hour");
		addStartNpc(KAHMAN);
		addTalkId(KAHMAN);
		addKillId(MONSTERS_TO_HUNT);
		registerQuestItems(FANG_OF_STAKATO);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState questState = getQuestState(player, false);
		String htmltext = null;
		
		if (questState == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31554-02.htm":
				questState.startQuest();
				break;
			case "31554-05.htm":
				htmltext = "31554-05.htm";
				break;
			case "31554-08.htm":
				questState.playSound(Sound.ITEMSOUND_QUEST_FINISH);
				questState.exitQuest(true);
				break;
			case "1":
				if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 12)
				{
					questState.takeItems(FANG_OF_STAKATO, 12);
					questState.rewardItems(4042, 1); // Enria
					htmltext = "31554-09.htm";
				}
				else
				{
					htmltext = "31554-07.htm";
				}
				break;
			case "2":
				if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 6)
				{
					questState.takeItems(FANG_OF_STAKATO, 6);
					questState.rewardItems(4043, 1); // Asofe
					htmltext = "31554-09.htm";
				}
				else
				{
					htmltext = "31554-07.htm";
				}
				break;
			case "3":
				if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 6)
				{
					questState.takeItems(FANG_OF_STAKATO, 6);
					questState.rewardItems(4044, 1); // Thons
					htmltext = "31554-09.htm";
				}
				else
				{
					htmltext = "31554-07.htm";
				}
				break;
			case "4":
				if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 81)
				{
					questState.takeItems(FANG_OF_STAKATO, 81);
					questState.rewardItems(1887, 10); // Varnish of Purity
					htmltext = "31554-09.htm";
				}
				else
				{
					htmltext = "31554-07.htm";
				}
				break;
			case "5":
				if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 33)
				{
					questState.takeItems(FANG_OF_STAKATO, 33);
					questState.rewardItems(1888, 5); // Synthetic Cokes
					htmltext = "31554-09.htm";
				}
				else
				{
					htmltext = "31554-07.htm";
				}
				break;
			case "6":
				if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 30)
				{
					questState.takeItems(FANG_OF_STAKATO, 30);
					questState.rewardItems(1889, 10); // Compound Braid
					htmltext = "31554-09.htm";
				}
				else
				{
					htmltext = "31554-07.htm";
				}
				break;
			case "7":
				if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 150)
				{
					questState.takeItems(FANG_OF_STAKATO, 150);
					questState.rewardItems(5550, 10); // Durable Metal Plate
					htmltext = "31554-09.htm";
				}
				else
				{
					htmltext = "31554-07.htm";
				}
				break;
			case "8":
				if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 131)
				{
					questState.takeItems(FANG_OF_STAKATO, 131);
					questState.rewardItems(1890, 10); // Mithril Alloy
					htmltext = "31554-09.htm";
				}
				else
				{
					htmltext = "31554-07.htm";
				}
				break;
			case "9":
				if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 123)
				{
					questState.takeItems(FANG_OF_STAKATO, 123);
					questState.rewardItems(1893, 5); // Oriharukon
					htmltext = "31554-09.htm";
				}
				else
				{
					htmltext = "31554-07.htm";
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState questState = getQuestState(killer, false);
		final L2PcInstance partyMember = getRandomPartyMemberState(killer, State.STARTED);
		
		if (partyMember == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		else if (partyMember.getQuestState(Q00640_TheZeroHour.class.getSimpleName()) == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		else
		{
			questState.giveItems(FANG_OF_STAKATO, (long) Config.RATE_QUEST_DROP);
			questState.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState questState = getQuestState(talker, true);
		String htmlText = getNoQuestMsg(talker);
		
		switch (questState.getState())
		{
			case State.CREATED:
				if (talker.getLevel() >= MIN_LEVEL)
				{
					final QuestState questState2 = questState.getPlayer().getQuestState(Q00109_InSearchOfTheNest.class.getSimpleName());
					if ((questState2 != null) && (questState2.getState() == State.COMPLETED))
					{
						htmlText = "31554-01.htm";
					}
					else
					{
						htmlText = "31554-10.htm";
					}
				}
				else
				{
					htmlText = "31554-00.htm";
				}
				break;
			case State.STARTED:
				if (questState.isCond(1))
				{
					if (questState.getQuestItemsCount(FANG_OF_STAKATO) >= 1)
					{
						htmlText = "31554-04.htm";
					}
					else
					{
						htmlText = "31554-03.htm";
					}
				}
				break;
		}
		return htmlText;
	}
}