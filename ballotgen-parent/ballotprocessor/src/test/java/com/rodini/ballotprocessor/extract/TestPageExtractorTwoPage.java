package com.rodini.ballotprocessor.extract;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotutils.Utils;

class TestPageExtractorTwoPage {

	final String testPath = "./src/test/java/";

	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Initialize.ballotTextRegex = Utils.compileRegex("(?m)^(?<id>\\d+) (?<name>.*)\n^COMMONWEALTH OF PENNSYLVANIA, COUNTY OF CHESTER$\n");
		Initialize.pageBreakRegex = Utils.compileRegex("(?m)Vote Both Sides");
		Initialize.onePageTextRegex = Utils.compileRegex("(?m)((.*\n)*^by regular ballot.\n)(?<page>((.*)\n)*)^Chester County$");
		Initialize.twoPageTextP1Regex = Utils.compileRegex("(?m)((.*\n)*^Chester County Board of\nElections\n)(?<page>((.*)\n)*?)^Vote Both Sides$");
		Initialize.twoPageTextP2Regex = Utils.compileRegex("(?m)(^Vote Both Sides$\n)(.*\n)*(^Vote Both Sides$\n)(?<page>((.*)\n)*)^Review$");
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSpecimentTwoPageBallot() throws IOException {
		// see specimen-2026-general-ATGLEN.txt
		String expectedPage1Text =
        """		
        Justice of the Supreme Court
        Vote for ONE
        Daniel McCaffery
        Democratic
        Carolyn Carluccio
        Republican
        Write-in
        Judge of the Superior Court
        Vote for no more than TWO
        Jill Beck
        Democratic
        Timika Lane
        Democratic
        Maria Battista
        Republican
        Harry F. Smail Jr.
        Republican
        Write-in
        Write-in
        Judge of the Court of Common Pleas
        15th Judicial District
        Vote for no more than FIVE
        Sarah B. Black
        Democratic
        Deb Ryan
        Democratic
        Fredda D. Maddox
        Democratic
        Nicole Forzato
        Democratic
        Thomas McCabe
        Democratic
        Lou Mincarelli
        Republican
        PJ Redmond
        Republican
        Andy Rongaus
        Republican
        Don Kohler
        Republican
        Dave Black
        Republican
        Write-in
        Write-in
        Write-in
        Write-in
        Write-in
        County Commissioner
        Vote for no more than TWO
        Josh Maxwell
        Democratic
        Marian Moskowitz
        Democratic
        David C. Sommers
        Republican
        Eric Roe
        Republican
        Write-in
        Write-in
        Typ:01 Seq:0001 Spl:01
        Sheriff
        Vote for ONE
        Kevin Dykes
        Democratic
        Roy Kofroth
        Republican
        Write-in
        Prothonotary
        Vote for ONE
        Debbie Bookman
        Democratic
        Michael Taylor
        Republican
        Write-in
        Register of Wills
        Vote for ONE
        Michele Vaughn
        Democratic
        Terri Clark
        Republican
        Write-in
        Recorder of Deeds
        Vote for ONE
        Diane O'Dwyer
        Democratic
        Brian D. Yanoviak
        Republican
        Write-in
        """;
		String expectedPage2Text =
        """
        School Director
        Octorara Region 1
        Vote for no more than THREE
        Lisa Yelovich
        Democratic
        Brian K. Norris
        Democratic
        Karen A. Williamson
        Democratic/Republican
        Anthony Falgiatore
        Republican
        Joseph Rzonca
        Republican
        Write-in
        Write-in
        Write-in
        Member of Council
        Atglen Borough
        Vote for no more than TWO
        Zachary M. Hall
        Democratic
        Brian Hahn
        Republican
        Write-in
        Write-in
        Auditor Unexpired 2 Year Term
        Atglen Borough
        Vote for ONE
        Write-in
        OFFICIAL JUDICIAL RETENTION
        QUESTIONS INSTRUCTIONS TO
        VOTER
        To vote in FAVOR of the retention,
        blacken the oval ( ) to the left of the
        word YES.
        To vote AGAINST the retention,
        blacken the oval ( ) to the left of the
        word NO.
        VOTE ON EACH OF THE
        FOLLOWING JUDICIAL RETENTION
        QUESTIONS
        Superior Court Retention
        Election Question
        Shall Jack Panella be retained for an
        additional term as Judge of the
        Superior Court of the Commonwealth
        of Pennsylvania?
        YES
        NO
        Superior Court Retention
        Election Question
        Shall Victor P. Stabile be retained for
        an additional term as Judge of the
        Superior Court of the Commonwealth
        of Pennsylvania?
        YES
        NO
        Court of Common Pleas Retention
        Election Question
        Shall Patrick Carmody be retained
        for an additional term as Judge of the
        Court of Common Pleas, 15th Judicial
        District, Chester County?
        YES
        NO
        Court of Common Pleas Retention
        Election Question
        Shall John L. Hall be retained for an
        additional term as Judge of the Court
        of Common Pleas, 15th Judicial
        District, Chester County?
        YES
        NO
        """;
		String specimenText = Files.readString(Path.of(testPath + "specimen-2023-general-ATGLEN.txt"));
		List<Ballot> ballots = BallotExtractor.extract(specimenText);
		assertEquals(1, ballots.size());
		PageExtractor.extract(ballots);
		Ballot ballot = ballots.get(0);
		String page1Text = ballot.getPage1Text();
		String page2Text = ballot.getPage2Text();
		assertEquals(expectedPage1Text, page1Text);
		assertEquals(expectedPage2Text, page2Text);
		assertTrue(ballot.getRawText() == null);
		
	}

}
