/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.scripts.ai.npc.Teleports;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.scripts.ai.npc.AbstractNpcAI;

/**
 * Elroki teleport AI.<br>
 * Original Jython script by kerberos_20
 * @author Plim
 */
public class ElrokiTeleporters extends AbstractNpcAI
{
	// NPCs
	private static final int ORAHOCHIN = 32111;
	private static final int GARIACHIN = 32112;
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (npc.getId())
		{
			case ORAHOCHIN:
			{
				if (player.isInCombat())
				{
					return "32111-no.htm";
				}
				player.teleToLocation(4990, -1879, -3178);
				break;
			}
			case GARIACHIN:
			{
				player.teleToLocation(7557, -5513, -3221);
				break;
			}
		}
		return super.onTalk(npc, player);
	}
	
	private ElrokiTeleporters()
	{
		super(ElrokiTeleporters.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(ORAHOCHIN, GARIACHIN);
		addTalkId(ORAHOCHIN, GARIACHIN);
	}
	
	public static void main(String[] args)
	{
		new ElrokiTeleporters();
	}
}
