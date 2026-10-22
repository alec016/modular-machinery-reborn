package es.degrassi.mmr.common.manager.handler;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class MergedEnergyHandler extends SimpleEnergyHandler {
  private final SimpleEnergyHandler[] sources;
  public MergedEnergyHandler(SimpleEnergyHandler...sources) {
    super(
        totalSize(sources),
        maxOperation(sources, true),
        maxOperation(sources, false)
    );
    this.sources = sources;
  }

  @Override
  public void setMaxExtract(long maxExtract) {
    super.setMaxExtract(maxExtract);
    for (var source : sources) source.setMaxExtract(maxExtract);
  }

  @Override
  public void setMaxInsert(long maxInsert) {
    super.setMaxInsert(maxInsert);
    for (var source : sources) source.setMaxInsert(maxInsert);
  }

  @Override
  public int insert(int amount, TransactionContext transaction) {
    int inserted = 0;
    for (var source : sources) {
      var sim = source.insert(amount, transaction);
      amount -= sim;
      inserted += sim;
      if (amount <= 0) break;
    }
    return inserted;
  }

  @Override
  public int extract(int amount, TransactionContext transaction) {
    int extracted = 0;
    for (var source : sources) {
      var sim = source.extract(amount, transaction);
      amount -= sim;
      extracted += sim;
      if (amount <= 0) break;
    }
    return extracted;
  }

  private static long totalSize(SimpleEnergyHandler... sources) {
    long size = 0;
    for (SimpleEnergyHandler handler : sources) size += handler.getCapacityAsLong();
    return size;
  }

  private static long maxOperation(SimpleEnergyHandler[] sources, boolean insert) {
    long limit = 0;
    for (SimpleEnergyHandler handler : sources) {
      limit = Math.max(limit, insert ? handler.maxInsert : handler.maxExtract);
    }
    return limit;
  }

  @Override
  public void serialize(ValueOutput output) {
    super.serialize(output);
    for (int i = 0; i < sources.length; i++) {
      output.putChild("source%s".formatted(i), sources[i]);
    }
  }

  @Override
  public void deserialize(ValueInput input) {
    super.deserialize(input);
    for (int i = 0; i < sources.length; i++) {
      input.readChild("sources%s".formatted(i), sources[i]);
    }
  }
}
