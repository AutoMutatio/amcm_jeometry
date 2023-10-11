package org.jeometry.common.json;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.jeometry.common.collection.map.MapEx;
import org.jeometry.common.data.type.DataType;
import org.jeometry.common.data.type.DataTypeValueFactory;
import org.jeometry.common.exception.Exceptions;

public interface JsonObject extends MapEx, JsonType {
  JsonObject EMPTY = new JsonObject() {
    @Override
    public JsonObject clone() {
      return this;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
      return Collections.emptySet();
    }

    @Override
    public boolean equals(final Object object) {
      if (object instanceof Map<?, ?>) {
        final Map<?, ?> map = (Map<?, ?>)object;
        return map.isEmpty();
      } else {
        return false;
      }
    }

    @Override
    public boolean equals(final Object object,
      final Collection<? extends CharSequence> excludeFieldNames) {
      return equals(object);
    }

    @Override
    public String toString() {
      return "{}";
    }
  };

  static DataTypeValueFactory<JsonObject> HASH_FACTORY = Json.JSON_OBJECT
    .newFactory(JsonObjectHash::new);

  static DataTypeValueFactory<JsonObject> TREE_FACTORY = Json.JSON_OBJECT
    .newFactory(JsonObjectTree::new);

  static JsonObject hash() {
    return new JsonObjectHash();
  }

  static JsonObject hash(final Map<? extends String, ? extends Object> m) {
    if (m == null) {
      return new JsonObjectHash();
    } else {
      return new JsonObjectHash(m);
    }
  }

  static JsonObject hash(final String key, final Object value) {
    return new JsonObjectHash(key, value);
  }

  static <V> V mapTo(final JsonObject value, final Function<JsonObject, V> mapper) {
    if (value == null) {
      return null;
    } else {
      return mapper.apply(value);
    }
  }

  static JsonObject newItems(final List<?> items) {
    return new JsonObjectHash("items", items);
  }

  static JsonObject parse(final Object value) {
    return JsonParser.read(value);
  }

  static JsonObject tree() {
    return new JsonObjectTree();
  }

  static JsonObject tree(final Map<? extends String, ? extends Object> m) {
    return new JsonObjectTree(m);
  }

  static JsonObject tree(final String key, final Object value) {
    return new JsonObjectTree(key, value);
  }

  @Override
  default JsonObject add(final String key, final Object value) {
    MapEx.super.add(key, value);
    return this;
  }

  @Override
  default JsonObject addFieldValue(final String key, final Map<String, Object> source) {
    final Object value = source.get(key);
    if (value != null || containsKey(key)) {
      addValue(key, value);
    }
    return this;
  }

  @Override
  default JsonObject addFieldValue(final String key, final Map<String, Object> source,
    final String sourceKey) {
    final Object value = source.get(sourceKey);
    if (value != null || containsKey(key)) {
      addValue(key, value);
    }
    return this;
  }

  default JsonObject addFieldValues(final JsonObject source, final DataType dataType,
    final String... fieldNames) {
    for (final String fieldName : fieldNames) {
      final Object value = source.getValue(fieldName, dataType);
      if (value == null) {
        if (source.containsKey(fieldName)) {
          removeValue(fieldName);
        }
      } else {
        addValue(fieldName, value);
      }
    }
    return this;
  }

  default JsonObject addFieldValues(final JsonObject source, final String... fieldNames) {
    for (final String fieldName : fieldNames) {
      final Object value = source.getValue(fieldName);
      if (value == null) {
        if (source.containsKey(fieldName)) {
          removeValue(fieldName);
        }
      } else {
        addValue(fieldName, value);
      }
    }
    return this;
  }

  default JsonObject addNotEmpty(final String key, final Object value) {
    if (org.jeometry.common.util.Property.hasValue(value)) {
      addValue(key, value);
    }
    return this;
  }

