/*
 * Copyright © 2020 L2RogueLike
 *
 * This file is part of L2JDevs/L2RogueLike fork.
 *
 * L2JDevs & L2RogueLike is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JDevs & L2RogueLike is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.bypasshandlers;

import org.l2jdevs.gameserver.handler.IBypassHandler;
import org.l2jdevs.gameserver.model.L2Object;
import org.l2jdevs.gameserver.model.L2World;
import org.l2jdevs.gameserver.model.actor.L2Character;
import org.l2jdevs.gameserver.model.actor.instance.L2ChestInstance;
import org.l2jdevs.gameserver.model.actor.instance.L2PcInstance;
import org.l2jdevs.gameserver.model.items.instance.L2ItemInstance;
import org.l2jdevs.gameserver.network.serverpackets.ActionFailed;
import org.l2jdevs.gameserver.util.Util;
import org.l2jdevs.roguelike.DeluxeKeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import org.l2jdevs.gameserver.model.actor.L2Npc;

public class L2Chest implements IBypassHandler {
    private static final Logger LOG = LoggerFactory.getLogger(L2Chest.class);
    private static final String[] COMMANDS = {
            "L2Chest"
    };

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }

    @Override
    /**
     * @param cmd server bypass command, such as "L2Chest 18266 open" and so. See L2ChestInstance.openChest
     * @param _target always null?
     */
    public boolean useBypass(final String cmd, final L2PcInstance pc, final L2Character _target) {
        //final StringTokenizer st = new StringTokenizer(command);
        String[] vals = cmd.trim().split("\\s+");
        if (vals.length < 3) {
            LOG.error("L2Chest malformed useBypass cmd : " + cmd);
            return false;
        }
        final String cId = vals[1];
        final String action = vals[2];
        LOG.error("L2Chest useBypass cmd : " + cmd);
        final L2ChestInstance target;
        {
            L2Object t;
            try {
                LOG.error("L2Chest useBypass : id = " + cId);
                LOG.error("L2Chest useBypass : cmd = " + action);
                t = L2World.getInstance().findObject(Integer.parseInt(cId)); // template id!
            } catch (Exception ex) {
                t = null;
            }
            if (t instanceof L2ChestInstance)
                target = (L2ChestInstance) t;
            else {
                LOG.error("L2Chest useBypass : L2Object not found by id in L2World, id = " + cId);
                return false;
            }
        }
        if (target.isDead()) {
            LOG.error("L2Chest useBypass : chest is dead, objectId = " + cId);
            return false;
        }
        boolean res = true;
        switch (action) {
            case "unlock":
                res = evalChestLockOpenUnlock(pc, target);
                break;
            case "open":
            case "deluxe_key":
                res = evalChestLockOpenKey(pc, target);
                break;
            case "force":
                res = evalChestLockForce(pc, target);
                break;
            case "pick":
                res = evalChestLockPick(pc, target);
                break;
            case "untrap": // ? always repeat
                res = evalChestUntrap(pc, target);
                break;
            case "check": // ? always repeat
                res = evalChestCheck(pc, target);
                break;
            case "leave": // always leave
                evalChestLeave(pc, target);
                res = true;
                break;
            default:
                LOG.error("L2Chest useBypass : bad action : " + action);
                return false;
        }
        if (res)
            Util.sendHtml(pc, "<body>Ciao.</body>");
        else
            L2ChestInstance.openChestDialog(pc, target);
        pc.sendPacket(ActionFailed.STATIC_PACKET);
        return true;
    }

    /**
     * unset busy flags and so.
     * ... and close dialog window
     * ... or|and set delay?
     *
     * @param pc
     * @param target
     */
    private static void evalChestLeave(L2PcInstance pc, L2ChestInstance target) {
        LOG.error("L2Chest : leave");
        target.leave();
        //Util.sendHtml(pc, "<body>Ciao.</body>");
        //pc.sendPacket(ActionFailed.STATIC_PACKET);
    }

    /**
     * check for traps ... but trap(s) may spring
     * @param pc
     * @param target
     * @return true if trap springed
     */
    private static boolean evalChestCheck(L2PcInstance pc, L2ChestInstance target) {
        // fixme : stub
        LOG.error("L2Chest : trap check");
        target.setTrapKnown();
        return false;
    }

    /**
     * untrap trap(s) ... but trap(s) may spring
     * @param pc
     * @param target
     * @return true if trap springed
     */
    private static boolean evalChestUntrap(L2PcInstance pc, L2ChestInstance target) {
        // fixme : stub
        LOG.error("L2Chest : trap untrap");
        if(target.isTrapKnown() && target.isTrapped())
            target.setTrapped(false);
        return false;
    }

    /**
     * use deluxe key to open chest
     *
     * @param pc
     * @param chest
     */
    private boolean evalChestLockOpenKey(L2PcInstance pc, L2ChestInstance chest) {
        // fixme : stub
        LOG.error("L2Chest : deluxe_key");
        /* find optimal delux chest key:
         * 1. key grade lower or equal to current "best" key,
         * 2. number of keys lower or equal to current "best" key.
         * 3. key grade not less then level of chest,
         */
        L2ItemInstance[] deluxeKeys = DeluxeKeyUtils.getDeluxeKeys(pc);
        if(deluxeKeys.length < 1) return false;
        L2ItemInstance key = deluxeKeys[0];
        int keyLevel = DeluxeKeyUtils.getKeyLevel(key);
        // fixme: ? here check key.getCount() > 1
        if(deluxeKeys.length > 1)
            for(L2ItemInstance k : deluxeKeys) {
                // find first key for chest grade's range
                int kLvl = DeluxeKeyUtils.getKeyLevel(k); // keyLevel = keyGrade*10+9
                if(kLvl < 1 || k.getCount() < 1) continue;
                if(kLvl < chest.getEffectiveLevel()) {
                    if(kLvl > keyLevel) {
                        key = k;
                        keyLevel = kLvl;
                    }
                }
                else if(keyLevel < DeluxeKeyUtils.getKeyLevel(key)) {
                    key = k;
                    keyLevel = kLvl;
                }
            }
        if(key.getCount() < 1) return false; // oops...
        return chest.lockOpen(pc, key);
    }

    /**
     * use skill Unlock
     *
     * @param pc
     * @param target
     */
    private boolean evalChestLockOpenUnlock(L2PcInstance pc, L2ChestInstance target) {
        // fixme : stub
        LOG.error("L2Chest : unlock");
        return true;
    }

    private static boolean evalChestLockPick(L2PcInstance pc, L2ChestInstance target) {
        // fixme : stub
        LOG.error("L2Chest : lock pick");
        return target.lockOpen(pc);
    }

    private static boolean evalChestLockForce(L2PcInstance pc, L2ChestInstance target) {
        LOG.error("L2Chest : lock force");
        return target.lockForce(pc);
    }

}
