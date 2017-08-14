package nl.twente.bms.algo;

import com.carrotsearch.hppc.IntIntMap;
import com.carrotsearch.hppc.IntIntOpenHashMap;
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
        // manage the users in non descending order of minimum uncovered distance in the priority queue
        PriorityQueue<User> userQueue = new PriorityQueue<>();
        // manage the user pair feasible pair in a hashmap
//        HashMap<User, User> userFeasibleMap = new HashMap<>();
        IntIntMap userFeasibleMap = new IntIntOpenHashMap();

        for (int i = 0; i < users.size(); i++) {
            for (int j = 0; j < users.size(); j++) {
                if(i == j) continue;

                UserCoverGroup userCoverGroup = new UserCoverGroup(users.get(i), users.get(j));
                if(userCoverGroup.isInitFeasible()){
                    userFeasibleMap.put(i, j);
                    PriorityQueue<UserCoverGroup> queue = users.get(i).getQueue();
                    queue.offer(userCoverGroup);
                }
            }
        }

        for (int i = 0; i < users.size(); i++) {
            userQueue.offer(users.get(i));
        }

        logger.debug("User Feasible Map size: {}", userFeasibleMap.size());
        logger.debug("User Queue size: {}", userQueue.size());


        // sequential extension
        List<UserCoverGroup> userGroups = new ArrayList<>();
        PriorityQueue<UserCoverGroup> curQueue;

        for (int i = 0; i < users.size(); i++) {
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
                    firstGroup.updateQueue(userQueue);
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
