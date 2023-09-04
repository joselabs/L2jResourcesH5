package l2r.gameserver.scripts.handlers.itemhandlers;

import l2r.gameserver.handler.IItemHandler;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import gr.reunion.configsEngine.AioBufferConfigs;

/**
 * @author DoctorNo
 */
public class AioItemBuff implements IItemHandler
{
	private static final int ITEM_IDS = AioBufferConfigs.BUFF_ITEM_ID;
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		// No null pointers
		if (playable == null)
			return false;
		L2PcInstance player = null;
		// Just for players
		if (!(playable instanceof L2PcInstance))
		{
			return false;
		}
		
		player = (L2PcInstance) playable;
		
		int itemId = item.getId();
		if (itemId == AioBufferConfigs.BUFF_ITEM_ID)
		{
			String htmFile = "data/html/ItemBuffer/main.htm";
			NpcHtmlMessage msg = new NpcHtmlMessage(1);
			msg.setFile(player.getHtmlPrefix(), htmFile);
			player.sendPacket(msg);
		}
		return true;
	}
	
	public int getItemIds()
	{
		return ITEM_IDS;
	}
}