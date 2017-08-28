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
public class BasicAlgo {
    private static final Logger logger = LoggerFactory.getLogger(BasicAlgo.class);

    public static void run(List<User> users) {
        // Add a hash table between rider and the related group

        HashMap<User, ArrayList<UserCoverGroup>> userGroupListMap = new HashMap();
        for(int i = 0; i < users.size(); i++){
            userGroupListMap.put(users.get(i), new ArrayList<>());
        }

        List<User> sortedUserList = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            sortedUserList.add(users.get(i));
            for (int j = 0; j < users.size(); j++) {
                if(i == j) continue;
                UserCoverGroup userCoverGroup = new UserCoverGroup(users.get(i), users.get(j));
                if(userCoverGroup.isFeasibleBeforeInitMerge()){
                    ArrayList<UserCoverGroup> list = userGroupListMap.get(users.get(i));
                    list.add(userCoverGroup);
                }
            }
        }

//        Collections.sort(sortedUserList);

        logger.debug("Sorted User list size: {}", sortedUserList.size());

//        for (int i = 0; i < users.size(); i++) {
//            logger.debug("User {}'s List size: {}", sortedUserList.get(i).getUId(), userGroupListMap.get(sortedUserList.get(i)).size());
//            logger.debug(sortedUserList.get(i).toString());
//        }

        // sequential extension
        List<UserCoverGroup> userGroups = new ArrayList<>();
        ArrayList<UserCoverGroup> curList;

        for (int i = 0; i < users.size(); i++) {
            curList = userGroupListMap.get(sortedUserList.get(i));
            UserCoverGroup firstGroup = null;
            for(UserCoverGroup curGroup : curList){
                if(curGroup.isFeasibleBeforeInitMerge()){
                    firstGroup = curGroup;
                    break;
                }
            }

            if(firstGroup != null) {
                firstGroup.initMerge();

                for(UserCoverGroup curGroup : curList){
                    boolean isUpdated = curGroup.updateUncoveredDistance();
                    if (!curGroup.isFeasibleBeforeInitMerge()) {
                        continue;
                    }

                    if (isUpdated) {
                        firstGroup.addDriver(curGroup.getFirstDriverCandidate());
                    }
                }

                firstGroup.makeFeasible();
                if (firstGroup.isFeasible()) {
                    userGroups.add(firstGroup);
//                    firstGroup.refreshAndRegisterDriverSetAndUpdateQueue();
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
