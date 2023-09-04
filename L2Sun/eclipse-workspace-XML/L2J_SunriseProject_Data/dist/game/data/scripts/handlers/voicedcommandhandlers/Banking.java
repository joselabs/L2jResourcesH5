/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.voicedcommandhandlers;

import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.InventoryUpdate;

import gr.sr.configsEngine.configs.impl.BankingConfigs;

/**
 * This class trades Gold Bars for Adena and vice versa.
 * @author Ahmed
 */
public class Banking implements IVoicedCommandHandler
{
	private static String[] _voicedCommands =
	{
		"bank",
		"withdraw",
		"deposit"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equalsIgnoreCase("bank"))
		{
			activeChar.sendMessage(".deposit (" + BankingConfigs.BANKING_SYSTEM_ADENA + " Adena = " + BankingConfigs.BANKING_SYSTEM_GOLDBARS + " Goldbar) / .withdraw (" + BankingConfigs.BANKING_SYSTEM_GOLDBARS + " Goldbar = " + BankingConfigs.BANKING_SYSTEM_ADENA + " Adena)");
		}
		else if (command.equalsIgnoreCase("deposit"))
		{
			if (activeChar.getInventory().getInventoryItemCount(57, 0) >= BankingConfigs.BANKING_SYSTEM_ADENA)
			{
				InventoryUpdate iu = new InventoryUpdate();
				activeChar.getInventory().reduceAdena("BanbingAdenaDestroy", BankingConfigs.BANKING_SYSTEM_ADENA, activeChar, null);
				activeChar.getInventory().addItem("BanbingGoldbarCreate", 3470, BankingConfigs.BANKING_SYSTEM_GOLDBARS, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendInventoryUpdate(iu);
				activeChar.sendMessage("Thank you, you now have " + BankingConfigs.BANKING_SYSTEM_GOLDBARS + " Goldbar(s), and " + BankingConfigs.BANKING_SYSTEM_ADENA + " less adena.");
			}
			else
			{
				activeChar.sendMessage("You do not have enough Adena to convert to Goldbar(s), you need " + BankingConfigs.BANKING_SYSTEM_ADENA + " Adena.");
			}
		}
		else if (command.equalsIgnoreCase("withdraw"))
		{
			if (activeChar.getInventory().getInventoryItemCount(3470, 0) >= BankingConfigs.BANKING_SYSTEM_GOLDBARS)
			{
				InventoryUpdate iu = new InventoryUpdate();
				activeChar.getInventory().destroyItemByItemId("BankingGoldbarDestroy", 3470, BankingConfigs.BANKING_SYSTEM_GOLDBARS, activeChar, null);
				activeChar.getInventory().addAdena("BankingAdenaCreate", BankingConfigs.BANKING_SYSTEM_ADENA, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendInventoryUpdate(iu);
				activeChar.sendMessage("Thank you, you now have " + BankingConfigs.BANKING_SYSTEM_ADENA + " Adena, and " + BankingConfigs.BANKING_SYSTEM_GOLDBARS + " less Goldbar(s).");
			}
			else
			{
				activeChar.sendMessage("You do not have any Goldbars to turn into " + BankingConfigs.BANKING_SYSTEM_ADENA + " Adena.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}