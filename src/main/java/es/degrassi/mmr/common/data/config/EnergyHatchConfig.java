package es.degrassi.mmr.common.data.config;

import es.degrassi.mmr.client.util.EnergyDisplayUtil;
import es.degrassi.mmr.common.block.prop.EnergyHatchSize;
import lombok.Getter;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class EnergyHatchConfig {
  private static final EnergyHatchConfig INSTANCE;
  @Getter
  private static final ModConfigSpec spec;

  static {
    Pair<EnergyHatchConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(EnergyHatchConfig::new);
    INSTANCE = pair.getLeft();
    spec = pair.getRight();
  }

  public static EnergyHatchConfig get() {
    return INSTANCE;
  }

  public final ModConfigSpec.ConfigValue<Long> TINY_energy_size;
  public final ModConfigSpec.ConfigValue<Long> TINY_energy_transferRate;
  public final ModConfigSpec.ConfigValue<Long> SMALL_energy_size;
  public final ModConfigSpec.ConfigValue<Long> SMALL_energy_transferRate;
  public final ModConfigSpec.ConfigValue<Long> NORMAL_energy_size;
  public final ModConfigSpec.ConfigValue<Long> NORMAL_energy_transferRate;
  public final ModConfigSpec.ConfigValue<Long> REINFORCED_energy_size;
  public final ModConfigSpec.ConfigValue<Long> REINFORCED_energy_transferRate;
  public final ModConfigSpec.ConfigValue<Long> BIG_energy_size;
  public final ModConfigSpec.ConfigValue<Long> BIG_energy_transferRate;
  public final ModConfigSpec.ConfigValue<Long> HUGE_energy_size;
  public final ModConfigSpec.ConfigValue<Long> HUGE_energy_transferRate;
  public final ModConfigSpec.ConfigValue<Long> LUDICROUS_energy_size;
  public final ModConfigSpec.ConfigValue<Long> LUDICROUS_energy_transferRate;
  public final ModConfigSpec.ConfigValue<Long> ULTIMATE_energy_size;
  public final ModConfigSpec.ConfigValue<Long> ULTIMATE_energy_transferRate;
  public final ModConfigSpec.ConfigValue<Boolean> energy_displayFETooltip;
  public final ModConfigSpec.ConfigValue<Boolean> energy_displayIC2EUTooltip;
  public final ModConfigSpec.ConfigValue<EnergyDisplayUtil.EnergyType> energy_type;

  public EnergyHatchConfig(ModConfigSpec.Builder builder) {
      builder.push(EnergyHatchSize.TINY.getSerializedName());
      TINY_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.TINY.defaultConfigurationEnergy, 1, Long.MAX_VALUE);
      TINY_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.TINY.defaultConfigurationTransferLimit, 1, Long.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.SMALL.getSerializedName());
      SMALL_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.SMALL.defaultConfigurationEnergy, 1, Long.MAX_VALUE);
      SMALL_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.SMALL.defaultConfigurationTransferLimit, 1, Long.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.NORMAL.getSerializedName());
      NORMAL_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.NORMAL.defaultConfigurationEnergy, 1, Long.MAX_VALUE);
      NORMAL_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.NORMAL.defaultConfigurationTransferLimit, 1, Long.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.REINFORCED.getSerializedName());
      REINFORCED_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.REINFORCED.defaultConfigurationEnergy, 1, Long.MAX_VALUE);
      REINFORCED_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.REINFORCED.defaultConfigurationTransferLimit, 1, Long.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.BIG.getSerializedName());
      BIG_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.BIG.defaultConfigurationEnergy, 1, Long.MAX_VALUE);
      BIG_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.BIG.defaultConfigurationTransferLimit, 1, Long.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.HUGE.getSerializedName());
      HUGE_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.HUGE.defaultConfigurationEnergy, 1, Long.MAX_VALUE);
      HUGE_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.HUGE.defaultConfigurationTransferLimit, 1, Long.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.LUDICROUS.getSerializedName());
      LUDICROUS_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.LUDICROUS.defaultConfigurationEnergy, 1, Long.MAX_VALUE);
      LUDICROUS_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.LUDICROUS.defaultConfigurationTransferLimit, 1, Long.MAX_VALUE);
      builder.pop();
      builder.push(EnergyHatchSize.ULTIMATE.getSerializedName());
      ULTIMATE_energy_size = builder
          .comment("Energy storage size of the energy hatch.")
          .defineInRange("size", EnergyHatchSize.ULTIMATE.defaultConfigurationEnergy, 1, Long.MAX_VALUE);
      ULTIMATE_energy_transferRate = builder
          .comment("Defines the transfer limit for RF/FE things. \nIC2's transfer limit is defined by the voltage tier.")
          .defineInRange("transfer_rate", EnergyHatchSize.ULTIMATE.defaultConfigurationTransferLimit, 1, Long.MAX_VALUE);
      builder.pop();
      builder.push("Display");
      this.energy_displayFETooltip = builder
          .comment("Set to true, if the standard 'energy' FE (or RF) should be displayed in the tooltip of the energy hatch along with its transmission rates.")
          .define("displayFETooltip", true);
      this.energy_displayIC2EUTooltip = builder
          .comment("Set to true, if IC2's energy EU should be displayed in the tooltip of the energy hatch. Will only have effect if IC2 is installed.")
          .define("displayIC2EUTooltip", true);
      this.energy_type = builder
          .comment("Available options: 'FE', 'IC2_EU' - Default: FE - Set this to one of those 2 types to have GUI, recipe preview and energy be displayed in that type of energy in ALL ModularMachinery Reborn things.")
          .defineEnum("type", EnergyDisplayUtil.EnergyType.FE);
      builder.pop();
  }

  public long energySize(EnergyHatchSize size) {
    return switch (size) {
      case TINY -> TINY_energy_size.get();
      case SMALL -> SMALL_energy_size.get();
      case NORMAL -> NORMAL_energy_size.get();
      case REINFORCED -> REINFORCED_energy_size.get();
      case BIG -> BIG_energy_size.get();
      case HUGE -> HUGE_energy_size.get();
      case LUDICROUS -> LUDICROUS_energy_size.get();
      case ULTIMATE -> ULTIMATE_energy_size.get();
    };
  }

  public long energyLimit(EnergyHatchSize size) {
    return switch (size) {
      case TINY -> TINY_energy_transferRate.get();
      case SMALL -> SMALL_energy_transferRate.get();
      case NORMAL -> NORMAL_energy_transferRate.get();
      case REINFORCED -> REINFORCED_energy_transferRate.get();
      case BIG -> BIG_energy_transferRate.get();
      case HUGE -> HUGE_energy_transferRate.get();
      case LUDICROUS -> LUDICROUS_energy_transferRate.get();
      case ULTIMATE -> ULTIMATE_energy_transferRate.get();
    };
  }
}
