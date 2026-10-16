package es.degrassi.mmr.common.integration.jade;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.IAutoEntity;
import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.api.entity.IAutoOutputEntity;
import es.degrassi.mmr.common.entity.EffectDispenserEntity;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.util.RomanNumber;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class MachineHatchServerDataProvider implements IServerDataProvider<BlockAccessor> {
  public static final MachineHatchServerDataProvider INSTANCE = new MachineHatchServerDataProvider();
  public static final Identifier ID = ModularMachineryReborn.rl("hatch_server_data_provider");
  @Override
  public void appendServerData(CompoundTag nbt, BlockAccessor accessor) {
    if (accessor.getBlockEntity() instanceof ColorableMachineComponentEntity entity && entity.getLevel() != null) {
      CompoundTag tag = new CompoundTag();
      if (entity instanceof FuelTankEntity fuelTank) {
        CompoundTag fuelTag = new CompoundTag();
        fuelTag.putLong("amount", fuelTank.getFuelHandler().getFuel());
        fuelTag.putLong("capacity", fuelTank.getFuelHandler().getMaxFuel());
        tag.put("fuel", fuelTag);
      }

      if (entity instanceof RedstonePortEntity redstoneEntity) {
        CompoundTag redstoneTag = new CompoundTag();
        redstoneTag.putBoolean("emit", redstoneEntity.getMode().isOutput());
        redstoneTag.putInt(
            "power",
            redstoneEntity.getMode().isOutput()
                ? redstoneEntity.getOutputAmount()
                : redstoneEntity.provideComponent().getContainerProvider()
        );
        tag.put("redstone", redstoneTag);
      }

      if (entity instanceof EffectDispenserEntity effectEntity) {
        CompoundTag effectTag = new CompoundTag();
        effectTag.putBoolean("giving", effectEntity.isApplyingEffect());
        effectTag.putInt("radius", effectEntity.getSize().radius);
        effectTag.putBoolean("interdimensional", effectEntity.getSize().interdimensional);
        effectEntity.getEffect().ifPresent(effect -> {
          MobEffectInstance.CODEC.encodeStart(NbtOps.INSTANCE, effect).result().ifPresent(t -> {
            effectTag.put("instance", t);
            effectTag.putString("level", RomanNumber.toRoman(effect.getAmplifier() + 1));
          });
        });
        tag.put("effect", effectTag);
      }

      if (entity instanceof IAutoEntity<?> auto1) {
        CompoundTag autoTag = new CompoundTag();
        if (entity instanceof IAutoInputEntity) {
          autoTag.putBoolean("input", auto1.shouldAuto());
        }

        if (entity instanceof IAutoOutputEntity) {
          autoTag.putBoolean("output", auto1.shouldAuto());
        }
        tag.put("auto", autoTag);
      }

      nbt.put(ModularMachineryReborn.MODID + ".hatch", tag);
    }
  }

  @Override
  public Identifier getUid() {
    return ID;
  }
}
