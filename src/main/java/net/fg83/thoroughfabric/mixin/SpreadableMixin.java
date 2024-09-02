package net.fg83.thoroughfabric.mixin;

import net.fg83.thoroughfabric.ConfigManager;
import net.fg83.thoroughfabric.StepCountData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpreadableBlock.class)
public class SpreadableMixin {
	@Inject(method = "canSpread", at = @At("RETURN"), cancellable = true)
	private static void cancelSpread(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if(cir.getReturnValueZ()) {
			// Preconditions of vanilla calls
			assert world.getBlockState(pos).isOf(Blocks.DIRT);
			assert world instanceof ServerWorld;

			StepCountData scd = StepCountData.get((ServerWorld) world);
			int sc = scd.getStepCount(pos);
			int nsc = sc + ConfigManager.getConfig().spreadReps;

			scd.setStepCount(pos, Math.max(nsc, 0));

			if(nsc > 0) {
				cir.setReturnValue(false /* can't spread */);
			}
		}
	}
}
