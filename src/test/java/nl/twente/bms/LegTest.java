package nl.twente.bms;

import grph.Grph;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nl.twente.bms.struct.Leg;
import nl.twente.bms.struct.User;

/**
 * Unit test for simple App.
 */
public class LegTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LegTest(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( LegTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testLegCreation()
    {
//        Grph graph = TestGraph.graph;
//        User user = new User();
//        Leg leg = new Leg(graph, user, 1, 7, 1, 5);
//        assertEquals(leg.getCost(false), 4.5, 1e-5);
    }

    public void testMergeCheck()
    {
//        Grph graph = TestGraph.graph;
//        User userA = new User();
//        User userB = new User();
//        Leg legA = new Leg(graph, userA, 1, 7, 1, 5);
//        Leg legB = new Leg(graph, userB, 2, 7, 3, 6);
//        assertEquals(legA.getOverlappedCost(legB), 3.0, 1e-5);
//
//        legA = new Leg(graph, userA, 1, 6, 1, 5);
//        legB = new Leg(graph, userB, 2, 7, 3, 6);
//        assertEquals(legA.getOverlappedCost(legB), 1.5, 1e-5);
    }

    public void testMerge()
    {
//        Grph graph = TestGraph.graph;
//        User userA = new User();
//        User userB = new User();

//        Leg legA = new Leg(graph, userA, 1, 7, 1, 5);
//        Leg legB = new Leg(graph, userB, 2, 7, 3, 6);
//
//        legA.merge(legB);
//
//        assertEquals(legA.getCost(false), 4.5, 1e-5);
//        assertEquals(legB.getCost(false), 0.0, 1e-5);
//        assertEquals(legB.getCost(true),  0.0, 1e-5);
//
//        legA = new Leg(graph, userA, 1, 6, 1, 5);
//        legB = new Leg(graph, userB, 2, 7, 3, 6);
//
//        legA.merge(legB);
//
//        assertEquals(legA.getCost(false), 4.5, 1e-5);
//        assertEquals(legB.getCost(false), 0.0, 1e-5);
//        assertEquals(legB.getCost(true),  3.0, 1e-5);
//
//        legA = new Leg(graph, userA, 1, 6, 1, 5);
//        legB = new Leg(graph, userB, 3, 6, 2, 5);
//
//        legA.merge(legB);
//
//
//        assertEquals(legA.getCost(false), 4.5, 1e-5);
//        assertEquals(legB.getCost(false), 1.5, 1e-5);
//        assertEquals(legB.getCost(true),  1.5, 1e-5);
    }
}
