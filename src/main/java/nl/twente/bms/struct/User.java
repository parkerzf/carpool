package nl.twente.bms.struct;

import nl.twente.bms.model.ModelInstance;
import nl.twente.bms.utils.Utils;
import toools.set.IntHashSet;
import toools.set.IntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author zhaofeng
 * @since ${version}
 */

public class User implements Comparable<User> {

    private int uId;
    private int status = Utils.INDEPENDENT;
    private int totalDistance = 0;

    private List<Leg> legs = new ArrayList<>();

    private IntSet candidateParkingPointSet = new IntHashSet();
    private PriorityQueue<UserCoverGroup> queue = new PriorityQueue<>();

    public User(){
        this.uId = Utils.genNextUserId();
    }

    public User(int uid) {
        this.uId = uid;
    }

    public void addLeg(Leg leg){
        this.legs.add(leg);
    }

    public boolean isCandidateParkingPiont(int vertexId){
        return candidateParkingPointSet.contains(vertexId);
    }

    public boolean isCandidateParkingPiont(int[] vertexInfo){
        return candidateParkingPointSet.contains(getVertexId(vertexInfo));
    }

    public int getMinUncoveredDistance() {
        if(queue.size() != 0){
            return queue.peek().getUncoveredDistance();
        }
        else{
            return getTotalDistance();
        }
    }

    public boolean merge(User user) {
        int i = 0;
        int j = legs.size() - 1;
        int p = 0;
        int q = user.legs.size() - 1;

        //assert not less than 2 legs
        assert(j > 0 && q > 0);

        boolean isMerged = false;

        boolean isMergedLast = this.legAt(j).merge(user.legAt(q));

        if(isMergedLast){
            j--; q--;
            isMerged = true;
        }
        else if(Utils.isEarlier(this.legAt(j), user.legAt(q))){
            q--;
        }
        else if(Utils.isEarlier(user.legAt(q), this.legAt(j))){
            j--;
        }
        else{
            j--; q--;
        }
        boolean curIsMerged;
        while(i <= j && p <= q){
            curIsMerged = this.legAt(i).merge(user.legAt(p));

            if(curIsMerged){
                i++; p++;
                isMerged = true;
            }
            else if(Utils.isLater(this.legAt(i), user.legAt(p))){
                p++;
            }
            else if(Utils.isLater(user.legAt(p), this.legAt(i))){
                i++;
            }
            else{
                i++; p++;
            }
        }

        return isMerged;
    }

    public String getSummaryStr() {
        StringBuilder b = new StringBuilder();
        String status = "";
        if(getStatus() == Utils.DRIVER){
            status = "driver";
        }
        else if(getStatus() == Utils.RIDER){
            status = "rider";
        }
        else{
            status = "independent";
        }

        b.append(String.format("u%d, %s|cost: %.1f|total,min uncover: %d,%d|queue size: %d",
                uId, status, getCost(true), getTotalDistance(), getMinUncoveredDistance(), queue.size()));

        return b.toString();
    }

    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append(getSummaryStr());

        b.append('\n');

