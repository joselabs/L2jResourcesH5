package l2r.gameserver.scripts.quests;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.util.Rnd;

public class Q10502_FreyaEmbroideredSoulCloak extends Quest
{
	
	// NPC
	private static final int Olfadams = 32612;
	private static final int Freya = 29179;
	// Item
	private static final int Freyasoulfragment = 21723;
	
	// Reward
	private static final int CloakofFreya = 21720;
	
	public Q10502_FreyaEmbroideredSoulCloak()
	{
		super(10502, Q10502_FreyaEmbroideredSoulCloak.class.getSimpleName(), "Freya Embroidered Soul Cloak");
		addStartNpc(Olfadams);
		addTalkId(Olfadams);
		addKillId(Freya);
		
		questItemIds = new int[]
		{
			Freyasoulfragment
		};
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("32612-01.htm"))
		{
			st.set("cond", "1");
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
			htmltext = "32612-01.htm";
		}
		else if (event.equalsIgnoreCase("32612-03.htm"))
		{
			if (st.getQuestItemsCount(Freyasoulfragment) < 20)
			{
				st.set("cond", "1");
				st.playSound("ItemSound.quest_middle");
				htmltext = "32612-error.htm";
			}
			else
			{
				st.giveItems(CloakofFreya, 1);
				st.takeItems(Freyasoulfragment, 20);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(false);
				htmltext = "32612-reward.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (st.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		else if (st.isCreated())
		{
			if (player.getLevel() < 82)
			{
				htmltext = "32612-level_error.htm";
			}
			else
			{
				htmltext = "32612-00.htm";
			}
		}
		else if (st.getInt("cond") == 2)
		{
			htmltext = "32612-02.htm";
		}
		else
		{
			htmltext = "32612-01.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMember(player, 1);
		
		if (partyMember == null)
		{
			return super.onKill(npc, player, isPet);
		}
		
		QuestState st = partyMember.getQuestState(getName());
		
		if (st != null)
		{
			if (st.getQuestItemsCount(Freyasoulfragment) <= 19)
			{
				if (st.getQuestItemsCount(Freyasoulfragment) == 18)
				{
					st.giveItems(Freyasoulfragment, Rnd.get(1, 2));
					st.playSound("ItemSound.quest_itemget");
				}
				else if (st.getQuestItemsCount(Freyasoulfragment) == 19)
				{
					st.giveItems(Freyasoulfragment, 1);
					st.playSound("ItemSound.quest_itemget");
				}
				else
				{
					st.giveItems(Freyasoulfragment, Rnd.get(1, 3));
					st.playSound("ItemSound.quest_itemget");
				}
				if (st.getQuestItemsCount(Freyasoulfragment) >= 20)
				{
					st.set("cond", "2");
					st.playSound("ItemSound.quest_middle");
				}
			}
		}
		
		if (player.getParty() != null)
		{
			QuestState st2;
			for (L2PcInstance pmember : player.getParty().getMembers())
			{
				st2 = pmember.getQuestState(getName());
				
				if ((st2 != null) && (st2.getInt("cond") == 1) && (pmember.getObjectId() != partyMember.getObjectId()))
				{
					if (st2.getQuestItemsCount(Freyasoulfragment) <= 19)
					{
						if (st2.getQuestItemsCount(Freyasoulfragment) == 18)
						{
							st2.giveItems(Freyasoulfragment, Rnd.get(1, 2));
							st2.playSound("ItemSound.quest_itemget");
						}
						else if (st2.getQuestItemsCount(Freyasoulfragment) == 19)
						{
							st2.giveItems(Freyasoulfragment, 1);
							st2.playSound("ItemSound.quest_itemget");
						}
						else
						{
							st2.giveItems(Freyasoulfragment, Rnd.get(1, 3));
							st2.playSound("ItemSound.quest_itemget");
						}
						if (st2.getQuestItemsCount(Freyasoulfragment) >= 20)
						{
							st2.set("cond", "2");
							st2.playSound("ItemSound.quest_middle");
						}
					}
				}
			}
		}
		return super.onKill(npc, player, isPet);
	}
}