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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jeometry.common.collection.value.Single;
import org.jeometry.common.util.BaseCloseable;
import org.jeometry.common.util.Cancellable;
import org.jeometry.common.util.ExitLoopException;

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
public interface BaseIterable<T> extends Iterable<T> {

  default BaseCloseable closeable() {
    if (this instanceof final BaseCloseable closeable) {
      return closeable;
    } else {
      return BaseCloseable.EMPTY;
    }
  }

  default BaseIterable<T> filter(final Predicate<? super T> filter) {
    return new FilterIterator<T>(filter, iterator());
  }

  default Single<T> first() {
    return Single.ofNullable(getFirst());
  }

  default void forEach(final Cancellable cancellable,
    final BiConsumer<Cancellable, ? super T> action) {
    try (
      var c = closeable()) {
      if (iterator() != null) {
        try {
          for (final T item : this) {
            if (cancellable.isCancelled()) {
              return;
            } else {
              action.accept(cancellable, item);
            }
          }
        } catch (final ExitLoopException e) {
        }
      }
    }
  }

  default int forEach(final Cancellable cancellable, final Consumer<? super T> action) {
    int i = 0;
    try (
      var c = closeable()) {
      if (iterator() != null) {
        try {
          for (final T item : this) {
            if (cancellable.isCancelled()) {
              return -1;
            } else {
              action.accept(item);
            }
            i++;
          }
        } catch (final ExitLoopException e) {
        }
      }
    }
    return i;
  }

  /**
   * Visit each item returned from the reader until all items have been visited
   * or the visit method returns false.
   *
   * @param visitor The visitor.
   */
  @Override
  default void forEach(final Consumer<? super T> action) {
    try (
      var c = closeable()) {
      if (iterator() != null) {
        try {
          for (final T item : this) {
            action.accept(item);
          }
        } catch (final ExitLoopException e) {
        }
      }
    }
  }

  default T getFirst() {
    try (
      var c = closeable()) {
      final Iterator<T> iterator = iterator();
      if (iterator.hasNext()) {
        return iterator.next();
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  default <V extends T> Iterable<V> i() {
    return (Iterable<V>)this;
  }

  default <V> BaseIterable<V> map(final Function<? super T, V> converter) {
    return new MapIterator<>(iterator(), converter);
  }

  default Stream<T> parallelStream() {
    return StreamSupport.stream(spliterator(), true);
  }

  default void skipAll() {
    for (final Iterator<T> iterator = iterator(); iterator.hasNext();) {
    }
  }

  default Stream<T> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  /**
   * Read all items and return a List containing the items.
   *
   * @return The list of items.
   */
  default List<T> toList() {
    final List<T> items = new ArrayList<>();
    forEach(i -> items.add(i));
    return items;
  }
}
