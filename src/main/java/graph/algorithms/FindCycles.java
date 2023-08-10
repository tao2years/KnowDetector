package graph.algorithms;

import graph.Graph;

import java.util.List;
import java.util.stream.Collectors;

public class FindCycles {
    public <N, E> List<List<String>> findCycles(Graph<N, E> graph) {
        return new Tarjan().tarjan(graph)
                .stream()
                .filter(list -> list.size() > 1 || (list.size() == 1 && graph.hasEdge(list.get(0), list.get(0))))
                .collect(Collectors.toList());
    }
}
