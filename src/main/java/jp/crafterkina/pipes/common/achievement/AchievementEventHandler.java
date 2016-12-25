package jp.crafterkina.pipes.common.achievement;

import jp.crafterkina.pipes.common.pipe.strategy.StrategyAcceleration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;
import static jp.crafterkina.pipes.common.RegistryEntries.ITEM.*;
import static jp.crafterkina.pipes.common.achievement.EnumAchievement.*;

/**
 * Created by Kina on 2016/12/26.
 */
@Mod.EventBusSubscriber(modid = MOD_ID)
public class AchievementEventHandler{
    @SubscribeEvent
    protected static void onCrafted(PlayerEvent.ItemCraftedEvent event){
        if(event.crafting.getItem() == pipe){
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
        }
    }
}
