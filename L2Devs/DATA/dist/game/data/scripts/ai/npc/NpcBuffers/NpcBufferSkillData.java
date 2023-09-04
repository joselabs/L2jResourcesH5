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
package ai.npc.NpcBuffers;

import org.l2jdevs.gameserver.model.StatsSet;
import org.l2jdevs.gameserver.model.holders.SkillHolder;
import org.l2jdevs.gameserver.model.skills.Skill;
import org.l2jdevs.gameserver.model.skills.targets.AffectObject;
import org.l2jdevs.gameserver.model.skills.targets.AffectScope;

/**
 * @author UnAfraid
 */
public class NpcBufferSkillData
{
	private final SkillHolder _skill;
	private final int _initialDelay;
	private final int _delay;
	private final AffectScope _affectScope;
	private final AffectObject _affectObject;
	
	public NpcBufferSkillData(StatsSet set)
	{
		_skill = new SkillHolder(set.getInt("id"), set.getInt("level"));
		_initialDelay = set.getInt("initialDelay", 0) * 1000;
		_delay = set.getInt("delay") * 1000;
		_affectScope = set.getEnum("affectScope", AffectScope.class);
		_affectObject = set.getEnum("affectObject", AffectObject.class);
	}
	
	public AffectObject getAffectObject()
	{
		return _affectObject;
	}
	
	public AffectScope getAffectScope()
	{
		return _affectScope;
	}
	
	public int getDelay()
	{
		return _delay;
	}
	
	public int getInitialDelay()
	{
		return _initialDelay;
	}
	
	public Skill getSkill()
	{
		return _skill.getSkill();
	}
}
