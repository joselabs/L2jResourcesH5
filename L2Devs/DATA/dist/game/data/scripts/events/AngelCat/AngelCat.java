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
package events.AngelCat;

import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.event.LongTimeEvent;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.serverpackets.SystemMessage;

/**
 * Angel Cat event.
 * @author U3Games, Sacrifice
 */
public final class AngelCat extends LongTimeEvent
{
	private static final int ANGEL_CAT = 4308;
	private static final int GIFT_AMOUNT = 1;
	private static final int GIFT_ID = 21726; // Angel Cat's Blessing Event
	private static final int GIFT_REUSE = 24; // Retail 24 hours
	private static final String GIFT_REUSE_VARIABLE = AngelCat.class.getSimpleName() + "_reuse";
	
	private AngelCat()
	{
		super(AngelCat.class.getSimpleName(), "events");
		addFirstTalkId(ANGEL_CAT);
		addStartNpc(ANGEL_CAT);
		addTalkId(ANGEL_CAT);
	}
	
	public static void main(String[] args)
	{
		new AngelCat();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "gift":
			{
				final long reuse = player.getVariables().getLong(GIFT_REUSE_VARIABLE, 0);
				if (reuse > System.currentTimeMillis())
				{
					final long remainingTime = (reuse - System.currentTimeMillis()) / 1000;
					final int hours = (int) (remainingTime / 3600);
					final int minutes = (int) ((remainingTime % 3600) / 60);
					final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.AVAILABLE_AFTER_S1_S2_HOURS_S3_MINUTES);
					sm.addItemName(GIFT_ID);
					sm.addInt(hours);
					sm.addInt(minutes);
					player.sendPacket(sm);
				}
				else
				{
					player.addItem("AngelCat-Gift", GIFT_ID, GIFT_AMOUNT, npc, true);
					player.getVariables().set(GIFT_REUSE_VARIABLE, System.currentTimeMillis() + (GIFT_REUSE * 3600000));
				}
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + ".htm";
	}
}