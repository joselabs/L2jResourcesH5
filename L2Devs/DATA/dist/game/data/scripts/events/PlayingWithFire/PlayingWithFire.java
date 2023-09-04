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
package events.PlayingWithFire;

import org.l2jdevs.gameserver.data.xml.impl.MultisellData;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.event.LongTimeEvent;

/**
 * Playing With Fire event.
 * @author U3Games, Sacrifice
 */
public final class PlayingWithFire extends LongTimeEvent
{
	private static final int EVENT_MANAGER = 32099; // Tony the Cat
	
	private PlayingWithFire()
	{
		super(PlayingWithFire.class.getSimpleName(), "events");
		addFirstTalkId(EVENT_MANAGER);
		addStartNpc(EVENT_MANAGER);
		addTalkId(EVENT_MANAGER);
	}
	
	public static void main(String[] args)
	{
		new PlayingWithFire();
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		MultisellData.getInstance().separateAndSend(10005, player, npc, false);
		return null;
	}
}