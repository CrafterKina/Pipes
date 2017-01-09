package jp.crafterkina.pipes.client.tesr;

import jp.crafterkina.pipes.common.block.entity.TileEntityFluidTank;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Kina on 2016/12/29.
 */
public class TileEntityFluidTankRenderer extends TileEntitySpecialRenderer<TileEntityFluidTank>{
    private static final boolean DEBUG = true;

    @Override
    public void renderTileEntityAt(TileEntityFluidTank te, double x, double y, double z, float partialTicks, int destroyStage){
        if(DEBUG){
            FluidStack fluid = te.getContainingFluid();
            String text = fluid == null ? "EMPTY" : fluid.getLocalizedName() + ": " + fluid.amount;
            this.setLightmapDisabled(true);
            this.drawNameplate(te, text, x, y - 1, z, 12);
            this.setLightmapDisabled(false);
        }
    }
}
