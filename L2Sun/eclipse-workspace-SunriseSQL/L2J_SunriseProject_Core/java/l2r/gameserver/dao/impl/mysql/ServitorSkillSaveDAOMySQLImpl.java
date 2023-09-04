package l2r.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import l2r.Config;
import l2r.L2DatabaseFactory;
import l2r.gameserver.dao.ServitorSkillSaveDAO;
import l2r.gameserver.data.SummonEffectsTable;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.actor.instance.L2ServitorInstance;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.skills.L2Skill;

import gr.sr.utils.SqlBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servitor skill save DAO MySQL implementation.
 * @author vGodFather
 */
public class ServitorSkillSaveDAOMySQLImpl implements ServitorSkillSaveDAO
{
	private static Logger Log = LoggerFactory.getLogger("DAO");
	
	private static final String ADD_SKILL_SAVE = "REPLACE INTO character_summon_skills_save (ownerId,ownerClassIndex,summonSkillId,skill_id,skill_level,effect_count,effect_cur_time,buff_index) VALUES";
	private static final String RESTORE_SKILL_SAVE = "SELECT skill_id,skill_level,effect_count,effect_cur_time,buff_index FROM character_summon_skills_save WHERE ownerId=? AND ownerClassIndex=? AND summonSkillId=? ORDER BY buff_index ASC";
	private static final String DELETE_SKILL_SAVE = "DELETE FROM character_summon_skills_save WHERE ownerId=? AND ownerClassIndex=? AND summonSkillId=?";
	
	@Override
	public void load(final L2ServitorInstance actor)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			if (!SummonEffectsTable.getInstance().containsSkill(actor.getOwner(), actor.getReferenceSkill()))
			{
				try (PreparedStatement statement = con.prepareStatement(RESTORE_SKILL_SAVE))
				{
					statement.setInt(1, actor.getOwner().getObjectId());
					statement.setInt(2, actor.getOwner().getClassIndex());
					statement.setInt(3, actor.getReferenceSkill());
					try (ResultSet rset = statement.executeQuery())
					{
						while (rset.next())
						{
							int effectCount = rset.getInt("effect_count");
							int effectCurTime = rset.getInt("effect_cur_time");
							
							final L2Skill skill = SkillData.getInstance().getInfo(rset.getInt("skill_id"), rset.getInt("skill_level"));
							if (skill == null)
							{
								continue;
							}
							
							if (skill.hasEffects())
							{
								SummonEffectsTable.getInstance().addServitorEffect(actor.getOwner(), actor.getReferenceSkill(), skill, effectCount, effectCurTime);
							}
						}
					}
				}
			}
			
			delete(actor);
		}
		catch (Exception e)
		{
			Log.error("Could not restore " + actor + " active effect data: " + e.getMessage(), e);
		}
		finally
		{
			SummonEffectsTable.getInstance().applyServitorEffects(actor, actor.getOwner(), actor.getReferenceSkill());
		}
	}
	
	@Override
	public void insert(final L2ServitorInstance actor, final boolean storeEffects)
	{
		if (!storeEffects)
		{
			return;
		}
		
		// Clear list for overwrite
		SummonEffectsTable.getInstance().clearServitorEffects(actor.getOwner(), actor.getReferenceSkill());
		
		final int ownerId = actor.getOwner().getObjectId();
		final int ownerClassId = actor.getOwner().getClassIndex();
		final int servitorRefSkill = actor.getReferenceSkill();
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			Statement statement = con.createStatement())
		{
			// Delete all current stored effects for char to avoid dupe
			delete(actor);
			
			int buff_index = 0;
			
			final List<Integer> storedSkills = new LinkedList<>();
			
			final SqlBatch b = new SqlBatch(ADD_SKILL_SAVE);
			for (L2Effect effect : actor.getEffectList().getEffects())
			{
				if (effect == null)
				{
					continue;
				}
				
				final L2Skill skill = effect.getSkill();
				
				// Do not save heals.
				switch (skill.getAbnormalType())
				{
					case life_force_others:
						continue;
				}
				
				if (skill.isToggle())
				{
					continue;
				}
				
				// Dances and songs are not kept in retail.
				if (skill.isDance() && !Config.ALT_STORE_DANCES)
				{
					continue;
				}
				
				if (storedSkills.contains(skill.getReuseHashCode()))
				{
					continue;
				}
				
				storedSkills.add(skill.getReuseHashCode());
				
				final StringBuilder sb = new StringBuilder("(");
				sb.append(ownerId).append(",");
				sb.append(ownerClassId).append(",");
				sb.append(servitorRefSkill).append(",");
				sb.append(skill.getId()).append(",");
				sb.append(skill.getLevel()).append(",");
				sb.append(effect.getCount()).append(",");
				sb.append(effect.getTime()).append(",");
				sb.append(++buff_index).append(")");
				b.write(sb.toString());
				
				SummonEffectsTable.getInstance().addServitorEffect(actor.getOwner(), actor.getReferenceSkill(), skill, effect.getCount(), effect.getTime());
			}
			
			if (!b.isEmpty())
			{
				statement.executeUpdate(b.close());
			}
		}
		catch (Exception e)
		{
			Log.error("Could not store summon effect data for owner " + ownerId + ", class " + ownerClassId + ", skill " + servitorRefSkill + ", error", e);
		}
	}
	
	@Override
	public void delete(final L2ServitorInstance actor)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_SKILL_SAVE))
		{
			statement.setInt(1, actor.getOwner().getObjectId());
			statement.setInt(2, actor.getOwner().getClassIndex());
			statement.setInt(3, actor.getReferenceSkill());
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			Log.error("Could not delete servitor effects for " + actor + ": " + e.getMessage(), e);
		}
	}
}
