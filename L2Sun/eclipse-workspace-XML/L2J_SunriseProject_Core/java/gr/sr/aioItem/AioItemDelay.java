package gr.sr.aioItem;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import l2r.gameserver.model.actor.instance.L2PcInstance;

import gr.sr.configsEngine.configs.impl.AioItemsConfigs;

/**
 * The class used to create a delay for players, reducing the overhead due RequestBypassToServer.
 * @author vGodFather
 */
public class AioItemDelay implements Runnable
{
	L2PcInstance _player;
	/* A List which contains the player on delay */
	public static Set<L2PcInstance> _delayers = ConcurrentHashMap.newKeySet();
	
	public AioItemDelay(L2PcInstance player)
	{
		_player = player;
		_delayers.add(_player);
	}
	
	@Override
	public void run()
	{
		double start = System.currentTimeMillis();
		
		while (System.currentTimeMillis() < (start + (AioItemsConfigs.AIO_DELAY * 1000)))
		{
			// Do nothing
		}
		
		if (_delayers.contains(_player))
		{
			_delayers.remove(_player);
		}
	}
}