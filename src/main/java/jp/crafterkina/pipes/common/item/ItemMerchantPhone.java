package jp.crafterkina.pipes.common.item;

import jp.crafterkina.pipes.common.RegistryEntries;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

import static jp.crafterkina.pipes.common.PipesCore.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class ItemMerchantPhone extends Item{
    private ItemMerchantPhone(){
        setUnlocalizedName(MOD_ID + ".merchant_phone");
        setCreativeTab(CreativeTabs.MISC);
    }

    @SubscribeEvent
    protected static void interactOnEntity(PlayerInteractEvent.EntityInteract event){
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.getHeldItem(event.getHand());
        Entity target = event.getTarget();
        if(!(target instanceof IMerchant)) return;
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) stack.setTagCompound(compound = new NBTTagCompound());
        if(compound.hasUniqueId("Merchant") && !player.isSneaking()) return;
        compound.setUniqueId("Merchant", target.getUniqueID());
        player.sendMessage(new TextComponentTranslation("mes." + RegistryEntries.merchant_phone.getUnlocalizedName() + ".register"));
        event.setCanceled(true);
    }

    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(new ItemMerchantPhone().setRegistryName(MOD_ID, "merchant_phone"));
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack){
        return 32;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn){
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!(worldIn instanceof WorldServer)) return new ActionResult<>(EnumActionResult.PASS, stack);
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) stack.setTagCompound(compound = new NBTTagCompound());
        if(!compound.hasUniqueId("Merchant")) return new ActionResult<>(EnumActionResult.PASS, stack);
        UUID uuid = compound.getUniqueId("Merchant");
        if(uuid == null) return new ActionResult<>(EnumActionResult.PASS, stack);
        Entity entity = ((WorldServer) worldIn).getEntityFromUuid(uuid);
        if(!(entity instanceof IMerchant)){
            stack.getTagCompound().removeTag("MerchantMost");
            stack.getTagCompound().removeTag("MerchantLeast");
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        ((IMerchant) entity).setCustomer(playerIn);
        playerIn.displayVillagerTradeGui((IMerchant) entity);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
