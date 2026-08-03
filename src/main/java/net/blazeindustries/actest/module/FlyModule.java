package net.blazeindustries.actest.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.lwjgl.glfw.GLFW;

/**
 * Simulates a "fly hack" the way a real one works: it does NOT touch the
 * vanilla creative-mode abilities flag (a naive anticheat check that just
 * reads Abilities.flying would trivially catch that and isn't testing much).
 * Instead it overrides vertical velocity directly every tick, same as
 * survival-mode fly cheats do, so the server sees normal-looking movement
 * packets with impossible sustained upward velocity / zero gravity -- the
 * pattern a real vertical-motion anticheat check needs to catch.
 */
public class FlyModule extends TestModule {

	public double flySpeed = 0.6; // blocks/tick

	public FlyModule() {
		super("Fly", GLFW.GLFW_KEY_KP_1);
	}

	@Override
	protected void onTick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		if (player == null) return;

		double vertical = 0.0;
		if (client.options.jumpKey.isPressed()) {
			vertical = flySpeed;
		} else if (client.options.sneakKey.isPressed()) {
			vertical = -flySpeed;
		}

		var vel = player.getVelocity();
		player.setVelocity(vel.x, vertical, vel.z);
		// Prevent vanilla from re-applying gravity on top of our override this tick.
		player.setOnGround(false);
	}

	@Override
	protected void onDisable(MinecraftClient client) {
		if (client.player != null) {
			var vel = client.player.getVelocity();
			client.player.setVelocity(vel.x, 0, vel.z);
		}
	}
}
