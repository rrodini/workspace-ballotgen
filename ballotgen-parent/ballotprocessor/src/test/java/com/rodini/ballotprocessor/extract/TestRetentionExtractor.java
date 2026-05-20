package com.rodini.ballotprocessor.extract;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
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
import com.rodini.ballotprocessor.model.Retention;
import com.rodini.ballotutils.Utils;

import static com.rodini.ballotprocessor.extract.BallotText.*;

class TestRetentionExtractor {
	private static MockedAppender mockedAppender;
	private static Logger logger;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    logger = (Logger)LogManager.getLogger(RetentionExtractor.class);
	    logger.addAppender(mockedAppender);
	    logger.setLevel(Level.ERROR);
	    Initialize.retentionTextRegex = null;
	    Initialize.retentionNameRegex = null;
	}

	@BeforeEach
	void setUp() throws Exception {
		mockedAppender.messages.clear();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testExtractRetentions1() {
		// 300 Honey Brook Township 1 2023
		List<Retention> retentions;
	    Initialize.retentionTextRegex = Utils.compileRegex("(?mi)^(?<title>.*Retention\nElection Question$)(?<question>((.*)\n)*?)^YES\nNO");
	    Initialize.retentionNameRegex = Utils.compileRegex("((.*)\n)*^Shall (?<name>(.*)?) be retained.*\n");
		// Create dummy ballot that is needed.
		Ballot ballot = new Ballot("300_HONEY_BROOK_TOWNSHIP_1", "");
		retentions = RetentionExtractor.extractRetentions(ballot, HONEYBROOK_GENERAL_2023_BALLOT);
		// There should be 4 retentions.
		assertEquals(4, retentions.size());
		List<String> retentionTitles = Arrays.asList(
				"Superior Court Retention\nElection Question",
				"Superior Court Retention\nElection Question",
				"Court of Common Pleas Retention\nElection Question",
				"Court of Common Pleas Retention\nElection Question");
		List<String> retentionNames = Arrays.asList(
				"Jack Panella",
				"Victor P. Stabile",
				"Patrick Carmody",
				"John L. Hall"
				);
		for (int i = 0; i < 4; i++) {
			String title = retentions.get(i).getTitle();
			assertEquals(retentionTitles.get(i), title);			
			String name = retentions.get(i).getJudgeName();
			assertEquals(retentionNames.get(i), name);			
		}
		
	}

	
}
