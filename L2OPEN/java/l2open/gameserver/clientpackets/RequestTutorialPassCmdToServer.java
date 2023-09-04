package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.BypassManager.DecodedBypass;
import l2open.gameserver.model.quest.Quest;
import l2open.util.Log;

import java.util.HashMap;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket
{
	// format: cS

	String _bypass = null;
	DecodedBypass bp = null;

	@Override
	public void readImpl()
	{
		_bypass = readS();
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(player.getEventMaster() != null && player.getEventMaster().tutorialLinkHtml(player, _bypass))
			return;
		if(_bypass.endsWith("special"))
		{
			_bypass = _bypass.substring(0, _bypass.length()-8);
			bp = getClient().getActiveChar().decodeBypass(_bypass, true);

			if(ConfigValue.DebugBypassType == 1)
			{
				String log_str = "special bbs="+bp.bbs+" bypass='"+_bypass+"' enc_bypass_1='"+bp.bypass+"' enc_bypass_2='"+bp.bypass2+"'";
				Log.addMy(log_str, "debug_bypass" , getClient().getActiveChar().getName());
			}
			else if(ConfigValue.DebugBypassType == 2)
			{
				String log_str = getClient().getActiveChar().getName()+": special bbs="+bp.bbs+" bypass='"+_bypass+"' enc_bypass_1='"+bp.bypass+"' enc_bypass_2='"+bp.bypass2+"'";
				Log.add(log_str, "debug_bypass");
			}

			String command = bp.bypass.substring(8).trim();

			String[] word = command.split("\\s+");
			String[] args = command.substring(word[0].length()).trim().split("\\s+");
			String[] path = word[0].split(":");
			if(path.length != 2)
			{
				_log.warning("Bad Script bypass!");
				return;
			}
			HashMap<String, Object> variables = new HashMap<String, Object>();

			variables.put("npc", null);

			if(word.length == 1)
				player.callScripts(path[0], path[1], new Object[] {}, variables);
			else
				player.callScripts(path[0], path[1], new Object[] { args }, variables);
		}
		else
		{
			Quest tutorial = QuestManager.getQuest(255);
			if(tutorial != null)
				player.processQuestEvent(tutorial.getName(), _bypass, null);
		}
	}
}