package com.rodini.ballotprocessor.extract;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.regex.Pattern;

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
	    Initialize.writeIn = "Write-in\n"; // must have trailing \n
		Initialize.ballotTextRegex = Utils.compileRegex("(?m)^(?<id>\\d+) (?<name>.*) DEM\n^OFFICIAL CHESTER COUNTY$\n");
		Initialize.pageBreakRegex = Utils.compileRegex("(?m)Vote Both Sides");
		Initialize.onePageTextRegex = Utils.compileRegex("(?m)((.*\n)*^by regular ballot.\n)(?<page>((.*)\n)*)^Chester County$");
		Initialize.twoPageTextP1Regex = Utils.compileRegex("(?m)((.*\n)*^Chester County Board of\nElections\n)(?<page>((.*)\n)*?)^Vote Both Sides$");
		Initialize.twoPageTextP2Regex = Utils.compileRegex("(?m)(^Vote Both Sides$\n)(.*\n)*(^Vote Both Sides$\n)(?<page>((.*)\n)*)^Review$");
	    Initialize.contestTextRegex = new Pattern[2];
		Initialize.contestTextRegex[0] = Utils.compileRegex("^(?<title>(.*\n){1,3})(?<instructions>^Vote.*)\n(?<candidates>((.*\n){1})*?)^Write-in$");
	    Initialize.referendumTextRegex = null;
	    Initialize.retentionTextRegex = Utils.compileRegex("(?mi)^(?<title>.*Retention\nElection Question$)(?<question>((.*)\n)*?)^YES\nNO");
	    Initialize.retentionNameRegex = Utils.compileRegex("((.*)\n)*^Shall (?<name>(.*)?) be retained.*\n");
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
