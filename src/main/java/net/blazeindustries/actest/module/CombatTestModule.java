package net.blazeindustries.actest.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

/**
 * Bonus module covering two more of the classic anticheat checks:
 *  - Reach: attacks entities beyond vanilla's ~3 block interaction range,
 *    bypassing the client's own crosshair-distance limit by calling the
 *    interaction manager directly on any nearby target.
 *  - Killaura: auto-attacks the nearest living entity every tick without
 *    the player aiming at it at all -- tests angle/rotation-snap checks.
 *
 * Both are gated behind separate flags so you can test them independently.
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
	protected void onTick(MinecraftClient client) {
		if (!autoAttack) return;
		ClientPlayerEntity player = client.player;
		if (player == null || client.world == null || client.interactionManager == null) return;

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		double range = extendedReach ? reachDistance : 3.0;
		Box searchBox = player.getBoundingBox().expand(range);

		LivingEntity target = null;
		double closest = Double.MAX_VALUE;
		for (Entity e : client.world.getOtherEntities(player, searchBox)) {
			if (!(e instanceof LivingEntity living) || !living.isAlive() || living == player) continue;
			double dist = player.squaredDistanceTo(living);
			if (dist < range * range && dist < closest) {
				closest = dist;
				target = living;
			}
		}

		if (target != null) {
			// Bypasses the normal crosshair-target distance check that
			// MinecraftClient.doAttack() would otherwise enforce -- that's
			// the point: this is what a reach hack's attack call looks like.
			client.interactionManager.attackEntity(player, target);
			player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
			cooldown = attackCooldownTicks;
		}
	}
}
