package handlers.targethandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.handler.ITargetTypeHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.targets.L2TargetType;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.util.Util;

/**
 * @author vGodFather
 */
public class Artillery implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(L2Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		List<L2Character> targetList = new ArrayList<>();
		if ((target == null) || (((target == activeChar) || target.isAlikeDead()) && (skill.getCastRange() >= 0)) || (!target.isDoor()))
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			return _emptyTargetList;
		}
		
		// vGodFather Small trick just in case we miss actor face the target
		activeChar.setHeading(Util.calculateHeadingFrom(activeChar, target));
		
		final boolean srcInArena = (activeChar.isInsideZone(ZoneIdType.PVP) && !activeChar.isInsideZone(ZoneIdType.SIEGE));
		int affectRange = skill.getAffectRange();
		int maxTargets = skill.getAffectLimit();
		final Collection<L2Character> objs = activeChar.getKnownList().getKnownCharactersInRadius(target, affectRange);
		for (L2Character obj : objs)
		{
			if (obj.isDead())
			{
				continue;
			}
			
			if (!obj.isAutoAttackable(activeChar))
			{
				continue;
			}
			
			if (!L2Skill.checkForAreaOffensiveSkills(activeChar, obj, skill, srcInArena))
			{
				continue;
			}
			
			if (activeChar.isPlayable() && obj.isAttackable() && !skill.isOffensive())
			{
				continue;
			}
			
			if (obj.isPlayer() && skill.isOffensive() && activeChar.getActingPlayer().isFriend(obj.getActingPlayer()))
			{
				continue;
			}
			
			if ((maxTargets > 0) && (targetList.size() >= maxTargets))
			{
				break;
			}
			
			targetList.add(obj);
		}
		
		if (targetList.isEmpty())
		{
			return _emptyTargetList;
		}
		
		return targetList.toArray(new L2Character[targetList.size()]);
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return L2TargetType.ARTILLERY;
	}
}
