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
package handlers.punishmenthandlers;

import org.l2jdevs.gameserver.LoginServerThread;
import org.l2jdevs.gameserver.handler.IPunishmentHandler;
import org.l2jdevs.gameserver.model.L2World;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.punishment.PunishmentTask;
import org.l2jdevs.gameserver.model.punishment.PunishmentType;
import org.l2jdevs.gameserver.network.L2GameClient;

/**
 * This class handles ban punishment.
 * @author UnAfraid
 */
public class BanHandler implements IPunishmentHandler
{
	/**
	 * Applies all punishment effects from the player.
	 * @param player
	 */
	private static void applyToPlayer(L2PcInstance player)
	{
		player.logout();
	}
	
	@Override
	public PunishmentType getType()
	{
		return PunishmentType.BAN;
	}
	
	@Override
	public void onEnd(PunishmentTask task)
	{
		
	}
	
	@Override
	public void onStart(PunishmentTask task)
	{
		switch (task.getAffect())
		{
			case CHARACTER:
			{
				int objectId = Integer.parseInt(String.valueOf(task.getKey()));
				final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
				if (player != null)
				{
					applyToPlayer(player);
				}
				break;
			}
			case ACCOUNT:
			{
				String account = String.valueOf(task.getKey());
				final L2GameClient client = LoginServerThread.getInstance().getClient(account);
				if (client != null)
				{
					final L2PcInstance player = client.getActiveChar();
					if (player != null)
					{
						applyToPlayer(player);
					}
					else
					{
						client.closeNow();
					}
				}
				break;
			}
			case IP:
			{
				String ip = String.valueOf(task.getKey());
				for (L2PcInstance player : L2World.getInstance().getPlayers())
				{
					if (player.getIPAddress().equals(ip))
					{
						applyToPlayer(player);
					}
				}
				break;
			}
		}
	}
}
