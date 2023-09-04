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
package ai.npc.ForgeOfTheGods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jdevs.gameserver.GeoData;
import org.l2jdevs.gameserver.ThreadPoolManager;
import org.l2jdevs.gameserver.model.L2Spawn;
import org.l2jdevs.gameserver.model.L2Territory;
import org.l2jdevs.gameserver.model.Location;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.util.Rnd;
import org.l2jdevs.util.data.xml.IXmlReader;

/**
 * Tar Beetle zone spawn
 * @author malyelfik
 */
public class TarBeetleSpawn implements IXmlReader
{
	private final List<SpawnZone> zones = new ArrayList<>();
	private ScheduledFuture<?> spawnTask;
	private ScheduledFuture<?> shotTask;
	
	public TarBeetleSpawn()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("data/spawnZones/tar_beetle.xml");
		if (!zones.isEmpty())
		{
			spawnTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> zones.forEach(SpawnZone::refreshSpawn), 1000, 60000);
			shotTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> zones.forEach(SpawnZone::refreshShots), 300000, 300000);
		}
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		int i = 0;
		for (Node d = doc.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if (d.getNodeName().equals("list"))
			{
				for (Node r = d.getFirstChild(); r != null; r = r.getNextSibling())
				{
					if (r.getNodeName().equals("spawnZone"))
					{
						NamedNodeMap attrs = r.getAttributes();
						final int npcCount = parseInteger(attrs, "maxNpcCount");
						final SpawnZone sp = new SpawnZone(npcCount, i);
						for (Node b = r.getFirstChild(); b != null; b = b.getNextSibling())
						{
							if (b.getNodeName().equals("zone"))
							{
								attrs = b.getAttributes();
								final int minZ = parseInteger(attrs, "minZ");
								final int maxZ = parseInteger(attrs, "maxZ");
								final Zone zone = new Zone();
								for (Node c = b.getFirstChild(); c != null; c = c.getNextSibling())
								{
									attrs = c.getAttributes();
									if (c.getNodeName().equals("point"))
									{
										final int x = parseInteger(attrs, "x");
										final int y = parseInteger(attrs, "y");
										zone.add(x, y, minZ, maxZ, 0);
									}
									else if (c.getNodeName().equals("bannedZone"))
									{
										final Zone bannedZone = new Zone();
										final int bMinZ = parseInteger(attrs, "minZ");
										final int bMaxZ = parseInteger(attrs, "maxZ");
										for (Node f = c.getFirstChild(); f != null; f = f.getNextSibling())
										{
											if (f.getNodeName().equals("point"))
											{
												attrs = f.getAttributes();
												int x = parseInteger(attrs, "x");
												int y = parseInteger(attrs, "y");
												bannedZone.add(x, y, bMinZ, bMaxZ, 0);
											}
										}
										zone.addBannedZone(bannedZone);
									}
								}
								sp.addZone(zone);
							}
						}
						zones.add(i++, sp);
					}
				}
			}
		}
	}
	
	public final void removeBeetle(L2Npc npc)
	{
		zones.get(npc.getVariables().getInt("zoneIndex", 0)).removeSpawn(npc);
		npc.deleteMe();
	}
	
	public final void unload()
	{
		if (spawnTask != null)
		{
			spawnTask.cancel(false);
		}
		if (shotTask != null)
		{
			shotTask.cancel(false);
		}
		zones.forEach(SpawnZone::unload);
		zones.clear();
	}
	
	private final class SpawnZone
	{
		private final List<Zone> _zones = new ArrayList<>();
		private final List<L2Npc> _spawn = new CopyOnWriteArrayList<>();
		private final int _maxNpcCount;
		private final int _index;
		
		public SpawnZone(int maxNpcCount, int index)
		{
			_maxNpcCount = maxNpcCount;
			_index = index;
		}
		
		public void addZone(Zone zone)
		{
			_zones.add(zone);
		}
		
		public void refreshShots()
		{
			if (_spawn.size() > 0)
			{
				for (L2Npc npc : _spawn)
				{
					final int val = npc.getScriptValue();
					if (val == 5)
					{
						npc.deleteMe();
						_spawn.remove(npc);
					}
					else
					{
						npc.setScriptValue(val + 1);
					}
				}
			}
		}
		
		public void refreshSpawn()
		{
			try
			{
				while (_spawn.size() < _maxNpcCount)
				{
					final Location location = _zones.get(Rnd.get(_zones.size())).getRandomPoint();
					if (location != null)
					{
						final L2Spawn spawn = new L2Spawn(18804);
						spawn.setHeading(Rnd.get(65535));
						spawn.setX(location.getX());
						spawn.setY(location.getY());
						spawn.setZ(GeoData.getInstance().getSpawnHeight(location));
						
						final L2Npc npc = spawn.doSpawn();
						npc.setIsNoRndWalk(true);
						npc.setIsImmobilized(true);
						npc.setIsInvul(true);
						npc.disableCoreAI(true);
						npc.setScriptValue(5);
						npc.getVariables().set("zoneIndex", _index);
						_spawn.add(npc);
					}
				}
			}
			catch (Exception e)
			{
				LOG.warn("{}: Could not refresh spawns!", getClass().getSimpleName(), e);
			}
		}
		
		public void removeSpawn(L2Npc obj)
		{
			_spawn.remove(obj);
		}
		
		public void unload()
		{
			_spawn.forEach(L2Npc::deleteMe);
			_spawn.clear();
			_zones.clear();
		}
	}
	
	private final class Zone extends L2Territory
	{
		private List<Zone> _bannedZones;
		
		public Zone()
		{
			super(1);
		}
		
		public void addBannedZone(Zone bZone)
		{
			if (_bannedZones == null)
			{
				_bannedZones = new ArrayList<>();
			}
			_bannedZones.add(bZone);
		}
		
		@Override
		public Location getRandomPoint()
		{
			Location location = super.getRandomPoint();
			while ((location != null) && isInsideBannedZone(location))
			{
				location = super.getRandomPoint();
			}
			return location;
		}
		
		private boolean isInsideBannedZone(Location location)
		{
			if (_bannedZones != null)
			{
				for (Zone z : _bannedZones)
				{
					if (z.isInside(location.getX(), location.getY()))
					{
						return true;
					}
				}
			}
			return false;
		}
	}
}