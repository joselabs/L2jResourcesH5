/*
  * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package hellbound.TowerOfInfinitum;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.instancemanager.HellboundManager;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.util.Util;

/**
 * @author GKR
 */
public class TowerOfInfinitum extends Quest
{
	private static final int JERIAN = 32302;
	private static final int GK_FIRST = 32745;
	private static final int GK_LAST = 32752;
	
	private static final int PASS_SKILL = 2357;
	
	private static final Map<Integer, Location[]> TELE_COORDS = new HashMap<Integer, Location[]>();
	
	public TowerOfInfinitum()
	{
		super(-1, TowerOfInfinitum.class.getSimpleName(), "hellbound");
		StartNpcs(JERIAN);
		TalkNpcs(JERIAN);
		
		for (int i = GK_FIRST; i <= GK_LAST; i++)
		{
			StartNpcs(i);
			TalkNpcs(i);
		}
		
		TELE_COORDS.put(32745, new Location[]
		{
			new Location(-22208, 277122, -13376),
			null
		});
		TELE_COORDS.put(32746, new Location[]
		{
			new Location(-22208, 277106, -11648),
			new Location(-22208, 277074, -15040)
		});
		TELE_COORDS.put(32747, new Location[]
		{
			new Location(-22208, 277120, -9920),
			new Location(-22208, 277120, -13376)
		});
		TELE_COORDS.put(32748, new Location[]
		{
			new Location(-19024, 277126, -8256),
			new Location(-22208, 277106, -11648)
		});
		TELE_COORDS.put(32749, new Location[]
		{
			new Location(-19024, 277106, -9920),
			new Location(-22208, 277122, -9920)
		});
		TELE_COORDS.put(32750, new Location[]
		{
			new Location(-19008, 277100, -11648),
			new Location(-19024, 277122, -8256)
		});
		TELE_COORDS.put(32751, new Location[]
		{
			new Location(-19008, 277100, -13376),
			new Location(-19008, 277106, -9920)
		});
		TELE_COORDS.put(32752, new Location[]
		{
			new Location(14602, 283179, -7500),
			new Location(-19008, 277100, -11648)
		});
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		int npcId = npc.getNpcId();
		
		if (event.equalsIgnoreCase("enter") && (npcId == JERIAN))
		{
			if (HellboundManager.getInstance().getLevel() >= 11)
			{
				L2Party party = player.getParty();
				if ((party != null) && (party.getPartyLeaderOID() == player.getObjectId()))
				{
					for (L2PcInstance partyMember : party.getPartyMembers())
					{
						if (!Util.checkIfInRange(300, partyMember, npc, true) || (partyMember.getFirstEffect(PASS_SKILL) == null))
						{
							return "32302-02.htm";
						}
					}
					for (L2PcInstance partyMember : party.getPartyMembers())
					{
						partyMember.teleToLocation(-22204, 277056, -15023, true);
					}
					htmltext = null;
				}
				else
				{
					htmltext = "32302-02a.htm";
				}
			}
			else
			{
				htmltext = "32302-02b.htm";
			}
		}
		else if ((event.equalsIgnoreCase("up") || event.equalsIgnoreCase("down")) && (npcId >= GK_FIRST) && (npcId <= GK_LAST))
		{
			int direction = event.equalsIgnoreCase("up") ? 0 : 1;
			L2Party party = player.getParty();
			if (party == null)
			{
				htmltext = "gk-noparty.htm";
			}
			else if (party.getPartyLeaderOID() != player.getObjectId())
			{
				htmltext = "gk-noreq.htm";
			}
			else
			{
				for (L2PcInstance partyMember : party.getPartyMembers())
				{
					if (!Util.checkIfInRange(1000, partyMember, npc, false) || (Math.abs(partyMember.getZ() - npc.getZ()) > 100))
					{
						return "gk-noreq.htm";
					}
				}
				
				final Location tele = TELE_COORDS.get(npcId)[direction];
				if (tele != null)
				{
					for (L2PcInstance partyMember : party.getPartyMembers())
					{
						partyMember.teleToLocation(tele, true);
					}
				}
				htmltext = null;
			}
		}
		return htmltext;
	}
}
