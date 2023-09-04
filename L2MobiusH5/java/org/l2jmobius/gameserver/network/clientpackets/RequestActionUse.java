/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.clientpackets;

import java.util.Arrays;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.ai.CtrlEvent;
import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.ai.NextAction;
import org.l2jmobius.gameserver.ai.SummonAI;
import org.l2jmobius.gameserver.data.BotReportTable;
import org.l2jmobius.gameserver.data.xml.PetDataTable;
import org.l2jmobius.gameserver.data.xml.PetSkillData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.enums.MountType;
import org.l2jmobius.gameserver.enums.PrivateStoreType;
import org.l2jmobius.gameserver.instancemanager.AirShipManager;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.instance.BabyPet;
import org.l2jmobius.gameserver.model.actor.instance.Pet;
import org.l2jmobius.gameserver.model.actor.instance.SiegeFlag;
import org.l2jmobius.gameserver.model.actor.instance.StaticObject;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.effects.EffectType;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.skill.AbnormalType;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.ChairSit;
import org.l2jmobius.gameserver.network.serverpackets.ExAskCoupleAction;
import org.l2jmobius.gameserver.network.serverpackets.ExBasicActionList;
import org.l2jmobius.gameserver.network.serverpackets.NpcSay;
import org.l2jmobius.gameserver.network.serverpackets.RecipeShopManageList;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * This class manages the action use request packet.
 * @author Zoey76
 */
public class RequestActionUse implements ClientPacket
{
	private static final int SIN_EATER_ID = 12564;
	private static final int SWITCH_STANCE_ID = 6054;
	private static final NpcStringId[] NPC_STRINGS =
	{
		NpcStringId.USING_A_SPECIAL_SKILL_HERE_COULD_TRIGGER_A_BLOODBATH,
		NpcStringId.HEY_WHAT_DO_YOU_EXPECT_OF_ME,
		NpcStringId.UGGGGGH_PUSH_IT_S_NOT_COMING_OUT,
		NpcStringId.AH_I_MISSED_THE_MARK
	};
	
