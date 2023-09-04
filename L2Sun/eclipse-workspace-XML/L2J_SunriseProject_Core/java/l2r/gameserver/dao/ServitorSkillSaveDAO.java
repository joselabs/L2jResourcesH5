package l2r.gameserver.dao;

import l2r.gameserver.model.actor.instance.L2ServitorInstance;

/**
 * Servitor skill save DAO interface.
 * @author vGodFather
 */
public interface ServitorSkillSaveDAO
{
	void load(L2ServitorInstance player);
	
	void insert(final L2ServitorInstance actor, final boolean storeEffects);
	
	void delete(final L2ServitorInstance actor);
}
