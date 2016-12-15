package nl.twente.bms.struct;

import nl.twente.bms.model.ModelInstance;
import nl.twente.bms.utils.Utils;

import java.util.HashSet;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class UserCoverGroup implements Comparable<UserCoverGroup> {

    private User rider;
    private User firstDriverCandidate;
    private HashSet<User> driverSet = new HashSet<>();
    //the distance that are not covered by this group's drivers
    private int uncoveredDistance;

    public UserCoverGroup(User rider, User driver){
        this.rider = rider;
        this.firstDriverCandidate = driver;

        int initCoveredDistance = driver.getCoveredDistance(rider);
        uncoveredDistance = rider.getTotalDistance() - initCoveredDistance;

    }


    public void initMerge() {
        boolean isMerged = firstDriverCandidate.merge(rider);
        if(isMerged){
            firstDriverCandidate.setStatus(Utils.DRIVER);
            driverSet.add(firstDriverCandidate);
            rider.setStatus(Utils.RIDER);
        }
    }

    public boolean updateUncoveredDistance(){
        //if no update, return false, else true
//        if(firstDriverCandidate.getStatus() == Utils.RIDER){
//            this.uncoveredDistance = rider.getTotalDistance();
//            return true;
//        }
        int newCoveredDistance = firstDriverCandidate.getCoveredDistance(rider);
        int newUncoveredDistance = rider.getTotalDistance() - newCoveredDistance;
        if(newUncoveredDistance != uncoveredDistance){
            this.uncoveredDistance = newUncoveredDistance;
            return true;
        }

        return false;
    }

    public void addDriver(User driver) {
        boolean isMerged = driver.merge(rider);
        if(isMerged){
            driver.setStatus(Utils.DRIVER);
            driverSet.add(driver);
        }
    }

    @Override
    public int compareTo(UserCoverGroup someUserGroup) {
        return Double.compare(uncoveredDistance, someUserGroup.uncoveredDistance);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\nrider:\n");
        sb.append(rider.toString());

        sb.append("\ndrivers:\n[");
        if(driverSet.size() == 0){
            sb.append("\n");
            sb.append(firstDriverCandidate.toString());
            sb.append(',');
        }
        else{
            for(User driver: driverSet){
                sb.append("\n");
                sb.append(driver.toString());
                sb.append(',');
            }
        }
        sb.append("\n]");

        return sb.toString();
    }

    public String getSummaryStr(){
        StringBuilder sb = new StringBuilder();
        sb.append("\nrider: u" + rider.getUId() + "|");
        sb.append("uncovered distance:" + rider.getUncoveredDistance() + "\n");
        sb.append("drivers:[");
        if(driverSet.size() == 0){
            sb.append("u" + firstDriverCandidate.getUId() + ",");
        }
        else{
            for(User driver: driverSet){
                sb.append("u" + driver.getUId() + ",");
            }
        }
        sb.append("]");
        return sb.toString();

    }

    public int getUncoveredDistance() {
//        return rider.getUncoveredDistance();
        return uncoveredDistance;
    }


    public Boolean hasDriver(User user) {
        return driverSet.contains(user);
    }

    public void syncTime(){
        rider.syncTime();
        for(User user: driverSet){
            user.syncTime();
        }
    }

    public boolean isInitFeasible() {
        return rider.getStatus() != Utils.DRIVER
                && firstDriverCandidate.getCoveredDistance(rider) != 0
                &&  firstDriverCandidate.getStatus() != Utils.RIDER;
    }

    public boolean isFeasible() {

        if(rider.getUId() == 55){
            System.out.println("debug!");
        }
        // start and end are the same candidate parking point
        int[] startVertexInfo = rider.getStartVertexInfo();
        int[] endVertexInfo = rider.getEndVertexInfo();

        if (rider.isCandidateParkingPiont(startVertexInfo) && rider.isCandidateParkingPiont(endVertexInfo)) {
            if (rider.getVertexId(startVertexInfo) != rider.getVertexId(endVertexInfo)) {
                return false;
            }
        } else {
            return false;
        }

        // the rider doesn't have break
        if(rider.hasBreak(startVertexInfo, endVertexInfo)) return false;

        // the rider has cost saving
        if (!hasSaving()) return false;

        return true;
    }

    public void makeFeasible(){

        int[] startVertexInfo = rider.getStartVertexInfo();
        int[] endVertexInfo = rider.getEndVertexInfo();


        if(rider.isCandidateParkingPiont(startVertexInfo) && rider.isCandidateParkingPiont(endVertexInfo)){
            if(rider.getVertexId(startVertexInfo) != rider.getVertexId(endVertexInfo)){
                int distToStart = rider.distanceToStart(startVertexInfo);
                int distToEnd = rider.distanceToEnd(endVertexInfo);

                if(distToStart < distToEnd){
                    startVertexInfo = rider.resetStart(startVertexInfo, endVertexInfo);
                }
                else{
                    endVertexInfo = rider.resetEnd(endVertexInfo, startVertexInfo);
                }
            }
        }
        else{
            startVertexInfo = rider.findAndResetToMostConservativeStart(startVertexInfo);
            endVertexInfo = rider.findAndResetToMostConservativeEnd(endVertexInfo);
        }

        rider.fillBreaksWithTaxi(startVertexInfo, endVertexInfo);

    }

    public boolean hasSaving() {
        if(rider.getCost(true) >= rider.getSelfDrivingCost()) return false;
        return true;
    }

    public User getFirstDriverCandidate() {
        return firstDriverCandidate;
    }


    public void clear() {
        for(User driver: driverSet){
            driver.clear();
        }
        rider.clear();
    }

    public void registerDriverSet() {
        for(User driver: driverSet){
            driver.storePrevStatus();
            ModelInstance.registeredDriverSet.add(driver);

        }
    }
}
