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
package handlers.usercommandhandlers;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.l2jdevs.gameserver.GameTimeController.MILLIS_IN_TICK;
import static org.l2jdevs.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;
import static org.l2jdevs.gameserver.model.TeleportWhereType.TOWN;
import static org.l2jdevs.gameserver.network.SystemMessageId.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT;
import static org.l2jdevs.gameserver.network.serverpackets.ActionFailed.STATIC_PACKET;

import org.l2jdevs.Config;
import org.l2jdevs.gameserver.GameTimeController;
import org.l2jdevs.gameserver.ThreadPoolManager;
import org.l2jdevs.gameserver.datatables.LanguageData;
import org.l2jdevs.gameserver.handler.IUserCommandHandler;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.entity.TvTEvent;
import org.l2jdevs.gameserver.model.holders.SkillHolder;
import org.l2jdevs.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jdevs.gameserver.network.serverpackets.SetupGauge;
import org.l2jdevs.gameserver.util.Broadcast;

/**
 * Unstuck user command.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class Unstuck implements IUserCommandHandler
{
	private static final long FIVE_MINUTES = MINUTES.toSeconds(5);
	
	private static final SkillHolder ESCAPE_5_MINUTES = new SkillHolder(2099);
	
	private static final SkillHolder ESCAPE_1_SECOND = new SkillHolder(2100);
	
	private static final int RETURN = 1050;
	
	private static final int[] COMMAND_IDS =
	{
		52
	};
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
                if(Config.L2JMOD_UNSTUCK_INFIGHT && activeChar.isInCombat())
                    return false;
		
		if (!TvTEvent.onEscapeUse(activeChar.getObjectId()))
		{
			activeChar.sendPacket(STATIC_PACKET);
			return false;
		}
		
		if (activeChar.isJailed())
		{
			activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "no_unstuck_in_jail"));
			return false;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return false;
		}
		
		if (activeChar.isCastingNow() || activeChar.isMovementDisabled() || activeChar.isMuted() || //
			activeChar.isAlikeDead() || activeChar.inObserverMode() || activeChar.isCombatFlagEquipped())
		{
			return false;
		}

		final int unstuckTimer = (activeChar.isGM() ? 1000 : Config.UNSTUCK_INTERVAL * 1000);
		activeChar.forceIsCasting(GameTimeController.getInstance().getGameTicks() + (unstuckTimer / MILLIS_IN_TICK));
		
		if (activeChar.isGM())
		{
			activeChar.doCast(ESCAPE_1_SECOND);
			return true;
		}
		
		if (Config.UNSTUCK_INTERVAL == FIVE_MINUTES)
		{
			activeChar.doCast(ESCAPE_5_MINUTES);
			return true;
		}
		
		if (Config.UNSTUCK_INTERVAL > 100)
		{
			activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "unstuck_time_minutes").replace("%s%", SECONDS.toMinutes(Config.UNSTUCK_INTERVAL) + ""));
		}
		else
		{
			activeChar.sendMessage(LanguageData.getInstance().getMsg(activeChar, "unstuck_time_seconds").replace("%s%", Config.UNSTUCK_INTERVAL + ""));
		}
		
		activeChar.getAI().setIntention(AI_INTENTION_IDLE);
		// SoE Animation section
		activeChar.setTarget(activeChar);
		activeChar.disableAllSkills();
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, RETURN, 1, unstuckTimer, 0), 900);
		activeChar.sendPacket(new SetupGauge(0, unstuckTimer));
		// End SoE Animation section
		
		// Continue execution later
		activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			if (!activeChar.isDead())
			{
				activeChar.setIsIn7sDungeon(false);
				activeChar.enableAllSkills();
				activeChar.setIsCastingNow(false);
				activeChar.setInstanceId(0);
				activeChar.teleToLocation(TOWN);
			}
		}, unstuckTimer));
		return true;
	}
}
