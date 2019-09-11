package org.texttechnologylab.utilities.uima;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.FsIndexFactory;
import org.apache.uima.fit.factory.TypePrioritiesFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.internal.ResourceManagerFactory;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.metadata.FsIndexCollection;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created on 11.09.19.
 */
public final class SanitizingJCasFactory {
	private SanitizingJCasFactory() {
	}
	
	public static JCas createText(String aText) throws UIMAException {
		return createText(aText, (String) null);
	}
	
	public static JCas createText(String aText, String aLanguage) throws UIMAException {
		JCas jcas = createJCas();
		
		aText = sanitize(aText);
		
		if (aText != null) {
			jcas.setDocumentText(aText);
		}
		
		if (aLanguage != null) {
			jcas.setDocumentLanguage(aLanguage);
		}
		
		return jcas;
	}
	
	private static String sanitize(String aText) {
		if (aText == null) return aText;
		
		// Replace CRLF with LF
		aText = aText.replaceAll("\r\n", "\n");
		
		// Remove dangling CR
		aText = aText.replaceAll("\r", "");
		
		return aText;
	}
	
	public static JCas createJCas() throws UIMAException {
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		TypePriorities tp = TypePrioritiesFactory.createTypePriorities();
		FsIndexCollection indexes = FsIndexFactory.createFsIndexCollection();
		ResourceManager resMgr = ResourceManagerFactory.newResourceManager();
		return CasCreationUtils.createCas(tsd, tp, indexes.getFsIndexes(), (Properties) null, resMgr).getJCas();
	}
	
	public static JCas createJCas(String... typeSystemDescriptorNames) throws UIMAException {
		return CasCreationUtils.createCas(TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemDescriptorNames), (TypePriorities) null, (FsIndexDescription[]) null).getJCas();
	}
	
	public static JCas createJCasFromPath(String... typeSystemDescriptorPaths) throws UIMAException {
		return createJCas(TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath(typeSystemDescriptorPaths));
	}
	
	public static JCas createJCas(TypeSystemDescription typeSystemDescription) throws UIMAException {
		return CasCreationUtils.createCas(typeSystemDescription, (TypePriorities) null, (FsIndexDescription[]) null).getJCas();
	}
	
	public static JCas createJCas(String fileName, TypeSystemDescription typeSystemDescription) throws UIMAException, IOException {
		JCas jCas = createJCas(typeSystemDescription);
		CasIOUtil.readJCas(jCas, new File(fileName));
		return jCas;
	}
}
