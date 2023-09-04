/*
 * Copyright (C) 2004-2014 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.scripting.scriptengine.listeners.player;

import l2r.gameserver.network.clientpackets.CharacterCreate;
import l2r.gameserver.network.clientpackets.CharacterDelete;
import l2r.gameserver.network.clientpackets.CharacterRestore;
import l2r.gameserver.network.clientpackets.CharacterSelect;
import l2r.gameserver.scripting.scriptengine.events.PlayerEvent;
import l2r.gameserver.scripting.scriptengine.impl.L2JListener;

/**
 * @author UnAfraid
 */
public abstract class PlayerListener extends L2JListener
{
	public PlayerListener()
	{
		register();
	}
	
	public abstract void onCharCreate(PlayerEvent event);
	
	public abstract void onCharDelete(PlayerEvent event);
	
	public abstract void onCharRestore(PlayerEvent event);
	
	public abstract void onCharSelect(PlayerEvent event);
	
	@Override
	public void register()
	{
		CharacterCreate.addPlayerListener(this);
		CharacterDelete.addPlayerListener(this);
		CharacterRestore.addPlayerListener(this);
		CharacterSelect.addPlayerListener(this);
	}
	
	@Override
	public void unregister()
	{
		CharacterCreate.removePlayerListener(this);
		CharacterDelete.removePlayerListener(this);
		CharacterRestore.removePlayerListener(this);
		CharacterSelect.removePlayerListener(this);
	}
}
