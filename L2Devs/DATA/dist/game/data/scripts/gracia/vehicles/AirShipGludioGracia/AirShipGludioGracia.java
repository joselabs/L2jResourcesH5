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
package gracia.vehicles.AirShipGludioGracia;

import org.l2jdevs.gameserver.ThreadPoolManager;
import org.l2jdevs.gameserver.instancemanager.AirShipManager;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.L2World;
import org.l2jdevs.gameserver.model.Location;
import org.l2jdevs.gameserver.model.VehiclePathPoint;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.instance.L2AirShipInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.network.NpcStringId;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.network.clientpackets.Say2;
import org.l2jdevs.gameserver.network.serverpackets.NpcSay;

import gracia.vehicles.AirShipController;

/**
 * @author DS, Sacrifice
 */
public final class AirShipGludioGracia extends AirShipController
{
	private static final int[] CONTROLLERS =
	{
		32607, // Keucereus Alliance Base Controller
		32609 // Warf of Gludio Airship's Controller
	};
	
	private static final int GLUDIO_DOCK_ID = 10; // Warf of Gludio Airship's
	private static final int GRACIA_DOCK_ID = 11; // Keucereus Alliance Base
	
	protected static final Location OUST_GLUDIO = new Location(-149392, 255232, -80);
	protected static final Location OUST_GRACIA = new Location(-186576, 243584, 2608);
	
	protected static final VehiclePathPoint[] GLUDIO_TO_WARPGATE =
	{
		new VehiclePathPoint(-151216, 252544, 231),
		new VehiclePathPoint(-160416, 256144, 222),
		new VehiclePathPoint(-167888, 256720, -509, 0, 41035) // teleport: x, y, z, speed=0, heading
	};
	
	protected static final VehiclePathPoint[] GRACIA_TO_WARPGATE =
	{
		new VehiclePathPoint(-187808, 244992, 2672),
		new VehiclePathPoint(-188528, 245920, 2465),
		new VehiclePathPoint(-189936, 245232, 1682),
		new VehiclePathPoint(-191200, 242960, 1523),
		new VehiclePathPoint(-190416, 239088, 1706),
		new VehiclePathPoint(-187488, 237104, 2768),
		new VehiclePathPoint(-184688, 238432, 2802),
		new VehiclePathPoint(-184528, 241104, 2816),
		new VehiclePathPoint(-182144, 243376, 2733),
		new VehiclePathPoint(-179440, 243648, 1337),
		new VehiclePathPoint(-174544, 246176, 39),
		new VehiclePathPoint(-172608, 247728, 398),
		new VehiclePathPoint(-171824, 250048, 425),
		new VehiclePathPoint(-169776, 254800, 282),
		new VehiclePathPoint(-168080, 256624, 343),
		new VehiclePathPoint(-157264, 255664, 221, 0, 64781) // teleport: x, y, z, speed=0, heading
	};
	
	protected static final VehiclePathPoint[] WARPGATE_TO_GLUDIO =
	{
		new VehiclePathPoint(-153424, 255376, 221),
		new VehiclePathPoint(-149552, 258160, 221),
		new VehiclePathPoint(-146896, 257088, 221),
		new VehiclePathPoint(-146672, 254224, 221),
		new VehiclePathPoint(-147856, 252704, 206),
		new VehiclePathPoint(-149392, 252544, 198)
	};
	
	protected static final VehiclePathPoint[] WARPGATE_TO_GRACIA =
	{
		new VehiclePathPoint(-169776, 254800, 282),
		new VehiclePathPoint(-171824, 250048, 425),
		new VehiclePathPoint(-172608, 247728, 398),
		new VehiclePathPoint(-174544, 246176, 39),
		new VehiclePathPoint(-179440, 243648, 1337),
		new VehiclePathPoint(-182608, 243952, 2739),
		new VehiclePathPoint(-184960, 245120, 2694),
		new VehiclePathPoint(-186944, 244560, 2617)
	};
	
	protected int _cycle = 0;
	
	protected final L2AirShipInstance _ship = AirShipManager.getInstance().getNewAirShip(-149392, 252544, 198, 33837);
	
	private boolean _foundAtcGludio = false;
	private boolean _foundAtcGracia = false;
	
	private L2Npc _atcGludio = null;
	private L2Npc _atcGracia = null;
	
