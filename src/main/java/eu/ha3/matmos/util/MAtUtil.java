package eu.ha3.matmos.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MAtUtil {

    public static EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public static World getWorld() {
        return Minecraft.getMinecraft().world;
    }

    public static int getPlayerX() {
        return (int)Math.floor(getPlayer().posX);
    }

    public static int getPlayerY() {
        return (int)Math.floor(getPlayer().posY);
    }

    public static int getPlayerZ() {
        return (int)Math.floor(getPlayer().posZ);
    }
    
    public static BlockPos getPlayerPos() {
        Vec3d doublePos = getPlayer().getPositionVector();
        return new BlockPos(  MathHelper.floor(doublePos.x),
                              MathHelper.floor(doublePos.y),
                              MathHelper.floor(doublePos.z));
    }

    public static boolean isUnderwaterAnyGamemode() {
        return getPlayer().isInsideOfMaterial(Material.WATER);
    }

    /**
     * Tells if y is within the height boundaries of the current world, where blocks can exist.
     */
    public static boolean isWithinBounds(BlockPos pos) {
        return pos.getY() >= 0 && pos.getY() < getWorld().getHeight();
    }

    /**
     * Clamps the y value to something that is within the current worlds' boundaries.
     */
    public static int clampToBounds(int y) {
        return Math.min(Math.max(0, y), getWorld().getHeight() - 1);
    }

    /**
     * Gets the block at a certain location in the current world. This method is not safe against
     * locations in undefined space.
     */
    public static Block getBlockAt(BlockPos pos) {
        return getWorld().getBlockState(pos).getBlock();
    }

    /**
     * Gets the name of the block at a certain location in the current world. If the location is in an
     * undefined space (lower than zero or higher than the current world getHeight(), or throws any
     * exception during evaluation), it will return a default string.
     */
    public static String getNameAt(BlockPos pos, String defaultIfFail) {
        if (!isWithinBounds(pos)) {
            return defaultIfFail;
        }

        return nameOf(getBlockAt(pos));
    }

    /**
     * Gets the unique name of a given block, defined by its interoperability identifier.
     */
    public static String nameOf(Block block) {
        return Block.REGISTRY.getNameForObject(block).toString();
    }

    /**
     * Gets the unique name of a given itemstack's item.
     */
    public static String nameOf(ItemStack itemStack) {
        return nameOf(itemStack.getItem());
    }

    /**
     * Gets the unique name of a given item.
     */
    public static String nameOf(Item item) {
        return Item.REGISTRY.getNameForObject(item).toString();
    }

    public static boolean isSoundMasterEnabled() {
        return Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0;
    }

    public static boolean isSoundAmbientEnabled() {
        return Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.AMBIENT) > 0;
    }

    /**
     * Play a sound.
     */
    public static void playSound(String name, float nx, float ny, float nz, float volume, float pitch, int attenuation, float rollf) {
        playSound(nx, ny, nz, name, volume, pitch);
    }

    /**
     * Play a sound.
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
     */
    public static String getPowerMetaAt(BlockPos pos, String defaultIfFail) {
        if (!isWithinBounds(pos)) {
            return defaultIfFail;
        }

        IBlockState state = Minecraft.getMinecraft().world.getBlockState(pos);
        return asPowerMeta(state.getBlock(), state.getBlock().getMetaFromState(state));
    }

    /**
     * Returns the PowerMeta, a string that combines the item name and the metadata of a certain block.
     */
    public static String asPowerMeta(ItemStack item) {
        return asPowerMeta(nameOf(item.getItem()), item.getMetadata());
    }

    /**
     * Returns the PowerMeta, a string that combines the block name and the metadata of a certain block.
     */
    public static String asPowerMeta(Block block, int meta) {
        return asPowerMeta(nameOf(block), meta);
    }

    /**
     * Returns the PowerMeta, a string that combines the item/block name and its metadata.
     */
    private static String asPowerMeta(String block, int meta) {
        return block + "^" + Integer.toString(meta);
    }

    /**
     * Returns the metadata of a certain block at the specified coordinates.
     */
    public static int getMetaAt(BlockPos pos, int defaultIdFail) {
        IBlockState state = getWorld().getBlockState(pos);
        return state.getBlock().getMetaFromState(state);
    }

    /**
     * Returns the metadata of a certain block at the specified coordinates.
     */
    public static String getMetaAsStringAt(BlockPos pos, String defaultIfFail) {
        if (!isWithinBounds(pos)) {
            return defaultIfFail;
        }

        return Integer.toString(getMetaAt(pos, 0));
    }

    /**
     * Returns the legacy number value of an item stack.
     */
    public static int legacyOf(ItemStack itemStack) {
        return Item.REGISTRY.getIDForObject(itemStack.getItem());
    }

    /**
     * Returns the legacy number value of a block.
     */
    public static int legacyOf(Block block) {
        return Block.REGISTRY.getIDForObject(block);
    }

    public static String sanitizeUniqueName(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-_]", "");
    }
}