  default JsonObject addNotEmpty(final String key, final Object value, final DataType dataType) {
    if (org.jeometry.common.util.Property.hasValue(value)) {
      addValue(key, dataType.toObject(value));
    }
    return this;
  }

  @Override
  default JsonObject addValue(final String key, final Object value) {
    MapEx.super.addValue(key, value);
    return this;
  }

  @Override
  default JsonObject addValue(final String key, final Object value, final DataType dataType) {
    MapEx.super.addValue(key, value, dataType);
    return this;
  }

  default JsonObject addValueClone(final String key, Object value) {
    value = JsonType.toJsonClone(value);
    return addValue(key, value);
  }

  default JsonObject addValues(final MapEx values) {
    if (values != null) {
      for (final String name : values.keySet()) {
        final Object value = values.getValue(name);
        addValue(name, value);
      }
    }
    return this;
  }

  default JsonObject addValuesClone(final MapEx values) {
    if (values != null) {
      for (final String name : values.keySet()) {
        Object value = values.getValue(name);
        if (value != null) {
          value = JsonType.toJsonClone(value);
        }
        addValue(name, value);
      }
    }
    return this;
  }

  @Override
  default Appendable appendJson(final Appendable appendable) {
    try {
      appendable.append('{');
      boolean first = true;
      for (final String key : keySet()) {
        if (first) {
          first = false;
        } else {
          appendable.append(',');
        }
        final Object value = get(key);
        appendable.append('"');
        JsonWriterUtil.charSequence(appendable, key);
        appendable.append("\":");
        JsonWriterUtil.appendValue(appendable, value);
      }
      appendable.append('}');
      return appendable;
    } catch (final IOException e) {
      throw Exceptions.wrap(e);
    }
  }

  @Override
  default JsonObject asJson() {
    return (JsonObject)JsonType.super.asJson();
  }

  @Override
  JsonObject clone();

  default Object getByPath(final String[] names) {
    return getByPath(names, 0);
  }

  default Object getByPath(final String[] names, final int offset) {
    if (offset == names.length) {
      return this;
    } else if (offset < names.length) {
      final String name = names[offset];
      Object value;
      if ("$".equals(name)) {
        value = this;
      } else {
        value = getValue(name);
      }
      if (offset + 1 == names.length) {
        return value;
      } else if (value instanceof JsonObject) {
        final JsonObject object = (JsonObject)value;
        return object.getByPath(names, offset + 1);
      }
    }
    return null;
  }

  @Override
  default boolean isEmpty() {
    return MapEx.super.isEmpty();
  }

  default <V> V mapTo(final Function<JsonObject, V> mapper) {
    return mapper.apply(this);
  }

  @Override
  default boolean removeEmptyProperties() {
    return MapEx.super.removeEmptyProperties();
  }

  @Override
  default JsonObject removeValues(final String... names) {
    MapEx.super.removeValues(names);
    return this;
  }

  default JsonObject renameProperty(final String oldName, final String newName) {
    if (hasValue(oldName) && !oldName.equals(newName)) {
      final Object value = removeValue(oldName);
      addValue(newName, value);
    }
    return this;
  }

  default JsonObject renameProperty(final String oldName, final String newName,
    final DataType dataType) {
    if (hasValue(oldName) && !oldName.equals(newName)) {
      final Object value = removeValue(oldName, dataType);
      addValue(newName, value);
    }
    return this;
  }

  @Override
  default JsonObject toJson() {
    return (JsonObject)JsonType.super.toJson();
  }

  @Override
  default String toJsonString() {
    return Json.toString(this);
  }

  @Override
  default String toJsonString(final boolean indent) {
    return Json.toString(this, indent);
  }

  default JsonObject withNonEmptyValues() {
    JsonObject result = this;
    for (final String key : keySet()) {
      if (!hasValue(key)) {
        if (result == this) {
          result = clone();
        }
        result.remove(key);
      }
    }
    return result;
  }

}