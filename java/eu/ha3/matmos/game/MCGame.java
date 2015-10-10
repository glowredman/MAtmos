package eu.ha3.matmos.game;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * @author dags_ <dags@dags.me>
 */

public class MCGame
{
    public static boolean isMultiplayer = false;

    public static World currentWorld;
    public static String worldName;
    public static BiomeGenBase currentBiome;

    public static EntityPlayer player;
    public static Position playerPosition = new Position();
    public static double playerXpos = 0D;
    public static double playerYpos = 0D;
    public static double playerZpos = 0D;
    public static float playerPitch = 0F;
    public static float playerYaw = 0F;

    public static boolean firstTick = true;

    public static void update()
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        player = minecraft.thePlayer;

        isMultiplayer = !minecraft.isSingleplayer();

        playerXpos = player.posX;
        playerYpos = player.posY;
        playerZpos = player.posZ;
        playerPitch = player.rotationPitch;
        playerYaw = player.rotationYaw;
        playerPosition.update(playerXpos, playerYpos, playerZpos);

        currentWorld = minecraft.theWorld;
        currentBiome = currentWorld.getBiomeGenForCoords(playerPosition);
        worldName = isMultiplayer ? "N/A" : currentWorld.getWorldInfo().getWorldName();
        if (firstTick)
            firstTick = false;
    }

    public static void drawString(String s, int x, int y)
    {
        drawString(s, x, y, 0xFFFFFF);
    }

    public static void drawString(String s, int x, int y, int color)
    {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s, x, y, color);
    }

    public static boolean noFall()
    {
        return player.isSpectator() || player.capabilities.isCreativeMode || player.capabilities.isFlying;
    }

    public static String getBlockName(BlockPos pos)
    {
        return getNameFor(currentWorld.getBlockState(pos).getBlock());
    }

    public static String getBlockName(int x, int y, int z)
    {
        return getNameFor(getBlock(x, y, z));
    }

    public static String  getNameFor(Block block)
    {
        return Block.blockRegistry.getNameForObject(block).toString();
    }

    public static Block getBlock(int x, int y, int z)
    {
        Block b = Blocks.air;
        if (y >= 0 && y <= 256)
        {
            Chunk c = MCGame.currentWorld.getChunkProvider().provideChunk(x >> 4, z >> 4);
            ExtendedBlockStorage[] storageArray = c.getBlockStorageArray();
            if (y >= 0 && y >> 4 < storageArray.length)
            {
                ExtendedBlockStorage storage = storageArray[y >> 4];
                if (storage != null)
                {
                    IBlockState state = storage.get(x & 15, y & 15, z & 15);
                    b = state.getBlock();
                }
            }
        }
        return b;
    }
}
