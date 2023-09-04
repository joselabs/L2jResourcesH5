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
package ai.npc.Dorian;

import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.network.NpcStringId;
import org.l2jdevs.gameserver.network.clientpackets.Say2;

import ai.npc.AbstractNpcAI;
import quests.Q00024_InhabitantsOfTheForestOfTheDead.Q00024_InhabitantsOfTheForestOfTheDead;

/**
 * Dorian (Raid Fighter) - Quest AI
 * @author malyelfik
 */
public final class Dorian extends AbstractNpcAI
{
	// NPC
	private static final int DORIAN = 25332;
	// Items
	private static final int SILVER_CROSS = 7153;
	private static final int BROKEN_SILVER_CROSS = 7154;
	
	private Dorian()
	{
		super(Dorian.class.getSimpleName(), "ai/npc");
		addSeeCreatureId(DORIAN);
	}
	
	public static void main(String[] args)
	{
		new Dorian();
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final L2PcInstance pl = creature.getActingPlayer();
			final QuestState qs = pl.getQuestState(Q00024_InhabitantsOfTheForestOfTheDead.class.getSimpleName());
			if ((qs != null) && qs.isCond(3))
			{
				takeItems(pl, SILVER_CROSS, -1);
				giveItems(pl, BROKEN_SILVER_CROSS, 1);
				qs.setCond(4, true);
				broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.THAT_SIGN);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
}