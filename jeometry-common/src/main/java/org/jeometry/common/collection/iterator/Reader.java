/*
 * Copyright 2004-2007 Revolution Systems Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jeometry.common.collection.iterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jeometry.common.collection.map.MapEx;
import org.jeometry.common.json.JsonObject;
import org.jeometry.common.util.BaseCloseable;
import org.jeometry.common.util.Cancellable;
import org.jeometry.common.util.ObjectWithProperties;

/**
 * <p>
 * The Reader interface defines methods for reading objects of type T. Objects
 * can either by read as a {@link List} or using an {@link Iterator} or visited
 * using a {@link Consumer}.
 * </p>
 * <p>
 * The simplest and most effecient way to loop through all objects in the reader
 * is to use the following loop.
 * </p>
 *
 * <pre>
 * Reader&lt;T&gt; reader = ...
 * for (T object : reader) {
 *   // Do something with the object.
 * }
 * </pre>
 *
 * @author Paul Austin
 * @param <T> The type of the item to read.
 */
public interface Reader<T>
  extends BaseIterable<T>, ObjectWithProperties, BaseCloseable, Cancellable {
  Reader<?> EMPTY = wrap(Collections.emptyIterator());

  @SuppressWarnings("unchecked")
  static <V> Reader<V> empty() {
    return (Reader<V>)EMPTY;
  }

  static <V> Reader<V> wrap(final Iterator<V> iterator) {
    return new IteratorReader<>(iterator);
  }

  /**
   * Close the reader and all resources associated with it.
   */
  @Override
  default void close() {
  }

  @Override
  default Reader<T> filter(final Predicate<? super T> filter) {
    final Iterator<T> iterator = iterator();
    return new FilterIterator<>(filter, iterator);
  }

  default <O> Reader<O> filter(final Predicate<T> filter, final Function<T, O> converter) {
    return filter(filter).map(converter);
  }

  default void forEach(final BiConsumer<Cancellable, ? super T> action) {
    forEach(this, action);
  }

  @Override
  default MapEx getProperties() {
    return JsonObject.EMPTY;
  }

  @Override
  @SuppressWarnings("unchecked")
  default <V extends T> Iterable<V> i() {
    return (Iterable<V>)this;
  }

  @Override
  default boolean isCancelled() {
    return false;
  }

  @Override
  default <O> Reader<O> map(final Function<? super T, O> converter) {
    final var iterator = iterator();
    return new IteratorConvertReader<>(iterator, converter);
  }

  /**
   * Open the reader so that it is ready to be read from.
   */
  default void open() {
  }

}