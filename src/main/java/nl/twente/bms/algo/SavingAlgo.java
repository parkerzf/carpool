package nl.twente.bms.algo;

import nl.twente.bms.struct.User;
import nl.twente.bms.struct.UserGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class SavingAlgo {
    private static final Logger logger = LoggerFactory.getLogger(SavingAlgo.class);

    public static void run(List<User> users) {

        // order the pairs in descending order of merged overlapped cost
        List<UserGroup> sortedUserGroupList = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                UserGroup userGroup = new UserGroup(users.get(i), users.get(j));
                sortedUserGroupList.add(userGroup);
            }
        }
        Collections.sort(sortedUserGroupList);
        logger.info("Sorted UserGroup list size: {}", sortedUserGroupList.size());

        // merge
        List<UserGroup> userGroups = new ArrayList<>();
        Hashtable<User, UserGroup> userUserGroupHashTable = new Hashtable<>();

        UserGroup group = sortedUserGroupList.get(0);
        logger.info("The largest saving pair: {}", group);
        group.initMerge(userUserGroupHashTable);
        userGroups.add(group);

        for(int i = 1; i < sortedUserGroupList.size(); ++i){
            UserGroup curGroup = sortedUserGroupList.get(i);
            logger.info("(" + curGroup.getFirstUser().getUId() + "," + curGroup.getSecondUser().getUId()+ "):" +
            curGroup.getInitOverlappedCost());

            UserGroup firstGroup = userUserGroupHashTable.get(curGroup.getFirstUser());
            UserGroup secondGroup = userUserGroupHashTable.get(curGroup.getSecondUser());

            if(firstGroup != null && secondGroup!= null){
                if(!firstGroup.equals(secondGroup)){
                    // Merge mode: (1,2), (3,4), (2,3) -> (1,2,3,4)
                    logger.info("Merge mode");
                    int firstGroupIdx = sortedUserGroupList.indexOf(firstGroup);
                    int secondGroupIdx = sortedUserGroupList.indexOf(secondGroup);
                    if(firstGroupIdx > secondGroupIdx){
                        boolean isMerged = secondGroup.merge(firstGroup, userUserGroupHashTable);
                        if(isMerged) userGroups.remove(firstGroup);
                    }
                    else{
                        boolean isMerged = firstGroup.merge(secondGroup, userUserGroupHashTable);
                        if(isMerged) userGroups.remove(secondGroup);
                    }
                }
                else{
                    logger.info("Skip mode");
                }
            }
            else if(firstGroup == null && secondGroup == null){
                // Init mode: (3,4) -> (3,4)
                logger.info("Init mode");
                curGroup.initMerge(userUserGroupHashTable);
                userGroups.add(curGroup);
            }
            else {
                // Extension mode: (1,2), (1,3) -> (1,2,3)
                logger.info("Extension mode");
                if (firstGroup != null) {
                    firstGroup.extend(curGroup.getSecondUser(), userUserGroupHashTable);
                } else {
                    secondGroup.extend(curGroup.getFirstUser(), userUserGroupHashTable);
                }
            }
        }




        // post separation


        for(UserGroup curGroup: userGroups){
            logger.info(curGroup.getSummaryStr());
        }

    }
}
