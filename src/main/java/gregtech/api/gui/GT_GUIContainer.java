package gregtech.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import gregtech.api.util.ColorsMetadataSection;
import gregtech.api.util.ColorsMetadataSectionSerializer;
import java.io.IOException;
import net.minecraft.client.resources.IResource;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import gregtech.api.util.GT_Log;

import static gregtech.GT_Mod.GT_FML_LOGGER;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * Main GUI-Container-Class which basically contains the Code needed to prevent crashes from improperly Coded Items.
 */
public class GT_GUIContainer extends GuiContainer {

    public boolean mCrashed = false;

    public ResourceLocation mGUIbackground;

    public IResource mGUIbackgroundResource;
    public ColorsMetadataSection cmSection;

    public String mGUIbackgroundPath;

    public GT_GUIContainer(Container aContainer, String aGUIbackground) {
        super(aContainer);
        mGUIbackground = new ResourceLocation(mGUIbackgroundPath = aGUIbackground);

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            loadTextureMetaData();
        }
    }

    public int getLeft() {
        return guiLeft;
    }

    public int getTop() {
        return guiTop;
    }

    private void loadTextureMetaData() {
        try {
            mGUIbackgroundResource = Minecraft.getMinecraft().getResourceManager().getResource(mGUIbackground);
        }
        catch (IOException ignore) {
        }

        if (mGUIbackgroundResource.hasMetadata()) {
            cmSection = (ColorsMetadataSection) mGUIbackgroundResource.getMetadata("colors");
        }
    }

    protected int getTextColorOrDefault(String textType, int defaultColor) {
        if (cmSection != null && cmSection.sKeyInTextColors(textType)) {
            return cmSection.getTextColorOrDefault(textType, defaultColor);
        }
        return defaultColor;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float parTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(mGUIbackground);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float parTicks) {
        try {
            super.drawScreen(mouseX, mouseY, parTicks);
        } catch (Throwable e) {
            try {
                Tessellator.instance.draw();
            } catch (Throwable f) {
                //
            }
        }
    }

    @Override
    public void handleMouseInput() {
        int delta = Mouse.getEventDWheel();
        if (delta != 0) {
            int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            onMouseWheel(i, j, delta);
        }
        super.handleMouseInput();
    }

    protected void onMouseWheel(int mx, int my, int delta) {
    }

    public boolean isMouseOverSlot(int slotIndex, int mx, int my)
    {
        int size = inventorySlots.inventorySlots.size();
        if (slotIndex < 0 || slotIndex >= size) {
            // slot does not exist somehow. log and carry on
            GT_FML_LOGGER.error("Slot {} required where only {} is present", slotIndex, size);
            return false;
        }
        Slot slot = inventorySlots.getSlot(slotIndex);
        return this.func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mx, my);
    }

    /*
    @Override
    protected void drawSlotInventory(Slot slot) {
        try {
        	super.drawSlotInventory(slot);
        } catch(Throwable e) {
            try {
            	Tessellator.instance.draw();
            } catch(Throwable f) {}
        	if (!mCrashed) {
        		GT_Log.out.println("Clientside Slot drawing Crash prevented. Seems one Itemstack causes Problems with negative Damage Values or the Wildcard Damage Value. This is absolutely NOT a Bug of the GregTech-Addon, so don't even think about reporting it to me, it's a Bug of the Mod, which belongs to the almost-crash-causing Item, so bug that Mods Author and not me! Did you hear it? NOT ME!!!");
        		e.printStackTrace();
            	mCrashed = true;
        	}
        }
    }*/
}
