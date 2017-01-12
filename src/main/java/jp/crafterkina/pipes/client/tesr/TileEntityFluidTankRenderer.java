package jp.crafterkina.pipes.client.tesr;

import jp.crafterkina.pipes.common.block.entity.TileEntityFluidTank;
import jp.crafterkina.pipes.common.capability.wrapper.MultiTankWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

/**
 * Created by Kina on 2016/12/29.
 */
public class TileEntityFluidTankRenderer extends FastTESR<TileEntityFluidTank>{
    @Override
    public void renderTileEntityFast(TileEntityFluidTank te, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer){
        FluidStack fluid = te.getContainingFluid();
        if(fluid != null){
            int i = fluid.getFluid().getColor(fluid);
            float a = (float) (i >> 24 & 0xFF) / 0xFF;
            float r = (float) (i >> 16 & 0xFF) / 0xFF;
            float g = (float) (i >> 8 & 0xFF) / 0xFF;
            float b = (float) (i & 0xFF) / 0xFF;

            TextureMap texmap = Minecraft.getMinecraft().getTextureMapBlocks();
            TextureAtlasSprite sprite = texmap.getAtlasSprite(fluid.getFluid().getStill(fluid).toString());

            int combined = getWorld().getCombinedLight(te.getPos(), fluid.getFluid().getLuminosity(fluid));
            short sky = (short) (combined >> 16 & 65535);   //unsigned
            short block = (short) (combined & 65535);       //unsigned

            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            buffer.setTranslation(x + 0.5, y, z + 0.5);

            renderBlock(buffer, te.amountForRender(partialTicks) / 8000, 0.4f, a, r, g, b, sprite, sky, block, shouldRenderUpFace(te, 8000, te.amountForRender(partialTicks)));
        }
    }

    private boolean shouldRenderUpFace(TileEntityFluidTank tank, int limit, float amount){
        if(tank.getContainingFluid() == null) return false;
        if(limit > amount) return true;
        List<IFluidHandler> tanks = tank.getConnectedTanks(EnumFacing.UP);
        if(tanks.isEmpty()) return false;
        return new MultiTankWrapper(tanks.toArray(new IFluidHandler[tanks.size()])).drain(Integer.MAX_VALUE, false) == null;
    }

    private void renderBlock(VertexBuffer buffer, float height, @SuppressWarnings("SameParameterValue") float width, float a, float r, float g, float b, TextureAtlasSprite sprite, short sky, short block, boolean renderUpperFace){
        float uMin = sprite.getMinU();
        float vMin = sprite.getMinV();
        float uMax = sprite.getMaxU();
        float vMax = sprite.getMaxV();

        if(renderUpperFace){
            buffer.pos(width, height, -width).color(r, g, b, a).tex(uMax, vMax).lightmap(sky, block).endVertex();
            buffer.pos(-width, height, -width).color(r, g, b, a).tex(uMin, vMax).lightmap(sky, block).endVertex();
            buffer.pos(-width, height, width).color(r, g, b, a).tex(uMin, vMin).lightmap(sky, block).endVertex();
            buffer.pos(width, height, width).color(r, g, b, a).tex(uMax, vMin).lightmap(sky, block).endVertex();
        }

        buffer.pos(-width, height, -width).color(r, g, b, a).tex(uMax, vMax).lightmap(sky, block).endVertex();
        buffer.pos(-width, 0, -width).color(r, g, b, a).tex(uMin, vMax).lightmap(sky, block).endVertex();
        buffer.pos(-width, 0, width).color(r, g, b, a).tex(uMin, vMin).lightmap(sky, block).endVertex();
        buffer.pos(-width, height, width).color(r, g, b, a).tex(uMax, vMin).lightmap(sky, block).endVertex();

        buffer.pos(width, height, width).color(r, g, b, a).tex(uMax, vMax).lightmap(sky, block).endVertex();
        buffer.pos(width, 0, width).color(r, g, b, a).tex(uMin, vMax).lightmap(sky, block).endVertex();
        buffer.pos(width, 0, -width).color(r, g, b, a).tex(uMin, vMin).lightmap(sky, block).endVertex();
        buffer.pos(width, height, -width).color(r, g, b, a).tex(uMax, vMin).lightmap(sky, block).endVertex();

        buffer.pos(-width, height, width).color(r, g, b, a).tex(uMax, vMax).lightmap(sky, block).endVertex();
        buffer.pos(-width, 0, width).color(r, g, b, a).tex(uMin, vMax).lightmap(sky, block).endVertex();
        buffer.pos(width, 0, width).color(r, g, b, a).tex(uMin, vMin).lightmap(sky, block).endVertex();
        buffer.pos(width, height, width).color(r, g, b, a).tex(uMax, vMin).lightmap(sky, block).endVertex();

        buffer.pos(width, height, -width).color(r, g, b, a).tex(uMax, vMax).lightmap(sky, block).endVertex();
        buffer.pos(width, 0, -width).color(r, g, b, a).tex(uMin, vMax).lightmap(sky, block).endVertex();
        buffer.pos(-width, 0, -width).color(r, g, b, a).tex(uMin, vMin).lightmap(sky, block).endVertex();
        buffer.pos(-width, height, -width).color(r, g, b, a).tex(uMax, vMin).lightmap(sky, block).endVertex();
    }
}
