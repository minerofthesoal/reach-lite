package net.blazeindustries.actest.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class SpeedModule extends TestModule {

	public double multiplier = 2.5;

	public SpeedModule() {
		super("Speed", GLFW.GLFW_KEY_KP_2);
	}

	@Override
	protected void onTick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null) return;

		Vec3 vel = player.getDeltaMovement();
		double horizSq = vel.x * vel.x + vel.z * vel.z;
		if (horizSq > 0.0004 && player.onGround()) {
			player.setDeltaMovement(vel.x * multiplier, vel.y, vel.z * multiplier);
		}
	}
}
