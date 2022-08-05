package org.texttechnologylab.utilities.uima.jcas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCopier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public class JCasTTLabUtils {

    public static TOP exists(TOP source, JCas targetCas, int iStandoff, boolean bFirst) throws ClassNotFoundException {

        TOP pReturn = null;

        Class c = Class.forName(source.getType().toString());

        if (source instanceof Annotation) {

            Annotation pAnno = (Annotation) source;

            try {
                if (!bFirst) {
                    pReturn = (TOP) JCasUtil.selectAt(targetCas, c, pAnno.getBegin(), pAnno.getEnd()).stream().findFirst().get();
                } else {
                    pReturn = (TOP) JCasUtil.selectAt(targetCas, c, pAnno.getBegin() + iStandoff, pAnno.getEnd() + iStandoff).stream().findFirst().get();
                }
            } catch (Exception e) {

            }

        }

        return pReturn;


    }

    public static TOP createAnnotation(JCas pCas, TOP pAnnotation, int iStandoff, boolean bFirst) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class c = Class.forName(pAnnotation.getType().toString());

        TOP fs = exists(pAnnotation, pCas, iStandoff, bFirst);

        if (fs == null) {
            fs = (TOP) c.getConstructor(JCas.class).newInstance(pCas);
        } else {
            return fs;
        }


        TOP finalFs = fs;
        pAnnotation.getType().getFeatures().forEach(f -> {

            if (!f.getRange().toString().contains(".Sofa")) {

                if (f.getRange().isPrimitive()) {
                    if ((f.getShortName().equalsIgnoreCase("begin") || f.getShortName().equalsIgnoreCase("end")) && !bFirst) {
                        int iValue = Integer.valueOf(pAnnotation.getFeatureValueAsString(f));
                        iValue += iStandoff;
                        finalFs.setFeatureValueFromString(f, "" + iValue);
                    } else {
                        finalFs.setFeatureValueFromString(f, pAnnotation.getFeatureValueAsString(f));
                    }
                } else {

                    TOP nAnno = null;
                    try {
                        if (pAnnotation.getFeatureValue(f) != null) {
                            nAnno = createAnnotation(pCas, (TOP) pAnnotation.getFeatureValue(f), iStandoff, bFirst);
                            if (nAnno != null) {
                                finalFs.setFeatureValue(f, nAnno);
                            }
                        }

                    } catch (ClassNotFoundException e) {
                        System.out.println(e.getCause() + "\t" + e.getMessage());
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }

            }


        });

        fs.addToIndexes();


        return fs;

    }

    public static JCas splitAndProcess(JCas pTarget, JCas sentenceReferenceCas, int iSplitSize, JCasAnnotator_ImplBase pAnnotator) throws AnalysisEngineProcessException {

        long textLength = pTarget.getDocumentText().length();

        if (textLength < iSplitSize) {
            pAnnotator.process(pTarget);
            return pTarget;
        }

        try {

            int lStart = 0;
            int lLastMax = 0;
            boolean finish = false;

            if (sentenceReferenceCas == null) {
                sentenceReferenceCas = pTarget;
            }

            do {
                AtomicInteger standoff_Begin = new AtomicInteger(-1);
                AtomicInteger standoff_End = new AtomicInteger(-1);

                JCasUtil.selectCovered(sentenceReferenceCas, Sentence.class, lStart, lLastMax + iSplitSize).forEach(s -> {
                    if (standoff_Begin.get() < 0 || standoff_Begin.get() > s.getBegin()) {
                        standoff_Begin.set(s.getBegin());
                    }
                    if (standoff_End.get() < 0 || standoff_End.get() < s.getEnd()) {
                        standoff_End.set(s.getEnd());
                    }
                });

                JCas tCas = null;

                System.out.println(standoff_Begin.get() + " -> " + standoff_End.get());

                if (standoff_Begin.get() > 0 && lStart == 0) {
                    tCas = JCasFactory.createText(pTarget.getDocumentText().substring(0, standoff_End.get()), pTarget.getDocumentLanguage());
                } else if ((textLength - standoff_Begin.get()) < iSplitSize) {
                    tCas = JCasFactory.createText(pTarget.getDocumentText().substring(standoff_Begin.get(), (int) textLength), pTarget.getDocumentLanguage());
                    finish = true;
                } else {
                    tCas = JCasFactory.createText(pTarget.getDocumentText().substring(standoff_Begin.get(), standoff_End.get()), pTarget.getDocumentLanguage());
                }


                pAnnotator.process(tCas);

                if (lStart > 0) {
                    JCasUtil.select(tCas, Annotation.class).forEach(a -> {
                        a.setBegin(a.getBegin() + standoff_Begin.get());
                        a.setEnd(a.getEnd() + standoff_Begin.get());
                    });
                }

                CasCopier.copyCas(tCas.getCas(), pTarget.getCas(), false);

                lLastMax = standoff_End.get();
                lStart = lLastMax;
            }
            while (!finish);

        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        } catch (UIMAException e) {
            e.printStackTrace();
        }

        return pTarget;


    }


    public static JSONObject fs2JSON(TOP fs, boolean bDetail) throws JSONException {
        JSONObject fsfObj = new JSONObject();
        for (Feature feature : fs.getType().getFeatures()) {
            if (feature.getName().equals("uima.cas.AnnotationBase:sofa")) {
                continue;
            }
            switch (feature.getRange().getName()) {
                case "uima.cas.String": {
                    fsfObj.put(feature.getShortName(), fs.getStringValue(feature));
                    break;
                }
                case "uima.cas.Integer": {
                    fsfObj.put(feature.getShortName(), fs.getIntValue(feature));
                    break;
                }
                case "uima.cas.Float": {
                    fsfObj.put(feature.getShortName(), fs.getFloatValue(feature));
                    break;
                }
                case "uima.cas.Long": {
                    fsfObj.put(feature.getShortName(), fs.getLongValue(feature));
                    break;
                }
                case "uima.cas.Boolean": {
                    fsfObj.put(feature.getShortName(), fs.getBooleanValue(feature));
                    break;
                }
                case "uima.cas.Short": {
                    fsfObj.put(feature.getShortName(), fs.getShortValue(feature));
                    break;
                }
                case "uima.cas.Double": {
                    fsfObj.put(feature.getShortName(), fs.getDoubleValue(feature));
                    break;
                }
                case "uima.cas.Byte": {
                    fsfObj.put(feature.getShortName(), fs.getByteValue(feature));
                    break;
                }
                default: {
                    TOP fSF = (TOP) fs.getFeatureValue(feature);
                    if (fSF != null) {
                        if (fSF.getType().getName().equals("uima.cas.FSArray")) {
                            JSONArray jsonArray = new JSONArray();
                            FSArray array = (FSArray) fSF;
                            for (int i = 0; i < array.size(); i++) {
                                TOP arrayElement = (TOP) array.get(i);
                                jsonArray.put(arrayElement.getAddress());
                            }
                            fsfObj.put(feature.getShortName(), jsonArray);
                        } else {
                            if (bDetail) {
                                fsfObj.put(feature.getShortName(), fs2JSON(fSF, bDetail));
                            } else {
                                fsfObj.put(feature.getShortName(), fSF.getAddress());
                            }

                        }
                    } else {
                        fsfObj.put(feature.getShortName(), "null");
                    }
                }
            }
        }

        JSONObject fsObj = new JSONObject();
        fsObj.put("_addr", fs.getAddress());
        fsObj.put("_type", fs.getType().getName());
        fsObj.put("features", fsfObj);
        return fsObj;
    }
}
