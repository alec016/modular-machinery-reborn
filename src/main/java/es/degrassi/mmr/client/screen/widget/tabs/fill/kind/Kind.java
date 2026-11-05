package es.degrassi.mmr.client.screen.widget.tabs.fill.kind;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.screen.widget.tabs.TabTexts;
import es.degrassi.mmr.client.screen.widget.tabs.fill.AbstractFillTab;
import es.degrassi.mmr.client.screen.widget.tabs.fill.pick.Pick;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class Kind<PICK extends Pick, FILTER> {
  protected static final int MAX_CANDIDATES = 90;
  private final Identifier id;
  private final int height;
  private final TabTexts text;
  private final boolean hasPick;

  @Nullable
  public List<FILTER> displayCache = null;

  public Kind(String path, int height, TabTexts text, boolean hasPick) {
    this(mmr(path), height, text, hasPick);
  }
  public Kind(String path, int height, boolean hasPick) {
    this(path, height, TabTexts.fillReasonFull, hasPick);
  }
  public Kind(String path, TabTexts text, boolean hasPick) {
    this(path, AbstractFillTab.FILL_SIMPLE_OPEN_HEIGHT, text, hasPick);
  }
  public Kind(String path, boolean hasPick) {
    this(path, AbstractFillTab.FILL_SIMPLE_OPEN_HEIGHT, TabTexts.fillReasonFull, hasPick);
  }

  public static Identifier mmr(String path) {
    return ModularMachineryReborn.rl(path);
  }

  protected boolean matches(boolean byMod, String name, Identifier id, String needle) {
    if (byMod) {
      return id.getNamespace().contains(needle);
    }
    return needle.isEmpty()
        || name.toLowerCase(Locale.ROOT).contains(needle)
        || id.getPath().contains(needle);
  }

  protected boolean tagMatches(Identifier location, String needle) {
    return needle.isEmpty()
        || location.getPath().contains(needle)
        || location.getNamespace().contains(needle)
        || tagExtracted(location, needle);
  }

  protected boolean tagExtracted(Identifier location, String needle) {
    var s = needle.split(":");
    if (s.length < 2) return false;
    return location.getNamespace().equals(s[0])
        && location.getPath().startsWith(s[1]);
  }

  public void candidates(List<PICK> result, boolean byMod, boolean byTag, String needle, BlockEntity entity, @Nullable Player player) {}
}
