package l2r.gameserver.dao;

import l2r.gameserver.model.actor.instance.L2PetInstance;

/**
 * Pet skill save DAO interface.
 * @author vGodFather
 */
public interface PetSkillSaveDAO
{
	void load(L2PetInstance player);
	
	void insert(final L2PetInstance actor, final boolean storeEffects);
	
	void delete(final L2PetInstance actor);
}
