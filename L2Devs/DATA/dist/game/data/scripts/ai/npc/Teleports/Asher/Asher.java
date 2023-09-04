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
package ai.npc.Teleports.Asher;

import org.l2jdevs.gameserver.model.Location;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.itemcontainer.Inventory;

import ai.npc.AbstractNpcAI;

/**
 * Asher AI.
 * @author Adry_85
 * @since 2.6.0.0
 */
public class Asher extends AbstractNpcAI
{
	// NPC
	private static final int ASHER = 32714;
	// Location
	private static final Location LOCATION = new Location(43835, -47749, -792);
	// Misc
	private static final int ADENA = 50000;
	
	private Asher()
	{
		super(Asher.class.getSimpleName(), "ai/npc/Teleports");
		addFirstTalkId(ASHER);
		addStartNpc(ASHER);
	}
	
	public static void main(String[] args)
	{
		new Asher();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("teleport"))
		{
			if (player.getAdena() >= ADENA)
			{
				player.teleToLocation(LOCATION);
				takeItems(player, Inventory.ADENA_ID, ADENA);
			}
			else
			{
				return "32714-02.html";
			}
		}
		else if (event.equals("32714-01.html"))
		{
			return event;
		}
		return super.onAdvEvent(event, npc, player);
	}
}