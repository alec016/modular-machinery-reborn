package es.degrassi.mmr.client.screen.widget.tabs.fill.kind;

import es.degrassi.mmr.client.screen.widget.tabs.TabTexts;
import es.degrassi.mmr.client.screen.widget.tabs.fill.pick.Pick;

public abstract class NumberKind extends Kind<Pick, Object> {
  protected NumberKind(String path, int height, TabTexts text) {
    super(path, height, text, false);
  }
  protected NumberKind(String path, TabTexts text) {
    super(path, text, false);
  }
  protected NumberKind(String path) {
    super(path, false);
  }
}
