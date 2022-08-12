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
import net.minecraft.util.JsonUtils;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;

@SideOnly(Side.CLIENT)
public class ColorsMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer {
    public ColorsMetadataSection deserialize(JsonElement mcmetaSection, Type type, JsonDeserializationContext context) {
        Map<String, String> hexColorMap = new HashMap<String, String>() {{
            put("title", "");
            put("text", "");
            put("value", "");
        }};
        JsonObject jsonObject = JsonUtils.getJsonElementAsJsonObject(mcmetaSection, "metadata section");
        JsonObject textColors = JsonUtils.func_152754_s(jsonObject, "textColor");
        for (String key : hexColorMap.keySet()) {
            hexColorMap.replace(key, JsonUtils.getJsonObjectStringFieldValueOrDefault(textColors, key, ""));
        }
        return new ColorsMetadataSection(hexColorMap);
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
