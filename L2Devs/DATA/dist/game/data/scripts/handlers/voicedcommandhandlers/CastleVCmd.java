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
package handlers.voicedcommandhandlers;

import org.l2jdevs.gameserver.datatables.LanguageData;
import org.l2jdevs.gameserver.handler.IVoicedCommandHandler;
import org.l2jdevs.gameserver.instancemanager.CastleManager;
import org.l2jdevs.gameserver.model.actor.instance.L2DoorInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.entity.Castle;
import org.l2jdevs.gameserver.network.SystemMessageId;

/**
 * @author Zoey76
 */
public class CastleVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"opendoors",
		"closedoors",
		"ridewyvern"
	};
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		switch (command)
		{
			case "opendoors":
				if (!params.equals("castle"))
				{
					activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "castle_no_lord_open_door"));
					return false;
				}
				
				if (!activeChar.isClanLeader())
				{
					activeChar.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CAN_ISSUE_COMMANDS);
					return false;
				}
				
				final L2DoorInstance door = (L2DoorInstance) activeChar.getTarget();
				if (door == null)
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					return false;
				}
				
				final Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().getCastleId());
				if (castle == null)
				{
					activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "castle_no_clan_own"));
					return false;
				}
				
				if (castle.getSiege().isInProgress())
				{
					activeChar.sendPacket(SystemMessageId.GATES_NOT_OPENED_CLOSED_DURING_SIEGE);
					return false;
				}
				
				if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
				{
					activeChar.sendPacket(SystemMessageId.GATE_IS_OPENING);
					door.openMe();
				}
				break;
			case "closedoors":
				if (!params.equals("castle"))
				{
					activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "castle_no_lord_closed_door"));
					return false;
				}
				if (!activeChar.isClanLeader())
				{
					activeChar.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CAN_ISSUE_COMMANDS);
					return false;
				}
				final L2DoorInstance door2 = (L2DoorInstance) activeChar.getTarget();
				if (door2 == null)
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					return false;
				}
				final Castle castle2 = CastleManager.getInstance().getCastleById(activeChar.getClan().getCastleId());
				if (castle2 == null)
				{
					activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "castle_no_clan_own"));
					return false;
				}
				
				if (castle2.getSiege().isInProgress())
				{
					activeChar.sendPacket(SystemMessageId.GATES_NOT_OPENED_CLOSED_DURING_SIEGE);
					return false;
				}
				
				if (castle2.checkIfInZone(door2.getX(), door2.getY(), door2.getZ()))
				{
					activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "castle_gate_closed"));
					door2.closeMe();
				}
				break;
			case "ridewyvern":
				if (activeChar.isClanLeader() && (activeChar.getClan().getCastleId() > 0))
				{
					activeChar.mount(12621, 0, true);
				}
				break;
		}
		return true;
	}
}
