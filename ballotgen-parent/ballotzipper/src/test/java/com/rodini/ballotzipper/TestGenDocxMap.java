package com.rodini.ballotzipper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rodini.zoneprocessor.DataRoot;
import com.rodini.zoneprocessor.ZoneProcessor;
/**
 * TestGenDocxMap is a CLIENT of ZoneProcessor
 * 
 * uses: test-dir-01-store
 * 
 * @author Bob Rodini
 *
 */
class TestGenDocxMap {

	private static MockedAppender mockedAppender;
	private static Logger logger;
	private static DataRoot dataRoot;

	@BeforeAll
	static void setupClass() {
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    logger = (Logger)LogManager.getLogger(GenDocxMap.class);
	    logger.addAppender(mockedAppender);
	    logger.setLevel(Level.DEBUG);
	}

	@AfterAll
	public static void teardown() {
		logger.removeAppender(mockedAppender);
		mockedAppender.stop();
	}

	
	@BeforeEach
	void setUp() throws Exception {
	    mockedAppender.messages.clear();
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	@Test
	void testGoodDir() throws IOException{
		String [] args = {"./src/test/java/test-dir-01", "./src/test/java/test-dir-01-zips"};
		String zonePropsPath = "./src/test/java/test01-zoneprocessor.properties";
		
		Initialize.initialize(args, zonePropsPath);
		GenDocxMap.clearDocxNoMap();
		GenDocxMap.processInDir();
		Map<String, MuniFiles> docxNoMap = GenDocxMap.getDocxNoMap();
		// Must call stop() or EclispeStore temp files are left.
		ZoneProcessor.stop();
		assertEquals(3, docxNoMap.size());
	}

}
