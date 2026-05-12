package com.rodini.ballotzipper;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.rodini.ballotutils.Utils;
import com.rodini.zoneprocessor.ZoneProcessor;
import com.rodini.zoneprocessor.ZoneFactory;
import com.rodini.zoneprocessor.DataRoot;
import com.rodini.zoneprocessor.PrecinctFactory;
/**
/**
 * Uses: test-dir-01.csv   test-dir-01
 *       test-dir-02.csv   test-dir-02
 *       test-dir-03.csv   test-dir-03
 * 
 * @author Bob Rodini
 *
 */
class TestGenZipFiles {

	private static MockedAppender mockedAppender;
	private static Logger logger;
	private static DataRoot dataRoot;

	@BeforeAll
	static void setupClass() {
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    logger = (Logger)LogManager.getLogger(GenZipFiles.class);
	    logger.addAppender(mockedAppender);
	    logger.setLevel(Level.ERROR);
	}

	@AfterAll
	public static void teardown() {
		logger.removeAppender(mockedAppender);
		mockedAppender.stop();
	}

	
	@BeforeEach
	void setUp() throws Exception {
	    mockedAppender.messages.clear();
//	    dataRoot = ZoneProcessor.getDataRoot();
//	    dataRoot.clearZoneNoZoneMap();
//	    dataRoot.clearPrecinctNoPrecinctMap();
//	    dataRoot.clearPrecinctNoZoneMap();
//		GenDocxMap.clearDocxNoMap();
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	//@Disabled
	@Test
	void testDir01() {
		String [] args = {"./src/test/java/test-dir-01", "./src/test/java/test-dir-01-zips"};
		String zonePropsPath = "./src/test/java/test01-zoneprocessor.properties";
		Initialize.initialize(args, zonePropsPath);
	    dataRoot = ZoneProcessor.getDataRoot();
	    GenDocxMap.clearDocxNoMap();
		GenDocxMap.processInDir();
		GenZipFiles.genZips(dataRoot);
		// Don't forget to call this.
		ZoneProcessor.stop();
		assertEquals(0, mockedAppender.messages.size());
	}
	//@Disabled
	@Test
	void testDir02() {
		String [] args = {"./src/test/java/test-dir-02", "./src/test/java/test-dir-02-zips"};
		String zonePropsPath = "./src/test/java/test02-zoneprocessor.properties";
		Initialize.initialize(args,zonePropsPath);
	    dataRoot = ZoneProcessor.getDataRoot();
		GenDocxMap.processInDir();
		GenZipFiles.genZips(dataRoot);
		// Don't forget to call this.
		ZoneProcessor.stop();
		// ERROR (GenMuniMap) - duplicate precinct no. 010
		// ERROR (GenZipFiles) - DOCX precinct 014 lacks CSV precinct
		assertTrue(mockedAppender.messages.contains("CSV precinct 013 lacks DOCX files"));
		assertTrue(mockedAppender.messages.contains("DOCX precinct 014 lacks CSV precinct"));
	}
	//@Disabled
	@Test
	void testDir03() {
		String [] args = {"./src/test/java/test-dir-03", "./src/test/java/test-dir-03-zips"};
		String zonePropsPath = "./src/test/java/test03-zoneprocessor.properties";
		Initialize.initialize(args,zonePropsPath);
	    dataRoot = ZoneProcessor.getDataRoot();
		GenDocxMap.processInDir();
		GenZipFiles.genZips(dataRoot);
		// Don't forget to call this.
		ZoneProcessor.stop();
		// ERROR (GenZipFiles) - CSV precinct 020 lacks DOCX files
		// ERROR (GenZipFiles) - DOCX precinct 004 lacks CSV precinct
		assertTrue(mockedAppender.messages.contains("CSV precinct 020 lacks DOCX files"));
		assertTrue(mockedAppender.messages.contains("DOCX precinct 004 lacks CSV precinct"));
	}
}
