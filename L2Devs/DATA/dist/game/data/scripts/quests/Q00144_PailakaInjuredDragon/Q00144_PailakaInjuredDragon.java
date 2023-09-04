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
package quests.Q00144_PailakaInjuredDragon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.l2jdevs.gameserver.ai.CtrlEvent;
import org.l2jdevs.gameserver.ai.CtrlIntention;
import org.l2jdevs.gameserver.datatables.SkillData;
import org.l2jdevs.gameserver.enums.audio.Sound;
import org.l2jdevs.gameserver.instancemanager.InstanceManager;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.L2Npc;
import org.l2jdevs.gameserver.model.actor.L2Summon;
import org.l2jdevs.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PetInstance;
import org.l2jdevs.gameserver.model.entity.Instance;
import org.l2jdevs.gameserver.model.instancezone.InstanceWorld;
import org.l2jdevs.gameserver.model.quest.Quest;
import org.l2jdevs.gameserver.model.quest.QuestState;
import org.l2jdevs.gameserver.model.quest.State;
import org.l2jdevs.gameserver.model.zone.L2ZoneType;
import org.l2jdevs.gameserver.network.SystemMessageId;
import org.l2jdevs.gameserver.util.Util;
import org.l2jdevs.util.Rnd;

public class Q00144_PailakaInjuredDragon extends Quest
{
	private static final int MIN_LEVEL = 73;
	private static final int MAX_LEVEL = 77;
	private static final int MAX_SUMMON_LEVEL = 80;
	private static final int EXIT_TIME = 5;
	private static final int INSTANCE_ID = 45;
	protected static final int[] TELEPORT =
	{
		125757,
		-40928,
		-3736
	};
	
	private static final Map<Integer, int[]> NOEXIT_ZONES = new HashMap<>();
	static
	{
		NOEXIT_ZONES.put(200001, new int[]
		{
			123167,
			-45743,
			-3023
		});
		NOEXIT_ZONES.put(200002, new int[]
		{
			117783,
			-46398,
			-2560
		});
		NOEXIT_ZONES.put(200003, new int[]
		{
			116791,
			-51556,
			-2584
		});
		NOEXIT_ZONES.put(200004, new int[]
		{
			117993,
			-52505,
			-2480
		});
		NOEXIT_ZONES.put(200005, new int[]
		{
			113226,
			-44080,
			-2776
		});
		NOEXIT_ZONES.put(200006, new int[]
		{
			110326,
			-45016,
			-2444
		});
		NOEXIT_ZONES.put(200007, new int[]
		{
			118341,
			-55951,
			-2280
		});
		NOEXIT_ZONES.put(200008, new int[]
		{
			110127,
			-41562,
			-2332
		});
	}
	
