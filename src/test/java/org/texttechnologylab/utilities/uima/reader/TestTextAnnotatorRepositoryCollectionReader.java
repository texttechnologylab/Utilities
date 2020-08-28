package org.texttechnologylab.utilities.uima.reader;

import com.google.common.io.Files;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

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
			String sessionId = Files.asCharSource(new File("src/test/resources/session.id"), UTF_8)
					.read()
					.trim();
			String[] fileIds = Files.asCharSource(new File("src/test/resources/file_ids.txt"), UTF_8)
					.readLines()
					.toArray(new String[0]);
			TextAnnotatorRepositoryCollectionReader collection = ((TextAnnotatorRepositoryCollectionReader) CollectionReaderFactory.createReader(
					TextAnnotatorRepositoryCollectionReader.class,
					TextAnnotatorRepositoryCollectionReader.PARAM_SOURCE_LOCATION, xmiPath,
					TextAnnotatorRepositoryCollectionReader.PARAM_TARGET_LOCATION, txtPath,
					TextAnnotatorRepositoryCollectionReader.PARAM_SESSION_ID, sessionId,
					TextAnnotatorRepositoryCollectionReader.PARAM_FORCE_RESERIALIZE, false,
					TextAnnotatorRepositoryCollectionReader.PARAM_FILE_IDS, fileIds
			));

			while (!collection.isDone())
				Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\nDone");
	}
}