        for (int i = 0; i < legs.size(); ++i) {
            b.append(String.format("%s\n", legs.get(i)));
        }
        return b.toString();
    }

    public int getUId() {
        return uId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public double getOverlappedCost(User user) {
//        List<Leg> otherLegs = user.getLegs();
//        double cost = 0;
//        for(int i = 0; i< Math.min(legs.size(), otherLegs.size()); i++)
//            cost += this.legAt(i).getOverlappedCost(user.legAt(i));

        int i = 0;
        int j = legs.size() - 1;
        int p = 0;
        int q = user.legs.size() - 1;

        //assert not less than 2 legs
        assert(j > 0 && q > 0);

        double cost;

        cost = this.legAt(j).getOverlappedCost(user.legAt(q));

        if(cost > 0){
            j--; q--;
        }
        else if(Utils.isEarlier(this.legAt(j), user.legAt(q))){
            q--;
        }
        else if(Utils.isEarlier(user.legAt(q), this.legAt(j))){
            j--;
        }
        else{
            j--; q--;
        }

        double curCost;
        while(i <= j && p <= q){
            curCost = this.legAt(i).getOverlappedCost(user.legAt(p));

            if(curCost > 0){
                i++; p++;
                cost += curCost;
            }
            else if(Utils.isLater(this.legAt(i), user.legAt(p))){
                p++;
            }
            else if(Utils.isLater(user.legAt(p), this.legAt(i))){
                i++;
            }
            else{
                i++; p++;
            }
        }

        return cost;
    }

    public int[] getStartVertexInfo(){
        int[] vertexInfo = new int[2];
        Arrays.fill(vertexInfo, -1);

        for(int i = 0; i < legs.size(); i++){
            Leg leg = legAt(i);
            for(int j = 0; j < leg.getSize() -1 ; j++){
                if (leg.edgeIsCovered(j)){
                    vertexInfo[0] = i;
                    vertexInfo[1] = j;
                    return vertexInfo;
                }
            }
        }
        return vertexInfo;
    }

    public int[] getEndVertexInfo(){
        int[] vertexInfo = new int[2];
        Arrays.fill(vertexInfo, -1);

        for(int i = legs.size() -1 ; i >= 0; i--){
            Leg leg = legAt(i);
            for(int j = leg.getSize() - 2; j >= 0 ; j--){
                if (leg.edgeIsCovered(j)){
                    vertexInfo[0] = i;
                    vertexInfo[1] = j + 1;
                    return vertexInfo;
                }
            }
        }
        return vertexInfo;
    }

    public boolean needSelfCar(){
        if(getStatus() != Utils.RIDER) return true;

        int[] startVertexInfo = getStartVertexInfo();
        int[] endVertexInfo = getEndVertexInfo();

        if(startVertexInfo[0] == 0
                && startVertexInfo[1] == 0
                && endVertexInfo[0] == legs.size() - 1
                && endVertexInfo[1] == legAt(legs.size() - 1).getSize() - 1) return false;
        else return true;

    }

    public int getVertexId(int[] vertexInfo){
        return legs.get(vertexInfo[0]).getVertexAt(vertexInfo[1]);
    }

    private Leg legAt(int i) {
        return legs.get(i);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return this.getUId() == other.getUId();
    }

    public void syncTime() {
        for(Leg leg: legs){
            leg.syncTime();
        }
    }

    public List<Leg> getLegs() {
        return legs;
    }


    public double getCost(boolean withTaxiCost) {
        double cost = 0;
        for(Leg leg: legs){
            cost += leg.getCost(withTaxiCost);
        }
        return cost;
    }

    public double getCommuterCost(boolean withTaxiCost) {
        double cost = legs.get(0).getCost(withTaxiCost);
        cost += legs.get(legs.size()-1).getCost(withTaxiCost);
        return cost;
    }

    public double getSelfDrivingCost() {
        return getTotalDistance() * Utils.DRIVING_EUR_PER_KM;
    }

    public double getCommuterSelfDrivingCost() {
        return getCommuterDistance() * Utils.DRIVING_EUR_PER_KM;
    }

    public double getTaxiCost() {
        return getTaxiDistance() * Utils.TAXI_EUR_PER_KM;
    }

    public double getCommuterTaxiCost() {
        return getCommuterTaxiDistance() * Utils.TAXI_EUR_PER_KM;
    }

    public int getTotalDistance() {
        if(totalDistance == 0){
            for(Leg leg: legs){
                totalDistance += leg.getLength();
            }
        }
        return totalDistance;
    }

    public int getCommuterDistance(){
        int totalDistance = legs.get(0).getLength();
        totalDistance += legs.get(legs.size()-1).getLength();
        return totalDistance;
    }

    public int getCoveredDistance(){
        int coveredDistance = 0;
        for(Leg leg: legs){
            coveredDistance += leg.getCoveredDistance();
        }
        return coveredDistance;
    }

    public int getCommuterCoveredDistance(){
        int coveredDistance = legs.get(0).getCoveredDistance();
        coveredDistance += legs.get(legs.size()-1).getCoveredDistance();
        return coveredDistance;
    }


    public int getTaxiDistance(){
        int taxiDistance = 0;
        for(Leg leg: legs){
            taxiDistance += leg.getTaxiDistance();
        }
        return taxiDistance;
    }

    public int getCommuterTaxiDistance(){
        int taxiDistance = legs.get(0).getTaxiDistance();
        taxiDistance += legs.get(legs.size()-1).getTaxiDistance();
        return taxiDistance;
    }

    public int getUncoveredDistance(){
        return getTotalDistance() - getCoveredDistance();
    }


    public int getCoveredDistance(User user) {

        int i = 0;
        int j = legs.size() - 1;
        int p = 0;
        int q = user.legs.size() - 1;

        //assert not less than 2 legs
        assert(j > 0 && q > 0);

        int coveredDistance;

        coveredDistance = this.legAt(j).getCoveredDistance(user.legAt(q));

        if(coveredDistance > 0){
            j--; q--;
        }
        else if(Utils.isEarlier(this.legAt(j), user.legAt(q))){
            q--;
        }
        else if(Utils.isEarlier(user.legAt(q), this.legAt(j))){
            j--;
        }
        else{
            j--; q--;
        }

        int curCoveredDistance;
        while(i <= j && p <= q){
            curCoveredDistance = this.legAt(i).getCoveredDistance(user.legAt(p));

            if(curCoveredDistance > 0){
                i++; p++;
                coveredDistance += curCoveredDistance;
            }
            else if(Utils.isLater(this.legAt(i), user.legAt(p))){
                p++;
            }
            else if(Utils.isLater(user.legAt(p), this.legAt(i))){
                i++;
            }
            else{
                i++; p++;
            }
        }

        return coveredDistance;
    }


    public int getTimeDeviation() {
        int timeDeviation = 0;
        for(Leg leg: legs){
            timeDeviation += leg.getTimeDeviation();
        }
        return timeDeviation;
    }

    public int getCommuterTimeDeviation() {
        int timeDeviation = legs.get(0).getTimeDeviation();
        timeDeviation += legs.get(legs.size()-1).getTimeDeviation();
        return timeDeviation;
    }

    public int getCoveredTimes() {
        if(status != Utils.RIDER) return 0;
        int coveredTimes = 0;
        for(Leg leg: legs){
            coveredTimes += leg.getCoveredTimes();
        }
        return coveredTimes;
    }

    public int getCommuterCoveredTimes() {
        if(status != Utils.RIDER) return 0;
        int coveredTimes = legs.get(0).getCoveredTimes();
        coveredTimes += legs.get(legs.size()-1).getCoveredTimes();
        return coveredTimes;
    }

    public int getMatchDistance(int cap) {
        if(status != Utils.DRIVER) return 0;
        int matchDistance = 0;
        for(Leg leg: legs){
            matchDistance += leg.getMatchDistance(cap);
        }
        return matchDistance;
    }

    public int getCommuterMatchDistance(int cap) {
        if(status != Utils.DRIVER) return 0;
        int matchDistance = legs.get(0).getMatchDistance(cap);
        matchDistance += legs.get(legs.size()-1).getMatchDistance(cap);
        return matchDistance;
    }

    public void setPickupTimes(int[] pickupTimesDistributionTotal, int[] pickupTimesDistributionCommuter, int[] pickupTimesDistributionBusiness) {
        if(status != Utils.RIDER) return;
        for(int i =0; i< legs.size(); i++){
            Leg leg = legs.get(i);
            Utils.increaseCount(pickupTimesDistributionTotal, leg.getPickupTimes());
            if(i == 0 || i == legs.size() -1){
                Utils.increaseCount(pickupTimesDistributionCommuter, leg.getPickupTimes());
            }
            else{
                Utils.increaseCount(pickupTimesDistributionBusiness, leg.getPickupTimes());
            }
        }
    }

    public void genCandidateParkingPoints(){
        IntHashSet vertexSet = new IntHashSet();
        for(Leg leg: legs){
            //the first vertex of next leg is always the same the the last vertex of the prev leg, so we only count it once
            for(int i = 0; i < leg.getSize() - 1; i++){
                int v = leg.getVertexAt(i);
                if(vertexSet.contains(v)){
                    candidateParkingPointSet.add(v);
                }
                else{
                    vertexSet.add(v);
                }
            }
        }
        //special case: the first leg's first vertex and the last leg's last vertex
        int firstV = legAt(0).getVertexAt(0);
        Leg lastLeg = legAt(legs.size()-1);
        int lastV = lastLeg.getVertexAt(lastLeg.getSize() - 1);
        if(firstV == lastV){
            candidateParkingPointSet.add(firstV);
        }
    }

    @Override
    public int compareTo(User someUser) {
        return Double.compare(getMinUncoveredDistance(), someUser.getMinUncoveredDistance());
    }

    public void clear() {
        for(Leg leg: legs){
            leg.clear();
        }
        if(!ModelInstance.registeredDriverSet.contains(this)) setStatus(Utils.INDEPENDENT);
    }

    public int[] findAndResetToMostConservativeStart(int[] startVertexInfo) {
        int legIdx = startVertexInfo[0];
        int vertexIdx = startVertexInfo[1];
        int newLegIdx = -1;
        int newVertexIdx = -1;

        if(isCandidateParkingPiont(startVertexInfo)){
            //forward search and reset the edges between origin and new start
            Leg leg = legAt(legIdx);
            for(int i = vertexIdx + 1; i < leg.getSize(); i++){
                if(!isCandidateParkingPiont(leg.getVertexAt(i))){
                    newLegIdx = legIdx;
                    newVertexIdx = i-1;
                    break;
                }
                else{
                    leg.resetLeg(i-1);
                }
            }

            if(newLegIdx == -1) {
                for (int i = legIdx + 1; i < legs.size(); i++) {
                    if(newLegIdx != -1) {
                        break;
                    }
                    leg = legAt(i);
                    for(int j = 1; j < leg.getSize(); j++){
                        if(!isCandidateParkingPiont(leg.getVertexAt(j))){
                            newLegIdx = i;
                            newVertexIdx = j-1;
                            break;
                        }
                        else{
                            leg.resetLeg(j-1);
                        }
                    }
                }
            }
        }
        else{
            //backward search
            Leg leg = legAt(legIdx);
            for(int i = vertexIdx-1 ; i >= 0; i--){
                if(isCandidateParkingPiont(leg.getVertexAt(i))){
                    newLegIdx = legIdx;
                    newVertexIdx = i;
                    break;
                }
            }

            if(newLegIdx == -1) {
                for (int i = legIdx - 1; i >= 0; i--) {
                    if(newLegIdx != -1) {
                        break;
                    }
                    leg = legAt(i);
                    for(int j = leg.getSize() - 2; j >= 0; j--){
                        if(isCandidateParkingPiont(leg.getVertexAt(j))){
                            newLegIdx = i;
                            newVertexIdx = j;
                            break;
                        }
                    }
                }
            }
        }

        int[] newVertexInfo = new int[2];
        newVertexInfo[0] = newLegIdx;
        newVertexInfo[1] = newVertexIdx;
        return newVertexInfo;
    }

    public int[] findAndResetToMostConservativeEnd(int[] endVertexInfo) {
        int legIdx = endVertexInfo[0];
        int vertexIdx = endVertexInfo[1];
        int newLegIdx = -1;
        int newVertexIdx = -1;

        if(isCandidateParkingPiont(endVertexInfo)){
            //backward search and reset the edges between origin and new end
            Leg leg = legAt(legIdx);
            for(int i = vertexIdx - 1; i >=0; i--){
                if(!isCandidateParkingPiont(leg.getVertexAt(i))){
                    newLegIdx = legIdx;
                    newVertexIdx = i+1;
                    break;
                }
                else{
                    leg.resetLeg(i);
                }
            }

            if(newLegIdx == -1) {
                for (int i = legIdx - 1; i >= 0; i--) {
                    if(newLegIdx != -1) {
                        break;
                    }
                    leg = legAt(i);
                    for(int j = leg.getSize() - 2; j >= 0; j--) {
                        if (!isCandidateParkingPiont(leg.getVertexAt(j))) {
                            newLegIdx = i;
                            newVertexIdx = j+1;
                            break;
                        } else {
                            leg.resetLeg(j);
                        }
                    }
                }
            }
        }
        else{
            //forward search
            Leg leg = legAt(legIdx);
            for(int i = vertexIdx + 1; i < leg.getSize(); i++){
                if(isCandidateParkingPiont(leg.getVertexAt(i))){
                    newLegIdx = legIdx;
                    newVertexIdx = i;
                    break;
                }
            }

            if(newLegIdx == -1) {
                for (int i = legIdx + 1; i < legs.size(); i++) {
                    if(newLegIdx != -1) {
                        break;
                    }
                    leg = legAt(i);
                    for(int j = 0; j < leg.getSize(); j++){
                        if(isCandidateParkingPiont(leg.getVertexAt(j))){
                            newLegIdx = i;
                            newVertexIdx = j;
                            break;
                        }
                    }
                }
            }
        }

        int[] newVertexInfo = new int[2];
        newVertexInfo[0] = newLegIdx;
        newVertexInfo[1] = newVertexIdx;
        return newVertexInfo;
    }

    public void fillBreaksWithTaxi(int[] startVertexInfo, int[] endVertexInfo) {
        int startLegIdx = startVertexInfo[0];
        int startVertexIdx = startVertexInfo[1];

        int endLegIdx = endVertexInfo[0];
        int endVertexIdx = endVertexInfo[1];

        Leg leg = legAt(startLegIdx);

        for(int i = startVertexIdx; i < leg.getSize() - 1; i++){
            leg.setTaxiWhenNeeded(i);
        }

        for(int i = startLegIdx + 1; i < endLegIdx; i++) {
            leg = legAt(i);
            for(int j = 0; j < leg.getSize() -1; j++){
                leg.setTaxiWhenNeeded(j);
            }
        }

        leg = legAt(endLegIdx);

        for(int i = 0; i < endVertexIdx; i++) {
            leg.setTaxiWhenNeeded(i);
        }
    }

    public int distanceToStart(int[] startVertexInfo) {
        int distanceToStart = 0;
        int legIdx = startVertexInfo[0];
        int vertexIdx = startVertexInfo[1];

        Leg leg;
        for(int i = 0; i < legIdx; i++){
            leg = legAt(i);
            distanceToStart += leg.getLength();
        }

        leg = legAt(legIdx);
        for(int i = 0; i < vertexIdx; i++){
            distanceToStart += leg.getLengthAt(i);
        }
        return distanceToStart;
    }

    public int distanceToEnd(int[] endVertexInfo) {
        int distanceToEnd = 0;
        int legIdx = endVertexInfo[0];
        int vertexIdx = endVertexInfo[1];

        Leg leg = legAt(legIdx);
        for(int i = vertexIdx; i < leg.getSize() - 1; i++){
            distanceToEnd += leg.getLengthAt(i);
        }

        for(int i = legIdx +1; i < legs.size(); i++){
            leg = legAt(i);
            distanceToEnd += leg.getLength();
        }
        return distanceToEnd;
    }

    public int[] resetStart(int[] startVertexInfo, int[] endVertexInfo) {
        int startLegIdx = startVertexInfo[0];
        int startVertexIdx = startVertexInfo[1];

        int vertexToMatch = getVertexId(endVertexInfo);

        int newStartLegIdx = -1;
        int newStartVertexIdx = -1;

        Leg leg = legAt(startLegIdx);
        for(int i = startVertexIdx + 1; i < leg.getSize(); i++){
            if(leg.getVertexAt(i) != vertexToMatch){
                leg.resetLeg(i-1);
            }
            else{
                newStartLegIdx = startLegIdx;
                newStartVertexIdx = i;
                break;
            }
        }

        if(newStartLegIdx == -1) {
            for (int i = startLegIdx + 1; i < legs.size(); i++) {
                if(newStartLegIdx != -1) {
                    break;
                }
                leg = legAt(i);
                for(int j = 1; j < leg.getSize(); j++){
                    if(leg.getVertexAt(j) != vertexToMatch){
                        leg.resetLeg(j-1);
                    }
                    else{
                        newStartLegIdx = i;
                        newStartVertexIdx = j;
                        break;
                    }
                }
            }
        }

        int[] newStartVertexInfo = new int[2];
        newStartVertexInfo[0] = newStartLegIdx;
        newStartVertexInfo[1] = newStartVertexIdx;
        return newStartVertexInfo;
    }

    public int[] resetEnd(int[] endVertexInfo, int[] startVertexInfo) {
        int endLegIdx = endVertexInfo[0];
        int endVertexIdx = endVertexInfo[1];

        int vertexToMatch = getVertexId(startVertexInfo);

        int newEndLegIdx = -1;
        int newEndVertexIdx = -1;

        Leg leg = legAt(endLegIdx);
        for(int i = endVertexIdx -1; i >= 0; i--){
            if(leg.getVertexAt(i) != vertexToMatch){
                leg.resetLeg(i);
            }
            else{
                newEndLegIdx = endLegIdx;
                newEndVertexIdx = i;
                break;
            }
        }

        if(newEndLegIdx == -1) {
            for (int i = endLegIdx - 1; i >= 0; i--) {
                if(newEndLegIdx != -1) {
                    break;
                }
                leg = legAt(i);
                for(int j = leg.getSize() - 2; j >= 0; j--){
                    if(leg.getVertexAt(j) != vertexToMatch){
                        leg.resetLeg(j);
                    }
                    else{
                        newEndLegIdx = i;
                        newEndVertexIdx = j;
                        break;
                    }
                }
            }
        }

        int[] newEndVertexInfo = new int[2];
        newEndVertexInfo[0] = newEndLegIdx;
        newEndVertexInfo[1] = newEndVertexIdx;
        return newEndVertexInfo;
    }

    public void storePrevStatus() {
        for(Leg leg: legs){
            leg.storePrevStatus();
        }
    }

    public boolean hasBreak(int[] startVertexInfo, int[] endVertexInfo) {
        int startLegIdx = startVertexInfo[0];
        int startVertexIdx = startVertexInfo[1];

        int endLegIdx = endVertexInfo[0];
        int endVertexIdx = endVertexInfo[1];

        Leg leg = legAt(startLegIdx);

        for(int i = startVertexIdx; i < leg.getSize() - 1; i++){
            if(!leg.edgeIsCovered(i)) return true;
        }

        for(int i = startLegIdx + 1; i < endLegIdx; i++) {
            leg = legAt(i);
            for(int j = 0; j < leg.getSize() -1; j++){
                if(!leg.edgeIsCovered(j)) return true;
            }
        }

        leg = legAt(endLegIdx);

        for(int i = 0; i < endVertexIdx; i++) {
            if(!leg.edgeIsCovered(i)) return true;
        }

        return false;
    }

    public PriorityQueue<UserCoverGroup> getQueue() {
        return queue;
    }
}
