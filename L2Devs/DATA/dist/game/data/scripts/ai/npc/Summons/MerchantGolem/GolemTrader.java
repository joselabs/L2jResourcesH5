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
package ai.npc.Summons.MerchantGolem;

import org.l2jdevs.gameserver.model.actor.L2Npc;

import ai.npc.AbstractNpcAI;

/**
 * Golem Trader AI.
 * @author Zoey76
 */
public final class GolemTrader extends AbstractNpcAI
{
	// NPC
	private static final int GOLEM_TRADER = 13128;
	
	private GolemTrader()
	{
		super(GolemTrader.class.getSimpleName(), "ai/npc/Summons");
		addSpawnId(GOLEM_TRADER);
	}
	
	public static void main(String[] args)
	{
		new GolemTrader();
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.scheduleDespawn(180000);
		return super.onSpawn(npc);
	}
}
