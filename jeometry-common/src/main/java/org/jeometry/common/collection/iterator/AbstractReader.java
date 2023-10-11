package org.jeometry.common.collection.iterator;

import org.jeometry.common.util.BaseObjectWithProperties;
import org.jeometry.common.util.Cancellable;

public abstract class AbstractReader<T> extends BaseObjectWithProperties implements Reader<T> {

  private Cancellable cancellable;

  @Override
  public void cancel() {
    if (this.cancellable == null) {
      super.cancel();
    } else {
      this.cancellable.cancel();
    }
  }

  @Override
  public boolean isCancelled() {
    if (this.cancellable == null) {
      return super.isCancelled();
    } else {
      return this.cancellable.isCancelled();
    }
  }

  public AbstractReader<T> setCancellable(final Cancellable cancellable) {
    this.cancellable = cancellable;
    return this;
  }
}