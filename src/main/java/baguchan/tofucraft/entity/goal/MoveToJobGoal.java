package baguchan.tofucraft.entity.goal;

import baguchan.tofucraft.entity.Tofunian;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class MoveToJobGoal extends MoveToBlockGoal {
	private final Tofunian creature;

	private boolean restockComplete;

	public MoveToJobGoal(Tofunian creature, double speedIn, int length) {
		super(creature, speedIn, length);
		this.creature = creature;
	}

	public boolean canUse() {
		return (this.creature.level().isDay() && this.creature.getRole() != Tofunian.Roles.TOFUNIAN && this.creature.getTofunainJobBlock() != null && this.creature.level().dayTime() > 2000 && this.creature.level().dayTime() < 9000 && !this.creature.isBaby() && super.canUse());
	}

	public boolean canContinueToUse() {
		return (super.canContinueToUse() && this.creature.level().isDay() && this.creature.getTofunainJobBlock() != null && this.creature.getRole() != Tofunian.Roles.TOFUNIAN);
	}

	@Override
	protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
		BlockState blockstate = worldIn.getBlockState(pos);
		return Tofunian.Roles.getJobBlock(this.creature.getRole().getPoiType()).contains(blockstate);
	}


	protected boolean findNearestBlock() {
		if (this.creature.getTofunainJobBlock() != null &&
				isValidTarget(this.creature.level(), this.creature.getTofunainJobBlock())) {
			this.blockPos = this.creature.getTofunainJobBlock();
			return true;
		}
		return false;
	}

	@Override
	public void stop() {
		super.stop();
		restockComplete = false;
	}

	public double acceptedDistance() {
		return 2.0D;
	}
}