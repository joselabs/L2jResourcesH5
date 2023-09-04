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
package handlers.targethandlers;

import static org.l2jdevs.gameserver.model.skills.targets.L2TargetType.ENEMY_ONLY;
import static org.l2jdevs.gameserver.network.SystemMessageId.INCORRECT_TARGET;

import org.l2jdevs.gameserver.handler.ITargetTypeHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.instance.L2AdventurerInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2AuctioneerInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2CastleDoormenInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2ClanHallDoormenInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2ClanHallManagerInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2DawnPriestInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2DoorInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2DoormenInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2DungeonGatekeeperInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2DuskPriestInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2FortDoormenInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2FortLogisticsInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2FortManagerInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2GuardInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2MerchantInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2NpcBufferInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2NpcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2ObservationInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2OlympiadManagerInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PetManagerInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2RaceManagerInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2SepulcherNpcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2SignsPriestInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2TamedBeastInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2TeleporterInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2TerrainObjectInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2TrainerInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2VillageMasterDElfInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2VillageMasterDwarfInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2VillageMasterFighterInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2VillageMasterInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2VillageMasterKamaelInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2VillageMasterMysticInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2VillageMasterOrcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2VillageMasterPriestInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2WarehouseInstance;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.model.skills.targets.L2TargetType;
import org.l2jdevs.gameserver.model.zone.ZoneId;

/**
 * Enemy Only target type handler.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class EnemyOnly implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		switch (skill.getAffectScope())
		{
			case SINGLE:
			{
				if (target == null)
				{
					return EMPTY_TARGET_LIST;
				}
				
				final L2PcInstance player = activeChar.getActingPlayer();
				if (target.isDead() //
					|| (skill.getTargetType() == L2TargetType.SELF) //
					|| (target instanceof L2AdventurerInstance) //
					|| (target instanceof L2AuctioneerInstance) //
					|| (target instanceof L2CastleDoormenInstance) //
					|| (target instanceof L2ClanHallDoormenInstance) //
					|| (target instanceof L2ClanHallManagerInstance) //
					|| (target instanceof L2DoorInstance) //
					|| (target instanceof L2DoormenInstance) //
					|| (target instanceof L2DungeonGatekeeperInstance) //
					|| (target instanceof L2DuskPriestInstance) //
					|| (target instanceof L2DawnPriestInstance) //
					|| (target instanceof L2FortDoormenInstance) //
					|| (target instanceof L2FortLogisticsInstance) //
					|| (target instanceof L2FortManagerInstance) //
					|| (target instanceof L2GuardInstance) //
					|| (target instanceof L2MerchantInstance) //
					|| (target instanceof L2NpcBufferInstance) //
					|| (target instanceof L2NpcInstance) //
					|| (target instanceof L2ObservationInstance) //
					|| (target instanceof L2OlympiadManagerInstance) //
					|| (target instanceof L2PetManagerInstance) //
					|| (target instanceof L2RaceManagerInstance) //
					|| (target instanceof L2SepulcherNpcInstance) //
					|| (target instanceof L2SignsPriestInstance) //
					|| (target instanceof L2TamedBeastInstance) //
					|| (target instanceof L2TeleporterInstance) //
					|| (target instanceof L2TerrainObjectInstance) //
					|| (target instanceof L2TrainerInstance) //
					|| (target instanceof L2VillageMasterDElfInstance) //
					|| (target instanceof L2VillageMasterDwarfInstance) //
					|| (target instanceof L2VillageMasterFighterInstance) //
					|| (target instanceof L2VillageMasterInstance) //
					|| (target instanceof L2VillageMasterKamaelInstance) //
					|| (target instanceof L2VillageMasterMysticInstance) //
					|| (target instanceof L2VillageMasterOrcInstance) //
					|| (target instanceof L2VillageMasterPriestInstance) //
					|| (target instanceof L2WarehouseInstance) //
					|| (!target.isAttackable() //
						&& (player != null) //
						&& player.isInPartyWith(target) //
						&& player.isInClanWith(target) //
						&& player.isInAllyWith(target) //
						&& player.isInCommandChannelWith(target) //
						&& player.isOnSameSiegeSideWith(target) //
						&& !(player.isInsideZone(ZoneId.PVP) && target.isInsideZone(ZoneId.PVP)) //
						&& !player.isAttackable() //
						&& !player.isInOlympiadMode() //
						&& !player.isAtWarWith(target) //
						&& !player.checkIfPvP(target) //
						&& !player.checkIfPvP(target)))
				{
					activeChar.sendPacket(INCORRECT_TARGET);
					return EMPTY_TARGET_LIST;
				}
				
				return new L2Character[]
				{
					target
				};
			}
		}
		return EMPTY_TARGET_LIST;
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return ENEMY_ONLY;
	}
}