package org.texttechnologylab.utilities.helper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class BorlandUtils {

    public static final String delemiter = "¤";
    public static final String fieldDelemiter = "¶";

    public static enum DATATYPE {String, StringSet, StringList, Integer, IntegerDistribution, Long, Float, Double, Boolean};

    public static String createHeader(Map<String,DATATYPE> vertices, Map<String,DATATYPE> edges){

        StringBuilder sb = new StringBuilder();

            sb.append("directed");
            sb.append("\n");
            sb.append("SimilarityGraph");
            sb.append("\n");

            sb.append(createAttributesVertices(vertices));
            sb.append(createAttributesEdges(edges));

            sb.append("ProbabilityMassOfGraph: 0");
            sb.append("\n");

        return sb.toString();

    }

    public static void writeHeader(Map<String,DATATYPE> vertices, Map<String,DATATYPE> edges, File pFile) throws IOException {

        String tString = createHeader(vertices, edges);
        StringUtils.writeContent(tString, pFile);

    }

    public static String createAttributesVertices(Map<String,DATATYPE> keyMap){

        StringBuilder sb = new StringBuilder();

            sb.append("Vertex Attributes:");

            keyMap.keySet().forEach(k->{

                sb.append("[");
                    sb.append(k);
                    sb.append(delemiter);
                    sb.append(keyMap.get(k).toString());
                sb.append("];");

            });
            sb.append("\n");

        return sb.toString();

    }

    public static String createAttributesEdges(Map<String,DATATYPE> keyMap){

        StringBuilder sb = new StringBuilder();

            sb.append("Edge Attributes:");

            keyMap.keySet().forEach(k->{

                sb.append("[");
                    sb.append(k);
                    sb.append(delemiter);
                    sb.append(keyMap.get(k));
                sb.append("];");

            });
            sb.append("\n");
        return sb.toString();

    }

    public static void writeVertex(Object id, Map<String,Object> valueMap, File fFile) throws IOException {

        StringUtils.writeContent(addVertex(id, valueMap), fFile, true);

    }

    public static String addVertex(Object id, Map<String,Object> valueMap){

        StringBuilder sb = new StringBuilder();

            sb.append(id);
            sb.append(delemiter);

            valueMap.keySet().forEach(k->{
                boolean bValid = false;
                Object pValue = valueMap.get(k);

                if(pValue instanceof String){
                    if(((String)pValue).length()>0){
                        bValid=true;
                    }
                }
                else{
                    bValid=true;
                }

                if(bValid) {

                    sb.append("[");
                    sb.append(k);
                    sb.append(delemiter);

                    if(valueMap.get(k) instanceof String){
                        sb.append(((String)valueMap.get(k)).replace("[", "'").replace("]", "'"));
                    }
                    else{
                        sb.append(valueMap.get(k));
                    }


                    sb.append(delemiter);
                    sb.append("]");
                    sb.append(delemiter);
                }
            });
            sb.append("\n");
        return sb.toString();

    }


    public static void writeEdge(Object source, Object target, float fWeight, Map<String, Object> values, File fFile) throws IOException {
        StringUtils.writeContent(addEdge(source, target, fWeight, values), fFile, true);
    }

    public static void writeEdge(Object source, Object target, Map<String, Object> values, File fFile) throws IOException {
        StringUtils.writeContent(addEdge(source, target, 0.0f, values), fFile, true);
    }

    public static String addEdge(Object source, Object target, Map<String, Object> values) {
        return addEdge(source, target, 0.0f, values);
    }

    public static String addEdge(Object source, Object target, float fWeight, Map<String, Object> values){

        StringBuilder sb = new StringBuilder();

            sb.append(source);
            sb.append(delemiter);
            sb.append(target);
            sb.append(delemiter);


            sb.append(fWeight);
            sb.append(delemiter);


            values.keySet().forEach(k->{
                sb.append("[");
                sb.append(k);
                sb.append(delemiter);
                sb.append(values.get(k));
                sb.append(delemiter);
                sb.append("]");
                sb.append(delemiter);
            });

            sb.append("\n");
        return sb.toString();

    }

    public static String createBorland(String sHeader, String sVertices, String sEdges){

        StringBuilder sb = new StringBuilder();

            sb.append(sHeader);
            sb.append("Vertices:");
            sb.append("\n");
            sb.append(sVertices);

            sb.append("Edges:");
            sb.append("\n");
            sb.append(sEdges);

        return sb.toString();

    }

}
