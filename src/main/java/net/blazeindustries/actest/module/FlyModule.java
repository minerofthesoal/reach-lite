package net.blazeindustries.actest.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

/**
 * See the 1.21.11 FlyModule for full documentation. Mojmap equivalents used
 * here: ClientPlayerEntity -> LocalPlayer, getVelocity/setVelocity ->
 * getDeltaMovement/setDeltaMovement, isOnGround/setOnGround likely unchanged.
 */
public class FlyModule extends TestModule {

	public double flySpeed = 0.6;

	public FlyModule() {
		super("Fly", GLFW.GLFW_KEY_KP_1);
	}

	@Override
	protected void onTick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null) return;

		double vertical = 0.0;
		// VERIFY: Options field names -- Mojmap historically prefixes these
		// "key" (keyJump, keyShift). If these don't resolve, check
		// Minecraft.getInstance().options in your IDE.
		if (client.options.keyJump.isDown()) {
			vertical = flySpeed;
		} else if (client.options.keyShift.isDown()) {
			vertical = -flySpeed;
		}

		var vel = player.getDeltaMovement();
		player.setDeltaMovement(vel.x, vertical, vel.z);
		player.setOnGround(false);
	}

	@Override
	protected void onDisable(Minecraft client) {
		if (client.player != null) {
			var vel = client.player.getDeltaMovement();
			client.player.setDeltaMovement(vel.x, 0, vel.z);
		}
	}
}
