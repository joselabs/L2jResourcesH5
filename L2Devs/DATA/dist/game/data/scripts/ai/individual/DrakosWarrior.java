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
package ai.individual;

import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.holders.SkillHolder;

import ai.npc.AbstractNpcAI;

/**
 * Drakos Warrior AI.
 * @author Adry_85
 * @since 2.6.0.0
 */
public class DrakosWarrior extends AbstractNpcAI
{
	// NPCs
	private static final int DRAKOS_WARRIOR = 22822;
	private static final int DRAKOS_ASSASSIN = 22823;
	// Skill
	private static final SkillHolder SUMMON = new SkillHolder(6858);
	
	public DrakosWarrior()
	{
		super(DrakosWarrior.class.getSimpleName(), "ai/individual");
		addAttackId(DRAKOS_WARRIOR);
	}
	
	public static void main(String[] args)
	{
		new DrakosWarrior();
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (getRandom(100) < 1)
		{
			addSkillCastDesire(npc, npc, SUMMON, 99999999900000000L);
			final int count = 2 + getRandom(3);
			for (int i = 0; i < count; i++)
			{
				addSpawn(DRAKOS_ASSASSIN, npc.getX() + getRandom(200), npc.getY() + getRandom(200), npc.getZ(), 0, false, 0, false);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
}
