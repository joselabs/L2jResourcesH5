package l2r.gameserver.dao;

import l2r.gameserver.model.actor.instance.L2PcInstance;

/**
 * Player skill save DAO interface.
 * @author vGodFather
 */
public interface PlayerSkillSaveDAO
{
	void load(L2PcInstance player);
	
	void insert(final L2PcInstance player, final boolean storeEffects);
	
	void delete(final L2PcInstance player, final int classIndex);
}
