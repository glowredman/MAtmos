package eu.ha3.matmos.game.system;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.mumfrey.liteloader.PacketHandler;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

public class BlockModificationListener {
	
	public List<Class<? extends Packet>> getHandledPackets()
	{
		return Arrays.asList(S23PacketBlockChange.class, S22PacketMultiBlockChange.class);
	}

	public boolean handlePacket(INetHandler netHandler, Packet packet)
	{
		if(packet instanceof S23PacketBlockChange) {
			S23PacketBlockChange blockChange = (S23PacketBlockChange)packet;
			
			int x = blockChange.func_148879_d();
			int y = blockChange.func_148878_e();
			int z = blockChange.func_148877_f();
			Block newBlock = blockChange.func_148880_c();
			int newMetadata = blockChange.func_148881_g();
			
			//System.out.println(String.format("BlockChange x=%d y=%d z=%d block=%s metadata=%d", x, y, z, newBlock, newMetadata));
			
		} else if(packet instanceof S22PacketMultiBlockChange) {
			S22PacketMultiBlockChange multiBlockChange = (S22PacketMultiBlockChange)packet;
			ChunkCoordIntPair coords = multiBlockChange.func_148920_c();
			int recordCount = multiBlockChange.func_148922_e();
			byte[] data = multiBlockChange.func_148921_d();
			
			//System.out.println(String.format("MultiBlockChange chunkX=%d chunkZ=%d", coords.chunkXPos, coords.chunkZPos));
			
			for(int i = 0; i < recordCount; i++) {
				int relX = data[0] >> 4 & 15;
				int relZ = data[0] & 15;
				int y = data[1];
				
				int x = coords.chunkXPos * 16 + relX;
				int z = coords.chunkZPos * 16 + relZ;
				
				//System.out.println(String.format("MultiBlockChange block %d: x=%d y=%d z=%d", i, x, y, z));
			}
		}
		return true;
	}
	
}