	private static final int KETRA_ORC_SHAMAN = 32499;
	private static final int KETRA_ORC_SUPPORTER = 32502;
	private static final int KETRA_ORC_SUPPORTER2 = 32512;
	private static final int KETRA_ORC_INTELIGENCE_OFFICER = 32509;
	private static final int VARKA_SILENOS_RECRUIT = 18635;
	private static final int VARKA_SILENOS_FOOTMAN = 18636;
	private static final int VARKA_SILENOS_WARRIOR = 18642;
	private static final int VARKA_SILENOS_OFFICER = 18646;
	private static final int VARKAS_COMMANDER = 18654;
	private static final int VARKA_ELITE_GUARD = 18653;
	private static final int VARKA_SILENOS_GREAT_MAGUS = 18649;
	private static final int VARKA_SILENOS_GENERAL = 18650;
	private static final int VARKA_SILENOS_HEAD_GUARD = 18655;
	private static final int PROPHET_GUARD = 18657;
	private static final int VARKAS_PROPHET = 18659;
	private static final int VARKA_SILENOS_MEDIUM = 18644;
	private static final int VARKA_SILENOS_PRIEST = 18641;
	private static final int VARKA_SILENOS_SHAMAN = 18640;
	private static final int VARKA_SILENOS_SEER = 18648;
	private static final int VARKA_SILENOS_MAGNUS = 18645;
	private static final int DISCIPLE_OF_PROPHET = 18658;
	private static final int VARKA_HEAD_MAGUS = 18656;
	private static final int VARKA_SILENOS_GREAT_SEER = 18652;
	private static final int ANTYLOPE_1 = 18637;
	private static final int ANTYLOPE_2 = 18643;
	private static final int ANTYLOPE_3 = 18651;
	private static final int FLAVA = 18647;
	private static final int LATANA = 18660;
	private static final int SPEAR = 13052;
	private static final int ENCHSPEAR = 13053;
	private static final int LASTSPEAR = 13054;
	private static final int STAGE1 = 13056;
	private static final int STAGE2 = 13057;
	private static final int SHIELD_POTION = 13032;
	private static final int HEAL_POTION = 13033;
	private static final int PSHIRT = 13296;
	private static final int SCROLL_OF_ESCAPE = 736;
	private static int buff_counter = 5;
	private static final int[] NPCS =
	{
		KETRA_ORC_SHAMAN,
		KETRA_ORC_SUPPORTER,
		KETRA_ORC_INTELIGENCE_OFFICER,
		KETRA_ORC_SUPPORTER2
	};
	
	private static final int[] WALL_MONSTERS =
	{
		VARKA_SILENOS_FOOTMAN,
		VARKA_SILENOS_WARRIOR,
		VARKA_SILENOS_OFFICER,
		VARKAS_COMMANDER,
		VARKA_SILENOS_RECRUIT,
		PROPHET_GUARD,
		VARKA_ELITE_GUARD,
		VARKA_SILENOS_GREAT_MAGUS,
		VARKA_SILENOS_GENERAL,
		VARKA_SILENOS_HEAD_GUARD,
		PROPHET_GUARD,
		VARKAS_PROPHET,
		DISCIPLE_OF_PROPHET,
		VARKA_HEAD_MAGUS,
		VARKA_SILENOS_GREAT_SEER,
		VARKA_SILENOS_SHAMAN,
		VARKA_SILENOS_MAGNUS,
		VARKA_SILENOS_SEER,
		VARKA_SILENOS_MEDIUM,
		VARKA_SILENOS_PRIEST
	};
	
	private static final int[] OTHER_MONSTERS =
	{
		ANTYLOPE_1,
		ANTYLOPE_2,
		ANTYLOPE_3,
		FLAVA
	};
	
	private static final int[] ITEMS =
	{
		SPEAR,
		ENCHSPEAR,
		LASTSPEAR,
		STAGE1,
		STAGE2,
		SHIELD_POTION,
		HEAL_POTION
	};
	
	private static final int[][] BUFFS =
	{
		{
			4357,
			2
		},
		{
			4342,
			2
		},
		{
			4356,
			3
		},
		{
			4355,
			3
		},
		{
			4351,
			6
		},
		{
			4345,
			3
		},
		{
			4358,
			3
		},
		{
			4359,
			3
		},
		{
			4360,
			3
		},
		{
			4352,
			2
		},
		{
			4354,
			4
		},
		{
			4347,
			6
		}
	};
	private static final ArrayList<PailakaDrop> DROPLIST = new ArrayList<>();
	static
	{
		DROPLIST.add(new PailakaDrop(HEAL_POTION, 80));
		DROPLIST.add(new PailakaDrop(SHIELD_POTION, 30));
	}
	private static final int[][] HP_HERBS_DROPLIST =
	{
		{
			8601,
			1,
			40
		},
		{
			8600,
			1,
			70
		}
	};
	private static final int[][] MP_HERBS_DROPLIST =
	{
		{
			8604,
			1,
			40
		},
		{
			8603,
			1,
			70
		}
	};
	
