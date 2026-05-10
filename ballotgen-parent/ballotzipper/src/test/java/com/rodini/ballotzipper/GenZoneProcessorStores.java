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
class GenZoneProcessorStores {

	private static MockedAppender mockedAppender;
	private static Logger logger;
	private static DataRoot dataRoot;

	public static void main(String [] args) {
		setupClass();
//		genTestDir01();
//		genTestDir02();
//		genTestDir03();
	}
	

	
	static void setupClass() {
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    logger = (Logger)LogManager.getLogger(ZoneProcessor.class);
	    logger.addAppender(mockedAppender);
	    logger.setLevel(Level.ERROR);
	}

	static void teardown() {
		logger.removeAppender(mockedAppender);
		mockedAppender.stop();
	}

	
	static void setUp() {
	    mockedAppender.messages.clear();
	    dataRoot = ZoneProcessor.getDataRoot();
	    dataRoot.clearZoneNoZoneMap();
	    dataRoot.clearPrecinctNoPrecinctMap();
	    dataRoot.clearPrecinctNoZoneMap();
	}

	static void genTestDir01() {
//		setUp();
		String [] args = {"./src/test/java/test-dir-01.csv", "./src/test/java/test-dir-01-store"};
		ZoneProcessor.main(args);
	}
	static void genTestDir02() {
		String [] args = {"./src/test/java/test-dir-02.csv", "./src/test/java/test-dir-02-store"};
		ZoneProcessor.main(args);
	}
	static void genTestDir03() {
		String [] args = {"./src/test/java/test-dir-03.csv", "./src/test/java/test-dir-03-store"};
		ZoneProcessor.main(args);
	}
}
