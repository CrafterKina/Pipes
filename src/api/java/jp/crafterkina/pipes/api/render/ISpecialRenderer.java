package jp.crafterkina.pipes.api.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Low level renderer interface.
 *
 * @author Kina
 * @version 1.0
 * @see net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 * @since 1.0
 */
@SideOnly(Side.CLIENT)
public interface ISpecialRenderer{
    @CapabilityInject(ISpecialRenderer.class)
    Capability<ISpecialRenderer> CAPABILITY = null;

    ResourceLocation[] DESTROY_STAGES = {new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png")};

    static void setLightmapDisabled(boolean disabled){
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);

        if(disabled){
            GlStateManager.disableTexture2D();
        }else{
            GlStateManager.enableTexture2D();
        }

        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    static void bindTexture(TileEntityRendererDispatcher dispatcher, ResourceLocation location){
        TextureManager texturemanager = dispatcher.renderEngine;

        if(texturemanager != null){
            texturemanager.bindTexture(location);
        }
    }

    void renderAt(TileEntity te, double x, double y, double z, float partialTicks, TileEntityRendererDispatcher dispatcher, int destroyStage);
}