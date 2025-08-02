/* (C)2025 */
package org.affinity.rdating.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph<T> {

  private final Map<T, List<T>> adjacencyList = new HashMap<>();

  public void addEdge(T from, T to) {
    adjacencyList.putIfAbsent(from, new ArrayList<>());
    adjacencyList.get(from).add(to);
  }

  public List<T> edges(T value) {
    return adjacencyList.getOrDefault(value, new ArrayList<>());
  }

  public List<T> vertices() {
    return new ArrayList<>(adjacencyList.keySet());
  }
}
