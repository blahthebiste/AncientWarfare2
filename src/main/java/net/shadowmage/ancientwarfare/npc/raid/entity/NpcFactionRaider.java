package net.shadowmage.ancientwarfare.npc.raid.entity;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

/**
 * Raider NPC — spawned during raids. Always hostile toward players and defenders.
 * Uses NpcAIAttackNearest to engage any valid nearby targets.
 */
public class NpcFactionRaider extends NpcFaction {

    private boolean initializedAI = false;

    public NpcFactionRaider(World world) {
        super(world, "Raiders");
    }

    @Override
    protected void initEntityAI() {
        if (initializedAI) return;
        initializedAI = true;

        // Vanilla-compatible movement and combat tasks
        this.tasks.addTask(0, new EntityAIAttackMelee(this, 1.2D, false));
        this.tasks.addTask(1, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));

        // Use AW2 custom AI for nearest attack targeting
        this.targetTasks.addTask(0, new NpcAIAttackNearest(this, new Predicate<Entity>() {
            @Override
            public boolean apply(Entity target) {
                if (!(target instanceof EntityLivingBase)) return false;

                // Skip raiders and other NPCFaction members
                if (target instanceof NpcFactionRaider) return false;
                if (target instanceof NpcFaction) return false;

                return true; // Valid target (player, guard, etc.)
            }
        }));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Move toward the Town Hall area periodically if idle
        if (!this.world.isRemote && this.getAttackTarget() == null && this.ticksExisted % 60 == 0) {
            if (this.getEntityData().hasKey("raidTargetX")) {
                int x = this.getEntityData().getInteger("raidTargetX");
                int y = this.getEntityData().getInteger("raidTargetY");
                int z = this.getEntityData().getInteger("raidTargetZ");
                this.getNavigator().tryMoveToXYZ(x, y, z, 1.0D);
            }
        }
    }

    @Override
    public boolean canDespawn() {
        // Never despawn during an active raid
        return false;
    }

    @Override
    public String getNpcType() {
        // Used for registry mapping
        return "combat.raider";
    }
}
