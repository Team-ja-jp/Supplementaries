package net.mehvahdjukaar.supplementaries.renderers;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.mehvahdjukaar.supplementaries.blocks.BellowsBlock;
import net.mehvahdjukaar.supplementaries.blocks.BellowsBlockTile;
import net.mehvahdjukaar.supplementaries.setup.Registry;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class BellowsBlockTileRenderer extends TileEntityRenderer<BellowsBlockTile> {
    public BellowsBlockTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(BellowsBlockTile tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn,
                       int combinedOverlayIn) {
        BlockState state = Registry.BELLOWS.getDefaultState().with(BellowsBlock.TILE, 1);
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

        float dh = MathHelper.lerp(partialTicks, tile.prevHeight, tile.height);

        matrixStackIn.push();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        Direction dir = tile.getDirection();
        matrixStackIn.rotate(dir.getOpposite().getRotation());
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90)) ;


        matrixStackIn.push();
        matrixStackIn.translate(0, -1+(3/16d), 0);

        //matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180)) ;

        matrixStackIn.translate(-0.5, -0.5, -0.5);
        matrixStackIn.translate(0, -dh,0);

        blockRenderer.renderBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        matrixStackIn.pop();

        matrixStackIn.push();
        //matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90 + MathHelper.lerp(partialTicks, tile.prevYaw, tile.yaw)));
        matrixStackIn.translate(-0.5, -0.5, -0.5);
        matrixStackIn.translate(0, dh,0);

        blockRenderer.renderBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        matrixStackIn.pop();

        float j = 3.2f;
        matrixStackIn.translate( -0.5, -0.5-dh*(j/2),-0.5);
        matrixStackIn.scale(1, 1+j*dh, 1);


        BlockState state1 = Registry.BELLOWS.getDefaultState().with(BellowsBlock.TILE, 2);
        blockRenderer.renderBlock(state1, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);

        matrixStackIn.pop();
    }
}