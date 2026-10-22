
package es.degrassi.mmr.common.manager.handler;

import com.google.common.primitives.Ints;
import es.degrassi.experiencelib.util.TransferPreconditions;
import es.degrassi.mmr.common.util.InventoryUpdateListener;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SimpleEnergyHandler implements EnergyHandler, ValueIOSerializable {
  protected long energy;
  protected long capacity;
  @Setter
  @Getter
  protected long maxInsert;
  @Setter
  @Getter
  protected long maxExtract;

  private final EnergyJournal energyJournal = new EnergyJournal();
  private static final String ENERGY = "#";
  private static final String CAPACITY = "c";
  private static final String MAX_INSERT = "i";
  private static final String MAX_EXTRACT = "e";
  @Setter
  private InventoryUpdateListener listener;
  public SimpleEnergyHandler(long capacity, long maxInsert, long maxExtract) {
    this(capacity, maxInsert, maxExtract, 0);
  }

  public SimpleEnergyHandler(long capacity) {
    this(capacity, capacity);
  }

  public SimpleEnergyHandler(long capacity, long transfer) {
    this(capacity, transfer, transfer);
  }

  public SimpleEnergyHandler(long capacity, long maxInsert, long maxExtract, long energy) {
    TransferPreconditions.checkNonNegative(capacity, maxInsert, maxExtract, energy);
    this.energy = energy;
    this.capacity = capacity;
    this.maxInsert = maxInsert;
    this.maxExtract = maxExtract;
  }

  /**
   * Directly overwrites the energy amount of the handler.
   *
   * @throws IllegalArgumentException if the amount is negative
   */
  public void set(long amount) {
    TransferPreconditions.checkNonNegative(amount);

    if (this.energy != amount) {
      long previousAmount = this.energy;
      this.energy = amount;
      onEnergyChanged(previousAmount);
    }
  }

  protected void onEnergyChanged(long previousAmount) {
    if (listener != null) {
      listener.onChange();
    }
  }

  @Override
  public long getAmountAsLong() {
    return energy;
  }

  @Override
  public long getCapacityAsLong() {
    return capacity;
  }

  @Override
  public int insert(int amount, TransactionContext transaction) {
    TransferPreconditions.checkNonNegative(amount);

    long inserted = Math.min(capacity - energy, Math.min(amount, maxInsert));
    if (inserted > 0) {
      energyJournal.updateSnapshots(transaction);
      energy += inserted;
      return Ints.saturatedCast(inserted);
    }
    return 0;
  }

  @Override
  public int extract(int amount, TransactionContext transaction) {
    TransferPreconditions.checkNonNegative(amount);

    long extracted = Math.min(energy, Math.min(amount, maxExtract));
    if (extracted > 0) {
      energyJournal.updateSnapshots(transaction);
      energy -= extracted;
      return Ints.saturatedCast(extracted);
    }
    return 0;
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putLong(ENERGY, getAmountAsLong());
    output.putLong(CAPACITY, getCapacityAsLong());
    output.putLong(MAX_INSERT, maxInsert);
    output.putLong(MAX_EXTRACT, maxExtract);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.energy = input.getLongOr(ENERGY, 0);
    this.capacity = input.getLongOr(CAPACITY, 0);
    this.maxInsert = input.getLongOr(MAX_INSERT, 0);
    this.maxExtract = input.getLongOr(MAX_EXTRACT, 0);
  }

  private class EnergyJournal extends SnapshotJournal<Long> {
    @Override
    protected Long createSnapshot() {
      return energy;
    }

    @Override
    protected void revertToSnapshot(Long snapshot) {
      energy = snapshot;
    }

    @Override
    protected void onRootCommit(Long originalState) {
      long previousAmount = originalState;
      if (energy != previousAmount) {
        onEnergyChanged(previousAmount);
      }
    }
  }
}
