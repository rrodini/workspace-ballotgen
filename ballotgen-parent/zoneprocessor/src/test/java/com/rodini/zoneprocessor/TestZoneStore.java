package com.rodini.zoneprocessor;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestZoneStore {
	private static MockedAppender mockedAppender;
	private static Logger logger;
//	private static DataRoot root;

	@BeforeAll
	static void setupClass() {
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    logger = (Logger)LogManager.getLogger(ZoneProcessor.class);
	    logger.addAppender(mockedAppender);
//	    root = new DataRoot();
	}

	@AfterAll
	public static void teardown() {
		logger.removeAppender(mockedAppender);
//		mockedAppender.stop();
	}

	
	@BeforeEach
	void setUp() throws Exception {
	    mockedAppender.messages.clear();
	    logger.setLevel(Level.ERROR);
//		root.clearZoneNoZoneMap();
//		root.clearPrecinctNoPrecinctMap();
//		root.clearPrecinctNoZoneMap();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testMain() {
		String [] args = {"./src/test/java/test-precinct-zone-2024.csv", "./src/test/java/test-precinct-zone-2024"};
		ZoneProcessor.main(args);
	}

}
