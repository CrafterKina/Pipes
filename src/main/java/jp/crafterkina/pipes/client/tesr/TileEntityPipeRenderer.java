package jp.crafterkina.pipes.client.tesr;

import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.pipe.FlowingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

/**
 * Created by Kina on 2016/12/14.
 */
@SideOnly(Side.CLIENT)
public class TileEntityPipeRenderer extends TileEntitySpecialRenderer<TileEntityPipe>{
    @Override
    public void renderTileEntityAt(TileEntityPipe te, double x, double y, double z, float partialTicks, int destroyStage){
        Set<FlowingItem> flowingItem = te.getFlowingItems();
        flowingItem.forEach(i -> {
            //Vec3d vec3d = item.getVelocity().scale(tick).addVector(x, y, z);
            GlStateManager.pushMatrix();

            float v = te.getWorld().getTotalWorldTime() - i.tick + partialTicks;
            Vec3d vec = new Vec3d(0.5, 0.5, 0.5).add(!i.turned ? i.item.getDirection().scale(-1).scale(0.5) : Vec3d.ZERO);

            GlStateManager.translate(vec.xCoord + x + i.item.getVelocity().xCoord * v * 0.5, vec.yCoord + y + i.item.getVelocity().yCoord * v * 0.5, vec.zCoord + z + i.item.getVelocity().zCoord * v * 0.5);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(0.125, 0.125, 0.125);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            renderItemStack(i.item.getStack());

            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        });
    }

    private void renderItemStack(ItemStack stack){
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
    }
}
