package com.wuest.from_the_depths.davoleo;

import com.google.gson.JsonObject;
import com.wuest.from_the_depths.FromTheDepths;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.Set;

@ParametersAreNonnullByDefault
public class TotemTextureLoader implements IResourcePack, IResourceType {

    public static final String DOMAIN = "from_the_depths_totem";
    private final IResourcePack fromTheDepthsRP = FMLClientHandler.instance().getResourcePackFor(FromTheDepths.MODID);

    public static void generateItemModels(String bossKey) throws IOException {
        File modelFile = FromTheDepths.proxy.modDirectory.resolve("models/item/" + bossKey + ".json").toFile();
        boolean wasCreated = modelFile.createNewFile();
        if (wasCreated) {
            JsonObject modelObj = new JsonObject();
            modelObj.addProperty("parent", "item/generated");

            JsonObject texturesObj = new JsonObject();
            texturesObj.addProperty("layer0", "from_the_depths_totem:" + bossKey + ".png");
            modelObj.add("textures", texturesObj);

            BufferedWriter writer = new BufferedWriter(new FileWriter(modelFile));
            writer.close();
        }
        else {
            FromTheDepths.logger.warn("Warning: Item Model File for boss " + bossKey + " couldn't be generated!");
        }
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException
    {
        if (resourceExists(location)) {
            File file = FromTheDepths.proxy.modDirectory.resolve(location.getPath()).toFile();
            return new FileInputStream(file);
        }
        else {
            return fromTheDepthsRP.getInputStream(new ResourceLocation(FromTheDepths.MODID, "textures/items/item_totem_of_summoning.png"));
        }
    }

    @Override
    public boolean resourceExists(ResourceLocation location)
    {
        return FromTheDepths.proxy.modDirectory.resolve(location.getPath()).toFile().exists();
    }

    @Nonnull
    @Override
    public Set<String> getResourceDomains()
    {
        return Collections.singleton(DOMAIN);
    }

    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
    {
        return null;
    }

    @Nonnull
    @Override
    public BufferedImage getPackImage() throws IOException
    {
        InputStream stream = fromTheDepthsRP.getInputStream(new ResourceLocation(FromTheDepths.MODID, "textures/items/item_totem_of_summoning.png"));
        return ImageIO.read(stream);
    }

    @Nonnull
    @Override
    public String getPackName()
    {
        return "Totem Texture Loader";
    }
}
