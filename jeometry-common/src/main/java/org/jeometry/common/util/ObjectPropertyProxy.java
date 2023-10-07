package org.jeometry.common.util;

public interface ObjectPropertyProxy<T, O> {
  void clearValue();

  T getValue(final O object);
}
