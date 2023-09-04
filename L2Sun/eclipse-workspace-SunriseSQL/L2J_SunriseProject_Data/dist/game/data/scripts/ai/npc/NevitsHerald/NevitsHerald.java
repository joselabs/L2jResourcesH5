package ai.npc.NevitsHerald;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import l2r.gameserver.model.L2World;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.network.serverpackets.NpcSay;
import l2r.util.Rnd;

import ai.npc.AbstractNpcAI;

public class NevitsHerald extends AbstractNpcAI
{
	private static final Set<L2Npc> spawns = ConcurrentHashMap.newKeySet();
	private static boolean isActive = false;
	
	private static final int NevitsHerald = 4326;
	private static final int[] Antharas =
	{
		29019,
		29066,
		29067,
		29068
	};
	private static final int Valakas = 29028;
	private static final NpcStringId[] spam =
	{
		NpcStringId.SHOW_RESPECT_TO_THE_HEROES_WHO_DEFEATED_THE_EVIL_DRAGON_AND_PROTECTED_THIS_ADEN_WORLD,
		NpcStringId.SHOUT_TO_CELEBRATE_THE_VICTORY_OF_THE_HEROES,
		NpcStringId.PRAISE_THE_ACHIEVEMENT_OF_THE_HEROES_AND_RECEIVE_NEVITS_BLESSING
	};
	
	private static final Location[] NEVITS_HERALD_LOC =
	{
		new Location(86971, -142772, -1336, 20480), // Town of Schuttgart
		new Location(44165, -48494, -792, 32768), // Rune Township
		new Location(148017, -55264, -2728, 49152), // Town of Goddard
		new Location(147919, 26631, -2200, 16384), // Town of Aden
		new Location(82325, 53278, -1488, 16384), // Town of Oren
		new Location(81925, 148302, -3464, 49152), // Town of Giran
		new Location(111678, 219197, -3536, 49152), // Heine
		new Location(16254, 142808, -2696, 16384), // Town of Dion
		new Location(-13865, 122081, -2984, 32768), // Town of Gludio
		new Location(-83248, 150832, -3136, 32768), // Gludin Village
		new Location(116899, 77256, -2688, 49152) // Hunters Village
	};
	
	// Skill
	private static final SkillHolder FALL_OF_THE_DRAGON = new SkillHolder(23312);
	
	public NevitsHerald()
	{
		super(NevitsHerald.class.getSimpleName(), "ai/npc");
		
		addFirstTalkId(NevitsHerald);
		addStartNpc(NevitsHerald);
		addTalkId(NevitsHerald);
		addKillId(Antharas);
		addKillId(Valakas);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "4326.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		
		if (npc.getId() == NevitsHerald)
		{
			if (event.equalsIgnoreCase("buff"))
			{
				if (player.isAffectedBySkill(FALL_OF_THE_DRAGON.getSkillId()))
				{
					return "4326-1.htm";
				}
				
				npc.setTarget(player);
				npc.doCast(FALL_OF_THE_DRAGON);
				return null;
			}
		}
		else if (event.equalsIgnoreCase("text_spam"))
		{
			cancelQuestTimer("text_spam", npc, player);
			npc.broadcastPacket(new NpcSay(NevitsHerald, Say2.SHOUT, NevitsHerald, spam[Rnd.get(0, spam.length - 1)]));
			startQuestTimer("text_spam", 60000, npc, player);
			return null;
		}
		else if (event.equalsIgnoreCase("despawn"))
		{
			despawnHeralds();
		}
		return htmltext;
	}
	
	private void despawnHeralds()
	{
		if (!spawns.isEmpty())
		{
			for (L2Npc npc : spawns)
			{
				npc.deleteMe();
			}
		}
		isActive = false;
		spawns.clear();
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		ExShowScreenMessage message = new ExShowScreenMessage(npc.getId() == Valakas ? NpcStringId.THE_EVIL_FIRE_DRAGON_VALAKAS_HAS_BEEN_DEFEATED : NpcStringId.THE_EVIL_LAND_DRAGON_ANTHARAS_HAS_BEEN_DEFEATED, 2, 10000);
		// message.setUpperEffect(true);
		
		for (L2PcInstance onlinePlayer : L2World.getInstance().getPlayers())
		{
			if (onlinePlayer.isOnline())
			{
				onlinePlayer.sendPacket(message);
			}
		}
		
		if (!isActive)
		{
			isActive = true;
			
			spawns.clear();
			
			for (Location loc : NEVITS_HERALD_LOC)
			{
				L2Npc herald = addSpawn(NevitsHerald, loc, false, 0);
				spawns.add(herald);
			}
			startQuestTimer("despawn", 14400000, npc, killer);
			startQuestTimer("text_spam", 3000, npc, killer);
		}
		return null;
	}
}
