package com.wuest.from_the_depths.davoleo;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * GSON Type Adapter for the {@link ResourceLocation} class
 */
public class ResourceLocationTypeAdapter extends TypeAdapter<ResourceLocation> {

    @Override
    public void write(JsonWriter out, ResourceLocation value) throws IOException
    {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.toString());
    }

    @Override
    public ResourceLocation read(JsonReader in) throws IOException
    {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return new ResourceLocation(in.nextString());
    }

}
