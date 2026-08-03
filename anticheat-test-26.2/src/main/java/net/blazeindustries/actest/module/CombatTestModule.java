package net.blazeindustries.actest.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.lwjgl.glfw.GLFW;

/**
 * VERIFY: interactionManager -> gameMode; the attack method is likely
 * gameMode.attack(Player, Entity) in Mojang mappings but double check --
 * this is one of the less-documented method names.
 */
public class CombatTestModule extends TestModule {

	public boolean extendedReach = true;
	public boolean autoAttack = true;
	public double reachDistance = 6.0;
	public int attackCooldownTicks = 4;

	private int cooldown = 0;

	public CombatTestModule() {
		super("Combat", GLFW.GLFW_KEY_KP_5);
	}

	@Override
	protected void onTick(Minecraft client) {
		if (!autoAttack) return;
		LocalPlayer player = client.player;
		if (player == null || client.level == null || client.gameMode == null) return;

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		double range = extendedReach ? reachDistance : 3.0;
		AABB searchBox = player.getBoundingBox().inflate(range);

		LivingEntity target = null;
		double closest = Double.MAX_VALUE;
		for (Entity e : client.level.getEntities(player, searchBox)) {
			if (!(e instanceof LivingEntity living) || !living.isAlive() || living == player) continue;
			double dist = player.distanceToSqr(living);
			if (dist < range * range && dist < closest) {
				closest = dist;
				target = living;
			}
		}

		if (target != null) {
			client.gameMode.attack(player, target);
			player.swing(InteractionHand.MAIN_HAND);
			cooldown = attackCooldownTicks;
		}
	}
}
