package gregtech.api.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.data.IMetadataSection;
import gregtech.api.util.GT_Log;

@SideOnly(Side.CLIENT)
public class ColorsMetadataSection implements IMetadataSection {
    private final int textColor;
    private final String hexColor;

    public ColorsMetadataSection(String hexColor) {
        this.hexColor = hexColor;

        int textColor = 0;
        try {
            GT_Log.err.println("ColorsSerializer: Converting");
            if (!hexColor.isEmpty()) textColor = Integer.parseUnsignedInt(hexColor,16);
        }
        catch (final NumberFormatException e) {
            GT_Log.err.println("Couldn't format color correctly for: " + hexColor);
        }
        GT_Log.err.println("ColorsSerializer: Converted: " + hexColor);

        this.textColor = textColor;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public String getHexColor() {
        return this.hexColor;
    }
}
