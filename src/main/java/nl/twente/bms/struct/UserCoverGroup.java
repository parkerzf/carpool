package nl.twente.bms.struct;

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
    private boolean initMerged = false;

    public UserCoverGroup(User rider, User driver){
        this.rider = rider;
        this.firstDriverCandidate = driver;
        int initCoveredDistance = driver.getCoveredDistance(rider);
        uncoveredDistance = rider.getTotalDistance() - initCoveredDistance;
    }


    public void initMerge() {
        addDriver(firstDriverCandidate);
//        boolean isMerged = firstDriverCandidate.merge(rider);
//        assert (isMerged == true): "should be able to merge: " + getSummaryStr();
//        firstDriverCandidate.setStatus(Utils.DRIVER);
//        driverSet.add(firstDriverCandidate);
        rider.setStatus(Utils.RIDER);
        initMerged = true;
    }

    public void addDriver(User driver) {
        if(driver.getUId()==92){
            logger.debug("debug!");
        }
        boolean isMerged = driver.merge(rider);
        assert (isMerged == true): "should be able to merge: " + getSummaryStr();
        driver.setStatus(Utils.DRIVER);
        driverSet.add(driver);
        uncoveredDistance = rider.getUncoveredDistance();
    }

    public boolean updateUncoveredDistance(){
        if(getRider().getUId() == 76 && getFirstDriverCandidate().getUId() == 92){
            logger.debug("debug!");
        }
        assert (initMerged == false): "init merged: " + this.getSummaryStr();

        int newCoveredDistance = firstDriverCandidate.getCoveredDistance(rider);
        int newUncoveredDistance = rider.getTotalDistance() - newCoveredDistance;

        if (newUncoveredDistance < uncoveredDistance){
            logger.error(String.format("new < prev uncover: %d,%d|%s", newUncoveredDistance, uncoveredDistance, this.getSummaryStr()));
            System.exit(-1);
        }

        if(newUncoveredDistance > uncoveredDistance){
            this.uncoveredDistance = newUncoveredDistance;
            return true;
        }
        // no updates means that it is up to date so that it is ready to be added to the main group
        return false;
    }

    public boolean updateUncoveredDistance2(){
        assert (initMerged == false): "init merged: " + this.getSummaryStr();

        int newCoveredDistance = firstDriverCandidate.getCoveredDistance(rider);
        int newUncoveredDistance = rider.getTotalDistance() - newCoveredDistance;

        if (newUncoveredDistance < uncoveredDistance){
            logger.error(String.format("new < prev uncover: %d,%d|rider: u%d", newUncoveredDistance, uncoveredDistance, rider.getUId()));
            System.exit(-1);
        }
        if(newUncoveredDistance > uncoveredDistance){
            this.uncoveredDistance = newUncoveredDistance;
            return true;
        }
        // no updates means that it is up to date so that it is ready to be added to the main group
        return false;
    }

    @Override
    public int compareTo(UserCoverGroup someUserGroup) {
        return Double.compare(uncoveredDistance, someUserGroup.uncoveredDistance);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("rider:u" + rider.getUId() + "|");
        sb.append(String.format("total,uncover:%d,%d:", rider.getTotalDistance(), uncoveredDistance) + "|");
        sb.append(String.format("self cost,rider cost: %.1f,%.1f|", rider.getSelfDrivingCost(), rider.getCost(true)));

        if(driverSet.size() == 0){
            sb.append("candidate: u" + firstDriverCandidate.getUId());
        }
        else{
            sb.append("drivers:[");
            for(User driver: driverSet){
                sb.append("u" + driver.getUId() + ",");
            }
            sb.append("]");
        }
        return sb.toString();
    }

    public String getSummaryStr(){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("u%d(%s)|", rider.getUId(), Utils.getStatusStr(rider.getStatus())));
        sb.append(String.format("total,uncover:%d,%d|", rider.getTotalDistance(), uncoveredDistance));
        sb.append(String.format("self cost,rider cost: %.1f,%.1f|", rider.getSelfDrivingCost(), rider.getCost(true)));

        if(driverSet.size() == 0){
            sb.append(String.format("candidate: u%d(%s)", firstDriverCandidate.getUId(),
                    Utils.getStatusStr(firstDriverCandidate.getStatus())));
        }
        else{
            sb.append("drivers:[");
            for(User driver: driverSet){
                sb.append(String.format("u%d(%s),", driver.getUId(),
                        Utils.getStatusStr(driver.getStatus())));
            }
            sb.append("]");
        }
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

    public boolean isCoveredBeforeInitMerge() {
        assert initMerged == false;
        return rider.getTotalDistance() - uncoveredDistance > 0;
    }

    public boolean isFeasibleBeforeInitMerge() {
        assert initMerged == false;
        return rider.getStatus() != Utils.DRIVER
                &&  firstDriverCandidate.getStatus() != Utils.RIDER;
    }

    public boolean isAllCovered(){
        return uncoveredDistance == 0;
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

    public void updateQueue(PriorityQueue<User> userQueue, HashMap<User, ArrayList<UserCoverGroup>> driverGroupMap) {
        if(!driverSet.isEmpty()){
            for(User driver: driverSet){
                ArrayList<UserCoverGroup> userCoverGroupList = driverGroupMap.get(driver);
                 if(driver.getUId() == 39){
                    logger.debug(String.format("debug u%d", driver.getUId()));
                }
                logger.debug(String.format("driver u%d covergroup list size %d", driver.getUId(), userCoverGroupList.size()));
                for(UserCoverGroup curGroup: userCoverGroupList){
                    User rider = curGroup.getRider();
                    if(!ModelInstance.registeredFinishedRiderSet.contains(rider) && rider.getStatus() == Utils.INDEPENDENT){
                        // recompute the uncovered distance
                        int prevMinUncoveredDistance = rider.getMinUncoveredDistance();
                        PriorityQueue<UserCoverGroup> curQueue = rider.getQueue();

                        // update curQueue
                        logger.debug(String.format("before update pair: %s", curGroup.getSummaryStr()));
                        boolean isUpdated = curGroup.updateUncoveredDistance();
                        logger.debug(String.format("after  update pair: %s", curGroup.getSummaryStr()));
                        if(isUpdated) {
                            logger.debug(String.format("cross update: %s", curGroup.getSummaryStr()));
                            curQueue.remove(curGroup);
                            if(curGroup.uncoveredDistance < rider.getTotalDistance()) {
                                curQueue.offer(curGroup);
                            }
                            // update userQueue
                            int minUncoveredDistance = rider.getMinUncoveredDistance();

                            if (minUncoveredDistance < prevMinUncoveredDistance){
                                logger.error(String.format("new < prev minuncover: %d,%d|%s", minUncoveredDistance, prevMinUncoveredDistance, this.getSummaryStr()));
                                System.exit(-1);
                            }

                            if(minUncoveredDistance > prevMinUncoveredDistance){
                                userQueue.remove(rider);
                                if(minUncoveredDistance < rider.getTotalDistance()) {
                                    userQueue.offer(rider);
                                }
                            }
                        }
                    }
                }
            }
        }
        else{
            logger.error("The cover group is not merged yet!");
        }
    }

    public User getRider() {
        return rider;
    }
}
