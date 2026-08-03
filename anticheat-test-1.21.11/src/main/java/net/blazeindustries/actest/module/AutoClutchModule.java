package net.blazeindustries.actest.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.hit.BlockHitResult;
import net.minecraft.hit.HitResult;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

/**
 * Simulates an "auto bucket clutch" macro: watches fall speed, and once the
 * player is falling fast enough to take fall damage (or die to void/lava
 * below), automatically switches to a water bucket, places water straight
 * down, and -- once vanilla's own water-landing logic clears fall damage --
 * scoops the water back up so play can continue.
 *
 * This is one of the harder movement patterns for an anticheat to catch
 * correctly, because a legitimate player CAN do this manually; a good
 * anticheat needs to flag it on *timing* (sub-human reaction placement),
 * repetition, or angle snapping rather than the act of clutching itself.
 * Good test target for exactly that kind of check.
 */
public class AutoClutchModule extends TestModule {

	/** Downward velocity (blocks/tick, negative) that triggers a clutch attempt. */
	public double fallVelocityThreshold = -0.9;
	/** How far below the player to search for a surface to place water on. */
	public double downReach = 4.0;
	/** Ticks to wait after placing water before attempting to scoop it back up. */
	public int pickupDelayTicks = 6;
	/** If false, water is left behind instead of being picked back up. */
	public boolean autoPickup = true;

	private int cooldown = 0;
	private int pickupTimer = -1;

	public AutoClutchModule() {
		super("AutoClutch", GLFW.GLFW_KEY_KP_4);
	}

	@Override
	protected void onTick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		if (player == null || client.world == null || client.interactionManager == null) return;

		if (cooldown > 0) cooldown--;

		if (pickupTimer >= 0) {
			pickupTimer--;
			if (pickupTimer == 0 && autoPickup) {
				scoopWater(client, player);
				pickupTimer = -1;
			}
			return;
		}

		boolean fallingFast = !player.isOnGround() && player.getVelocity().y <= fallVelocityThreshold;
		if (fallingFast && cooldown == 0) {
			int waterSlot = findHotbarSlot(player, Items.WATER_BUCKET);
			if (waterSlot != -1) {
				placeWaterBelow(client, player, waterSlot);
				cooldown = pickupDelayTicks + 10;
				pickupTimer = pickupDelayTicks;
			}
		}
	}

	private void placeWaterBelow(MinecraftClient client, ClientPlayerEntity player, int waterSlot) {
		int prevSlot = player.getInventory().selectedSlot;
		player.getInventory().selectedSlot = waterSlot;

		Vec3d start = player.getPos();
		Vec3d end = start.add(0, -downReach, 0);
		BlockHitResult hit = client.world.raycast(new RaycastContext(
				start, end,
				RaycastContext.ShapeType.OUTLINE,
				RaycastContext.FluidHandling.NONE,
				player
		));

		if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
			client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
			player.swingHand(Hand.MAIN_HAND);
		}

		player.getInventory().selectedSlot = prevSlot;
	}

	private void scoopWater(MinecraftClient client, ClientPlayerEntity player) {
		// After placing water the stack in that slot auto-converts to an empty
		// bucket (vanilla bucket behaviour), so right-clicking the water again
		// refills it -- this is the "pickup" half of the clutch.
		int emptyBucketSlot = findHotbarSlot(player, Items.BUCKET);
		if (emptyBucketSlot == -1) return;

		int prevSlot = player.getInventory().selectedSlot;
		player.getInventory().selectedSlot = emptyBucketSlot;

		Vec3d start = player.getPos();
		Vec3d end = start.add(0, -downReach, 0);
		BlockHitResult hit = client.world.raycast(new RaycastContext(
				start, end,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.SOURCE_ONLY,
				player
		));

		if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
			client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
			player.swingHand(Hand.MAIN_HAND);
		}

		player.getInventory().selectedSlot = prevSlot;
	}

	private int findHotbarSlot(ClientPlayerEntity player, net.minecraft.item.Item item) {
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getStack(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}
}
