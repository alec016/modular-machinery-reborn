package es.degrassi.mmr.client.screen.widget.tabs;

import es.degrassi.mmr.common.block.prop.ConfigLoaded;
import es.degrassi.mmr.common.data.MMRClientConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.neoforged.fml.config.IConfigSpec;

@RequiredArgsConstructor
@Accessors(fluent = true)
public enum AutoIOConfig implements ConfigLoaded {
  CONFIG(74, 124, 240)
  ;
  @Getter
  public int width, height;
  @Getter
  public long ms;
  public final int defaultWidth, defaultHeight;
  public final long defaultMs;

  @Override
  public IConfigSpec get() {
    return MMRClientConfig.getSpec();
  }
}
