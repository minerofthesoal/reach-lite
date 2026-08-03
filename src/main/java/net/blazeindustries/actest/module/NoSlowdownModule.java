package net.blazeindustries.actest.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class NoSlowdownModule extends TestModule {

	public double restoreFactor = 4.0;

	public NoSlowdownModule() {
		super("NoSlowdown", GLFW.GLFW_KEY_KP_3);
	}

	@Override
	protected void onTick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null || client.level == null) return;

		BlockPos feet = player.blockPosition();
		var block = client.level.getBlockState(feet).getBlock();
		boolean slowingBlock = block == Blocks.COBWEB || block == Blocks.SOUL_SAND;

		if (slowingBlock) {
			Vec3 vel = player.getDeltaMovement();
			player.setDeltaMovement(vel.x * restoreFactor, vel.y, vel.z * restoreFactor);
		}
	}
}
