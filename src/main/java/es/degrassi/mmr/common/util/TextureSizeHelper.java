package es.degrassi.mmr.common.util;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * A helper class to find the size of a texture, do not call its methods on a dedicated server as it will immediately
 * crash.
 */
public class TextureSizeHelper {
  private TextureSizeHelper() {}

  private static final Logger LOGGER = LogManager.getLogger();
  private static final Map<Identifier, Pair<Integer, Integer>> SIZES = Maps.newHashMap();

  public static int getWidth(@Nullable Identifier texture) {
    if (texture == null)
      return 0;
    if (!texture.getPath().endsWith(".png"))
      texture = texture.withSuffix(".png");
    if (SIZES.containsKey(texture))
      return SIZES.get(texture).getLeft();
    else {
      try {
        BufferedImage image = ImageIO.read(Minecraft.getInstance().getResourceManager().open(texture));
        int width = image.getWidth();
        SIZES.put(texture, Pair.of(width, image.getHeight()));
        return width;
      } catch (IOException e) {
        log(texture);
      }
      return 0;
    }
  }

  public static int getHeight(@Nullable Identifier texture) {
    if (texture == null)
      return 0;
    if (!texture.getPath().endsWith(".png"))
      texture = texture.withSuffix(".png");
    if (SIZES.containsKey(texture))
      return SIZES.get(texture).getRight();
    else {
      try {
        BufferedImage image = ImageIO.read(Minecraft.getInstance().getResourceManager().open(texture));
        SIZES.put(texture, Pair.of(image.getWidth(), image.getHeight()));
        return image.getHeight();
      } catch (IOException e) {
        log(texture);
      }
      return 0;
    }
  }

  private static void log(Identifier texture) {
    LOGGER.warn("No texture found for location: {}", texture);
  }
}
