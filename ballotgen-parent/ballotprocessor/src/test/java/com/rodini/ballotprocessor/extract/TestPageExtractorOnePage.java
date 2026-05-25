package com.rodini.ballotprocessor.extract;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotutils.Utils;

class TestPageExtractorOnePage {

	final String testPath = "./src/test/java/";

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	    Initialize.writeIn = "Write-in\n"; // must have trailing \n
		Initialize.ballotTextRegex = Utils.compileRegex("(?m)^(?<id>\\d+) (?<name>.*) DEM\n^OFFICIAL CHESTER COUNTY$\n");
		Initialize.pageBreakRegex = Utils.compileRegex("(?m)Vote Both Sides");
		Initialize.onePageTextRegex = Utils.compileRegex("(?m)((.*\n)*^by regular ballot.\n)(?<page>((.*)\n)*)^Chester County$");
		Initialize.twoPageTextP1Regex = Utils.compileRegex("(?m)((.*\n)*^Chester County Board of\nElections\n)(?<page>((.*)\n)*?)^Vote Both Sides$");
		Initialize.twoPageTextP2Regex = Utils.compileRegex("(?m)(^Vote Both Sides$\n)(.*\n)*(^Vote Both Sides$\n)(?<page>((.*)\n)*)^Review$");
	    Initialize.contestTextRegex = new Pattern[2];
		Initialize.contestTextRegex[0] = Utils.compileRegex("^(?<title>(.*\n){1,3})(?<instructions>^Vote.*)\n(?<candidates>((.*\n){1})*?)^Write-in$");
	    Initialize.contestTextRegex[1] = null;
	    Initialize.referendumTextRegex = Utils.compileRegex("(?mi)^(?<title>(.*Referendum.*\n))(?<text>(.*\n)*?)^YES$\nNO$");
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
	void testSpecimenOnePageOneBallot() throws IOException {
		// see specimen-2026-primary-ATGLEN.txt
		String expectedPage1Text = 
        """
        Governor
        Vote for One
        Josh Shapiro
        Montgomery County
        Write-in
        Lieutenant Governor
        Vote for One
        Austin Davis
        Allegheny County
        Write-in
        Representative in Congress
        6th District
        Vote for One
        Chrissy Houlahan
        Chester County
        Write-in
        Senator in the
        General Assembly
        44th District
        Vote for One
        Katie Muth
        Chester County
        Write-in
        Representative in the
        General Assembly
        74th District
        Vote for One
        Dan Williams
        Sadsbury
        Write-in
        Member of Democratic
        State Committee
        Vote for ten (10).
        The five (5) females and the
        five (5) males with the highest
        number of votes shall be elected
        Carlotta D. Johnston Pugh
        Female - Tredyffrin
        Michelle Smith
        Female - Upper Uwchlan
        Charlotte Valyo
        Female - Schuylkill
        Paul Lahm
        Male - West Whiteland
        Kieran Francke
        Male - London Britain
        Jenn Fenn
        Female - West Bradford
        Brian J. McGinnis
        Male - West Chester
        Kristin Gerling
        Female - West Chester
        Christopher Kowerdovich
        Male - East Brandywine
        Diane O'Dwyer
        Female - Uwchlan
        Russ Phifer
        Male - London Grove
        Lani Frank
        Female - Willistown
        Ashley Lahm
        Female - West Whiteland
        Abdul Mughees Chaudhri
        Male - Upper Uwchlan
        Write-in
        Write-in
        Write-in
        Write-in
        Write-in
        Write-in
        Write-in
        Write-in
        Write-in
        Write-in
        Typ:01 Seq:0001 Spl:01
        Democratic County
        Committee Member
        005 Atglen
        Vote for not more than Two
        Write-in
        Write-in				
        """;
		String specimenText = Files.readString(Path.of(testPath + "specimen-2026-primary-ATGLEN.txt"));
		List<Ballot> ballots = BallotExtractor.extract(specimenText);
		assertEquals(1, ballots.size());
		PageExtractor.extract(ballots);
		Ballot ballot = ballots.get(0);
		String page1Text = ballot.getPage1Text();
		String page2Text = ballot.getPage2Text();
		assertEquals(expectedPage1Text, page1Text);
		assertEquals("", page2Text);
		assertTrue(ballot.getRawText() == null);
	}

}
