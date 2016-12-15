package nl.twente.bms.utils;

import grph.Grph;
import grph.io.AbstractGraphReader;
import grph.io.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import nl.twente.bms.struct.Key;
import nl.twente.bms.struct.StationGraph;
import org.xml.sax.SAXException;

import toools.io.Utilities;
import toools.text.xml.XMLNode;
import toools.text.xml.XMLUtilities;

public class GraphReader extends AbstractGraphReader
{
    @Override
    public Grph readGraph(InputStream is) throws ParseException, IOException
    {
        try
        {
            return readGraph(is, null, null);
        }
        catch (SAXException e)
        {
            e.printStackTrace();
            throw new ParseException(e.getMessage(), -1);
        }
    }

    public Grph readGraph(InputStream is, Class vPropertyClass, Class ePropertyClass) throws ParseException,
            IOException, SAXException
    {
        XMLNode root = XMLUtilities.xml2node(new String(Utilities.readUntilEOF(is)), false);
        StationGraph g = new StationGraph();
        Map<String, Key> key_attributeName = new HashMap<>();

        if (root.getName().equals("graphml"))
        {
            for (XMLNode gn : root.getChildren())
            {
                if (gn.getName().equals("key"))
                {
                    String id = gn.getAttributes().get("id");
                    String target = gn.getAttributes().get("for");
                    String name = gn.getAttributes().get("attr.name");
                    String type = gn.getAttributes().get("attr.type");
                    key_attributeName.put(id, new Key(target, name, id, type));
                }
                else if (gn.getName().equals("graph"))
                {
                    boolean edgesAreDirectedByDefault = gn.getAttributes().get("edgedefault").equals("directed");

                    for (XMLNode elementNode : gn.getChildren())
                    {
                        if (elementNode.getName().equals("node"))
                        {
                            int id = Integer.valueOf(elementNode.getAttributes().get("id"));
                            g.addVertex(id);
                        }
                        else if (elementNode.getName().equals("edge"))
                        {
                            int s = Integer.valueOf(elementNode.getAttributes().get("source"));
                            int d = Integer.valueOf(elementNode.getAttributes().get("target"));
                            int e = g.addSimpleEdge(s, d, edgesAreDirectedByDefault);
                            for (XMLNode elemNode : elementNode.getChildren()){
                                g.setEdgeWeight(e, (int) Math.round(Double.valueOf(elemNode.getText(false))));
                            }
                        }
                        else
                        {
                            throw new ParseException("node is neither a node or an edge", 0);
                        }
                    }
                }
                else
                {
                    throw new ParseException("child node is not graph", 0);
                }
            }
        }
        else
        {
            throw new ParseException("root node is not graphml", 0);
        }

        return g;
    }
}
