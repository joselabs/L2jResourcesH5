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
package instances.SecretAreaInTheKeucereusFortress1;

import org.l2jdevs.gameserver.model.Location;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.instancezone.InstanceWorld;
import org.l2jdevs.gameserver.model.quest.QuestState;

import instances.AbstractInstance;
import quests.Q10270_BirthOfTheSeed.Q10270_BirthOfTheSeed;

/**
 * Secret Area in the Keucereus Fortress instance zone.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class SecretAreaInTheKeucereusFortress1 extends AbstractInstance
{
	// NPC
	private static final int GINBY = 32566;
	
	// Location
	private static final Location START_LOC = new Location(-23530, -8963, -5413);
	// Misc
	private static final int TEMPLATE_ID = 117;
	public SecretAreaInTheKeucereusFortress1()
	{
		super(SecretAreaInTheKeucereusFortress1.class.getSimpleName());
		addStartNpc(GINBY);
		addTalkId(GINBY);
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance)
	{
		if (firstEntrance)
		{
			world.addAllowed(player.getObjectId());
		}
		teleportPlayer(player, START_LOC, world.getInstanceId(), false);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(Q10270_BirthOfTheSeed.class.getSimpleName());
		if ((st != null) && (st.getMemoState() >= 5) && (st.getMemoState() < 20))
		{
			enterInstance(player, new SAKF1World(), "SecretAreaInTheKeucereusFortress.xml", TEMPLATE_ID);
			if (st.isMemoState(5))
			{
				st.setMemoState(10);
			}
			return "32566-01.html";
		}
		return super.onTalk(npc, player);
	}
	
	protected class SAKF1World extends InstanceWorld
	{
		
	}
}
