package com.eyespage.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;

/**
 * Created by cylee on 15/4/20.
 */
public class SyncLongSparseArray <E> extends LongSparseArray {
  public SyncLongSparseArray() {
    super();
  }

  public SyncLongSparseArray(int initialCapacity) {
    super(initialCapacity);
  }

  @Override public synchronized LongSparseArray clone() {
    return super.clone();
  }

  @Override public synchronized Object get(long key) {
    return super.get(key);
  }

  @Override public synchronized Object get(long key, Object valueIfKeyNotFound) {
    return super.get(key, valueIfKeyNotFound);
  }

  @Override public synchronized void delete(long key) {
    super.delete(key);
  }

  @Override public synchronized void remove(long key) {
    super.remove(key);
  }

  @Override public synchronized void removeAt(int index) {
    super.removeAt(index);
  }

  @Override public synchronized void put(long key, Object value) {
    super.put(key, value);
  }

  @Override public synchronized int size() {
    return super.size();
  }

  @Override public synchronized long keyAt(int index) {
    return super.keyAt(index);
  }

  @NonNull @Override synchronized public Object valueAt(int index) {
    return super.valueAt(index);
  }

  @Override public synchronized void setValueAt(int index, Object value) {
    super.setValueAt(index, value);
  }

  @Override public synchronized int indexOfKey(long key) {
    return super.indexOfKey(key);
  }

  @Override public synchronized int indexOfValue(Object value) {
    return super.indexOfValue(value);
  }

  @Override public synchronized void clear() {
    super.clear();
  }

  @Override public synchronized void append(long key, Object value) {
    super.append(key, value);
  }

  @Override public synchronized String toString() {
    return super.toString();
  }
}
