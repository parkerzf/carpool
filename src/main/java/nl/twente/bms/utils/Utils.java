package nl.twente.bms.utils;

import nl.twente.bms.struct.Leg;
import nl.twente.bms.struct.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class Utils {
    public static final double DRIVING_EUR_PER_KM = 0.5;
    public static double TAXI_EUR_PER_KM = 1000;
    public static final int SPEED_PER_MIN = 1;
    public static final int CAPACITY = 4;

    public static final int INDEPENDENT = 0;
    public static final int DRIVER = 1;
    public static final int RIDER = 2;

    public static int nextUserId = 0;
    public static int nextLegId = 0;



    public static int genNextUserId(){
        return nextUserId++;
    }

    public static int genNextLegId(){
        return nextLegId++;
    }

    public static boolean isOverlapped(int minX, int maxX, int minY, int maxY){

        if(minX >= minY && minX <= maxY) return true;

        if(minY >= minX && minY <= maxX) return true;

        return false;
    }

    public static boolean isOverlapped(Leg legA, Leg legB) {
        return isOverlapped(legA.getEarliestDepartureTime(), legA.getLatestArrivalTime(),
                legB.getEarliestDepartureTime(), legB.getLatestArrivalTime());
    }

    public static boolean isLater(Leg legA, Leg legB){
        return legA.getLatestArrivalTime() > legB.getLatestArrivalTime();
    }

    public static boolean isEarlier(Leg legA, Leg legB) {
        return legA.getEarliestDepartureTime() < legB.getEarliestDepartureTime();
    }

    public static Pair<Integer, Integer> getOverlappedInterval(int minX, int maxX, int minY, int maxY) {
        return Pair.of(Math.max(minX, minY), Math.min(maxX, maxY));
    }

    public static double computeCost(List<User> users) {
        double cost = 0;
        for(User user: users){
            cost += user.getCost(true);
        }
        return cost;
    }

    public static void increaseCount(int[] arr, int key){
        assert key < arr.length;
        arr[key]++;
    }

    public static double roundPercent(double val){
        return Math.round(val * 10000.0) / 100.0;
    }

    public static String getFileName(String filePath) {
        int idx = filePath.lastIndexOf("/");
        return idx >= 0 ? filePath.substring(idx + 1) : filePath;
    }

}