	public AirShipGludioGracia()
	{
		super(-1, AirShipGludioGracia.class.getSimpleName(), "gracia/vehicles");
		addStartNpc(CONTROLLERS);
		addFirstTalkId(CONTROLLERS);
		addTalkId(CONTROLLERS);
		_ship.setOustLoc(OUST_GLUDIO);
		_ship.setInDock(GLUDIO_DOCK_ID);
		_ship.registerEngine(new RunAirShip());
		_ship.runEngine(60000);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (player.isTransformed())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED);
			return null;
		}
		else if (player.isParalyzed())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED);
			return null;
		}
		else if (player.isDead() || player.isFakeDeath())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD);
			return null;
		}
		else if (player.isFishing())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING);
			return null;
		}
		else if (player.isInCombat())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE);
			return null;
		}
		else if (player.isInDuel())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL);
			return null;
		}
		else if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING);
			return null;
		}
		else if (player.isCastingNow())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_CASTING);
			return null;
		}
		else if (player.isCursedWeaponEquipped())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_CURSED_WEAPON_IS_EQUIPPED);
			return null;
		}
		else if (player.isCombatFlagEquipped())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG);
			return null;
		}
		else if (player.hasSummon() || player.isMounted())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED);
			return null;
		}
		else if (_ship.isInDock() && _ship.isInsideRadius(player, 600, true, false))
		{
			_ship.addPassenger(player);
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + ".html";
	}
	
	@Override
	public boolean unload(boolean removeFromList)
	{
		if (_ship != null)
		{
			_ship.oustPlayers();
			_ship.deleteMe();
		}
		return super.unload(removeFromList);
	}
	
	protected void broadcastInGludio(NpcStringId npcString)
	{
		if (!_foundAtcGludio)
		{
			_foundAtcGludio = true;
			_atcGludio = findController();
		}
		if (_atcGludio != null)
		{
			_atcGludio.broadcastPacket(new NpcSay(_atcGludio.getObjectId(), Say2.NPC_SHOUT, _atcGludio.getId(), npcString));
		}
	}
	
	protected void broadcastInGracia(NpcStringId npcStringId)
	{
		if (!_foundAtcGracia)
		{
			_foundAtcGracia = true;
			_atcGracia = findController();
		}
		if (_atcGracia != null)
		{
			_atcGracia.broadcastPacket(new NpcSay(_atcGracia.getObjectId(), Say2.NPC_SHOUT, _atcGracia.getId(), npcStringId));
		}
	}
	
	private L2Npc findController()
	{
		// Check objects around the ship
		for (L2Object obj : L2World.getInstance().getVisibleObjects(_ship, 600))
		{
			if (obj.isNpc())
			{
				for (int id : CONTROLLERS)
				{
					if (obj.getId() == id)
					{
						return (L2Npc) obj;
					}
				}
			}
		}
		return null;
	}
	
	private final class RunAirShip implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				switch (_cycle)
				{
					case 0:
					{
						broadcastInGludio(NpcStringId.THE_REGULARLY_SCHEDULED_AIRSHIP_THAT_FLIES_TO_THE_GRACIA_CONTINENT_HAS_DEPARTED);
						_ship.setInDock(0);
						_ship.executePath(GLUDIO_TO_WARPGATE);
						break;
					}
					case 1:
					{
						_ship.setOustLoc(OUST_GRACIA);
						ThreadPoolManager.getInstance().scheduleGeneral(this, 5000);
						break;
					}
					case 2:
					{
						_ship.executePath(WARPGATE_TO_GRACIA);
						break;
					}
					case 3:
					{
						broadcastInGracia(NpcStringId.THE_REGULARLY_SCHEDULED_AIRSHIP_HAS_ARRIVED_IT_WILL_DEPART_FOR_THE_ADEN_CONTINENT_IN_1_MINUTE);
						_ship.setInDock(GRACIA_DOCK_ID);
						_ship.oustPlayers();
						ThreadPoolManager.getInstance().scheduleGeneral(this, 60000);
						break;
					}
					case 4:
					{
						broadcastInGracia(NpcStringId.THE_REGULARLY_SCHEDULED_AIRSHIP_THAT_FLIES_TO_THE_ADEN_CONTINENT_HAS_DEPARTED);
						_ship.setInDock(0);
						_ship.executePath(GRACIA_TO_WARPGATE);
						break;
					}
					case 5:
					{
						_ship.setOustLoc(OUST_GLUDIO);
						ThreadPoolManager.getInstance().scheduleGeneral(this, 5000);
						break;
					}
					case 6:
					{
						_ship.executePath(WARPGATE_TO_GLUDIO);
						break;
					}
					case 7:
					{
						broadcastInGludio(NpcStringId.THE_REGULARLY_SCHEDULED_AIRSHIP_HAS_ARRIVED_IT_WILL_DEPART_FOR_THE_GRACIA_CONTINENT_IN_1_MINUTE);
						_ship.setInDock(GLUDIO_DOCK_ID);
						_ship.oustPlayers();
						ThreadPoolManager.getInstance().scheduleGeneral(this, 60000);
						break;
					}
				}
				_cycle++;
				if (_cycle > 7)
				{
					_cycle = 0;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}