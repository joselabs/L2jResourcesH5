package l2r.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import l2r.Config;
import l2r.L2DatabaseFactory;
import l2r.gameserver.dao.PetSkillSaveDAO;
import l2r.gameserver.data.SummonEffectsTable;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.actor.instance.L2PetInstance;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.skills.L2Skill;

import gr.sr.utils.SqlBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pet skill save DAO MySQL implementation.
 * @author vGodFather
 */
public class PetSkillSaveDAOMySQLImpl implements PetSkillSaveDAO
{
	private static Logger Log = LoggerFactory.getLogger("DAO");
	
	private static final String ADD_SKILL_SAVE = "REPLACE INTO character_pet_skills_save (petObjItemId,skill_id,skill_level,effect_count,effect_cur_time,buff_index) VALUES";
	private static final String RESTORE_SKILL_SAVE = "SELECT petObjItemId,skill_id,skill_level,effect_count,effect_cur_time,buff_index FROM character_pet_skills_save WHERE petObjItemId=? ORDER BY buff_index ASC";
	private static final String DELETE_SKILL_SAVE = "DELETE FROM character_pet_skills_save WHERE petObjItemId=?";
	
	@Override
	public void load(final L2PetInstance actor)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps1 = con.prepareStatement(RESTORE_SKILL_SAVE);
			PreparedStatement ps2 = con.prepareStatement(DELETE_SKILL_SAVE))
		{
			if (!SummonEffectsTable.getInstance().containsPetId(actor.getControlObjectId()))
			{
				ps1.setInt(1, actor.getControlObjectId());
				try (ResultSet rset = ps1.executeQuery())
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
							SummonEffectsTable.getInstance().addPetEffect(actor.getControlObjectId(), skill, effectCount, effectCurTime);
						}
					}
				}
			}
			
			ps2.setInt(1, actor.getControlObjectId());
			ps2.executeUpdate();
		}
		catch (Exception e)
		{
			Log.error("Could not restore " + actor + " active effect data: " + e.getMessage(), e);
		}
		finally
		{
			SummonEffectsTable.getInstance().applyPetEffects(actor, actor.getControlObjectId());
		}
	}
	
	@Override
	public void insert(final L2PetInstance actor, final boolean storeEffects)
	{
		if (!storeEffects)
		{
			return;
		}
		
		// Clear list for overwrite
		SummonEffectsTable.getInstance().clearPetEffects(actor.getControlObjectId());
		
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
				sb.append(actor.getControlObjectId()).append(",");
				sb.append(skill.getId()).append(",");
				sb.append(skill.getLevel()).append(",");
				sb.append(effect.getCount()).append(",");
				sb.append(effect.getTime()).append(",");
				sb.append(++buff_index).append(")");
				b.write(sb.toString());
				
				SummonEffectsTable.getInstance().addPetEffect(actor.getControlObjectId(), skill, effect.getCount(), effect.getTime());
			}
			
			if (!b.isEmpty())
			{
				statement.executeUpdate(b.close());
			}
		}
		catch (Exception e)
		{
			Log.error("Could not store pet effect data: ", e);
		}
	}
	
	@Override
	public void delete(final L2PetInstance actor)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_SKILL_SAVE))
		{
			statement.setInt(1, actor.getControlObjectId());
			statement.execute();
		}
		catch (Exception e)
		{
			Log.error("Could not delete " + actor + " active effect data: " + e.getMessage(), e);
		}
	}
}
