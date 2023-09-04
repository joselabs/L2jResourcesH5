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
package village_master.DwarfWarehouseChange1;

import org.l2jdevs.Config;
import org.l2jdevs.gameserver.enums.CategoryType;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.base.ClassId;

import ai.npc.AbstractNpcAI;

/**
 * Dwarf class transfer AI.
 * @author Adry_85
 */
public final class DwarfWarehouseChange1 extends AbstractNpcAI
{
	// NPCs
	private static int[] NPCS =
	{
		30498, // Moke
		30503, // Rikadio
		30594, // Ranspo
		32092, // Alder
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
	private static final int RING_OF_RAVEN = 1642;
	// Class
	private static final int SCAVENGER = 54;
	
	private DwarfWarehouseChange1()
	{
		super(DwarfWarehouseChange1.class.getSimpleName(), "village_master");
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	public static void main(String[] args)
	{
		new DwarfWarehouseChange1();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "30498-01.htm": // warehouse_chief_moke003f
			case "30498-02.htm": // warehouse_chief_moke006fa
			case "30498-03.htm": // warehouse_chief_moke007fa
			case "30498-04.htm": // warehouse_chief_moke006fb
			case "30503-01.htm": // warehouse_chief_rikadio003f
			case "30503-02.htm": // warehouse_chief_rikadio006fa
			case "30503-03.htm": // warehouse_chief_rikadio007fa
			case "30503-04.htm": // warehouse_chief_rikadio006fb
			case "30594-01.htm": // warehouse_chief_ranspo003f
			case "30594-02.htm": // warehouse_chief_ranspo006fa
			case "30594-03.htm": // warehouse_chief_ranspo007fa
			case "30594-04.htm": // warehouse_chief_ranspo006fb
			case "32092-01.htm": // warehouse_chief_older003f
			case "32092-02.htm": // warehouse_chief_older006fa
			case "32092-03.htm": // warehouse_chief_older007fa
			case "32092-04.htm": // warehouse_chief_older006fb
			{
				htmltext = event;
				break;
			}
			case "54":
			{
				htmltext = ClassChangeRequested(player, npc, Integer.valueOf(event));
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP))
		{
			htmltext = npc.getId() + "-01.htm"; // fnClassList1
		}
		else
		{
			htmltext = npc.getId() + "-05.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	private String ClassChangeRequested(L2PcInstance player, L2Npc npc, int classId)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP))
		{
			htmltext = npc.getId() + "-06.htm"; // fnYouAreSecondClass
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			htmltext = npc.getId() + "-07.htm"; // fnYouAreThirdClass
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
		{
			htmltext = "30498-12.htm"; // fnYouAreFourthClass
		}
		else if ((classId == SCAVENGER) && (player.getClassId() == ClassId.dwarvenFighter))
		{
			if (player.getLevel() < 20)
			{
				if (hasQuestItems(player, RING_OF_RAVEN))
				{
					htmltext = npc.getId() + "-08.htm"; // fnLowLevel11
				}
				else
				{
					htmltext = npc.getId() + "-09.htm"; // fnLowLevelNoProof11
				}
			}
			else if (hasQuestItems(player, RING_OF_RAVEN))
			{
				takeItems(player, RING_OF_RAVEN, -1);
				player.setClassId(SCAVENGER);
				player.setBaseClass(SCAVENGER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				if(Config.L2JMOD_SHADOW_COUPONS)
					giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
				htmltext = npc.getId() + "-10.htm"; // fnAfterClassChange11
			}
			else
			{
				htmltext = npc.getId() + "-11.htm"; // fnNoProof11
			}
		}
		return htmltext;
	}
}
