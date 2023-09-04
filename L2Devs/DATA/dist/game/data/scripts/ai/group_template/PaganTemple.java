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

import org.l2jdevs.gameserver.model.Location;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.holders.SkillHolder;

import ai.npc.AbstractNpcAI;

/**
 * Pagan Temple AI.
 * @author ShinichiYao
 */
public final class PaganTemple extends AbstractNpcAI
{
	final private static int TRIOL_LAYPERSON = 22142; // Triol's Layperson
	final private static int TRIOL_BELIEVER = 22143; // Triol's Believer
	final private static int TRIOL_PRIEST_1 = 22146; // Triol's Priest
	final private static int TRIOL_PRIEST_2 = 22151; // Triol's Priest
	final private static int ANDREAS_CAPTAIN = 22175; // Andreas Captain of the Royal Guard
	
	private static final SkillHolder WIDE_WILD_SWEEP = new SkillHolder(4612, 9); // NPC Wide Wild Sweep
	
	private static final Location[] LOC =
	{
		new Location(-16128, -35888, -10726),
		new Location(-17029, -39617, -10724),
		new Location(-15729, -42001, -10724)
	};
	
	private PaganTemple()
	{
		super(PaganTemple.class.getSimpleName(), "ai/group_template");
		addAttackId(ANDREAS_CAPTAIN);
		addAggroRangeEnterId(ANDREAS_CAPTAIN, TRIOL_LAYPERSON, TRIOL_BELIEVER, TRIOL_PRIEST_1, TRIOL_PRIEST_2);
	}
	
	public static void main(String[] args)
	{
		new PaganTemple();
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		switch (npc.getId())
		{
			case ANDREAS_CAPTAIN:
			{
				if ((player != null) && player.isInParty() && (player.getParty().getMemberCount() == 9) && npc.isScriptValue(0))
				{
					final L2PcInstance target = player.getParty().getMembers().get(getRandom(player.getParty().getMembers().size()));
					if ((target != null) && (npc.calculateDistance(target, true, false) < 1500))
					{
						target.teleToLocation(LOC[getRandom(2)]);
						npc.setScriptValue(1);
						break;
					}
				}
				break;
			}
			case TRIOL_LAYPERSON:
			case TRIOL_BELIEVER:
			case TRIOL_PRIEST_1:
			case TRIOL_PRIEST_2:
			{
				if ((player != null) && player.isInParty() && (player.getParty().getMemberCount() > 5) && npc.isScriptValue(0))
				{
					final L2PcInstance target = player.getParty().getMembers().get(getRandom(player.getParty().getMembers().size()));
					if ((target != null) && (npc.calculateDistance(target, true, false) < 1500))
					{
						target.teleToLocation(LOC[getRandom(2)]);
						npc.setScriptValue(1);
						break;
					}
				}
				break;
			}
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final L2MonsterInstance mob = (L2MonsterInstance) npc;
		if ((getRandom(10) < 3) && (mob.getCurrentHp() < (mob.getMaxHp() * 0.7)))
		{
			mob.doCast(WIDE_WILD_SWEEP);
			mob.doDie(attacker);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
}