package net.blazeindustries.actest.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/**
 * Simulates a speedhack by scaling horizontal velocity every tick, on top of
 * whatever vanilla's own input handling already produced. This is the
 * simplest and most common real-world implementation of a speed cheat, and
 * produces the kind of "moved further than max sprint speed allows" packet
 * sequence your anticheat's horizontal-speed check needs to flag.
 */
public class SpeedModule extends TestModule {

	public double multiplier = 2.5;

	public SpeedModule() {
		super("Speed", GLFW.GLFW_KEY_KP_2);
	}

	@Override
	protected void onTick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		if (player == null) return;

		Vec3d vel = player.getVelocity();
		double horizSq = vel.x * vel.x + vel.z * vel.z;
		// Only scale while the player is actually trying to move, so we don't
		// amplify tiny residual drift/knockback into a launch.
		if (horizSq > 0.0004 && player.isOnGround()) {
			player.setVelocity(vel.x * multiplier, vel.y, vel.z * multiplier);
		}
	}
}
