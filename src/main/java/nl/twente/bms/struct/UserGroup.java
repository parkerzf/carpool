package nl.twente.bms.struct;

import brite.Util.Util;
import nl.twente.bms.utils.Utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class UserGroup implements Comparable<UserGroup> {

    private List<User> users;
    private double initOverlappedCost;

    public UserGroup(User userA, User userB){
        users = new ArrayList<>();
        users.add(userA);
        users.add(userB);
        initOverlappedCost = userA.getOverlappedCost(userB);
    }


    public void initMerge(Hashtable<User, UserGroup> userUserGroupHashtable) {
        assert (users.size() == 2);
        users.get(0).merge(users.get(1));
        userUserGroupHashtable.put(users.get(0), this);
        userUserGroupHashtable.put(users.get(1), this);
    }



    public boolean extend(User user, Hashtable<User, UserGroup> userUserGroupHashTable){
        double singleCost = user.getCost(false);

        // case 1: the user becomes a driver, check whether it can take any rider
        double driverCost = getCostAsDriver(user);

        // case 2: the user becomes a rider, check whether any driver can take the user
        double riderCost = getCostAsRider(user);

        if(driverCost >= singleCost && riderCost >= singleCost){
            return false;
        }

        if(driverCost < riderCost) {
            assignAsDriver(user);
        }
        else{
            assignAsRider(user);
        }

        users.add(user);
        userUserGroupHashTable.put(user, this);
        return true;
    }


    // Merge someUserGroup to this group without changing the role
    public boolean merge(UserGroup someUserGroup, Hashtable<User, UserGroup> userUserGroupHashTable) {

        boolean isMerged = false;
        for(User curUser: users){
            if(curUser.getStatus() == Utils.DRIVER){
                isMerged |= someUserGroup.assignAsDriver(curUser);
            }
        }

        for(User curUser: someUserGroup.users){
            if(curUser.getStatus() == Utils.DRIVER){
                isMerged |= assignAsDriver(curUser);
            }
        }

        if(isMerged){
            for(User curUser: someUserGroup.users){
                users.add(curUser);
                userUserGroupHashTable.put(curUser, this);
            }
        }

        return isMerged;
    }


    private boolean assignAsDriver(User user) {
        user.setStatus(Utils.DRIVER);
        boolean isMerged = false;
        for(User curUser: users){
            if(curUser.getStatus() != Utils.DRIVER){
                boolean merged = user.merge(curUser);
                if(merged)
                    curUser.setStatus(Utils.RIDER);
                isMerged |= merged;
            }
        }

        return isMerged;
    }


    private boolean assignAsRider(User user) {
        boolean isMerged = false;
        for(User curUser: users){
            if(curUser.getStatus() == Utils.DRIVER){
                boolean merged = curUser.merge(user);
                isMerged |= merged;
                if(merged)
                    user.setStatus(Utils.RIDER);
            }
        }
        return isMerged;
    }


    // Note that this is the saving upper bound, by considering each driver individually
    private double getCostAsRider(User user) {
        double cost = user.getCost(false);

        for(User curUser: users){
            if(curUser.getStatus() == Utils.DRIVER){
                cost -= curUser.getOverlappedCost(user);
            }
        }

        return cost;
    }

    // Note that this is the saving upper bound, by considering each rider individually
    private double getCostAsDriver(User user) {
        double cost = user.getCost(false);

        for(User curUser: users){
            if(curUser.getStatus() != Utils.DRIVER){
                cost -= user.getOverlappedCost(curUser);
            }
        }

        return cost;
    }

    @Override
    public int compareTo(UserGroup someUserGroup) {
        // desc order of overlapped cost
        return Double.compare(someUserGroup.initOverlappedCost, initOverlappedCost);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n[");
        for(User user: users){
            sb.append("\n");
            sb.append(user.toString());
            sb.append(',');
        }
        sb.append("\n]");
        return sb.toString();
    }

    public String getSummaryStr(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n[\n");
        sb.append("#users:" + users.size() + "\n");
        sb.append("uncovered distance:" + getUncoveredDistance() + "\n");
        for(User user: users){
            if(user.getStatus() == Utils.DRIVER)
                sb.append("u" + user.getUId() + ":driver|");
            else if(user.getStatus() == Utils.RIDER)
                sb.append("u" + user.getUId() + ":rider|");
            else
                sb.append("u" + user.getUId() + ":independent|");
        }
        sb.append("\n]");
        return sb.toString();

    }

    public int getUncoveredDistance() {
        int uncoveredDistance = 0;
        for(User user: users){
            uncoveredDistance += user.getUncoveredDistance();
        }
        return uncoveredDistance;
    }


    public User getFirstUser() {
        return users.get(0);
    }

    public User getSecondUser() {
        return users.get(1);
    }

    public Boolean hasUser(User user) {
        return users.contains(user);
    }

    public void syncTime(){
        for(User user: users){
            user.syncTime();
        }
    }

    public double getInitOverlappedCost() {
        return initOverlappedCost;
    }
}
