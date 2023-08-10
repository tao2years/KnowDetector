package graph.algorithms;

import graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Components {

    public <N, E> List<Graph<N, E>> getComponents(Graph<N, E> graph) {

        Map<String, Boolean> visited = new HashMap<>();
        List<Graph<N, E>> graphs = new ArrayList<>();

        graph.getNodes().forEach(nodeId -> {
            Graph<N, E> component = new Graph<>();
            findComponent(visited, graph, nodeId, component);
            if (component.nodeCount() > 0) {
                graphs.add(component);
            }
        });
        return graphs;
    }

    private <N, E> void findComponent(Map<String, Boolean> visited, Graph<N, E> graph, String nodeId, Graph<N, E> component) {
        if (visited.containsKey(nodeId))
            return;
        visited.put(nodeId, true);

        component.setNode(nodeId, graph.getNode(nodeId));

        graph.successors(nodeId).forEach(successor -> {
            findComponent(visited, graph, successor, component);
            component.setEdge(nodeId, successor, graph.getEdge(nodeId, successor));
//            System.out.println("--------- \n" + graph.getEdge(nodeId, successor)+ "\n---------");
        });
        graph.predecessors(nodeId).forEach(predecessor -> {
            findComponent(visited, graph, predecessor, component);
            component.setEdge(predecessor, nodeId, graph.getEdge(predecessor, nodeId));
        });
    }

}
