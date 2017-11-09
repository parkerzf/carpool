package nl.twente.bms;


import grph.io.GraphBuildException;
import grph.io.ParseException;
import nl.twente.bms.algo.MatchingSeqStaticAlgo;
import nl.twente.bms.model.ModelInstance;
import nl.twente.bms.model.ModelStats;
import nl.twente.bms.utils.ExcelWriter;
import nl.twente.bms.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class AppStaticTaxiMatch
{
    private static final Logger logger = LoggerFactory.getLogger(AppStaticTaxiMatch.class);

    public static void main( String[] args ) throws IOException, ParseException, GraphBuildException {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        String graphPath = args[0];
        String userPath = args[1];
        if(args.length > 2){
            Utils.TAXI_EUR_PER_KM = Double.parseDouble(args[2]);
        }
        ModelInstance.initInstance(2, graphPath, userPath);

        ModelStats.computeInitCost(ModelInstance.users);

        long start = System.currentTimeMillis();
        MatchingSeqStaticAlgo.run(ModelInstance.users, true, false);
        long end = System.currentTimeMillis();
        ModelStats.runTime = (end - start) / 1000 + Math.round(((end - start) % 1000)/10.0)/100.0;

        ModelStats.computeStats(ModelInstance.users);
        String excelFilePath = userPath.substring(0, userPath.length() - 4) + ".xlsm";
        ExcelWriter.writeOutput(excelFilePath, AppStaticTaxiMatch.class.getName(), 1);

        logger.debug("*******************************************************");
        logger.debug("init total cost: " + ModelStats.initCost);
        logger.debug("init total cost commuter: " + ModelStats.initCostCommuter);
        logger.debug("init total cost business: " + ModelStats.initCostBusiness);
        logger.debug("total cost: " + ModelStats.totalCost);
        logger.debug("total cost commuter: " + ModelStats.totalCostCommuter);
        logger.debug("total cost business: " + ModelStats.totalCostBusiness);
        logger.debug("cost saving total: " + ModelStats.costSavingTotal);
        logger.debug("cost saving commuter: " + ModelStats.costSavingCommuter);
        logger.debug("cost saving business: " + ModelStats.costSavingBusiness);

        logger.debug("taxi cost total: " + ModelStats.taxiCostTotal);
        logger.debug("taxi cost commuter: " + ModelStats.taxiCostCommuter);
        logger.debug("taxi cost business: " + ModelStats.taxiCostBusiness);
        logger.debug("taxi distance: " + ModelStats.taxiDistance);

        logger.debug("runtime: " + ModelStats.runTime);
        logger.debug("abs distance saving total %: " + Utils.roundPercent(ModelStats.absDistanceSavingTotalPercent));
        logger.debug("abs distance saving commuter %: " + Utils.roundPercent(ModelStats.absDistanceSavingCommuterPercent));
        logger.debug("abs distance saving  business %: " + Utils.roundPercent(ModelStats.absDistanceSavingBusinessPercent));
        logger.debug("rel distance saving commuter %: " + Utils.roundPercent(ModelStats.relDistanceSavingCommuterPercent));
        logger.debug("rel distance saving  business %: " + Utils.roundPercent(ModelStats.relDistanceSavingBusinessPercent));

        logger.debug("wait time total: " + ModelStats.waitTimeTotal);
        logger.debug("wait time commuter: " + ModelStats.waitTimeCommuter);
        logger.debug("wait time business: " + ModelStats.waitTimeBusiness);

        logger.debug("time deviation total: " + ModelStats.timeDeviationTotal);
        logger.debug("time deviation commuter: " + ModelStats.timeDeviationCommuter);
        logger.debug("time deviation business: " + ModelStats.timeDeviationBusiness);

        logger.debug("carpool times total: " + ModelStats.carpoolTimesTotal);
        logger.debug("carpool times commuter: " + ModelStats.carpoolTimesCommuter);
        logger.debug("carpool times business: " + ModelStats.carpoolTimesBusiness);

        logger.debug("pickup distribution total: " + Arrays.toString(ModelStats.pickupTimesDistributionTotal));
        logger.debug("pickup distribution commuter: " + Arrays.toString(ModelStats.pickupTimesDistributionCommuter));
        logger.debug("pickup distribution business: " + Arrays.toString(ModelStats.pickupTimesDistributionBusiness));

        logger.debug("single match total %: " + Utils.roundPercent(ModelStats.absSingleMatchDistanceTotalPercent));
        logger.debug("single match commuter %: " + Utils.roundPercent(ModelStats.absSingleMatchDistanceCommuterPercent));
        logger.debug("single match business %: " + Utils.roundPercent(ModelStats.absSingleMatchDistanceBusinessPercent));

        logger.debug("double match total %: " + Utils.roundPercent(ModelStats.absDoubleMatchDistanceTotalPercent));
        logger.debug("double match commuter %: " + Utils.roundPercent(ModelStats.absDoubleMatchDistanceCommuterPercent));
        logger.debug("double match business %: " + Utils.roundPercent(ModelStats.absDoubleMatchDistanceBusinessPercent));

        logger.debug("triple match total %: " + Utils.roundPercent(ModelStats.absTripleMatchDistanceTotalPercent));
        logger.debug("triple match commuter %: " + Utils.roundPercent(ModelStats.absTripleMatchDistanceCommuterPercent));
        logger.debug("triple match business %: " + Utils.roundPercent(ModelStats.absTripleMatchDistanceBusinessPercent));

        logger.debug("num cars: " + ModelStats.numCars);

        List<String> statList = Arrays.asList(
                Utils.getFileName(userPath),
                String.valueOf(ModelStats.initCost),
                String.valueOf(ModelStats.initCostCommuter),
                String.valueOf(ModelStats.initCostBusiness),
                String.valueOf(ModelStats.totalCost),
                String.valueOf(ModelStats.totalCostCommuter),
                String.valueOf(ModelStats.totalCostBusiness),
                String.valueOf(ModelStats.costSavingTotal), String.valueOf(ModelStats.costSavingCommuter),
                String.valueOf(ModelStats.costSavingBusiness),String.valueOf(ModelStats.taxiCostTotal),
                String.valueOf(ModelStats.taxiCostCommuter),String.valueOf(ModelStats.taxiCostBusiness),
                String.valueOf(ModelStats.taxiDistance),String.valueOf(ModelStats.runTime),
                String.valueOf(Utils.roundPercent(ModelStats.absDistanceSavingTotalPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absDistanceSavingCommuterPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absDistanceSavingBusinessPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.relDistanceSavingCommuterPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.relDistanceSavingBusinessPercent)),
                String.valueOf(ModelStats.waitTimeTotal),
                String.valueOf(ModelStats.waitTimeCommuter),String.valueOf(ModelStats.waitTimeBusiness),
                String.valueOf(ModelStats.timeDeviationTotal),String.valueOf(ModelStats.timeDeviationCommuter),
                String.valueOf(ModelStats.timeDeviationBusiness),String.valueOf(ModelStats.carpoolTimesTotal),
                String.valueOf(ModelStats.carpoolTimesCommuter),String.valueOf(ModelStats.carpoolTimesBusiness),
                Arrays.toString(ModelStats.pickupTimesDistributionTotal),
                Arrays.toString(ModelStats.pickupTimesDistributionCommuter),
                Arrays.toString(ModelStats.pickupTimesDistributionBusiness),
                String.valueOf(Utils.roundPercent(ModelStats.absSingleMatchDistanceTotalPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absSingleMatchDistanceCommuterPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absSingleMatchDistanceBusinessPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absDoubleMatchDistanceTotalPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absDoubleMatchDistanceCommuterPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absDoubleMatchDistanceBusinessPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absTripleMatchDistanceTotalPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absTripleMatchDistanceCommuterPercent)),
                String.valueOf(Utils.roundPercent(ModelStats.absTripleMatchDistanceBusinessPercent)),
                String.valueOf(ModelStats.numCars)
                );


        String joinedStats = String.join("|", statList);
        logger.info(joinedStats);

    }
}
