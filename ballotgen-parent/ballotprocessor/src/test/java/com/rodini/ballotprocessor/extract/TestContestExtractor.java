package com.rodini.ballotprocessor.extract;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.rodini.ballotprocessor.BallotProcessor;
import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.*;
import com.rodini.ballotutils.Utils;

import static com.rodini.ballotprocessor.extract.BallotText.*;
import com.rodini.ballotutils.ElectionType;
import com.rodini.ballotutils.Party;

class TestContestExtractor {
	private static MockedAppender mockedAppender;
	private static Logger logger;
    private static List<String> titlesOfTicketContests = List.of(
    		"GOVERNOR AND LIEUTENANT GOVERNOR",
			"PRESIDENT AND VICE-PRESIDENT");
    private static List<String> titlesOfLocalContests = List.of(
		"AUDITOR",
		"CONSTABLE",
		"DEMOCRATIC COUNTY COMMISSIONER",
		"INSPECTOR OF ELECTIONS",
		"JUDGE OF ELECTIONS",
		"MAGISTERIAL DISTRICT JUDGE",
		"MAYOR",
		"MEMBER OF COUNCIL",
		"SCHOOL DIRECTOR",
		"TOWNSHIP COMMISSIONER",
		"TOWNSHIP SUPERVISOR",
		"TOWNSHIP SUPERVISOR AT LARGE",
		"TOWNSHIP DISTRICT SUPERVISOR",
		"TAX COLLECTOR",
		"DISTRICT SUPERVISOR",
		"COUNCIL",
		"SUPERVISOR",
		"BOROUGH COUNCIL");
	
	private static final List<String> titlesOfLocalContestsExceptions = List.of(
		"SCHOOL DIRECTOR OCTORARA REGION 1",
		"SCHOOL DIRECTOR UNIONVILLE CHADDS FORD REGION C",
		"SCHOOL DIRECTOR TWIN VALLEY REGION 2",
		"SCHOOL DIRECTOR SPRING FORD REGION 3");

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	    mockedAppender = new MockedAppender();
	    mockedAppender.start();
	    logger = (Logger)LogManager.getLogger(ContestExtractor.class);
	    logger.addAppender(mockedAppender);
	    logger.setLevel(Level.ERROR);
	    Initialize.writeIn = "Write-in\n"; // must have trailing \n
	    Initialize.contestTextRegex = new Pattern[2];
	    Initialize.contestTextRegex[0] = null;
	    Initialize.contestTextRegex[1] = null;
	    Initialize.titlesOfTicketContests = titlesOfTicketContests;
	    Initialize.titlesOfLocalContests = titlesOfLocalContests;
	    Initialize.titlesOfLocalContestsExceptions = titlesOfLocalContestsExceptions;
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		mockedAppender.messages.clear();
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	//@Disabled
	@Test
	void testExtractContests1() {
		// 005_ATGLEN Primary 2024
		List<Contest> contests;
		List<String> contestTitles = Arrays.asList(
				"President of the United States",
				"United States Senator",
				"Attorney General",
				"Auditor General",
				"State Treasurer",
				"Representative in Congress\n6th District",
				"Representative in the General\nAssembly\n74th District",
				"Delegate to the National\nConvention\n6th District"
				);
		// 2024_Primary_Dems context regexes
		Initialize.contestTextRegex[0] = Utils.compileRegex("^(?<title>(.*\n){1,3})(?<instructions>^Vote.*)\n(?<candidates>((.*\n){1})*?)^Write-in$");
		Initialize.contestTextRegex[1] = null;
		BallotProcessor.electionType = ElectionType.PRIMARY;
		BallotProcessor.endorsedParty = Party.DEMOCRATIC;
		// Create dummy ballot that is needed.
		Ballot ballot = new Ballot("005_ATGLEN", "");
		contests = ContestExtractor.extractContests(ballot, ATGLEN_PRIMARY_2024_BALLOT);
		// There should be 8 contests.
		assertEquals(8, contests.size());
		// And their names should match these
		for (int i = 0; i < 8; i++) {
			String contestTitle = contests.get(i).getTitle();
			assertEquals(contestTitles.get(i), contestTitle);			
		}
	}
	//@Disabled
	@Test
	void testExtractContests2() {
		// 350_MALVERN Primary 2023
		List<Contest> contests;
		List<String> contestTitles = Arrays.asList(
				"Justice of the Supreme Court",
				"Judge of the Superior Court",
				"Judge of the Commonwealth Court",
				"Judge of the Court of Common Pleas\n10 Year Term",
				"County Commissioner",
				"District Attorney",
				"Sheriff",
				"Prothonotary",
				"Register of Wills",
				"Recorder of Deeds",
				"School Director\nGreat Valley Region 2",
				"Member of Council\nMalvern Borough"
				);
		// 2023_Primary_Dems contest regexes
		Initialize.contestTextRegex[0] = Utils.compileRegex("^(?<title>(.*\n){1,3})(?<instructions>^Vote.*)\n(?<candidates>((.*\n){1})*?)^Write-in$");
		Initialize.contestTextRegex[1] = null;
		BallotProcessor.electionType = ElectionType.PRIMARY;
		BallotProcessor.endorsedParty = Party.DEMOCRATIC;
		// Create dummy ballot that is needed.
		Ballot ballot = new Ballot("350_MALVERN", "");
		contests = ContestExtractor.extractContests(ballot, MALVERN_PRIMARY_2023_BALLOT);
		// There should be 12 contests.
		assertEquals(12, contests.size());
		// And their names should match these
		for (int i = 0; i < 12; i++) {
			String contestTitle = contests.get(i).getTitle();
			assertEquals(contestTitles.get(i), contestTitle);			
		}
		
	}
	//@Disabled
	@Test
	void testExtractContests3() {
		// 005_ATGLEN General 2024
		List<Contest> contests;
		List<String> contestTitles = Arrays.asList(
				"Presidential Electors",
				"United States Senator",
				"Attorney General",
				"Auditor General",
				"State Treasurer",
				"Representative in Congress\n6th District",
				"Representative in the General\nAssembly\n74th District"
				);
		// 2024_general_election contest regexes
		// Note the precedence of regex[0] over regex[1]
		Initialize.contestTextRegex[0] = Utils.compileRegex("^(?<title>(.*\n){1})(?<instructions>^Vote for the candidates of one\nparty for President and\nVice-President, or insert the\nnames of candidates.\n)(?<candidates>((.*\n){1})*?)^Write-in$");
		Initialize.contestTextRegex[1] = Utils.compileRegex("^(?<title>(.*\n){1,3})(?<instructions>^Vote.*)\n(?<candidates>((.*\n){1})*?)^Write-in$");
		BallotProcessor.electionType = ElectionType.GENERAL;
		BallotProcessor.endorsedParty = Party.DEMOCRATIC;
		// Create dummy ballot that is needed.
		Ballot ballot = new Ballot("005_ATGLEN", "");
		contests = ContestExtractor.extractContests(ballot, ATGLEN_GENERAL_2024_BALLOT);
		// There should be 7 contests.
		assertEquals(7, contests.size());
		// And their names should match these
		for (int i = 0; i < 7; i++) {
			String contestTitle = contests.get(i).getTitle();
			assertEquals(contestTitles.get(i), contestTitle);			
		}
	}
	

}