	public Q00144_PailakaInjuredDragon()
	{
		super(144, Q00144_PailakaInjuredDragon.class.getSimpleName(), "Pailaka - Injured Dragon");
		addStartNpc(KETRA_ORC_SHAMAN);
		addKillId(LATANA);
		
		questItemIds = ITEMS;
		
		for (int npcId : NPCS)
		{
			addTalkId(npcId);
		}
		
		for (int mobId : OTHER_MONSTERS)
		{
			addKillId(mobId);
		}
		
		for (int mobId : WALL_MONSTERS)
		{
			addSpawnId(mobId);
			addKillId(mobId);
		}
		
		for (int zoneid : NOEXIT_ZONES.keySet())
		{
			addEnterZoneId(zoneid);
		}
	}
	
	protected static final void teleportPlayer(L2PcInstance player, int[] coords, int instanceId)
	{
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.setInstanceId(instanceId);
		player.teleToLocation(coords[0], coords[1], coords[2], true);
	}
	
	private static final void dropHerb(L2Npc mob, L2PcInstance player, int[][] drop)
	{
		final int chance = Rnd.get(100);
		for (int[] element : drop)
		{
			if (chance < element[2])
			{
				((L2MonsterInstance) mob).dropItem(player, element[0], element[1]);
				return;
			}
		}
	}
	
	private static final void dropItem(L2Npc mob, L2PcInstance player)
	{
		Collections.shuffle(DROPLIST);
		for (PailakaDrop pd : DROPLIST)
		{
			if (Rnd.get(100) < pd.getChance())
			{
				((L2MonsterInstance) mob).dropItem(player, pd.getItemID(), Rnd.get(1, 6));
				return;
			}
		}
	}
	
