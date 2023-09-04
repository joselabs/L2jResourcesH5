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

/**
 * Format: (ch) dd
 * @author -Wooden-
 *
 */
public class RequestDismissPartyRoom extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestDismissPartyRoom.class.getName());
	private static final String _C__D0_02_REQUESTDISMISSPARTYROOM = "[C] D0:02 RequestDismissPartyRoom";
	private int _data1;
	private int _data2;

	@Override
	protected void readImpl()
	{
		_data1 = readD();
		_data2 = readD();
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#runImpl()
	 */
	@Override
	protected void runImpl()
	{
		// TODO Auto-generated method stub
		_log.info("This packet is not well known : RequestDismissPartyRoom");
		_log.info("Data received: d:"+_data1+" d:"+_data2);

	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.BasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__D0_02_REQUESTDISMISSPARTYROOM;
	}

}
