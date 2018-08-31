package eu.ha3.matmos.util;

import eu.ha3.matmos.util.math.MAtMutableBlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/* x-placeholder */

public class MAtUtil {
    private static final MAtMutableBlockPos position = new MAtMutableBlockPos();

    public static MAtMutableBlockPos getPlayerPosition() {
        return position.of(getPlayerX(), getPlayerY(), getPlayerZ());
    }

    public static int getPlayerX() {
        return (int)Math.floor(Minecraft.getMinecraft().player.posX);
    }

    public static int getPlayerY() {
        return (int)Math.floor(Minecraft.getMinecraft().player.posY);
    }

    public static int getPlayerZ() {
        return (int)Math.floor(Minecraft.getMinecraft().player.posZ);
    }

    public static boolean isUnderwaterAnyGamemode() {
        return Minecraft.getMinecraft().player.isInsideOfMaterial(Material.WATER);
    }

    /**
     * Tells if y is within the height boundaries of the current world, where blocks can exist.
     *
     * @param  y
     * @return
     */
    public static boolean isWithinBounds(int y) {
        return y >= 0 && y < Minecraft.getMinecraft().world.getHeight();
    }

    /**
     * Clamps the y value to something that is within the current worlds' boundaries.
     *
     * @param  y
     * @return
     */
    public static int clampToBounds(int y) {
        return Math.max(Math.min(0, y), Minecraft.getMinecraft().world.getHeight() - 1);
    }

    /**
     * Gets the block at a certain location in the current world. This method is not safe against
     * locations in undefined space.
     *
     * @param  x
     * @param  y
     * @param  z
     * @return
     */
    @Deprecated
    public static Block getBlockAt(int x, int y, int z) {
        return getBlockAt(Minecraft.getMinecraft().world, x, y, z);
    }

    public static Block getBlockAt(BlockPos pos) {
        return Minecraft.getMinecraft().world.getBlockState(pos).getBlock();
    }

    /**
     * Gets the name of the block at a certain location in the current world. If the location is in an
     * undefined space (lower than zero or higher than the current world getHeight(), or throws any
     * exception during evaluation), it will return a default string.
     *
     * @param  x
     * @param  y
     * @param  z
     * @param  defaultIfFail
     * @return
     */
    @Deprecated
    public static String getNameAt(int x, int y, int z, String defaultIfFail) {
        if (!isWithinBounds(y)) return defaultIfFail;

        return getNameAt(Minecraft.getMinecraft().world, x, y, z);
    }

    public static String getNameAt(BlockPos pos, String defaultIfFail) {
        if (!isWithinBounds(pos.getY())) {
            return defaultIfFail;
        }

        return nameOf(getBlockAt(pos));
    }

    /**
     * Gets the block at a certain location in the given world. This method is not safe against
     * locations in undefined space.
     *
     * @param  world
     * @param  x
     * @param  y
     * @param  z
     * @return
     */
    private static Block getBlockAt(World world, int x, int y, int z) {
        return world.getBlockState(position.of(x, y, z)).getBlock();
    }

    /**
     * Gets the name of the block at a certain location in the given world. This method is not safe
     * against locations in undefined space.
     *
     * @param  world
     * @param  x
     * @param  y
     * @param  z
     * @return
     */
    private static String getNameAt(World world, int x, int y, int z) {
        return nameOf(getBlockAt(world, x, y, z));
    }

    //

    /**
     * Gets the unique name of a given block, defined by its interoperability identifier.
     *
     * @param  block
     * @return
     */
    public static String nameOf(Block block) {
        return Block.REGISTRY.getNameForObject(block).toString();
    }

    /**
     * Gets the unique name of a given itemstack's item.
     *
     * @param  itemStack
     * @return
     */
    public static String nameOf(ItemStack itemStack) {
        return nameOf(itemStack.getItem());
    }

    /**
     * Gets the unique name of a given item.
     *
     * @param  item
     * @return
     */
    public static String nameOf(Item item) {
        return Item.REGISTRY.getNameForObject(item).toString();
    }

    public static boolean isSoundMasterEnabled() {
        return Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0f;
    }

