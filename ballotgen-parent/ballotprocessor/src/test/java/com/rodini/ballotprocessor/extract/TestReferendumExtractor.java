package com.rodini.ballotprocessor.extract;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotprocessor.model.Referendum;
import com.rodini.ballotutils.Utils;

import static com.rodini.ballotprocessor.extract.BallotText.*;

class TestReferendumExtractor {
	private static MockedAppender mockedAppender;
	private static Logger logger;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    logger = (Logger)LogManager.getLogger(ReferendumExtractor.class);
	    logger.addAppender(mockedAppender);
	    logger.setLevel(Level.ERROR);
	    Initialize.referendumTextRegex = null;
	}

	@BeforeEach
	void setUp() throws Exception {
		mockedAppender.messages.clear();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testExtractReferendums1() {
		// 350_Malvern Primary 2023
		List<Referendum> referendums;
		// ATTENTION: title is ONE line.
	    Initialize.referendumTextRegex = Utils.compileRegex("(?mi)^(?<title>(.*Referendum.*\n))(?<text>(.*\n)*?)^YES$\nNO$");
		// Create dummy ballot that is needed.
		Ballot ballot = new Ballot("350_MALVERN", "");
		referendums = ReferendumExtractor.extractReferendums(ballot, MALVERN_PRIMARY_2023_BALLOT);
		// There should be 1 referendum.
		assertEquals(1, referendums.size());
		// And the name should match
		String title = referendums.get(0).getTitle();
		assertEquals("Malvern Borough Referendum", title);			
	}

	@Test
	void testExtractReferendums2() {
		// (?mi)^(?<title>((.*\n).*Referendum.*\n))(?<text>(.*\n)*?)^YES$\nNO$
		// 350_Malvern Primary 2023
		List<Referendum> referendums;
		// ATTENTION: title is ONE line.
	    Initialize.referendumTextRegex = Utils.compileRegex("(?mi)^(?<title>((.*\n){1}.*Referendum.*\n)(.*\n){1})(?<text>(.*\n)*?)^YES$\nNO$");
		// Create dummy ballot that is needed.
		Ballot ballot = new Ballot("300_HONEY_BROOK_TOWNSHIP_1", "");
		referendums = ReferendumExtractor.extractReferendums(ballot, HONEYBROOK_GENERAL_2023_BALLOT);
		// There should be 1 referendum.
		assertEquals(1, referendums.size());
		// And the name should match
		String title = referendums.get(0).getTitle();
		String expectedTitle = 	"Honey Brook Township:\nReferendum for Additional\nTownship Supervisors";
		assertEquals(expectedTitle, title);			
	}

	
}
