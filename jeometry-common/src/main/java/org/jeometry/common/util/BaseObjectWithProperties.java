package org.jeometry.common.util;

import org.jeometry.common.collection.map.MapEx;
import org.jeometry.common.collection.map.Maps;
import org.jeometry.common.exception.Exceptions;
import org.jeometry.common.json.JsonObject;

public class BaseObjectWithProperties implements ObjectWithProperties {
  private MapEx properties = JsonObject.hash();

  private boolean cancelled = false;

  public BaseObjectWithProperties() {
  }

  public void cancel() {
    this.cancelled = true;
  }

  @Override
  protected BaseObjectWithProperties clone() {
    try {
      final BaseObjectWithProperties clone = (BaseObjectWithProperties)super.clone();
      clone.properties = Maps.newLinkedHashEx(this.properties);
      return clone;
    } catch (final CloneNotSupportedException e) {
      return Exceptions.throwUncheckedException(e);
    }
  }

  @Override
  public void close() {
    clearProperties();
  }

  @Override
  public MapEx getProperties() {
    return this.properties;
  }

  public boolean isCancelled() {
    return this.cancelled;
  }

}
