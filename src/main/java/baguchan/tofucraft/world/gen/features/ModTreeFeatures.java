package baguchan.tofucraft.world.gen.features;

import baguchan.tofucraft.registry.TofuBlocks;
import baguchan.tofucraft.world.gen.foliage.TofuFoliagePlacer;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class ModTreeFeatures {

    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> TOFU_TREE = FeatureUtils.register("tofucraft:tofu_tree", Feature.TREE, createTofuTree().build());
    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> TOFU_TREE_BIG = FeatureUtils.register("tofucraft:tofu_tree_big", Feature.TREE, createTofuTreeBig().build());

    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> APRICOT_TREE = FeatureUtils.register("tofucraft:apricot_tree", Feature.TREE, createApricotTree().build());

    private static TreeConfiguration.TreeConfigurationBuilder createTofuTree() {
        return createStraightBlobTree(TofuBlocks.ISHITOFU.get(), TofuBlocks.LEAVES_TOFU.get(), 4, 2, 2).ignoreVines().dirt(BlockStateProvider.simple(TofuBlocks.TOFU_TERRAIN.get()));
    }

    private static TreeConfiguration.TreeConfigurationBuilder createTofuTreeBig() {
        return createStraightBlobTree(TofuBlocks.ISHITOFU.get(), TofuBlocks.LEAVES_TOFU.get(), 5, 3, 3).ignoreVines().dirt(BlockStateProvider.simple(TofuBlocks.TOFU_TERRAIN.get()));
    }

    private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block trunk, Block leaves, int trunkSize, int foliageSize, int twoLayerSize) {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(trunk), new StraightTrunkPlacer(trunkSize, 2, 0), BlockStateProvider.simple(leaves), new TofuFoliagePlacer(ConstantInt.of(foliageSize), ConstantInt.of(0), foliageSize + 1), new TwoLayersFeatureSize(twoLayerSize, twoLayerSize, twoLayerSize));
    }

    private static TreeConfiguration.TreeConfigurationBuilder createApricotTree() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new StraightTrunkPlacer(4, 2, 1),
                BlockStateProvider.simple(TofuBlocks.LEAVES_APRICOT.get()),
                new TofuFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(2, 1, 3)
        );

    }

    public static void init() {

    }
}
