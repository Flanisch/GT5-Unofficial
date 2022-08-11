package gregtech.api.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import gregtech.api.util.GT_Log;

@SideOnly(Side.CLIENT)
public class ColorsMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer {
    public ColorsMetadataSection deserialize(JsonElement mcmetaSection, Type type, JsonDeserializationContext context) {
        JsonObject jsonObject = JsonUtils.getJsonElementAsJsonObject(mcmetaSection, "metadata section");
        String hexColor = JsonUtils.getJsonObjectStringFieldValueOrDefault(jsonObject, "textColor", "");
        GT_Log.err.println("ColorsSerializer: Deserialized");
        return new ColorsMetadataSection(hexColor);
    }

    public JsonElement serialize(ColorsMetadataSection colorsMetaSection, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("textColor", colorsMetaSection.getHexColor());
        GT_Log.err.println("ColorsSerializer: Serialized");
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
