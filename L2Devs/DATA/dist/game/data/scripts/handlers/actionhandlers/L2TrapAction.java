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
package handlers.actionhandlers;

import org.l2jdevs.gameserver.enums.InstanceType;
import org.l2jdevs.gameserver.handler.IActionHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.network.SystemMessageId;

public class L2TrapAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact)
	{
		// Aggression target lock effect
		if (activeChar.isLockedTarget() && (activeChar.getLockedTarget() != target))
		{
			activeChar.sendPacket(SystemMessageId.FAILED_CHANGE_TARGET);
			return false;
		}
		
		activeChar.setTarget(target);
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2TrapInstance;
	}
}