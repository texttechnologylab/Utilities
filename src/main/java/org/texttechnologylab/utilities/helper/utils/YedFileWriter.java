package org.texttechnologylab.utilities.helper.utils;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

/**
 * Created by abrami on 29.01.16.
 */
public class YedFileWriter {

    private Graph graph = null;
    private String xml = null;

    public YedFileWriter(Graph graph) {
        this.graph = graph;
    }

    private String getGraphMLHeader() {
        String header = "<?xml version=\"1.0\" ?>";
        header += "\r\n<graphml\r\n  xmlns=\"http://graphml.graphdrawing.org/xmlns\"\r\n  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n  xmlns:y=\"http://www.yworks.com/xml/graphml\"\r\n  xmlns:yed=\"http://www.yworks.com/xml/yed/3\"\r\n  xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\r\n  http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\"\r\n>";
        header += "\r\n  <key for=\"node\" id=\"d5\" attr.name=\"description\" attr.type=\"string\" />";
        header += "\r\n  <key for=\"node\" id=\"d6\" yfiles.type=\"nodegraphics\"/>";
        header += "\r\n  <graph id=\"G\" edgedefault=\"directed\">";
        return header;
    }

    private String getGraphMLFooter() {
        String footer = "\r\n  </graph>\r\n</graphml>";
        return footer;
    }

    private String getNode(String id) {
        Vertex v = graph.getVertex(id);

        String node = "\r\n    <node id=\"" + id + "\">";
        node += "\r\n      <data key=\"d5\"/>";
        node += "\r\n      <data key=\"d6\">";
        node += "\r\n        <y:ShapeNode>";
        node += "\r\n          <y:NodeLabel>" + v.getProperty("label") + "</y:NodeLabel>";
        node += "\r\n          <y:Shape type=\""+v.getProperty("type")+"\"/>";
        node += "\r\n        </y:ShapeNode>";
        node += "\r\n      </data>";
        node += "\r\n    </node>";
        return node;
    }

    private String getEdge(Edge edge) {

        Vertex source = edge.getVertex(Direction.OUT);
        Vertex target = edge.getVertex(Direction.IN);
        String edgeId = (String) edge.getId();
        String sourceId = (String) source.getId();
        String targetId = (String) target.getId();
        String label = edge.getLabel();

        String edgeXml = "\r\n    <edge id=\"" + edgeId + "\" source=\"" + sourceId + "\" target=\"" + targetId + "\" label=\"" + label + "\">";
        edgeXml += "\r\n    </edge>";

        return edgeXml;
    }

    private void createGraphXml() {
        xml = getGraphMLHeader();

        // Create nodes
        Iterable<Vertex> vertices = graph.getVertices();
        Iterator<Vertex> verticesIterator = vertices.iterator();
        while (verticesIterator.hasNext()) {
            Vertex vertex = verticesIterator.next();
            String id = (String) vertex.getId();
            String node = getNode(id);
            xml += node;
        }
        // Create edges
        Iterable<Edge> edges = graph.getEdges();
        Iterator<Edge> edgesIterator = edges.iterator();
        while (edgesIterator.hasNext()) {
            Edge edge = edgesIterator.next();
            String edgeXml = getEdge(edge);
            xml += edgeXml;
        }

        xml += getGraphMLFooter();
    }

    public void outputGraph(final OutputStream out) throws IOException {
        createGraphXml();
        //System.out.println(xml);
        try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(out))) {
            br.write(xml);
            br.flush();
        }
        out.flush();
        out.close();
    }

    public static TinkerGraph createGraphML_TinkerGraph(JSONArray nodes, JSONArray edges){

        TinkerGraph tk = new TinkerGraph();

        for(int a=0; a<nodes.length(); a++){
            try {

                String label = nodes.getJSONObject(a).getString("name");

                if(label.contains("#")){
                    label = label.substring(label.indexOf("#"));
                }
                if(label.contains("&")){
                    label = label.replaceAll("&", "AND");
                }

                Vertex vN = tk.addVertex(nodes.getJSONObject(a).getString("uri"));
                vN.setProperty("label", label);
                vN.setProperty("type", nodes.getJSONObject(a).has("type") ? nodes.getJSONObject(a).getString("type"): "rectangle");

            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        for(int a=0; a<edges.length(); a++){
            try {

                JSONObject obj = edges.getJSONObject(a);

                if(obj.has("source") && obj.has("target")) {

                    tk.addEdge(System.currentTimeMillis()+"_"+Math.random(), tk.getVertex(obj.getString("source")), tk.getVertex(obj.getString("target")), obj.getString("name"));

                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }


        return tk;
    }

}
