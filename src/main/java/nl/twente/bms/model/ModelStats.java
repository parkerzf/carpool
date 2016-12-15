package nl.twente.bms.model;

import nl.twente.bms.struct.User;
import nl.twente.bms.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class ModelStats {
    public static double initCost;
    public static double initCostCommuter;
    public static double initCostBusiness;
    public static double totalCost;
    public static double totalCostCommuter;
    public static double totalCostBusiness;
//    public static double absCostSavingTotalPercent;
//    public static double absCostSavingCommuterPercent;
//    public static double absCostSavingBusinessPercent;
    public static double costSavingTotal;
    public static double costSavingCommuter;
    public static double costSavingBusiness;
    public static double taxiCostTotal;
    public static double taxiCostCommuter;
    public static double taxiCostBusiness;
    public static double taxiDistance;

//    public static double relTaxiCostCommuterPercent;
//    public static double relTaxiCostBusinessPercent;

    public static double runTime;
    public static double absDistanceSavingTotalPercent;
    public static double absDistanceSavingCommuterPercent;
    public static double absDistanceSavingBusinessPercent;
    public static double relDistanceSavingCommuterPercent;
    public static double relDistanceSavingBusinessPercent;

    public static double waitTimeTotal = 0;
    public static double waitTimeCommuter = 0;
    public static double waitTimeBusiness = 0;
    public static int timeDeviationTotal;
    public static int timeDeviationCommuter;
    public static int timeDeviationBusiness;
    // carry by other users, not taxi
    public static int carpoolTimesTotal;
    public static int carpoolTimesCommuter;
    public static int carpoolTimesBusiness;

    // pickup by other users, and taxi
//    public static HashMap<Integer, Integer> pickupTimesDistributionTotal = new HashMap<>();
//    public static HashMap<Integer, Integer> pickupTimesDistributionCommuter = new HashMap<>();
//    public static HashMap<Integer, Integer> pickupTimesDistributionBusiness = new HashMap<>();

    public static int[] pickupTimesDistributionTotal = new int[10];
    public static int[] pickupTimesDistributionCommuter = new int[10];
    public static int[] pickupTimesDistributionBusiness = new int[10];

    // the percentage is generated by dividing the matchDistanceTotal
    public static double absSingleMatchDistanceTotalPercent; // cap = 2
    public static double absSingleMatchDistanceCommuterPercent; // cap = 2
    public static double absSingleMatchDistanceBusinessPercent; // cap = 2
    public static double absDoubleMatchDistanceTotalPercent; // cap = 3
    public static double absDoubleMatchDistanceCommuterPercent; // cap = 3
    public static double absDoubleMatchDistanceBusinessPercent; // cap = 3
    public static double absTripleMatchDistanceTotalPercent; // cap = 4
    public static double absTripleMatchDistanceCommuterPercent; // cap = 4
    public static double absTripleMatchDistanceBusinessPercent; // cap = 4

    public static int numCars = 0;

    public static void computeStats(List<User> users) {

        int distanceTotal = 0;
        int distanceCommuter = 0;
        int distanceSavingTotal = 0;
        int distanceSavingCommuter = 0;

        Arrays.fill(pickupTimesDistributionTotal, 0);
        Arrays.fill(pickupTimesDistributionCommuter, 0);
        Arrays.fill(pickupTimesDistributionBusiness, 0);

        int matchDistanceTotal = 0;
        int singleMatchDistanceTotal = 0;
        int singleMatchDistanceCommuter = 0;

        int doubleMatchDistanceTotal = 0;
        int doubleMatchDistanceCommuter = 0;

        int tripleMatchDistanceTotal = 0;
        int tripleMatchDistanceCommuter = 0;

        for(User user: users){

            totalCost += user.getCost(true);
            totalCostCommuter += user.getCommuterCost(true);

            initCostBusiness = initCost - initCostCommuter;

            costSavingTotal += user.getSelfDrivingCost() - user.getCost(true);
            costSavingCommuter += user.getCommuterSelfDrivingCost() - user.getCommuterCost(true);

            taxiCostTotal += user.getTaxiCost();
            taxiCostCommuter += user.getCommuterTaxiCost();

            taxiDistance += user.getTaxiDistance();

            distanceTotal += user.getTotalDistance();
            distanceCommuter += user.getCommuterDistance();
            distanceSavingTotal += user.getCoveredDistance();
            distanceSavingCommuter += user.getCommuterCoveredDistance();

            timeDeviationTotal += user.getTimeDeviation();
            timeDeviationCommuter += user.getCommuterTimeDeviation();

            carpoolTimesTotal += user.getCoveredTimes();
            carpoolTimesCommuter += user.getCommuterCoveredTimes();

            user.setPickupTimes(pickupTimesDistributionTotal, pickupTimesDistributionCommuter, pickupTimesDistributionBusiness);

            matchDistanceTotal += user.getMatchDistance(0);

            singleMatchDistanceTotal += user.getMatchDistance(2);
            singleMatchDistanceCommuter += user.getCommuterMatchDistance(2);

            doubleMatchDistanceTotal += user.getMatchDistance(3);
            doubleMatchDistanceCommuter += user.getCommuterMatchDistance(3);

            tripleMatchDistanceTotal += user.getMatchDistance(4);
            tripleMatchDistanceCommuter += user.getCommuterMatchDistance(4);

            if(user.needSelfCar()){
                numCars++;
            }
        }
        totalCostBusiness = totalCost - totalCostCommuter;

        costSavingBusiness = costSavingTotal - costSavingCommuter;
        taxiCostBusiness = taxiCostTotal - taxiCostCommuter;
        int distanceSavingBusiness = distanceSavingTotal - distanceSavingCommuter;

        absDistanceSavingTotalPercent = distanceSavingTotal * 1.0/distanceTotal;
        absDistanceSavingCommuterPercent = distanceSavingCommuter * 1.0/distanceTotal;
        absDistanceSavingBusinessPercent = distanceSavingBusiness * 1.0/distanceTotal;

        relDistanceSavingCommuterPercent = distanceSavingCommuter * 1.0/distanceCommuter;

        int distanceBusiness = distanceTotal - distanceCommuter;
        relDistanceSavingBusinessPercent = distanceSavingBusiness * 1.0/distanceBusiness;


        timeDeviationBusiness = timeDeviationTotal - timeDeviationCommuter;

        carpoolTimesBusiness = carpoolTimesTotal - carpoolTimesCommuter;

        int singleMatchDistanceBusiness = singleMatchDistanceTotal - singleMatchDistanceCommuter;

        absSingleMatchDistanceTotalPercent = singleMatchDistanceTotal * 1.0/matchDistanceTotal;
        absSingleMatchDistanceCommuterPercent = singleMatchDistanceCommuter * 1.0/matchDistanceTotal;
        absSingleMatchDistanceBusinessPercent = singleMatchDistanceBusiness * 1.0/matchDistanceTotal;

        int doubleMatchDistanceBusiness = doubleMatchDistanceTotal - doubleMatchDistanceCommuter;

        absDoubleMatchDistanceTotalPercent = doubleMatchDistanceTotal * 1.0/matchDistanceTotal;
        absDoubleMatchDistanceCommuterPercent = doubleMatchDistanceCommuter * 1.0/matchDistanceTotal;
        absDoubleMatchDistanceBusinessPercent = doubleMatchDistanceBusiness * 1.0/matchDistanceTotal;

        int tripleMatchDistanceBusiness = tripleMatchDistanceTotal - tripleMatchDistanceCommuter;

        absTripleMatchDistanceTotalPercent = tripleMatchDistanceTotal * 1.0/matchDistanceTotal;
        absTripleMatchDistanceCommuterPercent = tripleMatchDistanceCommuter * 1.0/matchDistanceTotal;
        absTripleMatchDistanceBusinessPercent = tripleMatchDistanceBusiness * 1.0/matchDistanceTotal;


    }

    public static void computeInitCost(List<User> users) {
        for(User user: users){
            initCost += user.getCost(true);
            initCostCommuter += user.getCommuterCost(true);
        }
        initCostBusiness = initCost - initCostCommuter;
    }
}