package jp.crafterkina.pipes.common.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

/**
 * Created by Kina on 2016/12/26.
 */
@Mod.EventBusSubscriber(modid = MOD_ID)
public class AdvancementEventHandler{
    @SubscribeEvent
    protected static void onCrafted(PlayerEvent.ItemCraftedEvent event){
        /*if(event.crafting.getItem() == pipe){
            BUILD_PIPE.addStat(event.player);
            if(event.crafting.hasTagCompound() && event.crafting.getTagCompound().getBoolean("covered")){
                BUILD_COVERED_PIPE.addStat(event.player);
            }
        }else if(event.crafting.getItem() == processor_base){
            BUILD_PROCESSOR_BASE.addStat(event.player);
        }else if(event.crafting.getItem() == strategy_acceleration){
            BUILD_ACCELERATION_PROCESSOR.addStat(event.player);
            double acceleration = StrategyAcceleration.ItemAccelerateProcessor.acceleration(event.crafting);
            if(acceleration >= 6.75d){
                BUILD_ACCELERATION_PROCESSOR_OVER.addStat(event.player);
            }
            if(acceleration >= 100){
                BUILD_ACCELERATION_PROCESSOR_ULTIMATE.addStat(event.player);
            }
        }else if(event.crafting.getItem() == strategy_extraction){
            BUILD_EXTRACTION_PROCESSOR.addStat(event.player);
        }else if(event.crafting.getItem() == strategy_oneway){
            BUILD_ONEWAY_PROCESSOR.addStat(event.player);
        }else if(event.crafting.getItem() == merchant_phone){
            BUILD_MERCHANT_PHONE.addStat(event.player);
        }*/
    }

    private static Optional<PlayerAdvancements> getPlayerAdvancements(EntityPlayer player){
        return player instanceof EntityPlayerMP ? Optional.of(((EntityPlayerMP) player).getAdvancements()) : Optional.empty();
    }

    @Nullable
    private static Advancement getAdvancement(ResourceLocation id){
        return FMLServerHandler.instance().getServer().getAdvancementManager().getAdvancement(id);
    }

    private static void grantAdvancement(@Nonnull PlayerAdvancements playerAdvancements, @Nonnull Advancement advancement){
        playerAdvancements.getProgress(advancement).getRemaningCriteria().forEach(s -> playerAdvancements.grantCriterion(advancement, s));
    }
}
