package nl.twente.bms.algo;

import nl.twente.bms.model.ModelInstance;
import nl.twente.bms.struct.User;
import nl.twente.bms.struct.UserCoverGroup;
import nl.twente.bms.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class MatchingSeqAlgo {
    private static final Logger logger = LoggerFactory.getLogger(MatchingSeqAlgo.class);

    public static void run(List<User> users) {
        // manage the users in non descending order of minimum uncovered distance in the priority queue
        PriorityQueue<User> userQueue = new PriorityQueue<>();
        // manage the user pair feasible pair in a hashmap
        HashMap<User, ArrayList<UserCoverGroup>> driverGroupMap = new HashMap<>();

        for (int i = 0; i < users.size(); i++) {
            for (int j = 0; j < users.size(); j++) {
                if(i == j) continue;
                User userA = users.get(i);
                User userB = users.get(j);
                UserCoverGroup userCoverGroup = new UserCoverGroup(userA, userB);
                if(userCoverGroup.isCoveredBeforeInitMerge()){
                    ArrayList<UserCoverGroup> userCoverGroupList = driverGroupMap.get(userB);
                    if( userCoverGroupList == null){
                        userCoverGroupList = new ArrayList<>();
                    }
                    userCoverGroupList.add(userCoverGroup);
                    driverGroupMap.put(userB, userCoverGroupList);

                    PriorityQueue<UserCoverGroup> queue = userA.getQueue();
                    queue.offer(userCoverGroup);
                }
            }
        }

        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getQueue().size() > 0){
                userQueue.offer(users.get(i));
            }
            else{
                logger.info("u{} is not enqueued because no user cover it", users.get(i).getUId());
            }
        }



        logger.debug("Driver Group Map size: {}", driverGroupMap.size());
        logger.debug("User Queue size: {}", userQueue.size());

        for(User user: users) {
            logger.debug(user.getSummaryStr());
        }


        // sequential extension
        List<UserCoverGroup> userGroups = new ArrayList<>();

        User curUser;
        PriorityQueue<UserCoverGroup> curQueue;
        while((curUser = userQueue.poll()) != null){

            if(curUser.getUId() == 738){
                logger.debug("debug!");
            }
            if(curUser.getStatus() == Utils.DRIVER) continue;
            curQueue = curUser.getQueue();

            UserCoverGroup firstGroup;
            while((firstGroup = curQueue.poll()) != null){
                if(firstGroup.isFeasibleBeforeInitMerge() && firstGroup.isCoveredBeforeInitMerge()){
                    break;
                }
            }

            if(firstGroup != null) {
                firstGroup.initMerge();
                logger.debug(String.format("init merged: %s", firstGroup.getSummaryStr()));

                if(!firstGroup.isAllCovered()){
                    UserCoverGroup curGroup;
                    while ((curGroup = curQueue.poll()) != null) {
                        if (!curGroup.isFeasibleBeforeInitMerge()) {
                            continue;
                        }

                        logger.debug(String.format("before update pair: %s", curGroup.getSummaryStr()));
                        boolean isUpdated = curGroup.updateUncoveredDistance();
                        logger.debug(String.format("after  update pair: %s", curGroup.getSummaryStr()));

                        if(!curGroup.isCoveredBeforeInitMerge()){
                            continue;
                        }

                        if (isUpdated) {
                            curQueue.offer(curGroup);
                        } else {
                            firstGroup.addDriver(curGroup.getFirstDriverCandidate());
                            logger.debug(String.format("add: %s", firstGroup.getSummaryStr()));

                            if(firstGroup.isAllCovered()) break;
                        }
                    }
                    firstGroup.makeFeasible();
                }

                ModelInstance.registeredFinishedRiderSet.add(firstGroup.getRider());
                if (firstGroup.hasSaving()) {
                    userGroups.add(firstGroup);
                    firstGroup.registerDriverSet();
                    firstGroup.updateQueue(userQueue, driverGroupMap);
                    logger.debug(String.format("Final: %s", firstGroup.getSummaryStr()));
                }
                else {
                    firstGroup.clear();
                    logger.debug(String.format("Clear: %s", firstGroup.getSummaryStr()));
                }
            }
        }

        logger.debug("*******************************************************");

        for(UserCoverGroup finalGroup: userGroups) {
            logger.debug(finalGroup.getSummaryStr());
        }

        logger.debug("*******************************************************");

        for(User user: users) {
            logger.debug(user.getSummaryStr());
        }
    }
}
