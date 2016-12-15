package nl.twente.bms;


import grph.io.GraphBuildException;
import grph.io.ParseException;
import nl.twente.bms.algo.SavingSeqAlgo;
import nl.twente.bms.model.ModelInstance;
import nl.twente.bms.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;



public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws IOException, ParseException, GraphBuildException {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        logger.info("Total cost before: " + Utils.computeCost(ModelInstance.users));


        SavingSeqAlgo.run(ModelInstance.users);
//        SavingAlgo.run(ModelInstance.users);

        logger.info("Total cost after: " + Utils.computeCost(ModelInstance.users));

    }
}
