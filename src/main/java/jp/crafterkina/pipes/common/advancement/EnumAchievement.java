/*TODO Moving to json system
package jp.crafterkina.pipes.common.achievement;

import com.google.common.base.CaseFormat;
import jp.crafterkina.pipes.common.RegistryEntries;
import jp.crafterkina.pipes.common.item.ItemPipe;
import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyAcceleration;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyExtraction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import java.util.Arrays;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

public enum EnumAchievement{
    BUILD_PIPE(new Achievement("", "", 0, 0, ItemPipe.createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe), EnumPipeMaterial.WOOD), null).initIndependentStat()),
    BUILD_PROCESSOR_BASE(new Achievement("", "", 3, 1, RegistryEntries.ITEM.processor_base, BUILD_PIPE.achievement)),
    BUILD_ACCELERATION_PROCESSOR(new Achievement("", "", 5, 4, Items.SUGAR, BUILD_PROCESSOR_BASE.achievement)),
    BUILD_ACCELERATION_PROCESSOR_OVER(new Achievement("", "", 5, 7, StrategyAcceleration.ItemAccelerateProcessor.createStack(new ItemStack(RegistryEntries.ITEM.strategy_acceleration), 6.75d), BUILD_ACCELERATION_PROCESSOR.achievement)),
    BUILD_ACCELERATION_PROCESSOR_ULTIMATE(new Achievement("", "", 2, 8, StrategyAcceleration.ItemAccelerateProcessor.createStack(new ItemStack(RegistryEntries.ITEM.strategy_acceleration), 9d), BUILD_ACCELERATION_PROCESSOR_OVER.achievement).setSpecial()),
    BUILD_EXTRACTION_PROCESSOR(new Achievement("", "", 1, 3, StrategyExtraction.ItemExtractionProcessor.createStack(new ItemStack(RegistryEntries.ITEM.strategy_extraction), 0, 0, 0, 0x9F844D), BUILD_PROCESSOR_BASE.achievement)),
    BUILD_ONEWAY_PROCESSOR(new Achievement("", "", 6, -1, RegistryEntries.ITEM.strategy_oneway, BUILD_PROCESSOR_BASE.achievement)),
    BUILD_COVERED_PIPE(new Achievement("", "", -2, 2, ItemPipe.createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe), EnumPipeMaterial.WOOD, EnumDyeColor.WHITE), BUILD_PIPE.achievement)),
    BUILD_MERCHANT_PHONE(new Achievement("", "", 0, -3, RegistryEntries.ITEM.merchant_phone, null).initIndependentStat());

    public final Achievement achievement;

    EnumAchievement(Achievement achievement){
        Achievement register = new Achievement(MOD_ID + ":achievement." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name()), MOD_ID + "." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name()), achievement.displayColumn, achievement.displayRow, achievement.theItemStack, achievement.parentAchievement);
        if(achievement.getSpecial()){
            register = register.setSpecial();
        }
        if(achievement.isIndependent){
            register = register.initIndependentStat();
        }
        this.achievement = register.registerStat();
    }

    public static void registerPage(){
        AchievementPage.registerAchievementPage(new AchievementPage("Pipes", Arrays.stream(values()).map(a -> a.achievement).toArray(Achievement[]::new)));
    }

    public void addStat(EntityPlayer player){
        player.addStat(achievement);
    }
}
*/
