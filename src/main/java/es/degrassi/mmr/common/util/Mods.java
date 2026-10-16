package es.degrassi.mmr.common.util;

import net.neoforged.fml.ModList;

public interface Mods {
  static boolean isAthenaLoaded() {
    return ModList.get().isLoaded("athena");
  }

  static boolean isJEILoaded() {
    return ModList.get().isLoaded("jei");
  }

  static boolean isLDLibLoaded() {
    return ModList.get().isLoaded("ldlib2");
  }

  static boolean shouldAddExpHandlerToAe2() {
    return ModList.get().isLoaded("ae2") && ModList.get().isLoaded("appex");
  }
}
