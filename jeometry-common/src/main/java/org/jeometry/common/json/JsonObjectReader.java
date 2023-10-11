package org.jeometry.common.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.jeometry.common.collection.iterator.AbstractReader;
import org.jeometry.common.util.BaseCloseable;

public class JsonObjectReader extends AbstractReader<JsonObject> {
  private final java.io.Reader in;

  private Iterator<JsonObject> iterator;

  private boolean single = false;

  public JsonObjectReader(final InputStream in) {
    this.in = new InputStreamReader(in, StandardCharsets.UTF_8);
  }

  public JsonObjectReader(final java.io.Reader in) {
    this.in = in;
  }

  public JsonObjectReader(final java.io.Reader in, final boolean single) {
    this.in = in;
    this.single = single;
  }

  @Override
  public void close() {
    BaseCloseable.closeSilent(this.in);
  }

  @Override
  public Iterator<JsonObject> iterator() {
    if (this.iterator == null) {
      try {
        this.iterator = new JsonMapIterator(this.in, this.single);
      } catch (final IOException e) {
        throw new IllegalArgumentException("Unable to create Iterator:" + e.getMessage(), e);
      }
    }
    return this.iterator;
  }

  @Override
  public void open() {
  }
}