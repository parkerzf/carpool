package nl.twente.bms.struct;

import com.carrotsearch.hppc.IntIntMap;
import com.carrotsearch.hppc.IntIntOpenHashMap;
import grph.path.AbstractPath;
import grph.path.ArrayPath;
import grph.path.PathNotModifiableException;
import nl.twente.bms.model.ModelInstance;
import nl.twente.bms.utils.Utils;
import toools.set.IntHashSet;
import toools.set.IntSet;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class Leg extends AbstractPath{
    private int id = Utils.genNextLegId();
    private User initUser;
    private int[] vertices;
    private int[] lengths;
    private int length;
    private int size;
    private Leg[] legs;

    private int initEarliestDepartureTime;
    private int initLatestArrivalTime;
    private int prevEarliestDepartureTime;
    private int prevLatestArrivalTime;
    private int[] prevCapacities;

    private IntIntMap vertexIdxMap = new IntIntOpenHashMap();

    public Leg(StationGraph graph, User user, int source, int destination,
               int earliestDepartureTime, int latestArrivalTime) {
        this.initEarliestDepartureTime = earliestDepartureTime;
        this.initLatestArrivalTime = latestArrivalTime;

        this.prevEarliestDepartureTime = earliestDepartureTime;
        this.prevLatestArrivalTime = latestArrivalTime;


        if(source != -1 && destination != -1) {
            this.vertices = graph.getShortestPath(source, destination).toVertexArray();
            this.size = vertices.length;
            initUser = user;
            legs = new Leg[size];
            Arrays.fill(legs, this);
            lengths = new int[size-1];
            prevCapacities = new int[size];
            Arrays.fill(prevCapacities, 1);

            setMinTime(0, earliestDepartureTime);
            for(int i = 0; i < getSize() - 1; i++){
                lengths[i] = graph.getShortestDistance(vertices[i], vertices[i+1]);
                length += lengths[i];
                setMinTime(i+1, earliestDepartureTime + length / Utils.SPEED_PER_MIN);
            }
            int diffMaxMin = latestArrivalTime - getMinTime(size - 1);

            for(int i = 0; i < getSize(); i++){
                vertexIdxMap.put(vertices[i], i);
                setMaxTime(i, getMinTime(i) + diffMaxMin);
                setInitUsedCapacity(i, 1);
            }
        }
    }

    @Override
    public int getLength() {
        return length;
    }

    public int getLengthAt(int idx) {
        return lengths[idx];
    }

    public int getSize() {
        return size;
    }

    @Override
    public int getSource() {
        return vertices[0];
    }

    @Override
    public void setSource(int v) {
        vertices[0] = v;
    }

    @Override
    public int getVertexAt(int i) {
        return vertices[i];
    }

    @Override
    public AbstractPath clone() {
        return new ArrayPath(Arrays.copyOf(vertices, size));
    }

    @Override
    public int getDestination() {
        return vertices[size - 1];
    }

    @Override
    public int[] toVertexArray() {
        return vertices;
    }

    @Override
    public boolean containsVertex(int someVertex) {
        return this.vertexIdxMap.containsKey(someVertex);
    }

    @Override
    public int indexOfVertex(int v) {
        return this.vertexIdxMap.getOrDefault(v, -1);
    }

    @Override
    public void extend(int e, int v) {
        throw new PathNotModifiableException();
    }

    @Override
    public void reverse() {
        throw new IllegalStateException();
    }

    @Override
    public int getEdgeHeadingToVertexAt(int i) {
        return -1;
    }

    @Override
    public int getNumberOfVertices() {
        return size;
    }

    @Override
    public String toString() {
        if (getSize() == 0) {
            return "[path does not exist]";
        } else {
            StringBuilder b = new StringBuilder();
            b.append(String.format("l%d | cost: %.1f\n", id, getCost(true)));
            for (int i = 0; i < getSize(); ++i) {
                int v = this.getVertexAt(i);

                b.append(String.format("v%d:time[%d,%d]",
                                v, getMinTime(i), getMaxTime(i))
                );

                if (i < getSize() - 1) {
                    b.append(String.format(" --[id:%d, cap:%d, len:%d]->\n", legs[i].id, getUsedCapacity(i), lengths[i]));
                }
            }
            return b.toString();
        }
    }


    /**
     * Check how much cost can be saved by merging with the other leg
     * @param someLeg the leg to check
     * @return
     */
    public double getOverlappedCost(Leg someLeg){
        double overlappedCost = 0;
        //check the global time compatibility
        if(Utils.isOverlapped(this, someLeg)){
            //find meeting points with time constraint and capacity constraint
            if(this.getLength() > someLeg.getLength()){
                overlappedCost = computeCoveredDistance(someLeg) * Utils.DRIVING_EUR_PER_KM;
            }
            else {
                overlappedCost = someLeg.computeCoveredDistance(this) * Utils.DRIVING_EUR_PER_KM;
            }
        }

        return overlappedCost;
    }


    public int getCoveredDistance(Leg someLeg) {
        int coveredDistance = 0;
        //check the global time compatibility
        if(Utils.isOverlapped(this, someLeg)){
            //find meeting points with time constraint and capacity constraint
            coveredDistance = computeCoveredDistance(someLeg);
        }
        return coveredDistance;
    }

    public int getCoveredDistance() {
        assert(initUser != null);
        int coveredDistance = 0;
        for (int i = 0; i < getSize() -1 ; ++i) {
            if(edgeIsCovered(i)){
                coveredDistance += lengths[i];
            }
        }
        return coveredDistance;
    }

    private int computeCoveredDistance(Leg someLeg) {
        int meetingPoint = findMeetingPoint(someLeg);
        if(meetingPoint == -1) return 0;

        int coveredDistance = 0;
        int idx = indexOfVertex(meetingPoint);
        int somePathIdx = someLeg.indexOfVertex(meetingPoint);

        for (int i = idx + 1, j = somePathIdx + 1; i < getSize() && j < someLeg.getSize(); ++i, ++j){
            if(getVertexAt(i) == someLeg.getVertexAt(j) && hasCapacity(i)
                    // this edge is not carried by others or it is carried by taxi
                    && (!someLeg.edgeIsCovered(j-1))){
                //someLeg.legs[j-1].id == someLeg.id || someLeg.legs[j-1].id == 0)
                coveredDistance += lengths[i-1];
            }
        }

        return coveredDistance;
    }

    public int getTaxiDistance() {
        assert(initUser != null);
        int taxiDistance = 0;
        for (int i = 0; i < getSize() -1 ; ++i) {
            if(edgeIsTaxi(i)){
                taxiDistance += lengths[i];
            }
        }
        return taxiDistance;
    }


    public double getCost(boolean withTaxiCost){
        assert(initUser != null);
        double cost = 0;
        for (int i = 0; i < getSize() -1 ; ++i) {
            if(legs[i].id == 0 && withTaxiCost) {
                cost += lengths[i] * Utils.TAXI_EUR_PER_KM;
            }
            else if(legs[i].id == this.id){
                cost += lengths[i] * Utils.DRIVING_EUR_PER_KM;
            }
        }
        return cost;
    }

    public boolean merge(Leg someLeg) {
        boolean isMerged = false;
        if(Utils.isOverlapped(this, someLeg)){
            isMerged = carryPath(someLeg);
        }
        return isMerged;
    }

    private boolean carryPath(Leg someLeg){
        if(computeCoveredDistance(someLeg) == 0) return false;
        // the combining of two legs is feasible, then find the meeting point and
        // update the parking point if possible (the second parameter is true)
        int meetingPoint = findMeetingPoint(someLeg);

        int meetingIdx = indexOfVertex(meetingPoint);
        int somePathMeetingIdx = someLeg.indexOfVertex(meetingPoint);

        // Update min and max time
        int maxMin = Math.max(getMinTime(meetingIdx),
                someLeg.getMinTime(somePathMeetingIdx));

        int minMax = Math.min(getMaxTime(meetingIdx),
                someLeg.getMaxTime(somePathMeetingIdx));

        int diffMin = maxMin - getMinTime(meetingIdx);
        int somePathDiffMin = maxMin - someLeg.getMinTime(somePathMeetingIdx);

        int diffMax = getMaxTime(meetingIdx) - minMax;
        int somePathDiffMax = someLeg.getMaxTime(somePathMeetingIdx) - minMax;

        // updates the time on this leg
        IntStream.range(0, size).forEach(
                index -> {
                    if(legs[index].id != 0){
                        legs[index].updateMinTimeAtVertex(getVertexAt(index), diffMin);
                        legs[index].updateMaxTimeAtVertex(getVertexAt(index), -diffMax);
                    }
                    if(legs[index] != this){
                        updateMinTimeAtVertex(getVertexAt(index), diffMin);
                        updateMaxTimeAtVertex(getVertexAt(index), -diffMax);
                    }
                }
        );

        // updates the time on someLeg
        IntStream.range(0, someLeg.size).forEach(
                index -> {
                    if(someLeg.legs[index].id != 0){
                        someLeg.legs[index].updateMinTimeAtVertex(someLeg.getVertexAt(index), somePathDiffMin);
                        someLeg.legs[index].updateMaxTimeAtVertex(someLeg.getVertexAt(index), -somePathDiffMax);
                    }
                    if(someLeg.legs[index] != someLeg) {
                        someLeg.updateMinTimeAtVertex(someLeg.getVertexAt(index), somePathDiffMin);
                        someLeg.updateMaxTimeAtVertex(someLeg.getVertexAt(index), -somePathDiffMax);
                    }
                }
        );

        // updates the legs and the capacities on someLeg
        int i,j;
        for (i = meetingIdx + 1 , j = somePathMeetingIdx + 1; i < getSize() && j < someLeg.getSize(); ++i, ++j) {
            if(getVertexAt(i) == someLeg.getVertexAt(j) && hasCapacity(i)
                    // this edge is not carried by others or it is carried by taxi
                    && (someLeg.legs[j-1].id == someLeg.id || someLeg.legs[j-1].id == 0)){
                someLeg.legs[j-1] = legs[i-1];
                someLeg.increaseUsedCapacityAtVertex(j-1);
            }
            else{

                break;
            }
        }

//        someLeg.legs[someLeg.getSize() - 2] = someLeg.legs[someLeg.getSize() - 1];

        return true;
    }


    private boolean hasCapacity(int idx) {
        return getUsedCapacity(idx) < Utils.CAPACITY;
    }

    private int findMeetingPoint(Leg someLeg){
        int i;
        for (i = 0; i < someLeg.getSize(); ++i) {
            if(someLeg.edgeIsCovered(i)) continue;
            int v = someLeg.getVertexAt(i);
            int idx;
            if ((idx = indexOfVertex(v)) != -1
                && hasCapacity(idx)
                && Utils.isOverlapped(getMinTime(idx), getMaxTime(idx), someLeg.getMinTime(i), someLeg.getMaxTime(i))
                ) return v;
        }
        return -1;
    }

    public int getEarliestDepartureTime() {
        return getMinTime(0);
    }

    public int getLatestArrivalTime() {
        return getMaxTime(size - 1);
    }

    private void setInitUsedCapacity(int idx, int capacity) {
        legs[idx].setUsedCapacityAtVertex(vertices[idx], capacity);
    }

    private void increaseUsedCapacityAtVertex(int idx) {
        legs[idx].setUsedCapacityAtVertex(vertices[idx], getUsedCapacity(idx) + 1);
    }

    private void setUsedCapacityAtVertex(int v, int capacity) {
        ModelInstance.legVertexCapacityTable.put(id, v, capacity);
    }

    public int getUsedCapacity(int idx){
        if(legs[idx].id == 0){
            return 1;
        }
        return legs[idx].getUsedCapacityAtVertex(vertices[idx]);
    }

    private int getUsedCapacityAtVertex(int v) {
        return ModelInstance.legVertexCapacityTable.get(id, v);
    }

    public void setMinTime(int idx, int minTime){
        legs[idx].setMinTimeAtVertex(vertices[idx], minTime);
    }

    public int getMinTime(int idx){
        if(legs[idx].id == 0){
            return getMinTimeAtVertex(vertices[idx]);
        }
        return legs[idx].getMinTimeAtVertex(vertices[idx]);
    }

    private int getMinTimeAtVertex(int v) {
        return ModelInstance.legVertexMinTimeTable.get(id, v);
    }

    private void setMinTimeAtVertex(int v, int minTime) {
        ModelInstance.legVertexMinTimeTable.put(id, v, minTime);
    }


    private void updateMinTimeAtVertex(int v, int minTime) {
        Integer curVal = ModelInstance.legVertexMinTimeTable.get(id, v);
        if(curVal == null){
            curVal = 0;
        }
        ModelInstance.legVertexMinTimeTable.put(id, v, curVal + minTime);
    }

    public void setMaxTime(int idx, int maxTime){
        legs[idx].setMaxTimeAtVertex(vertices[idx], maxTime);
    }

    public int getMaxTime(int idx) {
        if (legs[idx].id == 0) {
            return getMaxTimeAtVertex(vertices[idx]);
        }
        return legs[idx].getMaxTimeAtVertex(vertices[idx]);
    }

    private int getMaxTimeAtVertex(int v) {
        return ModelInstance.legVertexMaxTimeTable.get(id, v);
    }

    private void setMaxTimeAtVertex(int v, int maxTime) {
        ModelInstance.legVertexMaxTimeTable.put(id, v, maxTime);
    }


    private void updateMaxTimeAtVertex(int v, int maxTime) {
        Integer curVal = ModelInstance.legVertexMaxTimeTable.get(id, v);
        if(curVal == null){
            curVal = 0;
        }
        ModelInstance.legVertexMaxTimeTable.put(id, v, curVal + maxTime);
    }


    public User getUser() {
        return initUser;
    }

    public void syncTime() {
        int firstMinTime = getMinTime(0);
        int firstMaxTime = getMaxTime(0);

        for(int i = 1; i < getSize(); i++){
            int curMinTime = getMinTime(i);
            int curMaxTime = getMaxTime(i);

            if(curMinTime > firstMinTime + i){
                firstMinTime = curMinTime - i;
            }

            if(curMaxTime < firstMaxTime + i){
                firstMaxTime = curMaxTime - i;
            }
        }

        for(int i = 0; i < getSize(); i++) {
            setMinTime(i, firstMinTime + i);
            setMaxTime(i, firstMaxTime + i);
        }
    }

    public IntSet getVertexSet(){
        IntSet vertexSet = new IntHashSet();
        vertexSet.addAll(vertices);
        return vertexSet;
    }

    public void clear() {
        legs = new Leg[size];
        Arrays.fill(legs, this);
        setMinTime(0, prevEarliestDepartureTime);
        int prevLengthSum = 0;
        for(int i = 0; i < getSize() - 1; i++){
            prevLengthSum += lengths[i];
            setMinTime(i+1, prevEarliestDepartureTime + prevLengthSum / Utils.SPEED_PER_MIN);
        }
        int diffMaxMin = prevLatestArrivalTime - getMinTime(size - 1);
        for(int i = 0; i < getSize(); i++){
            setMaxTime(i, getMinTime(i) + diffMaxMin);
            setInitUsedCapacity(i, prevCapacities[i]);
        }
    }


    public int[] getVertices(){
        return vertices;
    }


    public boolean edgeIsTaxi(int i) {
        return legs[i].id == 0;
    }


    public void setTaxiWhenNeeded(int idx) {
        if(legs[idx].id == id){
            legs[idx] = ModelInstance.taxiLeg;
        }
    }

    public boolean edgeIsCovered(int idx) {
        return legs[idx].id != id && legs[idx].id!= 0;
    }

    public void resetLeg(int idx) {
            legs[idx] = this;
    }

    public void storePrevStatus() {
        prevEarliestDepartureTime = getEarliestDepartureTime();
        prevLatestArrivalTime = getLatestArrivalTime();

        for(int idx = 0; idx < getSize(); idx++){
            prevCapacities[idx] = getUsedCapacity(idx);
        }
    }


    public int getTimeDeviation() {
        return initLatestArrivalTime - getMaxTime(getSize()-1);
    }


    public int getCoveredTimes() {
        int coveredTimes = 0;
        for (int i = 0; i < getSize() -1 ; ++i) {
            if(edgeIsCovered(i) && (i==0 || (legs[i].id != legs[i-1].id))){
                coveredTimes++;
            }
        }
        return coveredTimes;
    }

    public int getMatchDistance(int cap) {
        int matchDistance = 0;
        for (int i = 0; i < getSize() -1 ; ++i) {
            if(cap == 0 && getUsedCapacity(i) != 1){
                //all the match distance
                matchDistance+= getLengthAt(i);
            }
            else if(getUsedCapacity(i) == cap){
                matchDistance+= getLengthAt(i);
            }
        }
        return matchDistance;
    }

    public int getPickupTimes() {
        int pickupTimes = 0;
        for (int i = 0; i < getSize() -1 ; ++i) {
            if((edgeIsCovered(i) || edgeIsTaxi(i))
                    && (i==0 || (legs[i].id != legs[i-1].id))){
                pickupTimes++;
            }
        }
        return pickupTimes;
    }
}
