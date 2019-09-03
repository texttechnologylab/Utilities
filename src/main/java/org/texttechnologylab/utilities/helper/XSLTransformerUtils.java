package org.texttechnologylab.utilities.helper;

import com.google.common.io.Files;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class XSLTransformerUtils {

    public static File transformGraphMLtoTEX(StreamSource xsl, File pSource) throws TransformerException, IOException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance(
                "org.apache.xalan.processor.TransformerFactoryImpl", null);

        StreamSource source = new StreamSource(pSource);

        File tFile = TempFileHandler.getTempFile("aaa", "bba");
        StreamResult result = new StreamResult(tFile);

        Transformer trans = transformerFactory.newTransformer(xsl);
        trans.transform(source, result);


        String sType = new File(xsl.getSystemId()).getName();
        sType = sType.substring(0, sType.indexOf("."));

        String sFile = StringUtils.getContent(tFile);

        Set<String> colors = new HashSet<>(0);

        StringBuilder sb = new StringBuilder();

        Scanner scanner = new Scanner(sFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // process the line

            if(line.startsWith("\\definecolor")){
                if(!colors.contains(line)){
                    sb.append(line);
                    sb.append("\n");
                    colors.add(line);
                }
                else{
                    //System.out.println(line);
                }
            }
            else{
                sb.append(line);
                sb.append("\n");
            }


        }
        scanner.close();

        StringUtils.writeContent(sb.toString(), tFile);

        File rFile = new File("/tmp/"+pSource.getName().substring(0, pSource.getName().lastIndexOf("."))+"_"+sType+".tex");
        Files.move(tFile, rFile);

        rFile.deleteOnExit();

        return rFile;

    }

    public static  File reColorTexNodes(File pInput) throws IOException {

        String sContent = StringUtils.getContent(pInput);

        Map<String, String> colorMap = new HashMap<>(0);

        StringBuilder sb = new StringBuilder();


        Scanner scanner = new Scanner(sContent);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // process the line

            if(line.startsWith("\\node")){
                //System.out.println(line);
                String sNode = line.substring(line.indexOf("(")+1, line.indexOf(")"));
                String sColor = line.substring(line.indexOf("fill=")).replace("fill=", "").replaceAll("\\].*", "");
                //System.out.println(sNode+"-->"+sColor);
                colorMap.put(sNode, sColor);
            }

            if(line.startsWith("\\path")){
                //System.out.println(line);
                String sNode = line.substring(line.indexOf("(")+1, line.indexOf(")"));

                if(sNode.contains(".")){
                    sNode = sNode.substring(0, sNode.indexOf("."));
                }

                String tColor = colorMap.get(sNode);
                line = line.replaceAll("draw=.*\\]\\(", "draw="+tColor+"\\]\\(");

            }

            sb.append(line);
            sb.append("\n");



        }
        scanner.close();

        FileUtils.writeContent(sb.toString(), pInput);

        return pInput;

    }

    public static File graphMLtoTex(File pGraphMLFile, String sPDFLaTeXPath){

        // Tex!!
        ProcessBuilder pb = new ProcessBuilder( sPDFLaTeXPath, "--interaction=nonstopmode", "--enable-write18", pGraphMLFile.getAbsolutePath());
        //ProcessBuilder pb = new ProcessBuilder( "pdflatex", "--interaction=nonstopmode", tFile.getAbsolutePath());
        pb.directory(new File("/tmp/"));

        Process p = null;
        try {
            p = pb.start();

            try {
                // Create a new reader from the InputStream
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                // Take in the input
                String input;
                while((input = br.readLine()) != null){
                    // Print the input
                    System.out.println(input);
                }
                while((input = br2.readLine()) != null){
                    // Print the input
                    System.err.println(input);
                }
            } catch(IOException io) {
                io.printStackTrace();
            }

            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
        //p.waitFor();

        //String sFilePath = "/tmp/"+tFile.getName();
        String sFilePath = pGraphMLFile.getAbsolutePath().replace(".tex", ".pdf");

        File rFile = new File(sFilePath);
        return rFile;

    }

    public static File graphMLtoTex(File pGraphMLFile){

        return graphMLtoTex(pGraphMLFile, "/opt/texbin/pdflatex");

    }

}
