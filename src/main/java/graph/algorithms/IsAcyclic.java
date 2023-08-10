package graph.algorithms;

import graph.Graph;
public class IsAcyclic {
    public <N, E> boolean isAcyclic(Graph<N, E> graph) {
        try {
            new Topsort().topsort(graph);
        } catch (Topsort.CycleException e) {
            return false;
        }
        return true;
    }
}
