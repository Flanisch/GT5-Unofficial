package gregtech.common.gui;

import gregtech.api.gui.GT_GUIContainerMetaTile_Machine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;

import static gregtech.api.enums.GT_Values.RES_PATH_GUI;

public class GT_GUIContainer_MicrowaveEnergyTransmitter extends GT_GUIContainerMetaTile_Machine {

    private final int
        colorText = this.getTextColorOrDefault("text", 0xFAFAFF),
        colorTextTitle = this.getTextColorOrDefault("title", 0xFAFAFF);

    public GT_GUIContainer_MicrowaveEnergyTransmitter(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(new GT_Container_MicrowaveEnergyTransmitter(aInventoryPlayer, aTileEntity), RES_PATH_GUI + "Teleporter.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Teleporter", 46, 8, colorTextTitle);
        if (this.mContainer != null) {
            this.fontRendererObj.drawString("X: " + GT_Utility.parseNumberToString(((GT_Container_MicrowaveEnergyTransmitter) this.mContainer).mTargetX), 46, 16, colorText);
            this.fontRendererObj.drawString("Y: " + GT_Utility.parseNumberToString(((GT_Container_MicrowaveEnergyTransmitter) this.mContainer).mTargetY), 46, 24, colorText);
            this.fontRendererObj.drawString("Z: " + GT_Utility.parseNumberToString(((GT_Container_MicrowaveEnergyTransmitter) this.mContainer).mTargetZ), 46, 32, colorText);
            if (((GT_Container_MicrowaveEnergyTransmitter) this.mContainer).mEgg > 0) {
                this.fontRendererObj.drawString("Dim: " + GT_Utility.parseNumberToString(((GT_Container_MicrowaveEnergyTransmitter) this.mContainer).mTargetD), 46, 40, colorText);
                this.fontRendererObj.drawString("Dim Valid: " + (GT_Utility.isRealDimension(((GT_Container_MicrowaveEnergyTransmitter) this.mContainer).mTargetD) ? "Yes":"No"), 46, 48, colorText);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float parTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(parTicks, mouseX, mouseY);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
