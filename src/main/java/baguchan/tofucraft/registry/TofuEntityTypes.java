package baguchan.tofucraft.registry;

import baguchan.tofucraft.TofuCraftReload;
import baguchan.tofucraft.entity.*;
import baguchan.tofucraft.entity.projectile.FukumameEntity;
import baguchan.tofucraft.entity.projectile.NetherFukumameEntity;
import baguchan.tofucraft.entity.projectile.SoulFukumameEntity;
import baguchan.tofucraft.entity.projectile.ZundaArrow;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = TofuCraftReload.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TofuEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, TofuCraftReload.MODID);

	public static final RegistryObject<EntityType<Tofunian>> TOFUNIAN = ENTITIES.register("tofunian", () -> EntityType.Builder.of(Tofunian::new, MobCategory.CREATURE)
			.sized(0.6F, 1.2F).build("tofucraft:tofunian"));

	/*public static final EntityType<TravelerTofunianEntity> TRAVELER_TOFUNIAN = EntityType.Builder.of(TravelerTofunianEntity::new, MobCategory.CREATURE)
			.sized(0.6F, 1.2F).build("tofucraft:traveler_tofunian");*/

	public static final RegistryObject<EntityType<TofuCow>> TOFUCOW = ENTITIES.register("tofucow", () -> EntityType.Builder.of(TofuCow::new, MobCategory.CREATURE)
			.sized(0.9F, 1.4F).build("tofucraft:tofucow"));

	public static final RegistryObject<EntityType<TofuFish>> TOFUFISH = ENTITIES.register("tofufish", () -> EntityType.Builder.of(TofuFish::new, MobCategory.WATER_AMBIENT)
			.sized(0.5F, 0.3F).setTrackingRange(4).build("tofucraft:tofufish"));

	public static final RegistryObject<EntityType<TofuSlime>> TOFUSLIME = ENTITIES.register("tofuslime", () -> EntityType.Builder.of(TofuSlime::new, MobCategory.MONSTER)
			.sized(2.04F, 2.04F).build("tofucraft:tofuslime"));

    public static final RegistryObject<EntityType<TofuSpider>> TOFUSPIDER = ENTITIES.register("tofuspider", () -> EntityType.Builder.of(TofuSpider::new, MobCategory.MONSTER)
            .sized(0.95F, 0.55F).build("tofucraft:tofuspider"));

    public static final RegistryObject<EntityType<FukumameEntity>> FUKUMAME = ENTITIES.register("fukumame", () -> EntityType.Builder.<FukumameEntity>of(FukumameEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).updateInterval(20).build("tofucraft:fukumame"));

    public static final RegistryObject<EntityType<NetherFukumameEntity>> NETHER_FUKUMAME = ENTITIES.register("nether_fukumame", () -> EntityType.Builder.<NetherFukumameEntity>of(NetherFukumameEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).updateInterval(20).build("tofucraft:nether_fukumame"));

    public static final RegistryObject<EntityType<SoulFukumameEntity>> SOUL_FUKUMAME = ENTITIES.register("soul_fukumame", () -> EntityType.Builder.<SoulFukumameEntity>of(SoulFukumameEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).updateInterval(20).build("tofucraft:soul_fukumame"));

    public static final RegistryObject<EntityType<ZundaArrow>> ZUNDA_ARROW = ENTITIES.register("zunda_arrow", () -> EntityType.Builder.<ZundaArrow>of(ZundaArrow::new, MobCategory.MISC)
            .sized(0.5F, 0.5F).updateInterval(20).build("tofucraft:zunda_arrow"));


	/*public static final EntityType<ZundaArrowEntity> ZUNDA_ARROW = EntityType.Builder.<ZundaArrowEntity>of(ZundaArrowEntity::new, MobCategory.MISC)
			.sized(0.5F, 0.5F).build("tofucraft:zunda_arrow");*/

    @SubscribeEvent
    public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
        event.put(TOFUCOW.get(), TofuCow.createAttributes().build());
        event.put(TOFUNIAN.get(), Tofunian.createAttributes().build());
        event.put(TOFUFISH.get(), AbstractFish.createAttributes().build());
		event.put(TOFUSLIME.get(), Monster.createMonsterAttributes().build());
		event.put(TOFUSPIDER.get(), TofuSpider.createAttributes().build());

		SpawnPlacements.register(TOFUCOW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TofuCow::checkTofuAnimalSpawnRules);
		SpawnPlacements.register(TOFUNIAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
		SpawnPlacements.register(TOFUSLIME.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TofuSlime::checkMonsterSpawnRules);
		SpawnPlacements.register(TOFUSPIDER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TofuSpider::checkMonsterSpawnRules);
		SpawnPlacements.register(TOFUFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TofuFish::checkTofuFishSpawnRules);

	}
}
