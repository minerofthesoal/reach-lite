package net.blazeindustries.actest.module;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/**
 * Simulates the classic "NoSlowdown" cheat: ignores the friction Minecraft
 * applies while standing in cobweb or soul sand. This is one of the oldest
 * and most commonly checked movement anomalies in Minecraft anticheats
 * (a player moving at normal speed through a block that should slow them
 * to a crawl is an easy, high-confidence signal).
 */
public class NoSlowdownModule extends TestModule {

	public double restoreFactor = 4.0;

	public NoSlowdownModule() {
		super("NoSlowdown", GLFW.GLFW_KEY_KP_3);
	}

	@Override
	protected void onTick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		if (player == null || player.getWorld() == null) return;

		BlockPos feet = player.getBlockPos();
		var block = player.getWorld().getBlockState(feet).getBlock();
		boolean slowingBlock = block == Blocks.COBWEB || block == Blocks.SOUL_SAND;

		if (slowingBlock) {
			Vec3d vel = player.getVelocity();
			player.setVelocity(vel.x * restoreFactor, vel.y, vel.z * restoreFactor);
		}
	}
}
