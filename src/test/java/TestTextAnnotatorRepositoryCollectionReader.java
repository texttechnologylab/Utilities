import com.google.common.io.Files;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.junit.Test;
import org.texttechnologylab.utilities.uima.reader.TextAnnotatorRepositoryCollectionReader;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Created on 17.09.19.
 */
public class TestTextAnnotatorRepositoryCollectionReader {
	@Test
	public void testTADownload() {
		try {
			String xmiPath = "src/test/out/xmi/";
			String txtPath = "src/test/out/txt/";
			Paths.get(xmiPath).toFile().mkdirs();
			Paths.get(txtPath).toFile().mkdirs();
			String sessionId = Files.toString(new File("src/test/resources/session.id"), StandardCharsets.UTF_8).trim();
			TextAnnotatorRepositoryCollectionReader collection = ((TextAnnotatorRepositoryCollectionReader) CollectionReaderFactory.createReader(
					TextAnnotatorRepositoryCollectionReader.class,
					TextAnnotatorRepositoryCollectionReader.PARAM_SOURCE_LOCATION, xmiPath,
					TextAnnotatorRepositoryCollectionReader.PARAM_TARGET_LOCATION, txtPath,
					TextAnnotatorRepositoryCollectionReader.PARAM_SESSION_ID, sessionId,
					TextAnnotatorRepositoryCollectionReader.PARAM_FORCE_RESERIALIZE, true
					//						, XmiReader.PARAM_LOG_FREQ, -1
			));
			
			while (!collection.isDone())
				Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\nDone");
	}
}
