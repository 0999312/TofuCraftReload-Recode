package baguchan.tofucraft.item;

import baguchan.tofucraft.client.render.item.TofuShieldBWLR;
import baguchan.tofucraft.registry.TofuItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class TofuShieldItem extends ShieldItem {
	public static final int EFFECTIVE_BLOCK_DELAY = 5;
	public static final float MINIMUM_DURABILITY_DAMAGE = 3.0F;
	public static final String TAG_BASE_COLOR = "Base";

	public TofuShieldItem(Properties p_43089_) {
		super(p_43089_);
		DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
	}

	public boolean isValidRepairItem(ItemStack p_43091_, ItemStack p_43092_) {
		return p_43092_.is(TofuItems.TOFUMETAL.get()) || !p_43092_.is(ItemTags.PLANKS) && super.isValidRepairItem(p_43091_, p_43092_);
	}

	public void appendHoverText(ItemStack p_43094_, @Nullable Level p_43095_, List<Component> p_43096_, TooltipFlag p_43097_) {
	}


	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return new TofuShieldBWLR();
			}
		});
	}

}