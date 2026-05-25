package com.rodini.ballotprocessor;

import static org.junit.jupiter.api.Assertions.*;

import static java.util.stream.Collectors.joining;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rodini.ballotutils.Utils;

class TestInitializeGoodProperties {

	private static MockedAppender mockedAppender;
	private static Logger logger;

	static Properties testProps;
	static String propsPath = "./src/test/java/ballotprocessor-good.properties";
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
//		System.out.println("setUpBeforeClass entry.");
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    logger = (Logger)LogManager.getLogger(Initialize.class);
	    logger.addAppender(mockedAppender);
	    logger.setLevel(Level.ERROR);
		testProps = Utils.loadProperties(propsPath);
//		System.out.println("properties loaded.");
		// Properties will be loaded, and regexes compiled.
		Initialize.validateProperties(testProps);
//		System.out.println("setUpBeforeClass exit.");
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	void testRegexCompiled(String propRegex, Pattern regex) {
		String expected = Utils.getPropValue(testProps, propRegex);
		if (expected == null) {
			System.out.println("Failed to get property: " + propRegex);
		}
		String actual = regex.toString();
		assertEquals(expected, actual);
	}	
	@Test
	void testElectionTextRegex() {
		testRegexCompiled(Initialize.PROP_BALLOT_ELECTION_TEXT_REGEX, Initialize.electionTextRegex);
	}
	@Test
	void testBallotTextRegex() {
		testRegexCompiled(Initialize.PROP_BALLOT_TEXT_REGEX, Initialize.ballotTextRegex);
	}
	@Test
	void testPageBreakRegex() {
		testRegexCompiled(Initialize.PROP_BALLOT_PAGE_BREAK_REGEX, Initialize.pageBreakRegex);
	}
	@Test
	void testOnePageTextRegex() {
		testRegexCompiled(Initialize.PROP_BALLOT_ONEPAGE_TEXT_REGEX, Initialize.onePageTextRegex);
	}
	@Test
	void testTwoPageTextRegexes() {
		testRegexCompiled(Initialize.PROP_BALLOT_TWOPAGE_TEXT_P1_REGEX, Initialize.twoPageTextP1Regex);
		testRegexCompiled(Initialize.PROP_BALLOT_TWOPAGE_TEXT_P2_REGEX, Initialize.twoPageTextP2Regex);
	}
	@Test
	void testContestTextRegexes() {
		int count = Initialize.contestTextRegex.length;
		for (int i = 0; i < count; i++) {
			testRegexCompiled(Initialize.PROP_BALLOT_CONTEST_TEXT_PREFIX + "." + (i+1), Initialize.contestTextRegex[i]);			
		}
	}
	@Test
	void testReferendumTextRegex() {
		testRegexCompiled(Initialize.PROP_BALLOT_REFERENDUM_TEXT_REGEX, Initialize.referendumTextRegex);
	}
	@Test
	void testRetentionTextRegexex() {
		testRegexCompiled(Initialize.PROP_BALLOT_RETENTION_TEXT_REGEX, Initialize.retentionTextRegex);
		testRegexCompiled(Initialize.PROP_BALLOT_RETENTION_NAME_REGEX, Initialize.retentionNameRegex);
	}
	@Test
	void testWriteIn() {
		assertEquals(Utils.getPropValue(testProps, Initialize.PROP_BALLOT_WRITE_IN), Initialize.writeIn);
	}
	@Test
	void testTicketContests() {
		assertEquals(
			Utils.getPropValue(testProps, Initialize.PROP_TICKET_CONTEST_TITLES),
			Initialize.titlesOfTicketContests.stream().collect(joining(","))
		);
	}
	@Test
	void testLocalContests() {
		assertEquals(
			Utils.getPropValue(testProps, Initialize.PROP_LOCAL_CONTEST_TITLES),
			Initialize.titlesOfLocalContests.stream().collect(joining(","))
		);
	}
	@Test
	void testLocalContestsExceptions() {
		assertEquals(
			Utils.getPropValue(testProps, Initialize.PROP_LOCAL_CONTEST_EXCEPTION_TITLES),
			Initialize.titlesOfLocalContestsExceptions.stream().collect(joining(","))
		);
	}
	@Test
	void testElectionType() {
		assertEquals(
			Utils.getPropValue(testProps, Initialize.PROP_ELECTION_TYPE),
			BallotProcessor.electionType.toString()
		);
	}
	@Test
	void testEndorsedParty() {
		assertEquals(
			Utils.getPropValue(testProps, Initialize.PROP_ENDORSED_PARTY),
			BallotProcessor.endorsedParty.toString()
		);
	}
}