    public static boolean isSoundAmbientEnabled() {
        return Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.AMBIENT) > 0f;
    }

    /**
     * Play a sound.
     *
     * @param name
     * @param nx
     * @param ny
     * @param nz
     * @param volume
     * @param pitch
     * @param attenuation
     * @param rollf
     */
    public static void playSound(String name, float nx, float ny, float nz, float volume, float pitch, int attenuation, float rollf) {
        playSound(nx, ny, nz, name, volume, pitch);
    }

    /**
     * Play a sound.
     *
     * @param name
     * @param nx
     * @param ny
     * @param nz
     * @param volume
     * @param pitch
     */
    public static void playSound(String name, float nx, float ny, float nz, float volume, float pitch) {
        playSound(nx, ny, nz, name, volume, pitch);
    }

    private static void playSound(float x, float y, float z, String soundName, float volume, float pitch) {
        PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(new ResourceLocation(soundName), SoundCategory.MASTER, volume, pitch, false, 0, ISound.AttenuationType.LINEAR, x, y, z);
        Minecraft.getMinecraft().getSoundHandler().playSound(positionedsoundrecord);
    }

    /**
     * Returns the PowerMeta of the block at the specified coordinates.<br>
     * The PowerMeta is a string that combines the block name and the metadata of a certain block.
     *
     * @param  x
     * @param  y
     * @param  z
     * @param  defaultIfFail
     * @return
     */
    @Deprecated
    public static String getPowerMetaAt(int x, int y, int z, String defaultIfFail) {
        if (!isWithinBounds(y)) return defaultIfFail;

        Block block = getBlockAt(x, y, z);
        IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(position.of(x, y, z));
        return asPowerMeta(block, block.getMetaFromState(blockState));
    }

    public static String getPowerMetaAt(BlockPos pos, String defaultIfFail) {
        if (!isWithinBounds(pos.getY())) {
            return defaultIfFail;
        }

        IBlockState state = Minecraft.getMinecraft().world.getBlockState(pos);
        return asPowerMeta(state.getBlock(), state.getBlock().getMetaFromState(state));
    }

    /**
     * Returns the PowerMeta, a string that combines the item name and the metadata of a certain block.
     *
     * @param  item
     * @return
     */
    public static String asPowerMeta(ItemStack item) {
        return asPowerMeta(nameOf(item.getItem()), item.getMetadata());
    }

    /**
     * Returns the PowerMeta, a string that combines the item name and the metadata of a certain block.
     *
     * @param  item
     * @param  meta
     * @return
     */
    public static String asPowerMeta(Item item, int meta) {
        return asPowerMeta(nameOf(item), meta);
    }

    /**
     * Returns the PowerMeta, a string that combines the block name and the metadata of a certain block.
     *
     * @param  block
     * @param  meta
     * @return
     */
    public static String asPowerMeta(Block block, int meta) {
        return asPowerMeta(nameOf(block), meta);
    }

    /**
     * Returns the PowerMeta, a string that combines the item/block name and its metadata.
     *
     * @param  block
     * @param  meta
     * @return
     */
    public static String asPowerMeta(String block, int meta) {
        return block + "^" + Integer.toString(meta);
    }

    /**
     * Returns the metadata of a certain block at the specified coordinates.
     *
     * @param  x
     * @param  y
     * @param  z
     * @param  defaultIfFail
     * @return
     */
    @Deprecated
    public static int getMetaAt(int x, int y, int z, int defaultIfFail) {
        if (!isWithinBounds(y)) return defaultIfFail;

        IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(position.of(x, y, z));
        return blockState.getBlock().getMetaFromState(blockState);
    }

    public static int getMetaAt(BlockPos pos, int defaultIdFail) {
        IBlockState state = Minecraft.getMinecraft().world.getBlockState(pos);
        return state.getBlock().getMetaFromState(state);
    }

    /**
     * Returns the metadata of a certain block at the specified coordinates.
     *
     * @param  x
     * @param  y
     * @param  z
     * @param  defaultIfFail
     * @return
     */
    @Deprecated
    public static String getMetaAsStringAt(int x, int y, int z, String defaultIfFail) {
        if (!isWithinBounds(y)) return defaultIfFail;

        IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(position.of(x, y, z));
        return Integer.toString(blockState.getBlock().getMetaFromState(blockState));
    }

    public static String getMetaAsStringAt(BlockPos pos, String defaultIfFail) {
        if (!isWithinBounds(pos.getY())) {
            return defaultIfFail;
        }

        IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(pos);
        return Integer.toString(blockState.getBlock().getMetaFromState(blockState));
    }

    /**
     * Returns the legacy number value of an item stack.
     *
     * @param  itemStack
     * @return
     */
    public static int legacyOf(ItemStack itemStack) {
        return Item.REGISTRY.getIDForObject(itemStack.getItem());
    }

    /**
     * Returns the legacy number value of a block.
     *
     * @param  block
     * @return
     */
    public static int legacyOf(Block block) {
        return Block.REGISTRY.getIDForObject(block);
    }

    public static String sanitizeUniqueName(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-_]", "");
    }
}
