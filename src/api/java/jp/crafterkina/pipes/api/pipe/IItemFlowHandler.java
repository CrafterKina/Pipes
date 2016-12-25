package jp.crafterkina.pipes.api.pipe;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * Capability to handle item flowing.
 *
 * @author Kina
 * @since 1.0
 * @version 1.0
 * @see Capability
 */
public interface IItemFlowHandler{
    @CapabilityInject(IItemFlowHandler.class)
    Capability<IItemFlowHandler> CAPABILITY = null;

    /**
     * Pour into handler.
     *
     * @param item to flow
     * @return over-flowed
     */
    FlowItem flow(FlowItem item);
}
