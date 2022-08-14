package gregtech.api.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.HashMap;
import gregtech.api.enums.Dyes;
import net.minecraft.util.JsonUtils;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;

@SideOnly(Side.CLIENT)
public class ColorsMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer {
    public ColorsMetadataSection deserialize(JsonElement metadataColors, Type type, JsonDeserializationContext context) {
        Map<String, String> hexTextColorMap = new HashMap<String, String>() {{
            put("title", "");
            put("text", "");
            put("value", "");
            put("nei", "");
        }};

        JsonObject jsonObject = JsonUtils.getJsonElementAsJsonObject(metadataColors, "metadata section");
        if (jsonObject.has("textColor")) {
            JsonObject textColors = JsonUtils.func_152754_s(jsonObject, "textColor");
            for (String key : hexTextColorMap.keySet()) {
                hexTextColorMap.replace(key, JsonUtils.getJsonObjectStringFieldValueOrDefault(textColors, key, ""));
            }
        }

        Map<String, String> hexGuiTintMap = new HashMap<String, String>() {{
            put("Black", GT_Util.getRGBHex(Dyes.dyeBlack.getRGBA()));
            put("Red", GT_Util.getRGBHex(Dyes.dyeRed.getRGBA()));
            put("Green", GT_Util.getRGBHex(Dyes.dyeGreen.getRGBA()));
            put("Brown", GT_Util.getRGBHex(Dyes.dyeBrown.getRGBA()));
            put("Blue", GT_Util.getRGBHex(Dyes.dyeBlue.getRGBA()));
            put("Purple", GT_Util.getRGBHex(Dyes.dyePurple.getRGBA()));
            put("Cyan", GT_Util.getRGBHex(Dyes.dyeCyan.getRGBA()));
            put("Light Gray", GT_Util.getRGBHex(Dyes.dyeLightGray.getRGBA()));
            put("Gray", GT_Util.getRGBHex(Dyes.dyeGray.getRGBA()));
            put("Pink", GT_Util.getRGBHex(Dyes.dyePink.getRGBA()));
            put("Lime", GT_Util.getRGBHex(Dyes.dyeLime.getRGBA()));
            put("Yellow", GT_Util.getRGBHex(Dyes.dyeYellow.getRGBA()));
            put("Light Blue", GT_Util.getRGBHex(Dyes.dyeLightBlue.getRGBA()));
            put("Magenta", GT_Util.getRGBHex(Dyes.dyeMagenta.getRGBA()));
            put("Orange", GT_Util.getRGBHex(Dyes.dyeOrange.getRGBA()));
            put("White", GT_Util.getRGBHex(Dyes.dyeWhite.getRGBA()));
            put("Machine Metal", GT_Util.getRGBHex(Dyes.MACHINE_METAL.getRGBA()));
        }};
        boolean enableGuiTint = true;

        if (jsonObject.has("guiTint")) {
            JsonObject guiTints = JsonUtils.func_152754_s(jsonObject, "guiTint");
            enableGuiTint = JsonUtils.getJsonObjectBooleanFieldValueOrDefault(guiTints, "enableGuiTintWhenPainted", true);
            for (String key : hexGuiTintMap.keySet()) {
                if (enableGuiTint) {
                    GT_Log.out.println("GuiTints: replacing " + hexGuiTintMap.get(key) + " -> " + JsonUtils.getJsonObjectStringFieldValueOrDefault(guiTints, key, hexGuiTintMap.get(key)) + "(" + key + ")");
                    hexGuiTintMap.replace(key, JsonUtils.getJsonObjectStringFieldValueOrDefault(guiTints, key, hexGuiTintMap.get(key)));
                }
                else {
                    hexGuiTintMap.replace(key, GT_Util.getRGBHex(Dyes.dyeWhite.getRGBA()));
                }
            }
        }

        return new ColorsMetadataSection(hexTextColorMap, hexGuiTintMap, enableGuiTint);
    }

    public JsonElement serialize(ColorsMetadataSection colorsMetaSection, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("textColor", colorsMetaSection.getHexColor());
        return jsonObject;
    }

    public String getSectionName()
    {
        return "colors";
    }

    public JsonElement serialize(Object object, Type type, JsonSerializationContext context) {
        return this.serialize((ColorsMetadataSection) object, type, context);
    }
}
