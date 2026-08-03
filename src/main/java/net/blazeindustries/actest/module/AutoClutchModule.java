package net.blazeindustries.actest.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

/**
 * See the 1.21.11 AutoClutchModule for full documentation.
 * Mojmap equivalents: RaycastContext -> ClipContext, ShapeType -> ClipContext.Block,
 * FluidHandling -> ClipContext.Fluid, Hand -> InteractionHand,
 * interactionManager -> gameMode, interactBlock -> useItemOn.
 */
// VERIFY: gameMode.useItemOn()'s exact parameter list has shifted across
// versions (some added a Level parameter). Let your IDE's autocomplete
// correct the call sites below if this doesn't compile as written.
public class AutoClutchModule extends TestModule {

	public double fallVelocityThreshold = -0.9;
	public double downReach = 4.0;
	public int pickupDelayTicks = 6;
	public boolean autoPickup = true;

	private int cooldown = 0;
	private int pickupTimer = -1;

	public AutoClutchModule() {
		super("AutoClutch", GLFW.GLFW_KEY_KP_4);
	}

	@Override
	protected void onTick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null || client.level == null || client.gameMode == null) return;

		if (cooldown > 0) cooldown--;

		if (pickupTimer >= 0) {
			pickupTimer--;
			if (pickupTimer == 0 && autoPickup) {
				scoopWater(client, player);
				pickupTimer = -1;
			}
			return;
		}

		boolean fallingFast = !player.onGround() && player.getDeltaMovement().y <= fallVelocityThreshold;
		if (fallingFast && cooldown == 0) {
			int waterSlot = findHotbarSlot(player, Items.WATER_BUCKET);
			if (waterSlot != -1) {
				placeWaterBelow(client, player, waterSlot);
				cooldown = pickupDelayTicks + 10;
				pickupTimer = pickupDelayTicks;
			}
		}
	}

	private void placeWaterBelow(Minecraft client, LocalPlayer player, int waterSlot) {
		int prevSlot = player.getInventory().selected;
		player.getInventory().selected = waterSlot;

		Vec3 start = player.position();
		Vec3 end = start.add(0, -downReach, 0);
		BlockHitResult hit = client.level.clip(new ClipContext(
				start, end,
				ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE,
				player
		));

		if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
			client.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
			player.swing(InteractionHand.MAIN_HAND);
		}

		player.getInventory().selected = prevSlot;
	}

	private void scoopWater(Minecraft client, LocalPlayer player) {
		int emptyBucketSlot = findHotbarSlot(player, Items.BUCKET);
		if (emptyBucketSlot == -1) return;

		int prevSlot = player.getInventory().selected;
		player.getInventory().selected = emptyBucketSlot;

		Vec3 start = player.position();
		Vec3 end = start.add(0, -downReach, 0);
		BlockHitResult hit = client.level.clip(new ClipContext(
				start, end,
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.SOURCE_ONLY,
				player
		));

		if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
			client.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
			player.swing(InteractionHand.MAIN_HAND);
		}

		player.getInventory().selected = prevSlot;
	}

	private int findHotbarSlot(LocalPlayer player, Item item) {
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getItem(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}
}
