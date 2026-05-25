package com.rodini.ballotprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import com.ginsberg.junit.exit.ExpectSystemExit;

import com.rodini.ballotutils.Utils;
class TestInitializeCLIArgs {

	private static MockedAppender mockedAppender;
	private static Logger logger;

	@BeforeAll
	static void setupClass() {
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    // ATTENTION: ERRORs are logged by the Utils class
	    // and not by the Initialize class.
	    logger = (Logger)LogManager.getLogger(Utils.class);
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
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	@Test
	@Disabled
	@ExpectSystemExit
	void testInitializeArg0IsBad1() {
System.out.println("testInitializeArg0IsBad1");
		String [] args = {
				"./non-existent.txt",		
				"./src/test/java/test-ballot-store"
		};
		String expected = "file";
		Initialize.validateCommandLineArgs(args);
		assertEquals(1, mockedAppender.messages.size());
		assertTrue(mockedAppender.messages.get(0).startsWith(expected));
	}
	@Test
	@Disabled
	@ExpectSystemExit
	void testInitializeArg0IsBad2() {
System.out.println("testInitializeArg0IsBad2");
		String [] args = {
				"./src/test/java/Test.xyz",
				"./src/test/java/test-ballot-store"
		};
		String expected = "file";
		Initialize.validateCommandLineArgs(args);
		assertEquals(1, mockedAppender.messages.size());
System.out.println("*******");
for (int i =0; i < mockedAppender.messages.size(); i++ ) {
	System.out.println(mockedAppender.messages.get(0));
}
System.out.println("*******");	
		assertTrue(mockedAppender.messages.get(0).startsWith(expected));
	}
	@Test
	@Disabled
	@ExpectSystemExit
	void testInitializeArg1IsBad1() {
System.out.println("testInitializeArg1IsBad1");
		String [] args = {
				"./src/test/java/specimen-2023-general-ATGLEN.txt",
				"./non-existent-folder",
				"./src/test/java/test-ballot-store"
		};
		String expected = "incorrect";
		Initialize.validateCommandLineArgs(args);
		assertEquals(1, mockedAppender.messages.size());
System.out.println("*******");
for (int i =0; i < mockedAppender.messages.size(); i++ ) {
	System.out.println(mockedAppender.messages.get(i));
}
System.out.println("*******");	
		assertTrue(mockedAppender.messages.get(0).startsWith(expected));
	}
	@Test
	@Disabled
	@ExpectSystemExit
	void testInitializeArg2IsBad1() {
System.out.println("testInitializeArg2IsBad1");
		String [] args = {
				"./src/test/java/specimen-2023-general-ATGLEN.txt",
				"./non-existent-folder",
		};
		String expected = "invalid";
		Initialize.validateCommandLineArgs(args);
		assertEquals(1, mockedAppender.messages.size());
System.out.println("*******");	
for (int i =0; i < mockedAppender.messages.size(); i++ ) {
	System.out.println(mockedAppender.messages.get(i));
}
System.out.println("*******");	
		assertTrue(mockedAppender.messages.get(0).startsWith(expected));
	}
//	@Disabled
	@Test
	void testInitializeArgsGood() {
		String [] args = {
				"./src/test/java/specimen-2023-general-ATGLEN.txt",
				"./src/test/java/test-ballot-store"
		};
		Initialize.validateCommandLineArgs(args);
	}
	


}
