package nl.twente.bms.algo;

import nl.twente.bms.struct.User;
import nl.twente.bms.struct.UserCoverGroup;
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
        // manage the pairs in non descending order of uncovered distance in the priority queue
        // Add a sorted list of user, the user class add a new attribute minUnCoveredDistance, sort based on this
        // Add a hash table between rider and the related group

//        HashMap<User, PriorityQueue<UserCoverGroup>> userGroupQueueMap = new HashMap();
//        for(int i = 0; i < users.size(); i++){
//            userGroupQueueMap.put(users.get(i), new PriorityQueue<>());
//        }

//        List<User> sortedUserList = new ArrayList<>();
        PriorityQueue<User> userQueue = new PriorityQueue<>();

        for (int i = 0; i < users.size(); i++) {
//            sortedUserList.add(users.get(i));
            userQueue.offer(users.get(i));
            for (int j = 0; j < users.size(); j++) {
                if(i == j) continue;
                UserCoverGroup userCoverGroup = new UserCoverGroup(users.get(i), users.get(j));
                if(userCoverGroup.isInitFeasible()){
//                    PriorityQueue<UserCoverGroup> queue = userGroupQueueMap.get(users.get(i));
                    PriorityQueue<UserCoverGroup> queue = users.get(i).getQueue();
                    queue.offer(userCoverGroup);
                }
            }
        }

//        Collections.sort(sortedUserList);
//        logger.debug("Sorted User list size: {}", sortedUserList.size());
        logger.debug("User Queue size: {}", userQueue.size());

//        for (int i = 0; i < users.size(); i++) {
////            logger.debug("User {}'s Queue size: {}", sortedUserList.get(i).getUId(), userGroupQueueMap.get(sortedUserList.get(i)).size());
//            logger.debug("User {}'s Queue size: {}",
//                    sortedUserList.get(i).getUId(),
//                    sortedUserList.get(i).getQueue().size());
//            logger.debug(sortedUserList.get(i).toString());
//        }

        // sequential extension
        List<UserCoverGroup> userGroups = new ArrayList<>();
        PriorityQueue<UserCoverGroup> curQueue;

        for (int i = 0; i < users.size(); i++) {
//            curQueue = sortedUserList.get(i).getQueue();
            curQueue = userQueue.poll().getQueue();

            UserCoverGroup firstGroup;
            while((firstGroup = curQueue.poll()) != null){
                if(firstGroup.isInitFeasible()){
                    break;
                }
            }

            if(firstGroup != null) {
                firstGroup.initMerge();

                UserCoverGroup curGroup;
                while ((curGroup = curQueue.poll()) != null) {
                    boolean isUpdated = curGroup.updateUncoveredDistance();
                    if (!curGroup.isInitFeasible()) {
                        continue;
                    }

                    if (isUpdated) {
                        curQueue.offer(curGroup);
                    } else {
                        firstGroup.addDriver(curGroup.getFirstDriverCandidate());
                    }
                }

                firstGroup.makeFeasible();
                if (firstGroup.hasSaving()) {
                    userGroups.add(firstGroup);
                    firstGroup.registerDriverSet();
                }
                else{
                    firstGroup.clear();
                }
            }
        }

        for(UserCoverGroup finalGroup: userGroups) {
            logger.debug(finalGroup.getSummaryStr());
        }

        for(User user: users) {
            logger.debug(user.toString());
        }

    }
}
