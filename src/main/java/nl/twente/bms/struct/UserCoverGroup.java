package nl.twente.bms.struct;

import com.carrotsearch.hppc.IntIntMap;
import nl.twente.bms.model.ModelInstance;
import nl.twente.bms.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class UserCoverGroup implements Comparable<UserCoverGroup> {

    private static final Logger logger = LoggerFactory.getLogger(UserCoverGroup.class);
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

    public void updateQueue(PriorityQueue<User> userQueue, HashMap<User, ArrayList<User>> userFeasibleMap) {
        if(!driverSet.isEmpty()){
            for(User driver: driverSet){
                ArrayList<User> userList = userFeasibleMap.get(driver);
                for(User user: userList){
                    if(user.getStatus() == Utils.INDEPENDENT){
                        // recompute the uncovered distance

                        // 3 steps to update the userQueue
                    }
                }
            }
        }
        else{
            logger.error("The cover group is not merged yet!");
        }
    }
}
