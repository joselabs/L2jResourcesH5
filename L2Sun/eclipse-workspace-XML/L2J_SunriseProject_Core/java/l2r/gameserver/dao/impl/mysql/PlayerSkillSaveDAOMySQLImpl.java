package l2r.gameserver.dao.impl.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import l2r.Config;
import l2r.L2DatabaseFactory;
import l2r.gameserver.dao.PlayerSkillSaveDAO;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.TimeStamp;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.stats.Env;

import gr.sr.utils.SqlBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Player skill save DAO MySQL implementation.
 * @author vGodFather
 */
public class PlayerSkillSaveDAOMySQLImpl implements PlayerSkillSaveDAO
{
	private static Logger Log = LoggerFactory.getLogger("DAO");
	
	private static final String ADD_SKILL_SAVE = "REPLACE INTO `character_skills_save` (charId,skill_id,skill_level,effect_count,effect_cur_time,reuse_delay,systime,restore_type,class_index,buff_index) VALUES";
	private static final String RESTORE_SKILL_SAVE = "SELECT skill_id,skill_level,effect_count,effect_cur_time, reuse_delay, systime, restore_type FROM character_skills_save WHERE charId=? AND class_index=? ORDER BY buff_index ASC";
	private static final String DELETE_SKILL_SAVE = "DELETE FROM character_skills_save WHERE charId=? AND class_index=?";
	
	@Override
	public void load(final L2PcInstance player)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_SKILL_SAVE))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, player.getClassIndex());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					int effectCount = rset.getInt("effect_count");
					int effectCurTime = rset.getInt("effect_cur_time");
					long reuseDelay = rset.getLong("reuse_delay");
					long systime = rset.getLong("systime");
					int restoreType = rset.getInt("restore_type");
					
					final L2Skill skill = SkillData.getInstance().getInfo(rset.getInt("skill_id"), rset.getInt("skill_level"));
					if (skill == null)
					{
						continue;
					}
					
					final long remainingTime = systime - System.currentTimeMillis();
					if (remainingTime > 10)
					{
						player.disableSkill(skill, remainingTime);
						player.addTimeStamp(skill, reuseDelay, systime);
					}
					
					/**
					 * Restore Type 1 The remaning skills lost effect upon logout but were still under a high reuse delay.
					 */
					if (restoreType > 0)
					{
						continue;
					}
					
					/**
					 * Restore Type 0 These skill were still in effect on the character upon logout.<br>
					 * Some of which were self casted and might still have had a long reuse delay which also is restored.
					 */
					if (skill.hasEffects())
					{
						Env env = new Env();
						env.setCharacter(player);
						env.setTarget(player);
						env.setSkill(skill);
						
						L2Effect ef;
						for (EffectTemplate et : skill.getEffectTemplates())
						{
							ef = et.getEffect(env);
							if (ef != null)
							{
								ef.setCount(effectCount);
								ef.setFirstTime(effectCurTime);
								ef.scheduleEffect();
							}
						}
					}
				}
			}
			
			// Remove previously restored skills
			delete(player, player.getClassIndex());
		}
		catch (Exception e)
		{
			Log.error("Could not restore " + this + " active effect data: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void insert(final L2PcInstance player, final boolean storeEffects)
	{
		if (!storeEffects)
		{
			return;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			Statement statement = con.createStatement())
		{
			// Delete all current stored effects for char to avoid dupe
			delete(player, player.getClassIndex());
			
			final List<Integer> storedSkills = new ArrayList<>();
			
			// Store all effect data along with calulated remaining
			// reuse delays for matching skills. 'restore_type'= 0.
			
			final SqlBatch b = new SqlBatch(ADD_SKILL_SAVE);
			
			StringBuilder sb;
			int buff_index = 0;
			for (L2Effect effect : player.getEffectList().getEffects())
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
				
				final TimeStamp t = player.getSkillReuseTimeStamp(skill.getReuseHashCode());
				
				sb = new StringBuilder("(");
				sb.append(player.getObjectId()).append(",");
				sb.append(skill.getId()).append(",");
				sb.append(skill.getLevel()).append(",");
				sb.append(effect.getCount()).append(",");
				sb.append(effect.getTime()).append(",");
				sb.append((t != null) && t.hasNotPassed() ? t.getReuse() : 0).append(",");
				sb.append((t != null) && t.hasNotPassed() ? t.getStamp() : 0).append(",");
				sb.append(0).append(","); // Store type 0, active buffs/debuffs.
				sb.append(player.getClassIndex()).append(",");
				sb.append(++buff_index).append(")");
				b.write(sb.toString());
			}
			
			// Skills under reuse.
			final Map<Integer, TimeStamp> reuseTimeStamps = player.getSkillReuseTimeStamps();
			if (reuseTimeStamps != null)
			{
				for (Entry<Integer, TimeStamp> ts : reuseTimeStamps.entrySet())
				{
					final int hash = ts.getKey();
					if (storedSkills.contains(hash))
					{
						continue;
					}
					
					final TimeStamp t = ts.getValue();
					if ((t != null) && t.hasNotPassed())
					{
						storedSkills.add(hash);
						
						sb = new StringBuilder("(");
						sb.append(player.getObjectId()).append(",");
						sb.append(t.getSkillId()).append(",");
						sb.append(t.getSkillLvl()).append(",");
						sb.append(-1).append(",");
						sb.append(-1).append(",");
						sb.append(t.getReuse()).append(",");
						sb.append(t.getStamp()).append(",");
						sb.append(1).append(","); // Restore type 1, skill reuse.
						sb.append(player.getClassIndex()).append(",");
						sb.append(++buff_index).append(")");
						b.write(sb.toString());
					}
				}
			}
			
			if (!b.isEmpty())
			{
				statement.executeUpdate(b.close());
			}
		}
		catch (Exception e)
		{
			Log.error("Could not store char effect data: ", e);
		}
	}
	
	@Override
	public void delete(final L2PcInstance player, final int classIndex)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement deleteSkillReuse = con.prepareStatement(DELETE_SKILL_SAVE))
		{
			// Remove all effects info stored for this sub-class.
			deleteSkillReuse.setInt(1, player.getObjectId());
			deleteSkillReuse.setInt(2, classIndex);
			deleteSkillReuse.execute();
		}
		catch (Exception e)
		{
			Log.error("Could not modify sub class for " + player.getName() + " to class index " + classIndex + ": " + e.getMessage(), e);
			
			// This must be done in order to maintain data consistency.
			player.getSubClasses().remove(classIndex);
		}
	}
}
