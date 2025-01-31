package baguchan.tofucraft.blockentity.tfenergy;

import baguchan.tofucraft.blockentity.tfenergy.base.WorkerBaseBlockEntity;
import baguchan.tofucraft.inventory.TFAggregatorMenu;
import baguchan.tofucraft.recipe.AggregatorRecipe;
import baguchan.tofucraft.registry.TofuBlockEntitys;
import baguchan.tofucraft.registry.TofuRecipes;
import cn.mcmod_mmf.mmlib.utils.LevelUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("LossyEncoding")
public class TFAggregatorBlockEntity extends WorkerBaseBlockEntity implements MenuProvider {

    private final ItemStackHandler inventory;
    private LazyOptional<IItemHandler> inputHandler;
    private LazyOptional<IItemHandler> outputHandler;
    private final Object2IntOpenHashMap<ResourceLocation> experienceTracker;
    private int recipeTime;
    private int recipeTimeTotal;
    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int p_221476_1_) {
            switch (p_221476_1_) {
                case 0:
                    return TFAggregatorBlockEntity.this.recipeTime;
                case 1:
                    return TFAggregatorBlockEntity.this.recipeTimeTotal;
                case 2:
                    return TFAggregatorBlockEntity.this.energy;
                case 3:
                    return TFAggregatorBlockEntity.this.energyMax;
            }
            return 0;
        }

        @Override
        public void set(int p_221477_1_, int p_221477_2_) {
            switch (p_221477_1_) {
                case 0:
                    TFAggregatorBlockEntity.this.recipeTime = p_221477_2_;
                    break;
                case 1:
                    TFAggregatorBlockEntity.this.recipeTimeTotal = p_221477_2_;
                    break;
                case 2:
                    TFAggregatorBlockEntity.this.energy = p_221477_2_;
                    break;
                case 3:
                    TFAggregatorBlockEntity.this.energyMax = p_221477_2_;
                    break;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public TFAggregatorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(TofuBlockEntitys.TF_AGGREGATOR.get(), p_155229_, p_155230_, 50000);

        this.inventory = createHandler();
        this.inputHandler = LazyOptional.of(() -> new TFOvenItemHandler(inventory, Direction.UP));
        this.outputHandler = LazyOptional.of(() -> new TFOvenItemHandler(inventory, Direction.DOWN));
        this.experienceTracker = new Object2IntOpenHashMap<>();
    }

    public static void workingTick(Level level, BlockPos pos, BlockState state, TFAggregatorBlockEntity blockEntity) {
        boolean didInventoryChange = false;

        if (blockEntity.hasInput()) {
            Optional<AggregatorRecipe> recipe = blockEntity
                    .getMatchingRecipe(new RecipeWrapper(blockEntity.inventory));
            if (recipe.isPresent() && blockEntity.canWork(recipe.get())) {
                didInventoryChange = blockEntity.processRecipe(recipe.get());
            } else {
                blockEntity.recipeTime = 0;
            }
        } else if (blockEntity.recipeTime > 0) {
            blockEntity.recipeTime = 0;
        }

        if (didInventoryChange) {
            blockEntity.inventoryChanged();
        }
    }

    private boolean hasInput() {
        if (!inventory.getStackInSlot(0).isEmpty()) {
            return true;
        }
        return false;
    }

    private Optional<AggregatorRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (level == null) {
            return Optional.empty();
        }

        if (lastRecipeID != null) {
            Recipe<RecipeWrapper> recipe = level.getRecipeManager().getAllRecipesFor(TofuRecipes.RECIPETYPE_AGGREGATOR).stream()
                    .filter(now -> now.getId().equals(lastRecipeID)).findFirst().get();
            if (recipe instanceof AggregatorRecipe) {
                if (recipe.matches(inventoryWrapper, level)) {
                    return Optional.of((AggregatorRecipe) recipe);
                }
            }
        }

        if (checkNewRecipe) {
            Optional<AggregatorRecipe> recipe = level.getRecipeManager().getRecipeFor(TofuRecipes.RECIPETYPE_AGGREGATOR,
                    inventoryWrapper, level);
            if (recipe.isPresent()) {
                lastRecipeID = recipe.get().getId();
                return recipe;
            }
        }

        checkNewRecipe = false;
        return Optional.empty();
    }

    protected boolean canWork(AggregatorRecipe recipe) {
        if(this.isEnergyEmpty())
            return false;
        if (hasInput()) {
            ItemStack resultStack = recipe.getResultItem();
            if (resultStack.isEmpty()) {
                return false;
            } else {
                ItemStack outputStack = inventory.getStackInSlot(1);
                if (outputStack.isEmpty()) {
                    return true;
                } else if (!outputStack.sameItem(resultStack)) {
                    return false;
                } else if (outputStack.getCount() + resultStack.getCount() <= inventory.getSlotLimit(1)) {
                    return true;
                } else {
                    return outputStack.getCount() + resultStack.getCount() <= resultStack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }

    private boolean processRecipe(AggregatorRecipe recipe) {
        if (level == null) {
            return false;
        }

        ++recipeTime;
        this.drain(10, false);
        recipeTimeTotal = recipe.getRecipeTime();
        if (recipeTime < recipeTimeTotal) {
            return false;
        }

        recipeTime = 0;

        ItemStack resultStack = recipe.getResultItem();
        ItemStack outStack = inventory.getStackInSlot(1);
        if (outStack.isEmpty()) {
            inventory.setStackInSlot(1, resultStack.copy());
        } else if (outStack.sameItem(resultStack)) {
            outStack.grow(resultStack.getCount());
        }
        trackRecipeExperience(recipe);

        ItemStack slotStack = inventory.getStackInSlot(0);
        if (slotStack.hasContainerItem()) {
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 0.7;
            double z = worldPosition.getZ() + 0.5;
            LevelUtils.spawnItemEntity(level, inventory.getStackInSlot(0).getContainerItem(), x, y, z, 0F, 0.25F, 0F);
        }
        if (!slotStack.isEmpty()) {
            slotStack.shrink(1);
        }

        return true;
    }

    // Basic methods

    public void trackRecipeExperience(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeID = recipe.getId();
            experienceTracker.addTo(recipeID, 1);
        }
    }

    public void clearUsedRecipes(Player player) {
        grantStoredRecipeExperience(player.level, player.position());
        experienceTracker.clear();
    }

    public void grantStoredRecipeExperience(Level world, Vec3 pos) {
        for (Object2IntMap.Entry<ResourceLocation> entry : experienceTracker.object2IntEntrySet()) {
            world.getRecipeManager().byKey(entry.getKey()).ifPresent(recipe -> LevelUtils.splitAndSpawnExperience(world,
                    pos, entry.getIntValue(), ((AggregatorRecipe) recipe).getExperience()));
        }
    }


    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        recipeTime = compound.getInt("RecipeTime");
        recipeTimeTotal = compound.getInt("RecipeTimeTotal");
        CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");
        for (String key : compoundRecipes.getAllKeys()) {
            experienceTracker.put(new ResourceLocation(key), compoundRecipes.getInt(key));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("RecipeTime", recipeTime);
        compound.putInt("RecipeTimeTotal", recipeTimeTotal);
        compound.put("Inventory", inventory.serializeNBT());
        CompoundTag compoundRecipes = new CompoundTag();
        experienceTracker
                .forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        compound.put("RecipesUsed", compoundRecipes);
    }

    private CompoundTag writeItems(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Inventory", inventory.serializeNBT());
        return compound;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            if (side == null || side.equals(Direction.UP)) {
                return inputHandler.cast();
            } else {
                return outputHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < 2; ++i) {
            drops.add(inventory.getStackInSlot(i));
        }
        return drops;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return writeItems(new CompoundTag());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                if (slot == 0) {
                    checkNewRecipe = true;
                }
                inventoryChanged();
            }
        };
    }

    public static class TFOvenItemHandler implements IItemHandler {
        private static final int SLOT_INPUT = 0;
        private static final int SLOT_OUTPUT = 1;
        private final IItemHandler itemHandler;
        private final Direction side;

        public TFOvenItemHandler(IItemHandler itemHandler, @Nullable Direction side) {
            this.itemHandler = itemHandler;
            this.side = side;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return itemHandler.isItemValid(slot, stack);
        }

        @Override
        public int getSlots() {
            return itemHandler.getSlots();
        }

        @Override
        @Nonnull
        public ItemStack getStackInSlot(int slot) {
            return itemHandler.getStackInSlot(slot);
        }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (side == null || side.equals(Direction.UP)) {
                return slot < SLOT_INPUT ? itemHandler.insertItem(slot, stack, simulate) : stack;
            } else {
                return stack;
            }
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (side == null || side.equals(Direction.UP)) {
                return slot == SLOT_INPUT ? itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
            } else {
                return slot == SLOT_OUTPUT ? itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return itemHandler.getSlotLimit(slot);
        }
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.tofucraft.tf_aggregator");
    }

    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new TFAggregatorMenu(p_39954_, p_39955_, this, this.dataAccess);
    }

}
