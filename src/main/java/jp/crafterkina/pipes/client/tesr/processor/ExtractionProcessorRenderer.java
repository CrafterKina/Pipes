package jp.crafterkina.pipes.client.tesr.processor;

import jp.crafterkina.pipes.api.render.ISpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Kina on 2016/12/22.
 */
@SideOnly(Side.CLIENT)
public class ExtractionProcessorRenderer implements ISpecialRenderer{
    private final EnumFacing facing;
    private final double speed;
    private ItemStack stack;

    public ExtractionProcessorRenderer(ItemStack stack, EnumFacing facing, double speed){
        this.stack = stack;
        this.facing = facing.rotateAround(EnumFacing.Axis.Y).rotateAround(EnumFacing.Axis.Y).rotateAround(EnumFacing.Axis.Y);
        this.speed = speed;
    }

    @Override
    public void renderAt(TileEntity te, double x, double y, double z, float partialTicks, TileEntityRendererDispatcher dispatcher, int destroyStage){
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.25, 0.25, 0.25);
        GlStateManager.rotate(90, facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
        if(facing == EnumFacing.DOWN)
            GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate((float) ((te.getWorld().getTotalWorldTime() + partialTicks) * speed), 0, 1, 0);
        ISpecialRenderer.bindTexture(dispatcher, TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderItemStack(stack);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack){
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
    }
}
