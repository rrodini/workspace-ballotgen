package com.rodini.ballotprocessor.extract;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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
import com.rodini.ballotprocessor.model.Contest;
import com.rodini.ballotprocessor.model.Referendum;
import com.rodini.ballotprocessor.model.Retention;
import com.rodini.ballotutils.Utils;

class TestPageExtractorVoteFors {

	final String testPath = "./src/test/java/";
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
	    logger = (Logger)LogManager.getLogger(PageExtractor.class);
	    logger.addAppender(mockedAppender);
	    logger.setLevel(Level.ERROR);
		Initialize.ballotTextRegex = Utils.compileRegex("(?m)^(?<id>\\d+) (?<name>.*)\n^COMMONWEALTH OF PENNSYLVANIA, COUNTY OF CHESTER$\n");
		Initialize.pageBreakRegex = Utils.compileRegex("(?m)Vote Both Sides");
	    Initialize.writeIn = "Write-in\n"; // must have trailing \n
		Initialize.onePageTextRegex = Utils.compileRegex("(?m)((.*\n)*^by regular ballot.\n)(?<page>((.*)\n)*)^Chester County$");
		Initialize.twoPageTextP1Regex = Utils.compileRegex("(?m)((.*\n)*^Chester County Board of\nElections\n)(?<page>((.*)\n)*?)^Vote Both Sides$");
		Initialize.twoPageTextP2Regex = Utils.compileRegex("(?m)(^Vote Both Sides$\n)(.*\n)*(^Vote Both Sides$\n)(?<page>((.*)\n)*)^Review$");
	    Initialize.contestTextRegex = new Pattern[2];
		Initialize.contestTextRegex[0] = Utils.compileRegex("^(?<title>(.*\n){1})(?<instructions>^Vote for the candidates of one\nparty for President and\nVice-President, or insert the\nnames of candidates.\n)(?<candidates>((.*\n){1})*?)^Write-in$");
		Initialize.contestTextRegex[1] = Utils.compileRegex("^(?<title>(.*\n){1,3})(?<instructions>^Vote.*)\n(?<candidates>((.*\n){1})*?)^Write-in$");	    
	    Initialize.referendumTextRegex = Utils.compileRegex("(?mi)^(?<title>((.*\n){1}.*Referendum.*\n)(.*\n){1})(?<text>(.*\n)*?)^YES$\nNO$");
	    Initialize.titlesOfTicketContests = titlesOfTicketContests;
	    Initialize.titlesOfLocalContests = titlesOfLocalContests;
	    Initialize.titlesOfLocalContestsExceptions = titlesOfLocalContestsExceptions;
	    Initialize.retentionTextRegex = Utils.compileRegex("(?mi)^(?<title>.*Retention\nElection Question$)(?<question>((.*)\n)*?)^YES\nNO");
	    Initialize.retentionNameRegex = Utils.compileRegex("((.*)\n)*^Shall (?<name>(.*)?) be retained.*\n");
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() throws IOException {
		// 2023 General ATGLEN HONEYBROOK
		String specimenText = Files.readString(Path.of(testPath + "specimen-2023-general-ATGLEN-HONEYBROOK.txt"));
		List<Ballot> ballots = BallotExtractor.extract(specimenText);
		PageExtractor.extract(ballots);
		assertEquals(2, ballots.size());
		generateBallotObjects(ballots.get(1));
	}
	
	void generateBallotObjects(Ballot ballot) {
		StringWriter pw = new StringWriter();
		try {
			pw.write(ballot.getPrecinctNoName());
			pw.append('\n');
			for (Contest contest: ballot.getContests()) {
				pw.write(contest.toString());
			}
//			pw.append('\n');
			for (Referendum referendum: ballot.getReferendums()) {
				pw.write(referendum.toString());
			}
//			pw.append('\n');
			for (Retention retention: ballot.getRetentions()) {
				pw.write(retention.toString());
			}
//			pw.append('\n');
			
		} catch (Exception ex) {
			ex.printStackTrace();
			String msg = String.format("Exception writing ballot objects report: %s", ex.getMessage());			
			System.out.println(msg);
		}
		pw.flush();
		System.out.print(pw.getBuffer().toString());
	}
	

}