	private int _actionId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_actionId = packet.readInt();
		_ctrlPressed = (packet.readInt() == 1);
		_shiftPressed = (packet.readByte() == 1);
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Don't do anything if player is dead or confused
		if ((player.isFakeDeath() && (_actionId != 0)) || player.isDead() || player.isOutOfControl())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final BuffInfo info = player.getEffectList().getBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
		if (info != null)
		{
			for (AbstractEffect effect : info.getEffects())
			{
				if (!effect.checkCondition(_actionId))
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		// Don't allow to do some action if player is transformed
		if (player.isTransformed())
		{
			final int[] allowedActions = player.isTransformed() ? ExBasicActionList.ACTIONS_ON_TRANSFORM : ExBasicActionList.DEFAULT_ACTION_LIST;
			if (Arrays.binarySearch(allowedActions, _actionId) < 0)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				PacketLogger.warning(player + " used action which he does not have! Id = " + _actionId + " transform: " + player.getTransformation().getId());
				return;
			}
		}
		
		final Summon summon = player.getSummon();
		final WorldObject target = player.getTarget();
		switch (_actionId)
		{
			case 0: // Sit/Stand
			{
				if (player.isSitting() || !player.isMoving() || player.isFakeDeath())
				{
					useSit(client, player, target);
				}
				else
				{
					// Sit when arrive using next action.
					// Creating next action class.
					final NextAction nextAction = new NextAction(CtrlEvent.EVT_ARRIVED, CtrlIntention.AI_INTENTION_MOVE_TO, () -> useSit(client, player, target));
					// Binding next action to AI.
					player.getAI().setNextAction(nextAction);
				}
				break;
			}
			case 1: // Walk/Run
			{
				if (player.isRunning())
				{
					player.setWalking();
				}
				else
				{
					player.setRunning();
				}
				break;
			}
			case 10: // Private Store - Sell
			{
				player.tryOpenPrivateSellStore(false);
				break;
			}
			case 15: // Change Movement Mode (Pets)
			{
				if (validateSummon(client, summon, true))
				{
					((SummonAI) summon.getAI()).notifyFollowStatusChange();
				}
				break;
			}
			case 16: // Attack (Pets)
			{
				if (validateSummon(client, summon, true) && summon.canAttack(_ctrlPressed))
				{
					summon.doSummonAttack(target);
				}
				break;
			}
			case 17: // Stop (Pets)
			{
				if (validateSummon(client, summon, true))
				{
					summon.cancelAction();
				}
				break;
			}
			case 19: // Unsummon Pet
			{
				if (!validateSummon(client, summon, true))
				{
					break;
				}
				if (summon.isDead())
				{
					player.sendPacket(SystemMessageId.DEAD_PETS_CANNOT_BE_RETURNED_TO_THEIR_SUMMONING_ITEM);
					break;
				}
				if (summon.isAttackingNow() || summon.isInCombat() || summon.isMovementDisabled())
				{
					player.sendPacket(SystemMessageId.A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE);
					break;
				}
				if (summon.isHungry())
				{
					if (summon.isPet() && !((Pet) summon).getPetData().getFood().isEmpty())
					{
						player.sendPacket(SystemMessageId.YOU_MAY_NOT_RESTORE_A_HUNGRY_PET);
					}
					else
					{
						player.sendPacket(SystemMessageId.THE_HUNTING_HELPER_PET_CANNOT_BE_RETURNED_BECAUSE_THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_IT_LEAVES);
					}
					break;
				}
				summon.unSummon(player);
				break;
			}
			case 21: // Change Movement Mode (Servitors)
			{
				if (validateSummon(client, summon, false))
				{
					((SummonAI) summon.getAI()).notifyFollowStatusChange();
				}
				break;
			}
			case 22: // Attack (Servitors)
			{
				if (validateSummon(client, summon, false) && summon.canAttack(_ctrlPressed))
				{
					summon.doSummonAttack(target);
				}
				break;
			}
			case 23: // Stop (Servitors)
			{
				if (validateSummon(client, summon, false))
				{
					summon.cancelAction();
				}
				break;
			}
			case 28: // Private Store - Buy
			{
				player.tryOpenPrivateBuyStore();
				break;
			}
			case 32: // Wild Hog Cannon - Wild Cannon
			{
				useSkill(client, "DDMagic", false);
				break;
			}
			case 36: // Soulless - Toxic Smoke
			{
				useSkill(client, "RangeDebuff", false);
				break;
			}
			case 37: // Dwarven Manufacture
			{
				if (player.isAlikeDead() || player.isSellingBuffs())
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				if (player.getPrivateStoreType() != PrivateStoreType.NONE)
				{
					player.setPrivateStoreType(PrivateStoreType.NONE);
					player.broadcastUserInfo();
				}
				if (player.isSitting())
				{
					player.standUp();
				}
				player.sendPacket(new RecipeShopManageList(player, true));
				break;
			}
			case 38: // Mount/Dismount
			{
				player.mountPlayer(summon);
				break;
			}
			case 39: // Soulless - Parasite Burst
			{
				useSkill(client, "RangeDD", false);
				break;
			}
			case 41: // Wild Hog Cannon - Attack
			{
				if (validateSummon(client, summon, false))
				{
					if ((target != null) && (target.isDoor() || (target instanceof SiegeFlag)))
					{
						useSkill(client, 4230, false);
					}
					else
					{
						player.sendPacket(SystemMessageId.INVALID_TARGET);
					}
				}
				break;
			}
			case 42: // Kai the Cat - Self Damage Shield
			{
				useSkill(client, "HealMagic", false);
				break;
			}
			case 43: // Merrow the Unicorn - Hydro Screw
			{
				useSkill(client, "DDMagic", false);
				break;
			}
			case 44: // Big Boom - Boom Attack
			{
				useSkill(client, "DDMagic", false);
				break;
			}
			case 45: // Boxer the Unicorn - Master Recharge
			{
				useSkill(client, "HealMagic", player, false);
				break;
			}
			case 46: // Mew the Cat - Mega Storm Strike
			{
				useSkill(client, "DDMagic", false);
				break;
			}
			case 47: // Silhouette - Steal Blood
			{
				useSkill(client, "DDMagic", false);
				break;
			}
			case 48: // Mechanic Golem - Mech. Cannon
			{
				useSkill(client, "DDMagic", false);
				break;
			}
			case 51: // General Manufacture
			{
				// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
				if (player.isAlikeDead())
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				if (player.getPrivateStoreType() != PrivateStoreType.NONE)
				{
					player.setPrivateStoreType(PrivateStoreType.NONE);
					player.broadcastUserInfo();
				}
				if (player.isSitting())
				{
					player.standUp();
				}
				player.sendPacket(new RecipeShopManageList(player, false));
				break;
			}
			case 52: // Unsummon Servitor
			{
				if (validateSummon(client, summon, false))
				{
					if (summon.isAttackingNow() || summon.isInCombat())
					{
						player.sendPacket(SystemMessageId.A_SERVITOR_WHOM_IS_ENGAGED_IN_BATTLE_CANNOT_BE_DE_ACTIVATED);
						break;
					}
					summon.unSummon(player);
				}
				break;
			}
			case 53: // Move to target (Servitors)
			{
				if (validateSummon(client, summon, false) && (target != null) && (summon != target) && !summon.isMovementDisabled())
				{
					summon.setFollowStatus(false);
					summon.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, target.getLocation());
				}
				break;
			}
			case 54: // Move to target (Pets)
			{
				if (validateSummon(client, summon, true) && (target != null) && (summon != target) && !summon.isMovementDisabled())
				{
					summon.setFollowStatus(false);
					summon.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, target.getLocation());
				}
				break;
			}
			case 61: // Private Store Package Sell
			{
				player.tryOpenPrivateSellStore(true);
				break;
			}
			case 65: // Bot Report Button
			{
				if (Config.BOTREPORT_ENABLE)
				{
					BotReportTable.getInstance().reportBot(player);
				}
				else
				{
					player.sendMessage("This feature is disabled.");
				}
				break;
			}
			case 67: // Steer
			{
				if (player.isInAirShip() && player.getAirShip().setCaptain(player))
				{
					player.broadcastUserInfo();
				}
				break;
			}
			case 68: // Cancel Control
			{
				if (player.isInAirShip() && player.getAirShip().isCaptain(player) && player.getAirShip().setCaptain(null))
				{
					player.broadcastUserInfo();
				}
				break;
			}
			case 69: // Destination Map
			{
				AirShipManager.getInstance().sendAirShipTeleportList(player);
				break;
			}
			case 70: // Exit Airship
			{
				if (player.isInAirShip())
				{
					if (player.getAirShip().isCaptain(player))
					{
						if (player.getAirShip().setCaptain(null))
						{
							player.broadcastUserInfo();
						}
					}
					else if (player.getAirShip().isInDock())
					{
						player.getAirShip().oustPlayer(player);
					}
				}
				break;
			}
			case 71:
			case 72:
			case 73:
			{
				useCoupleSocial(client, _actionId - 55);
				break;
			}
			case 1000: // Siege Golem - Siege Hammer
			{
				if ((target != null) && target.isDoor())
				{
					useSkill(client, 4079, false);
				}
				break;
			}
			case 1001: // Sin Eater - Ultimate Bombastic Buster
			{
				if (validateSummon(client, summon, true) && (summon.getId() == SIN_EATER_ID))
				{
					summon.broadcastPacket(new NpcSay(summon.getObjectId(), ChatType.NPC_GENERAL, summon.getId(), NPC_STRINGS[Rnd.get(NPC_STRINGS.length)]));
				}
				break;
			}
			case 1003: // Wind Hatchling/Strider - Wild Stun
			{
				useSkill(client, "PhysicalSpecial", true);
				break;
			}
			case 1004: // Wind Hatchling/Strider - Wild Defense
			{
				useSkill(client, "Buff", player, true);
				break;
			}
			case 1005: // Star Hatchling/Strider - Bright Burst
			{
				useSkill(client, "DDMagic", true);
				break;
			}
			case 1006: // Star Hatchling/Strider - Bright Heal
			{
				useSkill(client, "Heal", player, true);
				break;
			}
			case 1007: // Feline Queen - Blessing of Queen
			{
				useSkill(client, "Buff1", player, false);
				break;
			}
			case 1008: // Feline Queen - Gift of Queen
			{
				useSkill(client, "Buff2", player, false);
				break;
			}
			case 1009: // Feline Queen - Cure of Queen
			{
				useSkill(client, "DDMagic", false);
				break;
			}
			case 1010: // Unicorn Seraphim - Blessing of Seraphim
			{
				useSkill(client, "Buff1", player, false);
				break;
			}
			case 1011: // Unicorn Seraphim - Gift of Seraphim
			{
				useSkill(client, "Buff2", player, false);
				break;
			}
			case 1012: // Unicorn Seraphim - Cure of Seraphim
			{
				useSkill(client, "DDMagic", false);
				break;
			}
			case 1013: // Nightshade - Curse of Shade
			{
				useSkill(client, "DeBuff1", false);
				break;
			}
			case 1014: // Nightshade - Mass Curse of Shade
			{
				useSkill(client, "DeBuff2", false);
				break;
			}
			case 1015: // Nightshade - Shade Sacrifice
			{
				useSkill(client, "Heal", false);
				break;
			}
			case 1016: // Cursed Man - Cursed Blow
			{
				useSkill(client, "PhysicalSpecial1", false);
				break;
			}
			case 1017: // Cursed Man - Cursed Strike
			{
				useSkill(client, "PhysicalSpecial2", false);
				break;
			}
			case 1031: // Feline King - Slash
			{
				useSkill(client, "PhysicalSpecial1", false);
				break;
			}
			case 1032: // Feline King - Spinning Slash
			{
				useSkill(client, "PhysicalSpecial2", false);
				break;
			}
			case 1033: // Feline King - Hold of King
			{
				useSkill(client, "PhysicalSpecial3", false);
				break;
			}
			case 1034: // Magnus the Unicorn - Whiplash
			{
				useSkill(client, "PhysicalSpecial1", false);
				break;
			}
			case 1035: // Magnus the Unicorn - Tridal Wave
			{
				useSkill(client, "PhysicalSpecial2", false);
				break;
			}
			case 1036: // Spectral Lord - Corpse Kaboom
			{
				useSkill(client, "PhysicalSpecial1", false);
				break;
			}
			case 1037: // Spectral Lord - Dicing Death
			{
				useSkill(client, "PhysicalSpecial2", false);
				break;
			}
			case 1038: // Spectral Lord - Dark Curse
			{
				useSkill(client, "PhysicalSpecial3", false);
				break;
			}
			case 1039: // Swoop Cannon - Cannon Fodder
			{
				useSkill(client, 5110, false);
				break;
			}
			case 1040: // Swoop Cannon - Big Bang
			{
				useSkill(client, 5111, false);
				break;
			}
			case 1041: // Great Wolf - Bite Attack
			{
				useSkill(client, "Skill01", true);
				break;
			}
			case 1042: // Great Wolf - Maul
			{
				useSkill(client, "Skill03", true);
				break;
			}
			case 1043: // Great Wolf - Cry of the Wolf
			{
				useSkill(client, "Skill02", true);
				break;
			}
			case 1044: // Great Wolf - Awakening
			{
				useSkill(client, "Skill04", true);
				break;
			}
			case 1045: // Great Wolf - Howl
			{
				useSkill(client, 5584, true);
				break;
			}
			case 1046: // Strider - Roar
			{
				useSkill(client, 5585, true);
				break;
			}
			case 1047: // Divine Beast - Bite
			{
				useSkill(client, 5580, false);
				break;
			}
			case 1048: // Divine Beast - Stun Attack
			{
				useSkill(client, 5581, false);
				break;
			}
			case 1049: // Divine Beast - Fire Breath
			{
				useSkill(client, 5582, false);
				break;
			}
			case 1050: // Divine Beast - Roar
			{
				useSkill(client, 5583, false);
				break;
			}
			case 1051: // Feline Queen - Bless The Body
			{
				useSkill(client, "buff3", false);
				break;
			}
			case 1052: // Feline Queen - Bless The Soul
			{
				useSkill(client, "buff4", false);
				break;
			}
			case 1053: // Feline Queen - Haste
			{
				useSkill(client, "buff5", false);
				break;
			}
			case 1054: // Unicorn Seraphim - Acumen
			{
				useSkill(client, "buff3", false);
				break;
			}
			case 1055: // Unicorn Seraphim - Clarity
			{
				useSkill(client, "buff4", false);
				break;
			}
			case 1056: // Unicorn Seraphim - Empower
			{
				useSkill(client, "buff5", false);
				break;
			}
			case 1057: // Unicorn Seraphim - Wild Magic
			{
				useSkill(client, "buff6", false);
				break;
			}
			case 1058: // Nightshade - Death Whisper
			{
				useSkill(client, "buff3", false);
				break;
			}
			case 1059: // Nightshade - Focus
			{
				useSkill(client, "buff4", false);
				break;
			}
			case 1060: // Nightshade - Guidance
			{
				useSkill(client, "buff5", false);
				break;
			}
			case 1061: // Wild Beast Fighter, White Weasel - Death blow
			{
				useSkill(client, 5745, true);
				break;
			}
			case 1062: // Wild Beast Fighter - Double attack
			{
				useSkill(client, 5746, true);
				break;
			}
			case 1063: // Wild Beast Fighter - Spin attack
			{
				useSkill(client, 5747, true);
				break;
			}
			case 1064: // Wild Beast Fighter - Meteor Shower
			{
				useSkill(client, 5748, true);
				break;
			}
			case 1065: // Fox Shaman, Wild Beast Fighter, White Weasel, Fairy Princess - Awakening
			{
				useSkill(client, 5753, true);
				break;
			}
			case 1066: // Fox Shaman, Spirit Shaman - Thunder Bolt
			{
				useSkill(client, 5749, true);
				break;
			}
			case 1067: // Fox Shaman, Spirit Shaman - Flash
			{
				useSkill(client, 5750, true);
				break;
			}
			case 1068: // Fox Shaman, Spirit Shaman - Lightning Wave
			{
				useSkill(client, 5751, true);
				break;
			}
			case 1069: // Fox Shaman, Fairy Princess - Flare
			{
				useSkill(client, 5752, true);
				break;
			}
			case 1070: // White Weasel, Fairy Princess, Improved Baby Buffalo, Improved Baby Kookaburra, Improved Baby Cougar, Spirit Shaman, Toy Knight, Turtle Ascetic - Buff control
			{
				useSkill(client, 5771, true);
				break;
			}
			case 1071: // Tigress - Power Strike
			{
				useSkill(client, "DDMagic", true);
				break;
			}
			case 1072: // Toy Knight - Piercing attack
			{
				useSkill(client, 6046, true);
				break;
			}
			case 1073: // Toy Knight - Whirlwind
			{
				useSkill(client, 6047, true);
				break;
			}
			case 1074: // Toy Knight - Lance Smash
			{
				useSkill(client, 6048, true);
				break;
			}
			case 1075: // Toy Knight - Battle Cry
			{
				useSkill(client, 6049, true);
				break;
			}
			case 1076: // Turtle Ascetic - Power Smash
			{
				useSkill(client, 6050, true);
				break;
			}
			case 1077: // Turtle Ascetic - Energy Burst
			{
				useSkill(client, 6051, true);
				break;
			}
			case 1078: // Turtle Ascetic - Shockwave
			{
				useSkill(client, 6052, true);
				break;
			}
			case 1079: // Turtle Ascetic - Howl
			{
				useSkill(client, 6053, true);
				break;
			}
			case 1080: // Phoenix Rush
			{
				useSkill(client, 6041, false);
				break;
			}
			case 1081: // Phoenix Cleanse
			{
				useSkill(client, 6042, false);
				break;
			}
			case 1082: // Phoenix Flame Feather
			{
				useSkill(client, 6043, false);
				break;
			}
			case 1083: // Phoenix Flame Beak
			{
				useSkill(client, 6044, false);
				break;
			}
			case 1084: // Switch State
			{
				if (summon instanceof BabyPet)
				{
					useSkill(client, 6054, true);
				}
				break;
			}
			case 1086: // Panther Cancel
			{
				useSkill(client, 6094, false);
				break;
			}
			case 1087: // Panther Dark Claw
			{
				useSkill(client, 6095, false);
				break;
			}
			case 1088: // Panther Fatal Claw
			{
				useSkill(client, 6096, false);
				break;
			}
			case 1089: // Deinonychus - Tail Strike
			{
				useSkill(client, 6199, true);
				break;
			}
			case 1090: // Guardian's Strider - Strider Bite
			{
				useSkill(client, 6205, true);
				break;
			}
			case 1091: // Guardian's Strider - Strider Fear
			{
				useSkill(client, 6206, true);
				break;
			}
			case 1092: // Guardian's Strider - Strider Dash
			{
				useSkill(client, 6207, true);
				break;
			}
			case 1093: // Maguen - Maguen Strike
			{
				useSkill(client, 6618, true);
				break;
			}
			case 1094: // Maguen - Maguen Wind Walk
			{
				useSkill(client, 6681, true);
				break;
			}
			case 1095: // Elite Maguen - Maguen Power Strike
			{
				useSkill(client, 6619, true);
				break;
			}
			case 1096: // Elite Maguen - Elite Maguen Wind Walk
			{
				useSkill(client, 6682, true);
				break;
			}
			case 1097: // Maguen - Maguen Return
			{
				useSkill(client, 6683, true);
				break;
			}
			case 1098: // Elite Maguen - Maguen Party Return
			{
				useSkill(client, 6684, true);
				break;
			}
			case 5000: // Baby Rudolph - Reindeer Scratch
			{
				useSkill(client, 23155, true);
				break;
			}
			case 5001: // Deseloph, Hyum, Rekang, Lilias, Lapham, Mafum - Rosy Seduction
			{
				useSkill(client, 23167, true);
				break;
			}
			case 5002: // Deseloph, Hyum, Rekang, Lilias, Lapham, Mafum - Critical Seduction
			{
				useSkill(client, 23168, true);
				break;
			}
			case 5003: // Hyum, Lapham, Hyum, Lapham - Thunder Bolt
			{
				useSkill(client, 5749, true);
				break;
			}
			case 5004: // Hyum, Lapham, Hyum, Lapham - Flash
			{
				useSkill(client, 5750, true);
				break;
			}
			case 5005: // Hyum, Lapham, Hyum, Lapham - Lightning Wave
			{
				useSkill(client, 5751, true);
				break;
			}
			case 5006: // Deseloph, Hyum, Rekang, Lilias, Lapham, Mafum, Deseloph, Hyum, Rekang, Lilias, Lapham, Mafum - Buff Control
			{
				useSkill(client, 5771, true);
				break;
			}
			case 5007: // Deseloph, Lilias, Deseloph, Lilias - Piercing Attack
			{
				useSkill(client, 6046, true);
				break;
			}
			case 5008: // Deseloph, Lilias, Deseloph, Lilias - Spin Attack
			{
				useSkill(client, 6047, true);
				break;
			}
			case 5009: // Deseloph, Lilias, Deseloph, Lilias - Smash
			{
				useSkill(client, 6048, true);
				break;
			}
			case 5010: // Deseloph, Lilias, Deseloph, Lilias - Ignite
			{
				useSkill(client, 6049, true);
				break;
			}
			case 5011: // Rekang, Mafum, Rekang, Mafum - Power Smash
			{
				useSkill(client, 6050, true);
				break;
			}
			case 5012: // Rekang, Mafum, Rekang, Mafum - Energy Burst
			{
				useSkill(client, 6051, true);
				break;
			}
			case 5013: // Rekang, Mafum, Rekang, Mafum - Shockwave
			{
				useSkill(client, 6052, true);
				break;
			}
			case 5014: // Rekang, Mafum, Rekang, Mafum - Ignite
			{
				useSkill(client, 6053, true);
				break;
			}
			case 5015: // Deseloph, Hyum, Rekang, Lilias, Lapham, Mafum, Deseloph, Hyum, Rekang, Lilias, Lapham, Mafum - Switch Stance
			{
				useSkill(client, 6054, true);
				break;
			}
			// Social Packets
			case 12: // Greeting
			{
				tryBroadcastSocial(client, 2);
				break;
			}
			case 13: // Victory
			{
				tryBroadcastSocial(client, 3);
				break;
			}
			case 14: // Advance
			{
				tryBroadcastSocial(client, 4);
				break;
			}
			case 24: // Yes
			{
				tryBroadcastSocial(client, 6);
				break;
			}
			case 25: // No
			{
				tryBroadcastSocial(client, 5);
				break;
			}
			case 26: // Bow
			{
				tryBroadcastSocial(client, 7);
				break;
			}
			case 29: // Unaware
			{
				tryBroadcastSocial(client, 8);
				break;
			}
			case 30: // Social Waiting
			{
				tryBroadcastSocial(client, 9);
				break;
			}
			case 31: // Laugh
			{
				tryBroadcastSocial(client, 10);
				break;
			}
			case 33: // Applaud
			{
				tryBroadcastSocial(client, 11);
				break;
			}
			case 34: // Dance
			{
				tryBroadcastSocial(client, 12);
				break;
			}
			case 35: // Sorrow
			{
				tryBroadcastSocial(client, 13);
				break;
			}
			case 62: // Charm
			{
				tryBroadcastSocial(client, 14);
				break;
			}
			case 66: // Shyness
			{
				tryBroadcastSocial(client, 15);
				break;
			}
		}
	}
	
	/**
	 * Use the sit action.
	 * @param client the game client
	 * @param player the player trying to sit
	 * @param target the target to sit, throne, bench or chair
	 * @return {@code true} if the player can sit, {@code false} otherwise
	 */
	protected boolean useSit(GameClient client, Player player, WorldObject target)
	{
		if (player.getMountType() != MountType.NONE)
		{
			return false;
		}
		
		if (!player.isSitting() && (target instanceof StaticObject) && (((StaticObject) target).getType() == 1) && player.isInsideRadius2D(target, StaticObject.INTERACTION_DISTANCE))
		{
			final ChairSit cs = new ChairSit(player, target.getId());
			client.sendPacket(cs);
			player.sitDown();
			player.broadcastPacket(cs);
			return true;
		}
		
		if (player.isFakeDeath())
		{
			player.stopEffects(EffectType.FAKE_DEATH);
		}
		else if (player.isSitting())
		{
			player.standUp();
		}
		else
		{
			player.sitDown();
		}
		return true;
	}
	
	/**
	 * Cast a skill for active summon.<br>
	 * Target is specified as a parameter but can be overwrited or ignored depending on skill type.
	 * @param client the game client
	 * @param skillId the skill Id to be casted by the summon
	 * @param target the target to cast the skill on, overwritten or ignored depending on skill type
	 * @param pet if {@code true} it'll validate a pet, if {@code false} it will validate a servitor
	 */
	private void useSkill(GameClient client, int skillId, WorldObject target, boolean pet)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Summon summon = player.getSummon();
		if (!validateSummon(client, summon, pet))
		{
			return;
		}
		
		if (!canControl(client, summon))
		{
			return;
		}
		
		int level = 0;
		if (summon.isPet())
		{
			level = PetDataTable.getInstance().getPetData(summon.getId()).getAvailableLevel(skillId, summon.getLevel());
		}
		else
		{
			level = PetSkillData.getInstance().getAvailableLevel(summon, skillId);
		}
		
		if (level > 0)
		{
			summon.setTarget(target);
			summon.useMagic(SkillData.getInstance().getSkill(skillId, level), _ctrlPressed, _shiftPressed);
		}
		
		if (skillId == SWITCH_STANCE_ID)
		{
			summon.switchMode();
		}
	}
	
	private void useSkill(GameClient client, String skillName, WorldObject target, boolean pet)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Summon summon = player.getSummon();
		if (!validateSummon(client, summon, pet))
		{
			return;
		}
		
		if (!canControl(client, summon))
		{
			return;
		}
		
		if ((summon instanceof BabyPet) && !((BabyPet) summon).isInSupportMode())
		{
			client.sendPacket(SystemMessageId.A_PET_ON_AUXILIARY_MODE_CANNOT_USE_SKILLS);
			return;
		}
		
		final SkillHolder skillHolder = summon.getTemplate().getParameters().getSkillHolder(skillName);
		if (skillHolder == null)
		{
			return;
		}
		
		final Skill skill = skillHolder.getSkill();
		if (skill != null)
		{
			summon.setTarget(target);
			summon.useMagic(skill, _ctrlPressed, _shiftPressed);
			if (skill.getId() == SWITCH_STANCE_ID)
			{
				summon.switchMode();
			}
		}
	}
	
	private boolean canControl(GameClient client, Summon summon)
	{
		if ((summon instanceof BabyPet) && !((BabyPet) summon).isInSupportMode())
		{
			client.sendPacket(SystemMessageId.A_PET_ON_AUXILIARY_MODE_CANNOT_USE_SKILLS);
			return false;
		}
		
		if (summon.isPet() && ((summon.getLevel() - client.getPlayer().getLevel()) > 20))
		{
			client.sendPacket(SystemMessageId.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Cast a skill for active summon.<br>
	 * Target is retrieved from owner's target, then validated by overloaded method useSkill(int, Creature).
	 * @param client the game client
	 * @param skillId the skill Id to use
	 * @param pet if {@code true} it'll validate a pet, if {@code false} it will validate a servitor
	 */
	private void useSkill(GameClient client, int skillId, boolean pet)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		useSkill(client, skillId, player.getTarget(), pet);
	}
	
	/**
	 * Cast a skill for active summon.<br>
	 * Target is retrieved from owner's target, then validated by overloaded method useSkill(int, Creature).
	 * @param client the game client
	 * @param skillName the skill name to use
	 * @param pet if {@code true} it'll validate a pet, if {@code false} it will validate a servitor
	 */
	private void useSkill(GameClient client, String skillName, boolean pet)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		useSkill(client, skillName, player.getTarget(), pet);
	}
	
	/**
	 * Validates the given summon and sends a system message to the master.
	 * @param client the game client
	 * @param summon the summon to validate
	 * @param checkPet if {@code true} it'll validate a pet, if {@code false} it will validate a servitor
	 * @return {@code true} if the summon is not null and whether is a pet or a servitor depending on {@code checkPet} value, {@code false} otherwise
	 */
	private boolean validateSummon(GameClient client, Summon summon, boolean checkPet)
	{
		if ((summon != null) && ((checkPet && summon.isPet()) || summon.isServitor()))
		{
			if (summon.isPet() && ((Pet) summon).isUncontrollable())
			{
				client.sendPacket(SystemMessageId.ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR);
				return false;
			}
			if (summon.isBetrayed())
			{
				client.sendPacket(SystemMessageId.YOUR_PET_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
				return false;
			}
			return true;
		}
		
		if (checkPet)
		{
			client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_PET);
		}
		else
		{
			client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR);
		}
		return false;
	}
	
	/**
	 * Try to broadcast SocialAction packet.
	 * @param client the game client
	 * @param id the social action Id to broadcast
	 */
	private void tryBroadcastSocial(GameClient client, int id)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		if (player.isFishing())
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_3);
			return;
		}
		
		if (player.canMakeSocialAction())
		{
			player.broadcastPacket(new SocialAction(player.getObjectId(), id));
		}
	}
	
	/**
	 * Perform a couple social action.
	 * @param client the game client
	 * @param id the couple social action Id
	 */
	private void useCoupleSocial(GameClient client, int id)
	{
		final Player requester = client.getPlayer();
		if (requester == null)
		{
			return;
		}
		
		final WorldObject target = requester.getTarget();
		if ((target == null) || !target.isPlayer())
		{
			client.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final int distance = (int) requester.calculateDistance2D(target);
		if ((distance > 125) || (distance < 15) || (requester.getObjectId() == target.getObjectId()))
		{
			client.sendPacket(SystemMessageId.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
			return;
		}
		
		SystemMessage sm;
		if (requester.isInStoreMode() || requester.isCrafting())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_IN_PRIVATE_SHOP_MODE_OR_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(requester);
			client.sendPacket(sm);
			return;
		}
		
		if (requester.isInCombat() || requester.isInDuel() || AttackStanceTaskManager.getInstance().hasAttackStanceTask(requester))
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(requester);
			client.sendPacket(sm);
			return;
		}
		
		if (requester.isFishing())
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_3);
			return;
		}
		
		if (requester.getKarma() > 0)
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(requester);
			client.sendPacket(sm);
			return;
		}
		
		if (requester.isInOlympiadMode())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(requester);
			client.sendPacket(sm);
			return;
		}
		
		if (requester.isInSiege())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_IN_A_CASTLE_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(requester);
			client.sendPacket(sm);
			return;
		}
		
		if (requester.isInHideoutSiege())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_PARTICIPATING_IN_A_HIDEOUT_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(requester);
			client.sendPacket(sm);
		}
		
		if (requester.isMounted() || requester.isFlyingMounted() || requester.isInBoat() || requester.isInAirShip())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_RIDING_A_SHIP_STEED_OR_STRIDER_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(requester);
			client.sendPacket(sm);
			return;
		}
		
		if (requester.isTransformed())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_CURRENTLY_TRANSFORMING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(requester);
			client.sendPacket(sm);
			return;
		}
		
		if (requester.isAlikeDead())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_CURRENTLY_DEAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(requester);
			client.sendPacket(sm);
			return;
		}
		
		// Checks for partner.
		final Player partner = target.getActingPlayer();
		if (partner.isInStoreMode() || partner.isCrafting())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_IN_PRIVATE_SHOP_MODE_OR_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.isInCombat() || partner.isInDuel() || AttackStanceTaskManager.getInstance().hasAttackStanceTask(partner))
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.getMultiSociaAction() > 0)
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_ALREADY_PARTICIPATING_IN_A_COUPLE_ACTION_AND_CANNOT_BE_REQUESTED_FOR_ANOTHER_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.isFishing())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_FISHING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.getKarma() > 0)
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.isInOlympiadMode())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.isInHideoutSiege())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_PARTICIPATING_IN_A_HIDEOUT_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.isInSiege())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_IN_A_CASTLE_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.isMounted() || partner.isFlyingMounted() || partner.isInBoat() || partner.isInAirShip())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_RIDING_A_SHIP_STEED_OR_STRIDER_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.isTeleporting())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_CURRENTLY_TELEPORTING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.isTransformed())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_CURRENTLY_TRANSFORMING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (partner.isAlikeDead())
		{
			sm = new SystemMessage(SystemMessageId.C1_IS_CURRENTLY_DEAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION);
			sm.addPcName(partner);
			client.sendPacket(sm);
			return;
		}
		
		if (requester.isAllSkillsDisabled() || partner.isAllSkillsDisabled())
		{
			client.sendPacket(SystemMessageId.THE_COUPLE_ACTION_WAS_CANCELLED);
			return;
		}
		
		requester.setMultiSocialAction(id, partner.getObjectId());
		sm = new SystemMessage(SystemMessageId.YOU_HAVE_REQUESTED_A_COUPLE_ACTION_WITH_C1);
		sm.addPcName(partner);
		client.sendPacket(sm);
		
		if ((requester.getAI().getIntention() != CtrlIntention.AI_INTENTION_IDLE) || (partner.getAI().getIntention() != CtrlIntention.AI_INTENTION_IDLE))
		{
			requester.getAI().setNextAction(new NextAction(CtrlEvent.EVT_ARRIVED, CtrlIntention.AI_INTENTION_MOVE_TO, () -> partner.sendPacket(new ExAskCoupleAction(requester.getObjectId(), id))));
			return;
		}
		
		if (requester.isCastingNow() || requester.isCastingSimultaneouslyNow())
		{
			requester.getAI().setNextAction(new NextAction(CtrlEvent.EVT_FINISH_CASTING, CtrlIntention.AI_INTENTION_CAST, () -> partner.sendPacket(new ExAskCoupleAction(requester.getObjectId(), id))));
			return;
		}
		
		partner.sendPacket(new ExAskCoupleAction(requester.getObjectId(), id));
	}
}
