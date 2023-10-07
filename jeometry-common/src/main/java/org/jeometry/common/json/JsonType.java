package org.jeometry.common.json;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.jeometry.common.collection.map.MapEx;
import org.jeometry.common.data.type.DataTypedValue;
import org.jeometry.common.exception.Exceptions;
import org.jeometry.common.util.BaseCloneable;

public interface JsonType extends DataTypedValue, Jsonable, BaseCloneable {

  @SuppressWarnings("unchecked")
  static <V> V toJsonClone(final V value) {
    if (value == null) {
      return null;
    } else if (value instanceof MapEx) {
      final MapEx map = (MapEx)value;
      return (V)JsonObject.hash().addValuesClone(map);
    } else if (value instanceof Map) {
      final Map<String, Object> map = (Map)value;
      return (V)map;
    } else if (value instanceof List) {
      final List<?> list = (List<?>)value;
      return (V)JsonList.array().addValuesClone(list);
    } else if (value instanceof BaseCloneable) {
      return (V)((BaseCloneable)value).clone();
    } else if (value instanceof Cloneable) {
      try {
        final Class<? extends Object> valueClass = value.getClass();
        if (!valueClass.isArray()) {
          final Method method = valueClass.getMethod("clone", BaseCloneable.ARRAY_CLASS_0);
          if (method != null) {
            return (V)method.invoke(value, BaseCloneable.ARRAY_OBJECT_0);
          }
        }
      } catch (final Throwable e) {
        return Exceptions.throwUncheckedException(e);
      }
    }
    return value;
  }

  @Override
  Appendable appendJson(Appendable appendable);

  @Override
  default JsonType asJson() {
    return this;
  }

  @Override
  JsonType clone();

  boolean isEmpty();

  boolean removeEmptyProperties();

  @Override
  default JsonType toJson() {
    return clone();
  }

}
