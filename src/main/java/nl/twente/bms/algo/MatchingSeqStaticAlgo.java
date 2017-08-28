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
public class MatchingSeqStaticAlgo {
    private static final Logger logger = LoggerFactory.getLogger(MatchingSeqStaticAlgo.class);

    public static void run(List<User> users) {
        // manage the users in non descending order of minimum uncovered distance in the sorted list
        List<User> userSortedList = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            userSortedList.add(users.get(i));
            for (int j = 0; j < users.size(); j++) {
                if(i == j) continue;
                User userA = users.get(i);
                User userB = users.get(j);
                UserCoverGroup userCoverGroup = new UserCoverGroup(userA, userB);
                if(userCoverGroup.isCoveredBeforeInitMerge()){
                    PriorityQueue<UserCoverGroup> queue = userA.getQueue();
                    queue.offer(userCoverGroup);
                }
            }
        }

        Collections.sort(userSortedList);
        logger.debug("Sorted User list size: {}", userSortedList.size());

        for (int i = 0; i < users.size(); i++) {
            logger.debug("User {}'s Queue size: {}", userSortedList.get(i).getUId(), userSortedList.get(i).getQueue().size());
            logger.debug(userSortedList.get(i).getSummaryStr());
        }


        // sequential extension
        List<UserCoverGroup> userGroups = new ArrayList<>();

        User curUser;
        PriorityQueue<UserCoverGroup> curQueue;
        for (int i = 0; i < users.size(); i++) {
            curUser = userSortedList.get(i);
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
                    logger.debug(String.format("Final: %s", firstGroup.getSummaryStr()));
                    userGroups.add(firstGroup);
                    firstGroup.refreshAndRegisterDriverSet();
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
