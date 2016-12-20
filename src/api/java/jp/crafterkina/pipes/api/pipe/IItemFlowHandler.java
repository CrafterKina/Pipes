package jp.crafterkina.pipes.api.pipe;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * Created by Kina on 2016/12/16.
 */
public interface IItemFlowHandler{
    @CapabilityInject(IItemFlowHandler.class)
    Capability<IItemFlowHandler> CAPABILITY = null;

    FlowItem flow(FlowItem item);

    int insertableMaximumStackSizeAtOnce();
}
