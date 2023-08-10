package model;

import graph.Graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GraphMapPair {
    Graph<Object, Object> userGraph = new Graph<>(true,true,false);
    Map<String, Set<String>> map = new HashMap<>();

    public GraphMapPair(Graph<Object, Object> userGraph, Map<String, Set<String>> map) {
        this.userGraph = userGraph;
        this.map = map;
    }

    public Graph<Object, Object> getUserGraph() {
        return userGraph;
    }

    public void setUserGraph(Graph<Object, Object> userGraph) {
        this.userGraph = userGraph;
    }

    public Map<String, Set<String>> getMap() {
        return map;
    }

    public void setMap(Map<String, Set<String>> map) {
        this.map = map;
    }
}
