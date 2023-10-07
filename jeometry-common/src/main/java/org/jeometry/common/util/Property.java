package org.jeometry.common.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.jeometry.common.data.type.DataTypes;
import org.jeometry.common.exception.Exceptions;

public interface Property {

  static boolean hasValue(final CharSequence string) {
    if (string != null) {
      final int length = string.length();
      for (int i = 0; i < length; i++) {
        final char character = string.charAt(i);
        if (!Character.isWhitespace(character)) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean hasValue(final Collection<?> collection) {
    if (collection == null || collection.isEmpty()) {
      return false;
    } else {
      return true;
    }
  }

  static boolean hasValue(final Emptyable value) {
    if (value == null) {
      return false;
    } else {
      return !value.isEmpty();
    }
  }

  static boolean hasValue(final Object value) {
    if (value == null) {
      return false;
    } else if (value instanceof String) {
      final String string = (String)value;
      return hasValue(string);
    } else if (value instanceof CharSequence) {
      final CharSequence string = (CharSequence)value;
      return hasValue(string);
    } else if (value instanceof Collection<?>) {
      final Collection<?> collection = (Collection<?>)value;
      return !collection.isEmpty();
    } else if (value instanceof Map<?, ?>) {
      final Map<?, ?> map = (Map<?, ?>)value;
      return !map.isEmpty();
    } else if (value instanceof Emptyable) {
      final Emptyable emptyable = (Emptyable)value;
      return !emptyable.isEmpty();
    } else {
      return true;
    }
  }

  static boolean hasValue(final Object[] array) {
    if (array == null || array.length > 1) {
      return false;
    } else {
      return true;
    }
  }

  static boolean hasValue(final String string) {
    if (string != null) {
      return !string.isBlank();
    }
    return false;
  }

  static boolean isEmpty(final Emptyable value) {
    if (value == null) {
      return true;
    } else {
      return value.isEmpty();
    }
  }

  static boolean isEmpty(final Object value) {
    if (value == null) {
      return true;
    } else if (value instanceof String) {
      final String string = (String)value;
      return !hasValue(string);
    } else if (value instanceof CharSequence) {
      final CharSequence string = (CharSequence)value;
      return !hasValue(string);
    } else if (value instanceof Collection<?>) {
      final Collection<?> collection = (Collection<?>)value;
      return collection.isEmpty();
    } else if (value instanceof Map<?, ?>) {
      final Map<?, ?> map = (Map<?, ?>)value;
      return map.isEmpty();
    } else if (value instanceof Emptyable) {
      final Emptyable emptyable = (Emptyable)value;
      return emptyable.isEmpty();
    } else {
      return false;
    }
  }

  static boolean isEmpty(final Object[] value) {
    if (value == null || value.length == 0) {
      return true;
    } else {
      return false;
    }
  }

  static boolean isEmpty(final String string) {
    if (string == null) {
      return true;
    } else {
      return string.isBlank();
    }
  }

  @SuppressWarnings("unchecked")
  static <T> T getSimple(final Object object, final String propertyName) {
    final PropertyDescriptor propertyDescriptor = PropertyDescriptorCache
      .getPropertyDescriptor(object, propertyName);
    if (propertyDescriptor != null) {
      final Method readMethod = propertyDescriptor.getReadMethod();
      if (readMethod == null) {
        return null;
      } else {
        try {
          return (T)readMethod.invoke(object);
        } catch (IllegalAccessException | IllegalArgumentException e) {
          Exceptions.throwUncheckedException(e);
        } catch (final InvocationTargetException e) {
          final Throwable targetException = e.getTargetException();
          Exceptions.throwUncheckedException(targetException);
        }
      }
    }
    return null;
  }

  /**
   *
   *
   * @param object
   * @param propertyName
   * @param value
   * @return True if the property existed.
   */
  static boolean setSimple(final Object object, final String propertyName,
    final Object value) {
    final PropertyDescriptor propertyDescriptor = PropertyDescriptorCache
      .getPropertyDescriptor(object, propertyName);
    if (propertyDescriptor != null) {
      final Class<?> propertyType = propertyDescriptor.getPropertyType();
      final Method writeMethod = propertyDescriptor.getWriteMethod();
      if (writeMethod != null) {
        Object convertedValue = DataTypes.toObject(propertyType, value);
        if (convertedValue == null && propertyType.isPrimitive()) {
          if (Number.class.isAssignableFrom(propertyType)) {
            convertedValue = DataTypes.toObject(propertyType, 0);
          } else if (Boolean.TYPE.equals(propertyType)) {
            convertedValue = false;
          } else if (Character.TYPE.equals(propertyType)) {
            convertedValue = ' ';
          }
        }
        try {
          writeMethod.invoke(object, convertedValue);
        } catch (final IllegalArgumentException e) {
          throw Exceptions.wrap("Invalid value: " + propertyName + "=" + convertedValue, e);
        } catch (final IllegalAccessException e) {
          Exceptions.throwUncheckedException(e);
        } catch (final InvocationTargetException e) {
          final Throwable targetException = e.getTargetException();
          Exceptions.throwUncheckedException(targetException);
        }
        return true;
      }
    }
    return false;
  }
}
