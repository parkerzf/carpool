package nl.twente.bms.struct;

import nl.twente.bms.model.ModelInstance;
import nl.twente.bms.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
        rider.setStatus(Utils.RIDER);
        initMerged = true;
    }

    public void addDriver(User driver) {
        if(driver.getUId()==25){
            logger.debug("debug!");
        }
        boolean isMerged = driver.merge(rider);
        if(isMerged == false){
            logger.error("should be able to merge: " + getSummaryStr());
            System.exit(-1);
        }
        driver.setStatus(Utils.DRIVER);
        driverSet.add(driver);
        uncoveredDistance = rider.getUncoveredDistance();
    }

    public boolean updateUncoveredDistance(){
        if(getRider().getUId() == 76 && getFirstDriverCandidate().getUId() == 92){
            logger.debug("debug!");
        }
        if(initMerged == true){
            logger.error("should not be merged: " + getSummaryStr());
            System.exit(-1);
        }

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

    public void makeFeasibleTaxi(){

        int[] startVertexInfo = rider.getStartVertexInfo();
        int[] endVertexInfo = rider.getEndVertexInfo();

        rider.fillBreaksWithTaxi(startVertexInfo, endVertexInfo);

        // clear driver if it is removed during the makefeasible process
        // update carriedlegs and carrylegs
        HashSet<User> newDriverSet = new HashSet<>();
        for(Leg leg: rider.getLegs()){
            for(Leg driverLeg: leg.getLegs()){
                if(driverLeg != leg && driverLeg.getId() != 0){
                    newDriverSet.add(driverLeg.getUser());
                }
            }
        }
        for(User driver: driverSet){
            if(!newDriverSet.contains(driver)){
                driver.clear();
            }
        }
        driverSet = newDriverSet;
    }

    public void makeFeasible(boolean isTaxiOnly){
        int[] startVertexInfo = rider.getStartVertexInfo();
        int[] endVertexInfo = rider.getEndVertexInfo();

        if(!isTaxiOnly) {
            if (rider.isCandidateParkingPiont(startVertexInfo) && rider.isCandidateParkingPiont(endVertexInfo)) {
                if (rider.getVertexId(startVertexInfo) != rider.getVertexId(endVertexInfo)) {
                    int distToStart = rider.distanceToStart(startVertexInfo);
                    int distToEnd = rider.distanceToEnd(endVertexInfo);

                    if (distToStart < distToEnd) {
                        startVertexInfo = rider.resetStart(startVertexInfo, endVertexInfo);
                    } else {
                        endVertexInfo = rider.resetEnd(endVertexInfo, startVertexInfo);
                    }
                }
            } else {
                startVertexInfo = rider.findAndResetToMostConservativeStart(startVertexInfo);
                endVertexInfo = rider.findAndResetToMostConservativeEnd(endVertexInfo);
            }
        }

        rider.fillBreaksWithTaxi(startVertexInfo, endVertexInfo);

        // clear driver if it is removed during the makefeasible process
        // update carriedlegs and carrylegs
        HashSet<User> newDriverSet = new HashSet<>();
        for(Leg leg: rider.getLegs()){
            for(Leg driverLeg: leg.getLegs()){
                if(driverLeg != leg && driverLeg.getId() != 0){
                    newDriverSet.add(driverLeg.getUser());
                }
            }
        }
        for(User driver: driverSet){
            if(!newDriverSet.contains(driver)){
                driver.clear();
            }
        }
        driverSet = newDriverSet;
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


    // for the static matching algo
    public void refreshAndRegisterDriverSet() {
        // update carriedlegs and carrylegs
        for(Leg leg: rider.getLegs()){
            for(Leg driverLeg: leg.getLegs()){
                if(driverLeg != leg && driverLeg.getId() != 0){
                    driverLeg.addCarryLeg(leg);
                    leg.addCarriedLeg(driverLeg);
                }
            }
        }

        Stack<Leg> stack = new Stack<>();
        HashSet<Leg> visited = new HashSet<>();

        // add all the rider's legs into stack
        for(Leg leg: rider.getLegs()){
            stack.push(leg);
        }

        while(!stack.isEmpty()){
            Leg curLeg = stack.pop();
            if(visited.contains(curLeg)){
                continue;
            }
            visited.add(curLeg);
            curLeg.makeTimeConsistency();
            if(curLeg.getUser().getStatus() == Utils.DRIVER){
                for(Leg nextLeg: curLeg.getCarryLegs()){
                    if(!nextLeg.isTimeConsistent()) {
                        stack.push(nextLeg);
                    }
                }
                // only driver needs to store the prev status, rider just needs to clear to be the init status
                curLeg.storePrevStatus();
            }
            else if (curLeg.getUser().getStatus() == Utils.RIDER){
                for(Leg nextLeg: curLeg.getCarriedLegs()){
                    if(!nextLeg.isTimeConsistent()) {
                        stack.push(nextLeg);
                    }
                    else{
                        // for driver's leg, if is already consistent, store the status
                        nextLeg.storePrevStatus();
                    }
                }
            }
            else{
                logger.error(String.format("Update independent user's leg is invalid: {}", curLeg.getUser()));
            }
        }

        // register driver to global driver set
        for(User driver: driverSet){
            ModelInstance.registeredDriverSet.add(driver);
        }
    }

    // for the dynamic matching algo
    public void refreshAndRegisterDriverSetAndUpdateQueue(PriorityQueue<User> userQueue,
                                                          HashMap<User, ArrayList<UserCoverGroup>> driverGroupMap) {
        if(rider.getUId() == 62){
            logger.debug("debug!");
        }
        // update carriedlegs and carrylegs
        for(Leg leg: rider.getLegs()){
            for(Leg driverLeg: leg.getLegs()){
                if(driverLeg != leg && driverLeg.getId() != 0){
                    driverLeg.addCarryLeg(leg);
                    leg.addCarriedLeg(driverLeg);
                }
            }
        }

        Stack<Leg> stack = new Stack<>();
        HashSet<Leg> visited = new HashSet<>();

        // add all the rider's legs into stack
        for(Leg leg: rider.getLegs()){
            stack.push(leg);
        }

        while(!stack.isEmpty()){
            Leg curLeg = stack.pop();
            if(visited.contains(curLeg)){
                continue;
            }
            visited.add(curLeg);
            curLeg.makeTimeConsistency();
            if(curLeg.getUser().getStatus() == Utils.DRIVER){
                for(Leg nextLeg: curLeg.getCarryLegs()){
                    if(!nextLeg.isTimeConsistent()) {
                        stack.push(nextLeg);
                    }
                }
                // only driver needs to store the prev status, rider just needs to clear to be the init status
                curLeg.storePrevStatus();
                updateQueueByDriver(curLeg.getUser(), userQueue, driverGroupMap);
            }
            else if (curLeg.getUser().getStatus() == Utils.RIDER){
                for(Leg nextLeg: curLeg.getCarriedLegs()){
                    if(!nextLeg.isTimeConsistent()) {
                        stack.push(nextLeg);
                    }
                    else{
                        // for driver's leg, if is already consistent, store the status
                        nextLeg.storePrevStatus();
                    }
                }
            }
            else{
                logger.error(String.format("Update independent user's leg is invalid: {}", curLeg.getUser()));
            }
        }

        // register driver to global driver set
        for(User driver: driverSet){
            ModelInstance.registeredDriverSet.add(driver);
            // still need to update queues for all the driverset even they are consistent
            updateQueueByDriver(driver, userQueue, driverGroupMap);
//            driver.storePrevStatus();
        }
    }

    private void updateQueueByDriver(User driver, PriorityQueue<User> userQueue, HashMap<User, ArrayList<UserCoverGroup>> driverGroupMap){
        ArrayList<UserCoverGroup> userCoverGroupList = driverGroupMap.get(driver);
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

    public User getRider() {
        return rider;
    }
}
