package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.item.IExtendedReachWeapon;

public class NpcAIAttackMeleeLongRange extends NpcAIAttack<NpcBase> {
	private float attackReach = 2.5F;

	public NpcAIAttackMeleeLongRange(NpcBase npc) {
		super(npc);
		setMutexBits(ATTACK + MOVE);
	}

	public void setAttackReachFromWeapon(ItemStack weapon) {
		float reach = 2.5F;
		if (weapon.getItem() instanceof IExtendedReachWeapon) {
			reach = ((IExtendedReachWeapon) weapon.getItem()).getReach() - 1F;
		}
		attackReach = reach;
	}

	@Override
	protected boolean shouldCloseOnTarget(double distanceToEntity) {
		double attackDistance = (npc.width / 2D) + (getTarget().width / 2D) + attackReach;
		return (distanceToEntity > (attackDistance * attackDistance)) || !npc.getEntitySenses().canSee(getTarget());
	}

	@Override
	protected void doAttack(double distanceToEntity) {
		npc.removeAITask(TASK_MOVE);
		if (getAttackDelay() <= 0) {
			npc.swingArm(EnumHand.MAIN_HAND);
			npc.attackEntityAsMob(getTarget());
			setAttackDelay(getCoolDown());
			npc.addExperience(AWNPCStatics.npcXpFromAttack);
			if ((Math.random() < 0.2 && npc instanceof NpcFaction)) {
				((NpcFaction) npc).playAttackSound();
			}
		}
	}

	private int getCoolDown() {
		int entityAttackCooldown = (int) (1.0D / npc.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0D);

		return entityAttackCooldown > 0 ? entityAttackCooldown : 20;
	}
}
