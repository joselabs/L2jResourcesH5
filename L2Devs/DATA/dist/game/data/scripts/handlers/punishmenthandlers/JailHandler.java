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
import org.l2jdevs.gameserver.ThreadPoolManager;
import org.l2jdevs.gameserver.cache.HtmCache;
import org.l2jdevs.gameserver.datatables.LanguageData;
import org.l2jdevs.gameserver.handler.IPunishmentHandler;
import org.l2jdevs.gameserver.model.L2World;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.tasks.player.TeleportTask;
import org.l2jdevs.gameserver.model.entity.TvTEvent;
import org.l2jdevs.gameserver.model.events.Containers;
import org.l2jdevs.gameserver.model.events.EventType;
import org.l2jdevs.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2jdevs.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jdevs.gameserver.model.olympiad.OlympiadManager;
import org.l2jdevs.gameserver.model.punishment.PunishmentTask;
import org.l2jdevs.gameserver.model.punishment.PunishmentType;
import org.l2jdevs.gameserver.model.zone.ZoneId;
import org.l2jdevs.gameserver.model.zone.type.L2JailZone;
import org.l2jdevs.gameserver.network.L2GameClient;
import org.l2jdevs.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This class handles jail punishment.
 * @author UnAfraid
 */
public class JailHandler implements IPunishmentHandler
{
	public JailHandler()
	{
		// Register global listener
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LOGIN, (OnPlayerLogin event) -> onPlayerLogin(event), this));
	}
	
	/**
	 * Applies all punishment effects from the player.
	 * @param task
	 * @param player
	 */
	private static void applyToPlayer(PunishmentTask task, L2PcInstance player)
	{
		player.setInstanceId(0);
		player.setIsIn7sDungeon(false);
		
		if (!TvTEvent.isInactive() && TvTEvent.isPlayerParticipant(player.getObjectId()))
		{
			TvTEvent.removeParticipant(player.getObjectId());
		}
		
		if (OlympiadManager.getInstance().isRegisteredInComp(player))
		{
			OlympiadManager.getInstance().removeDisconnectedCompetitor(player);
		}
		
		ThreadPoolManager.getInstance().scheduleGeneral(new TeleportTask(player, L2JailZone.getLocationIn()), 2000);
		
		// Open a Html message to inform the player
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		String content = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/jail_in.htm");
		if (content != null)
		{
			content = content.replaceAll("%reason%", task != null ? task.getReason() : "");
			content = content.replaceAll("%punishedBy%", task != null ? task.getPunishedBy() : "");
			msg.setHtml(content);
		}
		else
		{
			msg.setHtml("<html><body>You have been put in jail by an admin.</body></html>");
		}
		player.sendPacket(msg);
		if (task != null)
		{
			long delay = ((task.getExpirationTime() - System.currentTimeMillis()) / 1000);
			if (delay > 0)
			{
				player.sendMessage(LanguageData.getInstance().getMsg(player, "player_jail_time").replace("%s%", (delay > 60 ? ((delay / 60) + " m.") : delay + " s.")));
			}
			else
			{
				player.sendMessage(LanguageData.getInstance().getMsg(player, "player_jail_forever"));
			}
		}
	}
	
	/**
	 * Removes any punishment effects from the player.
	 * @param player
	 */
	private static void removeFromPlayer(L2PcInstance player)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new TeleportTask(player, L2JailZone.getLocationOut()), 2000);
		
		// Open a Html message to inform the player
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		String content = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/jail_out.htm");
		if (content != null)
		{
			msg.setHtml(content);
		}
		else
		{
			msg.setHtml("<html><body>You are free for now, respect server rules!</body></html>");
		}
		player.sendPacket(msg);
	}
	
	@Override
	public PunishmentType getType()
	{
		return PunishmentType.JAIL;
	}
	
	@Override
	public void onEnd(PunishmentTask task)
	{
		switch (task.getAffect())
		{
			case CHARACTER:
			{
				int objectId = Integer.parseInt(String.valueOf(task.getKey()));
				final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
				if (player != null)
				{
					removeFromPlayer(player);
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
						removeFromPlayer(player);
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
						removeFromPlayer(player);
					}
				}
				break;
			}
		}
	}
	
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final L2PcInstance activeChar = event.getActiveChar();
		if (activeChar.isJailed() && !activeChar.isInsideZone(ZoneId.JAIL))
		{
			applyToPlayer(null, activeChar);
		}
		else if (!activeChar.isJailed() && activeChar.isInsideZone(ZoneId.JAIL) && !activeChar.isGM())
		{
			removeFromPlayer(activeChar);
		}
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
					applyToPlayer(task, player);
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
						applyToPlayer(task, player);
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
						applyToPlayer(task, player);
					}
				}
				break;
			}
		}
	}
}
