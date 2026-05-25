package com.rodini.ballotprocessor.extract;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rodini.ballotprocessor.BallotProcessor;
import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotprocessor.model.Election;
import com.rodini.ballotutils.Utils;
import static com.rodini.ballotutils.Party.*;
import static com.rodini.ballotutils.ElectionType.*;

class TestElectionExtractor {
	private static MockedAppender mockedAppender;
	private static Logger logger;
	private static final String testPath = "./src/test/java/";
	private static final String ELECTION_2026_PRIMARY =
	        """
	        GENERAL PRIMARY ELECTION DEMOCRATIC BALLOT
	        COUNTY OF CHESTER, COMMONWEALTH OF PENNSYLVANIA
	        May 19, 2026
	        """;
	private static final String ELECTION_2023_GENERAL =
            """
            COMMONWEALTH OF PENNSYLVANIA, COUNTY OF CHESTER
            OFFICIAL MUNICIPAL ELECTION BALLOT
            NOVEMBER 7, 2023
            """;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    logger = (Logger)LogManager.getLogger(ElectionExtractor.class);
	    logger.addAppender(mockedAppender);
	    logger.setLevel(Level.ERROR);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
		mockedAppender.messages.clear();
	}

	@Test
	void testPrimaryElection1() throws IOException {
		String specimenText = Files.readString(Path.of(testPath + "specimen-2026-primary-ATGLEN.txt"));
		Initialize.electionTextRegex = Utils.compileRegex("(?mi)(?<title>^GENERAL PRIMARY ELECTION DEMOCRATIC BALLOT\n^COUNTY OF CHESTER, COMMONWEALTH OF PENNSYLVANIA$\n(.*\n){1})");
		BallotProcessor.electionType = PRIMARY;
		BallotProcessor.endorsedParty = DEMOCRATIC;
		BallotProcessor.ballots = new ArrayList<Ballot>();
		Election election = ElectionExtractor.extract(specimenText);
		assertEquals(ELECTION_2026_PRIMARY, election.getTitle());
		assertEquals("May 19, 2026", election.getDate());		
	}
	@Test
	void testPrimaryElection2() throws IOException {
		String specimenText = Files.readString(Path.of(testPath + "specimen-2026-primary-ATGLEN.txt"));
		Initialize.electionTextRegex = Utils.compileRegex("(?mi)(?<title>^GENERAL PRIMARY ELECTION DEMOCRATIC BALLOT\n^COUNTY OF CHESTER, COMMONWEALTH OF PENNSYLVANIA$\n(.*\n){1})");
		// Misconfiguration here.
		BallotProcessor.electionType = GENERAL;
		BallotProcessor.endorsedParty = DEMOCRATIC;
		BallotProcessor.ballots = new ArrayList<Ballot>();
		Election election = ElectionExtractor.extract(specimenText);
		assertEquals(ELECTION_2026_PRIMARY, election.getTitle());
		assertEquals("May 19, 2026", election.getDate());
		assertEquals(1, mockedAppender.messages.size());
		assertTrue(mockedAppender.messages.get(0).startsWith("ballotprocessor configured for GENERAL"));
	}

	@Test
	void testGeneralElection1() throws IOException {
		String specimenText = Files.readString(Path.of(testPath + "specimen-2023-general-ATGLEN.txt"));
		Initialize.electionTextRegex = Utils.compileRegex("(?mi)(?<title>^COMMONWEALTH OF PENNSYLVANIA, COUNTY OF CHESTER\n^OFFICIAL MUNICIPAL ELECTION BALLOT$\n(.*\n){1})");		
		// Misconfiguration here.
		BallotProcessor.electionType = PRIMARY;
		BallotProcessor.endorsedParty = DEMOCRATIC;
		BallotProcessor.ballots = new ArrayList<Ballot>();
		Election election = ElectionExtractor.extract(specimenText);
		assertEquals(ELECTION_2023_GENERAL, election.getTitle());
		assertEquals("NOVEMBER 7, 2023", election.getDate());		
		assertEquals(1, mockedAppender.messages.size());
		assertTrue(mockedAppender.messages.get(0).startsWith("ballotprocessor configured for PRIMARY"));
	}

}