	private static void giveBuff(L2Npc npc, L2PcInstance player, int skillId, int level)
	{
		npc.setTarget(player);
		npc.doCast(SkillData.getInstance().getSkill(skillId, level));
		buff_counter--;
		return;
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		final int cond = qs.getCond();
		if (event.equals("enter"))
		{
			enterInstance(player);
			return "32499-08.html";
		}
		else if (event.equals("enters"))
		{
			enterInstance(player);
			return "32499-10.html";
		}
		else if (event.equals("32499-02.htm"))
		{
			if (cond == 0)
			{
				qs.set("cond", "1");
				qs.setState(State.STARTED);
				qs.playSound(Sound.ITEMSOUND_QUEST_ACCEPT);
			}
		}
		else if (event.equals("32499-04.html"))
		{
			if (cond == 1)
			{
				qs.set("cond", "2");
				qs.playSound(Sound.ITEMSOUND_QUEST_ACCEPT);
			}
		}
		else if (event.equals("32502-04.html"))
		{
			if ((cond == 2) && !qs.hasQuestItems(SPEAR))
			{
				qs.set("cond", "3");
				qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				qs.giveItems(SPEAR, 1);
			}
		}
		else if (event.equals("32509-02.html"))
		{
			if ((cond == 3) && qs.hasQuestItems(SPEAR))
			{
				if (!qs.hasQuestItems(SPEAR) && !qs.hasQuestItems(STAGE1))
				{
					return "32509-07.html";
				}
				if (qs.hasQuestItems(SPEAR) && !qs.hasQuestItems(STAGE1))
				{
					return "32509-01.html";
				}
				if (qs.hasQuestItems(SPEAR) && qs.hasQuestItems(STAGE1))
				{
					qs.takeItems(SPEAR, -1);
					qs.takeItems(STAGE1, -1);
					qs.giveItems(ENCHSPEAR, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
					return "32509-02.html";
				}
			}
			if ((cond == 3) && qs.hasQuestItems(ENCHSPEAR))
			{
				if (!qs.hasQuestItems(ENCHSPEAR) && !qs.hasQuestItems(STAGE2))
				{
					return "32509-07.html";
				}
				if (qs.hasQuestItems(ENCHSPEAR) && !qs.hasQuestItems(STAGE2))
				{
					return "32509-01.html";
				}
				if (qs.hasQuestItems(ENCHSPEAR) && qs.hasQuestItems(STAGE2))
				{
					qs.takeItems(ENCHSPEAR, -1);
					qs.takeItems(STAGE2, -1);
					qs.giveItems(LASTSPEAR, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
					addSpawn(LATANA, 105732, -41787, -1782, 35742, false, 0, false, npc.getInstanceId());
					return "32509-03.html";
				}
			}
			if ((cond == 3) && qs.hasQuestItems(LASTSPEAR))
			{
				if (qs.hasQuestItems(LASTSPEAR))
				{
					return "32509-03a.html";
				}
				if (!qs.hasQuestItems(LASTSPEAR))
				{
					return "32509-07.html";
				}
			}
			return "32509-07.html";
		}
		else if (event.equals("32509-06.html"))
		{
			if (buff_counter < 1)
			{
				return "32509-04.html";
			}
		}
		else if (event.equals("32512-02.html"))
		{
			qs.unset("cond");
			qs.playSound(Sound.ITEMSOUND_QUEST_FINISH);
			qs.exitQuest(false);
			Instance inst = InstanceManager.getInstance().getInstance(npc.getInstanceId());
			inst.setDuration(EXIT_TIME * 60000);
			inst.setEmptyDestroyTime(0);
			if (inst.containsPlayer(player.getObjectId()))
			{
				player.setVitalityPoints(20000, true);
				qs.addExpAndSp(28000000, 2850000);
				qs.giveItems(SCROLL_OF_ESCAPE, 1);
				qs.giveItems(PSHIRT, 1);
			}
		}
		else if (event.startsWith("buff"))
		{
			if (buff_counter > 0)
			{
				final int nr = Integer.parseInt(event.split("buff")[1]);
				giveBuff(npc, player, BUFFS[nr - 1][0], BUFFS[nr - 1][1]);
				return "32509-06a.html";
			}
			return "32509-05.html";
		}
		else if (event.equals("latana_animation"))
		{
			npc.abortAttack();
			npc.abortCast();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			player.abortAttack();
			player.abortCast();
			player.stopMove(null);
			if (player.hasPet())
			{
				player.getSummon().abortAttack();
				player.getSummon().abortCast();
				player.getSummon().stopMove(null);
			}
			return null;
		}
		else if (event.equals("latana_animation2"))
		{
			npc.doCast(SkillData.getInstance().getSkill(5759, 1));
			npc.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player);
			return null;
		}
		return event;
	}
	
	@Override
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if ((character instanceof L2PcInstance) && !character.isDead() && !character.isTeleporting() && ((L2PcInstance) character).isOnline())
		{
			InstanceWorld world = InstanceManager.getInstance().getWorld(character.getInstanceId());
			if ((world != null) && (world.getTemplateId() == INSTANCE_ID))
			{
				final int[] zoneTeleport = NOEXIT_ZONES.get(zone.getId());
				if (zoneTeleport != null)
				{
					final Collection<L2Character> knowns = character.getKnownList().getKnownCharactersInRadius(700);
					for (L2Character npcs : knowns)
					{
						if (!(npcs instanceof L2Npc))
						{
							continue;
						}
						
						if (npcs.isDead())
						{
							continue;
						}
						
						teleportPlayer(character.getActingPlayer(), zoneTeleport, world.getInstanceId());
						break;
					}
				}
			}
		}
		return super.onEnterZone(character, zone);
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		QuestState qs = player.getQuestState(getName());
		if ((qs == null) || (qs.getState() != State.STARTED))
		{
			return null;
		}
		
		final int cond = qs.getCond();
		switch (npc.getId())
		{
			case VARKA_SILENOS_FOOTMAN:
			case VARKA_SILENOS_RECRUIT:
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				if ((cond == 3) && qs.hasQuestItems(SPEAR) && !qs.hasQuestItems(STAGE1) && (Rnd.get(100) < 5))
				{
					qs.giveItems(STAGE1, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
				spawnMageBehind(npc, player, VARKA_SILENOS_MEDIUM);
				checkIfLastInWall(npc);
				break;
			case VARKA_SILENOS_WARRIOR:
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				if ((cond == 3) && qs.hasQuestItems(SPEAR) && !qs.hasQuestItems(STAGE1) && (Rnd.get(100) < 10))
				{
					qs.giveItems(STAGE1, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
				spawnMageBehind(npc, player, VARKA_SILENOS_PRIEST);
				checkIfLastInWall(npc);
				break;
			case VARKA_ELITE_GUARD:
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				if ((cond == 3) && qs.hasQuestItems(SPEAR) && !qs.hasQuestItems(STAGE1) && (Rnd.get(100) < 15))
				{
					qs.giveItems(STAGE1, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
				spawnMageBehind(npc, player, VARKA_SILENOS_SHAMAN);
				checkIfLastInWall(npc);
				break;
			case VARKAS_COMMANDER:
			case VARKA_SILENOS_OFFICER:
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				if ((cond == 3) && qs.hasQuestItems(SPEAR) && !qs.hasQuestItems(STAGE1) && (Rnd.get(100) < 25))
				{
					qs.giveItems(STAGE1, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
				spawnMageBehind(npc, player, VARKA_SILENOS_SEER);
				checkIfLastInWall(npc);
				break;
			case VARKA_SILENOS_GREAT_MAGUS:
			case VARKA_SILENOS_GENERAL:
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				if ((cond == 3) && qs.hasQuestItems(ENCHSPEAR) && !qs.hasQuestItems(STAGE2) && (Rnd.get(100) < 5))
				{
					qs.giveItems(STAGE2, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
				spawnMageBehind(npc, player, VARKA_SILENOS_MAGNUS);
				checkIfLastInWall(npc);
				break;
			case VARKAS_PROPHET:
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				if ((cond == 3) && qs.hasQuestItems(ENCHSPEAR) && !qs.hasQuestItems(STAGE2) && (Rnd.get(100) < 10))
				{
					qs.giveItems(STAGE2, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
				spawnMageBehind(npc, player, DISCIPLE_OF_PROPHET);
				checkIfLastInWall(npc);
				break;
			case VARKA_SILENOS_HEAD_GUARD:
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				if ((cond == 3) && qs.hasQuestItems(ENCHSPEAR) && !qs.hasQuestItems(STAGE2) && (Rnd.get(100) < 20))
				{
					qs.giveItems(STAGE2, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
				spawnMageBehind(npc, player, VARKA_HEAD_MAGUS);
				checkIfLastInWall(npc);
				break;
			case PROPHET_GUARD:
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				if ((cond == 3) && qs.hasQuestItems(ENCHSPEAR) && !qs.hasQuestItems(STAGE2) && (Rnd.get(100) < 25))
				{
					qs.giveItems(STAGE2, 1);
					qs.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
				spawnMageBehind(npc, player, VARKA_SILENOS_GREAT_SEER);
				checkIfLastInWall(npc);
				break;
			case LATANA:
				qs.set("cond", "4");
				qs.playSound(Sound.ITEMSOUND_QUEST_MIDDLE);
				addSpawn(KETRA_ORC_SUPPORTER2, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0, false, npc.getInstanceId());
				break;
			case ANTYLOPE_1:
			case ANTYLOPE_2:
			case ANTYLOPE_3:
			case FLAVA:
				dropItem(npc, player);
				break;
			default:
				dropHerb(npc, player, HP_HERBS_DROPLIST);
				dropHerb(npc, player, MP_HERBS_DROPLIST);
				break;
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc instanceof L2MonsterInstance)
		{
			for (int mobId : WALL_MONSTERS)
			{
				if (mobId == npc.getId())
				{
					final L2MonsterInstance monster = (L2MonsterInstance) npc;
					monster.setIsImmobilized(true);
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public final String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		if (qs == null)
		{
			return htmltext;
		}
		
		final int cond = qs.getCond();
		switch (npc.getId())
		{
			case KETRA_ORC_SHAMAN:
				switch (qs.getState())
				{
					case State.CREATED:
						if (player.getLevel() < MIN_LEVEL)
						{
							return "32499-no.html";
						}
						if (player.getLevel() > MAX_LEVEL)
						{
							return "32499-over.html";
						}
						return "32499-00.htm";
					case State.STARTED:
						if (player.getLevel() < MIN_LEVEL)
						{
							return "32499-no.html";
						}
						if (player.getLevel() > MAX_LEVEL)
						{
							return "32499-over.html";
						}
						if (cond == 1)
						{
							return "32499-06.html";
						}
						else if (cond >= 2)
						{
							return "32499-09.html";
						}
					case State.COMPLETED:
						return "32499-completed.html";
					default:
						return "32499-no.html";
				}
			case KETRA_ORC_SUPPORTER:
				if (cond > 2)
				{
					return "32502-04.html";
				}
				return "32502-00.html";
			case KETRA_ORC_INTELIGENCE_OFFICER:
				return "32509-00.html";
			case KETRA_ORC_SUPPORTER2:
				if (qs.getState() == State.COMPLETED)
				{
					return "32512-03.html";
				}
				else if (cond == 4)
				{
					return "32512-01.html";
				}
		}
		
		return getNoQuestMsg(player);
	}
	
	private final void checkIfLastInWall(L2Npc npc)
	{
		final Collection<L2Character> knowns = npc.getKnownList().getKnownCharactersInRadius(700);
		for (L2Character npcs : knowns)
		{
			if (!(npcs instanceof L2Npc))
			{
				continue;
			}
			
			if (npcs.isDead())
			{
				continue;
			}
			
			final L2Npc knownNpc = (L2Npc) npcs;
			
			switch (npc.getId())
			{
				case VARKA_SILENOS_FOOTMAN:
				case VARKA_SILENOS_RECRUIT:
				case VARKA_SILENOS_WARRIOR:
					switch (knownNpc.getId())
					{
						case VARKA_SILENOS_FOOTMAN:
						case VARKA_SILENOS_RECRUIT:
						case VARKA_SILENOS_WARRIOR:
							return;
					}
					break;
				case VARKA_ELITE_GUARD:
				case VARKAS_COMMANDER:
				case VARKA_SILENOS_OFFICER:
					switch (knownNpc.getId())
					{
						case VARKA_ELITE_GUARD:
						case VARKAS_COMMANDER:
						case VARKA_SILENOS_OFFICER:
							return;
					}
					break;
				case VARKA_SILENOS_GREAT_MAGUS:
				case VARKA_SILENOS_GENERAL:
				case VARKAS_PROPHET:
					switch (knownNpc.getId())
					{
						case VARKA_SILENOS_GREAT_MAGUS:
						case VARKA_SILENOS_GENERAL:
						case VARKAS_PROPHET:
							return;
					}
					break;
				case VARKA_SILENOS_HEAD_GUARD:
				case PROPHET_GUARD:
					switch (knownNpc.getId())
					{
						case VARKA_SILENOS_HEAD_GUARD:
						case PROPHET_GUARD:
							return;
					}
					break;
			}
		}
		
		for (L2Character npcs : knowns)
		{
			if (!(npcs instanceof L2Npc))
			{
				continue;
			}
			
			if (npcs.isDead())
			{
				continue;
			}
			
			final L2Npc knownNpc = (L2Npc) npcs;
			
			switch (npc.getId())
			{
				case VARKA_SILENOS_FOOTMAN:
				case VARKA_SILENOS_RECRUIT:
				case VARKA_SILENOS_WARRIOR:
					switch (knownNpc.getId())
					{
						case VARKA_SILENOS_MEDIUM:
						case VARKA_SILENOS_PRIEST:
							knownNpc.abortCast();
							knownNpc.deleteMe();
							break;
					}
					break;
				case VARKA_ELITE_GUARD:
				case VARKAS_COMMANDER:
				case VARKA_SILENOS_OFFICER:
					switch (knownNpc.getId())
					{
						case VARKA_SILENOS_SHAMAN:
						case VARKA_SILENOS_SEER:
							knownNpc.abortCast();
							knownNpc.deleteMe();
							break;
					}
					break;
				case VARKA_SILENOS_GREAT_MAGUS:
				case VARKA_SILENOS_GENERAL:
				case VARKAS_PROPHET:
					switch (knownNpc.getId())
					{
						case VARKA_SILENOS_MAGNUS:
						case DISCIPLE_OF_PROPHET:
							knownNpc.abortCast();
							knownNpc.deleteMe();
							break;
					}
					break;
				case VARKA_SILENOS_HEAD_GUARD:
				case PROPHET_GUARD:
					switch (knownNpc.getId())
					{
						case VARKA_HEAD_MAGUS:
						case VARKA_SILENOS_GREAT_SEER:
							knownNpc.abortCast();
							knownNpc.deleteMe();
							break;
					}
					break;
			}
		}
	}
	
	private final void checkMaxSummonLevel(L2PcInstance player)
	{
		final L2Summon pet = player.getSummon();
		if (pet instanceof L2PetInstance)
		{
			if (pet.getLevel() > MAX_SUMMON_LEVEL)
			{
				pet.unSummon(player);
			}
		}
	}
	
	private final synchronized void enterInstance(L2PcInstance player)
	{
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		if (world != null)
		{
			if (world.getTemplateId() != INSTANCE_ID)
			{
				player.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_ANOTHER_INSTANT_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
				return;
			}
			Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
			if (inst != null)
			{
				checkMaxSummonLevel(player);
				
				teleportPlayer(player, TELEPORT, world.getInstanceId());
			}
		}
		else
		{
			final int instanceId = InstanceManager.getInstance().createDynamicInstance("PailakaInjuredDragon.xml");
			
			world = new InstanceWorld();
			world.setInstanceId(instanceId);
			world.setTemplateId(INSTANCE_ID);
			InstanceManager.getInstance().addWorld(world);
			checkMaxSummonLevel(player);
			world.addAllowed(player.getObjectId());
			teleportPlayer(player, TELEPORT, instanceId);
		}
	}
	
	private final void spawnMageBehind(L2Npc npc, L2PcInstance player, int mageId)
	{
		final double rads = Math.toRadians(Util.convertHeadingToDegree(npc.getSpawn().getHeading()) + 180);
		final int mageX = (int) (npc.getX() + (150 * Math.cos(rads)));
		final int mageY = (int) (npc.getY() + (150 * Math.sin(rads)));
		final L2Npc mageBack = addSpawn(mageId, mageX, mageY, npc.getZ(), npc.getSpawn().getHeading(), false, 0, true, npc.getInstanceId());
		mageBack.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 1000);
	}
	
	static final class Teleport implements Runnable
	{
		private final L2Character _char;
		private final int _instanceId;
		
		public Teleport(L2Character c, int id)
		{
			_char = c;
			_instanceId = id;
		}
		
		@Override
		public void run()
		{
			try
			{
				teleportPlayer((L2PcInstance) _char, TELEPORT, _instanceId);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static class PailakaDrop
	{
		private final int _itemId;
		private final int _chance;
		
		public PailakaDrop(int itemId, int chance)
		{
			_itemId = itemId;
			_chance = chance;
		}
		
		public int getChance()
		{
			return _chance;
		}
		
		public int getItemID()
		{
			return _itemId;
		}
	}
}