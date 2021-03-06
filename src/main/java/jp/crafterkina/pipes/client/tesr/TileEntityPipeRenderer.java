package jp.crafterkina.pipes.client.tesr;

import jp.crafterkina.pipes.api.render.ISpecialRenderer;
import jp.crafterkina.pipes.client.tesr.processor.DefaultProcessorRenderer;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.pipe.FlowingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
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
    public void render(TileEntityPipe te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        Set<FlowingItem> flowingItem = te.flowingItems;
        float time = te.getWorld().getTotalWorldTime() + partialTicks;
        flowingItem.forEach(i -> {
            GlStateManager.pushMatrix();

            float v = time - i.tick;
            Vec3d vec = new Vec3d(0.5, 0.5, 0.5).add(!i.turned ? i.item.getDirection().scale(-1).scale(0.5) : Vec3d.ZERO);

            GlStateManager.translate(vec.x + x + i.item.getVelocity().x * v * 0.5, vec.y + y + i.item.getVelocity().y * v * 0.5, vec.z + z + i.item.getVelocity().z * v * 0.5);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(0.132, 0.132, 0.132);
            GlStateManager.rotate(45, 0, 1, 0);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            renderItemStack(i.item.getStack());

            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        });
        renderProcessor(rendererDispatcher, te, x, y, z, partialTicks, destroyStage);
    }

    @SideOnly(Side.CLIENT)
    private void renderProcessor(TileEntityRendererDispatcher dispatcher, TileEntityPipe te, double x, double y, double z, float partialTicks, int destroyStage){
        ISpecialRenderer render;
        if(te.getProcessor().hasCapability(ISpecialRenderer.CAPABILITY, null))
            render = te.getProcessor().getCapability(ISpecialRenderer.CAPABILITY, null);
        else render = DefaultProcessorRenderer.INSTANCE;
        render.renderAt(te, x, y, z, partialTicks, dispatcher, destroyStage);
    }

    private void renderItemStack(ItemStack stack){
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
    }
}
