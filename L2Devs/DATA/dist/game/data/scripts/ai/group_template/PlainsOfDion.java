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
package ai.group_template;

import org.l2jdevs.gameserver.GeoData;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.network.NpcStringId;
import org.l2jdevs.gameserver.network.clientpackets.Say2;
import org.l2jdevs.gameserver.util.Util;

import ai.npc.AbstractNpcAI;

/**
 * AI for mobs in Plains of Dion (near Floran Village).
 * @author Gladicek
 */
public final class PlainsOfDion extends AbstractNpcAI
{
	private static final int DELU_LIZARDMEN[] =
	{
		21104, // Delu Lizardman Supplier
		21105, // Delu Lizardman Special Agent
		21107, // Delu Lizardman Commander
	};
	
	private static final NpcStringId[] MONSTERS_MSG =
	{
		NpcStringId.S1_HOW_DARE_YOU_INTERRUPT_OUR_FIGHT_HEY_GUYS_HELP,
		NpcStringId.S1_HEY_WERE_HAVING_A_DUEL_HERE,
		NpcStringId.THE_DUEL_IS_OVER_ATTACK,
		NpcStringId.FOUL_KILL_THE_COWARD,
		NpcStringId.HOW_DARE_YOU_INTERRUPT_A_SACRED_DUEL_YOU_MUST_BE_TAUGHT_A_LESSON
	};
	
	private static final NpcStringId[] MONSTERS_ASSIST_MSG =
	{
		NpcStringId.DIE_YOU_COWARD,
		NpcStringId.KILL_THE_COWARD,
		NpcStringId.WHAT_ARE_YOU_LOOKING_AT
	};
	
	private PlainsOfDion()
	{
		super(PlainsOfDion.class.getSimpleName(), "ai/group_template");
		addAttackId(DELU_LIZARDMEN);
	}
	
	public static void main(String[] args)
	{
		new PlainsOfDion();
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon)
	{
		if (npc.isScriptValue(0))
		{
			final int i = getRandom(5);
			if (i < 2)
			{
				broadcastNpcSay(npc, Say2.NPC_ALL, MONSTERS_MSG[i], player.getName());
			}
			else
			{
				broadcastNpcSay(npc, Say2.NPC_ALL, MONSTERS_MSG[i]);
			}
			
			for (L2Character obj : npc.getKnownList().getKnownCharactersInRadius(npc.getTemplate().getClanHelpRange()))
			{
				if (obj.isMonster() && Util.contains(DELU_LIZARDMEN, obj.getId()) && !obj.isAttackingNow() && !obj.isDead() && GeoData.getInstance().canSeeTarget(npc, obj))
				{
					final L2Npc monster = (L2Npc) obj;
					addAttackDesire(monster, player);
					broadcastNpcSay(monster, Say2.NPC_ALL, MONSTERS_ASSIST_MSG[getRandom(3)]);
				}
			}
			npc.setScriptValue(1);
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
}