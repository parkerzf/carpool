package nl.twente.bms.algo;

import nl.twente.bms.struct.User;
import nl.twente.bms.struct.UserGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class SavingSeqAlgo {
    private static final Logger logger = LoggerFactory.getLogger(SavingSeqAlgo.class);

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

        // sequential extension
        List<UserGroup> userGroups = new ArrayList<>();
        Hashtable<User, UserGroup> userUserGroupHashTable = new Hashtable<>();

        for(int i = 0; i < sortedUserGroupList.size(); ++i){
            UserGroup group = sortedUserGroupList.get(i);
            if(userUserGroupHashTable.containsKey(group.getFirstUser()) ||
                    userUserGroupHashTable.containsKey(group.getSecondUser())) continue;
            logger.info("The group to extend: {}", group);
            group.initMerge(userUserGroupHashTable);
            userGroups.add(group);
            for(int j = i+1; j < sortedUserGroupList.size(); ++j){
                UserGroup curGroup = sortedUserGroupList.get(j);
                logger.info("The current group: {}", curGroup);
                Boolean hasFirstUser = group.hasUser(curGroup.getFirstUser());
                Boolean hasSecondUser= group.hasUser(curGroup.getSecondUser());

                boolean isExtended = false;
                if(hasFirstUser && !hasSecondUser) {
                    isExtended = group.extend(curGroup.getSecondUser(), userUserGroupHashTable);
                }
                else if(!hasFirstUser&& hasSecondUser){
                    isExtended = group.extend(curGroup.getFirstUser(), userUserGroupHashTable);
                }

                if(isExtended){
                    logger.info("Group Extended: {}", group);
                }
            }
            group.syncTime();
        }

        // post separation



        for(UserGroup curGroup: userGroups){
            logger.info(curGroup.getSummaryStr());
        }
    }
}
