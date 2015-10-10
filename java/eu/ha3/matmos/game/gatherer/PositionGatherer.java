package eu.ha3.matmos.game.gatherer;

import eu.ha3.matmos.engine.Data;
import eu.ha3.matmos.engine.DataManager;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.Position;
import eu.ha3.matmos.util.NumberUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * @author dags_ <dags@dags.me>
 */

public class PositionGatherer implements DataGatherer
{
    private Data<Number> xPos = new Data<Number>();
    private Data<Number> yPos = new Data<Number>();
    private Data<Number> zPos = new Data<Number>();
    private Data<String> coords = new Data<String>();
    private Data<Number> pitch = new Data<Number>();
    private Data<Number> yaw = new Data<Number>();
    private Data<String> direction = new Data<String>();

    private Data<Number> overheadAir = new Data<Number>();
    private Data<Number> overheadThickness = new Data<Number>();
    private Data<Number> overheadTotalThickness = new Data<Number>();
    private Data<String> overheadFirstBlock = new Data<String>();
    private Data<String> blockUnderFoot = new Data<String>();
    private Data<Number> lightAtPos = new Data<Number>();

    private Data<String> biomeName = new Data<String>();
    private Data<Number> biomeId = new Data<Number>();

    @Override
    public DataGatherer register(DataManager manager)
    {
        manager.registerNum("position.x", xPos);
        manager.registerNum("position.y", yPos);
        manager.registerNum("position.z", zPos);
        manager.registerString("position.coords", coords);
        manager.registerNum("position.pitch", pitch);
        manager.registerNum("position.yaw", yaw);
        manager.registerString("position.direction", direction);

        manager.registerNum("position.overhead.ceilingHeight", overheadAir);
        manager.registerNum("position.overhead.ceilingThickness", overheadThickness);
        manager.registerNum("position.overhead.totalThickness", overheadTotalThickness);
        manager.registerString("position.overhead.firstBlock", overheadFirstBlock);
        manager.registerString("position.blockUnderFoot", blockUnderFoot);
        manager.registerNum("position.lightAtPos", lightAtPos);

        manager.registerString("position.biomeName", biomeName);
        manager.registerNum("position.biomeId", biomeId);
        return this;
    }

    @Override
    public void update()
    {
        float y = MCGame.player.rotationYaw % 360;
        y = y >= 0 ? y : 360 + y;
        pitch.value = NumberUtil.round1dp(MCGame.player.rotationPitch);
        yaw.value = NumberUtil.round1dp(y);
        direction.value = MCGame.player.getHorizontalFacing().getName();

        xPos.value = MCGame.playerPosition.getX();
        yPos.value = MCGame.playerPosition.getY();
        zPos.value = MCGame.playerPosition.getZ();
        coords.value = MCGame.playerPosition.serial();

        calcBlocksUpAndDown();
        lightAtPos.value = MCGame.currentWorld.getLight(MCGame.playerPosition);

        biomeName.value = MCGame.currentBiome.biomeName.replace(' ', '_');
        biomeId.value = MCGame.currentBiome.biomeID;
    }

    private void calcBlocksUpAndDown()
    {
        Position p = MCGame.playerPosition;
        int air = 0;
        int partSolid = 0;
        int totalSolid = 0;
        String firstSolid = "none";
        for (int y = p.getY() + 2; y < 256; y++)
        {
            Block b = MCGame.getBlock(p.getX(), y, p.getZ());
            if (!b.equals(Blocks.air))
            {
                totalSolid++;
                if (totalSolid == 1)
                {
                    firstSolid = MCGame.getNameFor(b);
                }
            }
            else if (totalSolid == 0)
            {
                air++;
            }
            else if (partSolid == 0)
            {
                partSolid = totalSolid;
            }
        }
        overheadAir.value = totalSolid > 0 ? air : -1;
        overheadThickness.value = partSolid;
        overheadTotalThickness.value = totalSolid;
        overheadFirstBlock.value = firstSolid;
        blockUnderFoot.value = MCGame.getBlockName(MCGame.playerPosition.down());
    }
}
