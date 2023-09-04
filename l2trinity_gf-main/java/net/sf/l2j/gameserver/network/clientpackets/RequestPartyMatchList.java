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
package net.sf.l2j.gameserver.network.clientpackets;

import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.ListPartyWating;

/**
 * Packetformat  Rev650  cdddddS
 *
 * @version $Revision: 1.1.4.4 $ $Date: 2005/03/27 15:29:30 $
 */

public class RequestPartyMatchList extends L2GameClientPacket
{
	private static final String _C__70_REQUESTPARTYMATCHLIST = "[C] 70 RequestPartyMatchList";
	private static Logger _log = Logger.getLogger(RequestPartyMatchList.class.getName());

	private int _status;
    @SuppressWarnings("unused")
	private int _unk1;
    @SuppressWarnings("unused")
	private int _unk2;
    @SuppressWarnings("unused")
	private int _unk3;
    @SuppressWarnings("unused")
	private int _unk4;
    @SuppressWarnings("unused")
	private String _unk5;


	@Override
	protected void readImpl()
	{
		_status = readD();
        //TODO analyse values _unk1-unk5
		/*
        _unk1 = readD();
        _unk2 = readD();
        _unk3 = readD();
        _unk4 = readD();
        _unk5 = readS();
        */
	}

	@Override
	protected void runImpl()
	{
		if (_status == 1)
		{
			// window is open fill the list
			// actually the client should get automatic updates for the list
			// for now we only fill it once

			//Collection<L2PcInstance> players = L2World.getInstance().getAllPlayers();
			//L2PcInstance[] allPlayers = players.toArray(new L2PcInstance[players.size()]);
			L2PcInstance[] empty = new L2PcInstance[] { };
			@SuppressWarnings("unused")
			ListPartyWating matchList = new ListPartyWating(empty);
			//sendPacket(matchList);
		}
		else if (_status == 3)
		{
			// client does not need any more updates
			if (Config.DEBUG) _log.fine("PartyMatch window was closed.");
		}
		else
		{
			if (Config.DEBUG) _log.fine("party match status: "+_status);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__70_REQUESTPARTYMATCHLIST;
	}
}
