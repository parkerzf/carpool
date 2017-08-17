package nl.twente.bms.model;

import com.google.common.collect.HashBasedTable;
import grph.io.ParseException;
import nl.twente.bms.struct.Leg;
import nl.twente.bms.struct.StationGraph;
import nl.twente.bms.struct.User;
import nl.twente.bms.utils.GraphReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class ModelInstance {
    public static StationGraph graph = new StationGraph();
    public static User taxiUser = new User();
    public static Leg taxiLeg = new Leg(graph, taxiUser, -1, -1, -1, -1);

    public static List<User> users = new ArrayList<>();
    public static HashBasedTable<Integer, Integer, Integer> legVertexCapacityTable = HashBasedTable.create();
    public static HashBasedTable<Integer, Integer, Integer> legVertexMinTimeTable = HashBasedTable.create();
    public static HashBasedTable<Integer, Integer, Integer> legVertexMaxTimeTable = HashBasedTable.create();

    public static HashSet<User> registeredDriverSet = new HashSet<>();
    public static HashSet<User> registeredFailedRiderSet = new HashSet<>();

    public static void initInstance(int type, String graphPath, String userPath){
        if(type == 0) initTestInstance();
        else if(type ==1) initParkingPointInstance();
        else initRealInstance(graphPath, userPath);
    }

    private static void initTestInstance() {
        // load graph structure
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);
        graph.addVertex(7);

        int e = graph.addUndirectedSimpleEdge(1, 2);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(2, 3);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(2, 4);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(4, 5);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(4, 6);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(4, 7);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(5, 7);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(6, 7);
        graph.setEdgeWeight(e, 1);

        // load user structure
        User userA = new User();
        Leg legA = new Leg(graph, userA, 1, 6, 1, 5);
        userA.addLeg(legA);
        Leg legABack = new Leg(graph, userA, 6, 1, 15, 20);
        userA.addLeg(legABack);
        userA.genCandidateParkingPoints();
        users.add(userA);


        User userB = new User();
        Leg legB = new Leg(graph, userB, 1, 5, 1, 5);
        userB.addLeg(legB);
        Leg legBBack = new Leg(graph, userB, 5, 1, 15, 20);
        userB.addLeg(legBBack);
        userB.genCandidateParkingPoints();
        users.add(userB);

        User userC = new User();
        Leg legC = new Leg(graph, userC, 3, 6, 1, 5);
        userC.addLeg(legC);
        Leg legCBack = new Leg(graph, userC, 6, 3, 15, 20);
        userC.addLeg(legCBack);
        userC.genCandidateParkingPoints();
        users.add(userC);
    }

    public static void initParkingPointInstance(){
        // load graph structure
        graph.addVertex(0);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);
        graph.addVertex(7);


        int e = graph.addUndirectedSimpleEdge(0, 1);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(1, 2);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(2, 3);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(3, 4);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(4, 5);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(5, 6);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(6, 7);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(7, 2);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(7, 8);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(8, 9);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(9, 10);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(10, 11);
        graph.setEdgeWeight(e, 1);
        e = graph.addUndirectedSimpleEdge(11, 12);
        graph.setEdgeWeight(e, 1);

        // load user structure
        User userA = new User();
        Leg legA1 = new Leg(graph, userA, 0, 4, 1, 6);
        userA.addLeg(legA1);
        Leg legA2 = new Leg(graph, userA, 4, 5, 6, 8);
        userA.addLeg(legA2);
        Leg legA3 = new Leg(graph, userA, 5, 6, 8, 10);
        userA.addLeg(legA3);
        Leg legA4 = new Leg(graph, userA, 6, 0, 10, 16);
        userA.addLeg(legA4);


        userA.genCandidateParkingPoints();
        users.add(userA);


        User userB = new User();
        Leg legB1 = new Leg(graph, userB, 1, 4, 1, 5);
        userB.addLeg(legB1);
        Leg legB2 = new Leg(graph, userB, 4, 5, 5, 8);
        userB.addLeg(legB2);
        Leg legB3 = new Leg(graph, userB, 5, 6, 8, 10);
        userB.addLeg(legB3);
        Leg legB4 = new Leg(graph, userB, 6, 7, 10, 11);
        userB.addLeg(legB4);
        Leg legB5 = new Leg(graph, userB, 7, 12, 11, 20);
        userB.addLeg(legB5);
        userB.genCandidateParkingPoints();
        users.add(userB);
    }

    public static void initRealInstance(String graphPath, String userPath){
        // load graph structure
        InputStream in = null;
        try {
            in = new FileInputStream(new File(graphPath));
//            in = new FileInputStream(new File("data/threshold_C51_0.5.graphml"));
//            in = new FileInputStream(new File("data/backup/threshold_C51_0.5.graphml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        GraphReader graphReader = new GraphReader();

        try {
            graph = (StationGraph) graphReader.readGraph(in);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        graph.computeAllSourceShortestDistances();

        // load user structure

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(userPath));
//            reader = new BufferedReader(new FileReader("data/backup/test_instance10.txt"));
//            reader = new BufferedReader(new FileReader("data/instance_25_40/threshold_C51_0.5_40_0_1_25.txt"));
//            reader = new BufferedReader(new FileReader("data/instance_25_60/threshold_C51_0.5_60_0_1_25.txt"));
//            reader = new BufferedReader(new FileReader("data/instance_25_80/threshold_C51_0.5_80_0_3_25.txt"));
//            reader = new BufferedReader(new FileReader("data/instance_25_100/threshold_C51_0.5_100_0_3_25.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        int MAX_USER_COUNT = 1000;
        int userCount = 0;
        int prevUId = -1;
        User user = null;
        try {
            while((line = reader.readLine()) != null){
                String[] legElems = line.split("\t");
                int currentUId = Integer.parseInt(legElems[0]);
                if(currentUId != prevUId ){
                    if(prevUId != -1) {
                        user.genCandidateParkingPoints();
                        users.add(user);
                    }
                    prevUId = currentUId;
                    userCount++;
                    if(userCount > MAX_USER_COUNT){
                        break;
                    }
                    user = new User(currentUId);
                }
                Leg leg = new Leg(graph, user, Integer.parseInt(legElems[2]),
                        Integer.parseInt(legElems[3]), Integer.parseInt(legElems[6]), Integer.parseInt(legElems[4]));
                user.addLeg(leg);
            }

            if(line == null){
                user.genCandidateParkingPoints();
                users.add(user);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
