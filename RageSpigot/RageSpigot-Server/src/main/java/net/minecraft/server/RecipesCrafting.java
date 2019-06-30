package net.minecraft.server;

/**
 * @since 11/12/2017
 */
import net.minecraft.server.BlockDirt.EnumDirtVariant;
import net.minecraft.server.BlockDoubleStepAbstract.EnumStoneSlabVariant;
import net.minecraft.server.BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant;
import net.minecraft.server.BlockQuartz.EnumQuartzVariant;
import net.minecraft.server.BlockRedSandstone.EnumRedSandstoneVariant;
import net.minecraft.server.BlockSand.EnumSandVariant;
import net.minecraft.server.BlockSandStone.EnumSandstoneVariant;
import net.minecraft.server.BlockStone.EnumStoneVariant;

public class RecipesCrafting {
	public RecipesCrafting() {
	}

	public void a(CraftingManager var1) {
		var1.registerShapedRecipe(new ItemStack(Blocks.CHEST), new Object[]{"###", "# #", "###", '#', Blocks.PLANKS});
		var1.registerShapedRecipe(new ItemStack(Blocks.TRAPPED_CHEST), new Object[]{"#-", '#', Blocks.CHEST, '-', Blocks.TRIPWIRE_HOOK});
		var1.registerShapedRecipe(new ItemStack(Blocks.ENDER_CHEST), new Object[]{"###", "#E#", "###", '#', Blocks.OBSIDIAN, 'E', Items.ENDER_EYE});
		var1.registerShapedRecipe(new ItemStack(Blocks.FURNACE), new Object[]{"###", "# #", "###", '#', Blocks.COBBLESTONE});
		var1.registerShapedRecipe(new ItemStack(Blocks.CRAFTING_TABLE), new Object[]{"##", "##", '#', Blocks.PLANKS});
		var1.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE), new Object[]{"##", "##", '#', new ItemStack(Blocks.SAND, 1, EnumSandVariant.SAND.a())});
		var1.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE), new Object[]{"##", "##", '#', new ItemStack(Blocks.SAND, 1, EnumSandVariant.RED_SAND.a())});
		var1.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE, 4, EnumSandstoneVariant.SMOOTH.a()), new Object[]{"##", "##", '#', new ItemStack(Blocks.SANDSTONE, 1, EnumSandstoneVariant.DEFAULT.a())});
		var1.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE, 4, EnumRedSandstoneVariant.SMOOTH.a()), new Object[]{"##", "##", '#', new ItemStack(Blocks.RED_SANDSTONE, 1, EnumRedSandstoneVariant.DEFAULT.a())});
		var1.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE, 1, EnumSandstoneVariant.CHISELED.a()), new Object[]{"#", "#", '#', new ItemStack(Blocks.STONE_SLAB, 1, EnumStoneSlabVariant.SAND.a())});
		var1.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE, 1, EnumRedSandstoneVariant.CHISELED.a()), new Object[]{"#", "#", '#', new ItemStack(Blocks.STONE_SLAB2, 1, EnumStoneSlab2Variant.RED_SANDSTONE.a())});
		var1.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 1, EnumQuartzVariant.CHISELED.a()), new Object[]{"#", "#", '#', new ItemStack(Blocks.STONE_SLAB, 1, EnumStoneSlabVariant.QUARTZ.a())});
		var1.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 2, EnumQuartzVariant.LINES_Y.a()), new Object[]{"#", "#", '#', new ItemStack(Blocks.QUARTZ_BLOCK, 1, EnumQuartzVariant.DEFAULT.a())});
		var1.registerShapedRecipe(new ItemStack(Blocks.STONEBRICK, 4), new Object[]{"##", "##", '#', new ItemStack(Blocks.STONE, 1, EnumStoneVariant.STONE.a())});
		var1.registerShapedRecipe(new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.P), new Object[]{"#", "#", '#', new ItemStack(Blocks.STONE_SLAB, 1, EnumStoneSlabVariant.SMOOTHBRICK.a())});
		var1.registerShapelessRecipe(new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.N), new Object[]{Blocks.STONEBRICK, Blocks.VINE});
		var1.registerShapelessRecipe(new ItemStack(Blocks.MOSSY_COBBLESTONE, 1), new Object[]{Blocks.COBBLESTONE, Blocks.VINE});
		var1.registerShapedRecipe(new ItemStack(Blocks.IRON_BARS, 16), new Object[]{"###", "###", '#', Items.IRON_INGOT});
		var1.registerShapedRecipe(new ItemStack(Blocks.GLASS_PANE, 16), new Object[]{"###", "###", '#', Blocks.GLASS});
		var1.registerShapedRecipe(new ItemStack(Blocks.REDSTONE_LAMP, 1), new Object[]{" R ", "RGR", " R ", 'R', Items.REDSTONE, 'G', Blocks.GLOWSTONE});
		var1.registerShapedRecipe(new ItemStack(Blocks.BEACON, 1), new Object[]{"GGG", "GSG", "OOO", 'G', Blocks.GLASS, 'S', Items.NETHER_STAR, 'O', Blocks.OBSIDIAN});
		var1.registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICK, 1), new Object[]{"NN", "NN", 'N', Items.NETHERBRICK});
		/*if(RageSpigot.INSTANCE.getConfig().is18Enabled()) {
			*//*var1.registerShapedRecipe(new ItemStack(Blocks.STONE, 2, EnumStoneVariant.DIORITE.a()), new Object[]{"CQ", "QC", 'C', Blocks.COBBLESTONE, 'Q', Items.QUARTZ});
			var1.registerShapelessRecipe(new ItemStack(Blocks.STONE, 1, EnumStoneVariant.GRANITE.a()), new Object[]{new ItemStack(Blocks.STONE, 1, EnumStoneVariant.DIORITE.a()), Items.QUARTZ});
			var1.registerShapelessRecipe(new ItemStack(Blocks.STONE, 2, EnumStoneVariant.ANDESITE.a()), new Object[]{new ItemStack(Blocks.STONE, 1, EnumStoneVariant.DIORITE.a()), Blocks.COBBLESTONE});*//*
		}*/
		var1.registerShapedRecipe(new ItemStack(Blocks.DIRT, 4, EnumDirtVariant.COARSE_DIRT.a()), new Object[]{"DG", "GD", 'D', new ItemStack(Blocks.DIRT, 1, EnumDirtVariant.DIRT.a()), 'G', Blocks.GRAVEL});
		/*if(RageSpigot.INSTANCE.getConfig().is18Enabled()) {
			*//*var1.registerShapedRecipe(new ItemStack(Blocks.STONE, 4, EnumStoneVariant.DIORITE_SMOOTH.a()), new Object[]{ "SS", "SS", 'S', new ItemStack(Blocks.STONE, 1, EnumStoneVariant.DIORITE.a()) });
			var1.registerShapedRecipe(new ItemStack(Blocks.STONE, 4, EnumStoneVariant.GRANITE_SMOOTH.a()), new Object[]{ "SS", "SS", 'S', new ItemStack(Blocks.STONE, 1, EnumStoneVariant.GRANITE.a()) });
			var1.registerShapedRecipe(new ItemStack(Blocks.STONE, 4, EnumStoneVariant.ANDESITE_SMOOTH.a()), new Object[]{ "SS", "SS", 'S', new ItemStack(Blocks.STONE, 1, EnumStoneVariant.ANDESITE.a()) });*//*
		}*/
		var1.registerShapedRecipe(new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.b), new Object[]{"SS", "SS", 'S', Items.PRISMARINE_SHARD});
		var1.registerShapedRecipe(new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.N), new Object[]{"SSS", "SSS", "SSS", 'S', Items.PRISMARINE_SHARD});
		var1.registerShapedRecipe(new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.O), new Object[]{"SSS", "SIS", "SSS", 'S', Items.PRISMARINE_SHARD, 'I', new ItemStack(Items.DYE, 1, EnumColor.BLACK.getInvColorIndex())});
		var1.registerShapedRecipe(new ItemStack(Blocks.SEA_LANTERN, 1, 0), new Object[]{"SCS", "CCC", "SCS", 'S', Items.PRISMARINE_SHARD, 'C', Items.PRISMARINE_CRYSTALS});
	}
}

