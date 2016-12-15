package nl.twente.bms.struct;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import grph.algo.distance.DistanceMatrix;
import grph.in_memory.InMemoryGrph;
import grph.path.Path;
import grph.path.SearchResultWrappedPath;
import grph.properties.NumericalProperty;
import nl.twente.bms.algo.DijkstraHeapAlgorithm;

/**
 * The class to store the station distance weighted graph
 *
 * @author Feng Zhao (feng.zhao@feedzai.com)
 * @since 1.0
 */
public class StationGraph extends InMemoryGrph {
    private final NumericalProperty weightProperty;
    private final Table<Integer, Integer, Integer> directDistanceTable;

    private DistanceMatrix shortestDistances;

    public StationGraph() {
        weightProperty = new NumericalProperty("weight", 16, 65535);
        directDistanceTable = HashBasedTable.create();
    }

    public void computeAllSourceShortestDistances(){
        shortestDistances = new DijkstraHeapAlgorithm(getWeightProperty()).computeDistanceMatrix(this);
    }

    public NumericalProperty getWeightProperty() {
        return weightProperty;
    }

    public int getEdgeWeight(int e) {
        assert getEdges().contains(e);
        return weightProperty.getValueAsInt(e);
    }

    public void setEdgeWeight(int e, int newWeight) {
        assert getEdges().contains(e);
        weightProperty.setValue(e, newWeight);
    }

    public int getDirectDistance(int u, int v) {
        assert getVertices().contains(u) : "vertex does not exist: " + u;
        assert getVertices().contains(v) : "vertex does not exist: " + v;

        return directDistanceTable.get(u, v);
    }

    public void setDirectDistance(int u, int v, int distance) {
        assert getVertices().contains(u) : "vertex does not exist: " + u;
        assert getVertices().contains(v) : "vertex does not exist: " + v;

        directDistanceTable.put(u, v, distance);
        directDistanceTable.put(v, u, distance);
    }

    /**
     * Get the shortest distance from source to destination
     *
     * @param source
     * @param destination
     * @return shortest distance
     */
    public int getShortestDistance(int source, int destination) {
        if(shortestDistances != null){
            return (int) shortestDistances.getDistance(destination, source);
        }
        else{
            return getDistance(getShortestPath(source, destination));
        }
    }


    public Path getShortestPath(int source, int destination) {
        return new SearchResultWrappedPath(new DijkstraHeapAlgorithm(getWeightProperty())
                .compute(this, source), source, destination);
    }

    /**
     * Get the path distance by adding the edge weight
     *
     * @param path the path on the station graph
     * @return the distance of the path
     */
    private int getDistance(Path path) {
        int distance = 0;
        for (int i = 1; i < path.getNumberOfVertices(); i++) {
            int v1 = path.getVertexAt(i - 1);
            int v2 = path.getVertexAt(i);
            int edge = getEdgesConnecting(v1, v2).toIntArray()[0];
            distance += getEdgeWeight(edge);
        }
        return distance;
    }

    /**
     * Get the vertex label in station graph
     *
     * @param vertex the station vertex id
     * @return the vertex label
     */
    public String getLabel(int vertex) {
        return this.getVertexLabelProperty().getValueAsString(vertex);
    }
}
