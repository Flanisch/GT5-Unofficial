package gregtech.api.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Map;
import java.util.HashMap;
import gregtech.api.util.GT_Log;
import net.minecraft.client.resources.data.IMetadataSection;

@SideOnly(Side.CLIENT)
public class ColorsMetadataSection implements IMetadataSection {
    private final Map<String, Integer> textColors;
    private final Map<String, String> hexColors;

    public ColorsMetadataSection(Map<String, String> hexColorMap) {
        this.hexColors = hexColorMap;
        Map<String, Integer> textColorMap = new HashMap<>();

        for (String key : hexColors.keySet()) {
            int colorValue = -1;
            String hex = hexColors.get(key);
            try {
                if (!hex.isEmpty()) colorValue = Integer.parseUnsignedInt(hex,16);
            }
            catch (final NumberFormatException e) {
                GT_Log.err.println("Couldn't format color correctly for: " + hex);
            }
            GT_Log.out.println("ColorsSerializer: Converted: " + hex + " -> " + colorValue); // Debug

            textColorMap.put(key, colorValue);
        }

        GT_Log.out.println("ColorsSerializer textColorMap: "); // Debug
        GT_Log.out.println(textColorMap);
        this.textColors = textColorMap;
    }

    public int getTextColorOrDefault(String key, int defaultColor) {
        GT_Log.out.println("ColorsMetadataSection: key -> " + this.textColors.get(key));
        return this.textColors.get(key) == -1 ? defaultColor : this.textColors.get(key);
    }

    public boolean sKeyInTextColors(String key) {
        return this.textColors.get(key) != -1;
    }

    public Map<String, String> getHexColorMap() {
        return this.hexColors;
    }

    public String getHexColorOrDefault(String key, String defaultHex) {
        return this.hexColors.get(key).isEmpty() ? defaultHex : this.hexColors.get(key);
    }
}
