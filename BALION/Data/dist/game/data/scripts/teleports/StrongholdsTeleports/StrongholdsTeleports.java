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
package teleports.StrongholdsTeleports;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * Strongholds teleport AI.<br>
 * Original Jython script by Kerberos.
 * @author Plim
 */
public class StrongholdsTeleports extends L2AttackableAIScript
{
	// NPCs
	private final static int[] NPCs =
	{
		32163,
		32181,
		32184,
		32186
	};
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return player.getLevel() < 20 ? String.valueOf(npc.getNpcId()) + ".htm" : String.valueOf(npc.getNpcId()) + "-no.htm";
	}
	
	public StrongholdsTeleports()
	{
		super(-1, StrongholdsTeleports.class.getSimpleName(), "teleports");
		FirstTalkNpcs(NPCs);
	}
}
