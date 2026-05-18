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

class TestBallotExtractor {
	final String testPath = "./src/test/java/";

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Initialize.ballotTextRegex = Utils.compileRegex("(?m)^(?<id>\\d+) (?<name>.*) DEM\n^OFFICIAL CHESTER COUNTY$\n");
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSpecimenOnePageOneBallot() throws IOException {
		// specimen-2026-primary-ATGLEN.txt
		String specimenText = Files.readString(Path.of(testPath + "specimen-2026-primary-ATGLEN.txt"));
		// Beware: must use leading spaces below.
		String expected = 
        """
        005 ATGLEN DEM
        OFFICIAL CHESTER COUNTY
        GENERAL PRIMARY ELECTION DEMOCRATIC BALLOT
        COUNTY OF CHESTER, COMMONWEALTH OF PENNSYLVANIA
        May 19, 2026
        INSTRUCTIONS TO VOTER
        1. TO VOTE YOU MUST COMPLETELY DARKEN THE OVAL ( ) TO THE LEFT OF YOUR CHOICE. An oval ( ) darkened to the left of the name
        of any candidate indicates a vote for that candidate.
        2. To cast a write-in vote for a person whose name is not on the ballot, you must darken the oval ( ) to the left of the line provided, and write or print
        the name in the blank space provided for that purpose.
        3. Use only black or blue ink pen.
        4. If you make a mistake DO NOT ERASE. Ask for a new ballot.
        5. WARNING:If you receive an absentee or mail-in ballot and return your voted ballot by the deadline, you may not vote at your polling place on
        Election Day. If you are unable to return your voted absentee or mail-in ballot by the deadline, you may only vote a provisional ballot at your polling
        place on Election Day, unless you surrender your absentee or mail-in ballot and declaration envelope to the Judge of Elections to be voided to vote
        by regular ballot.
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
        Chester County
        Board of Elections
        Review
        """;
		List<Ballot> ballots = BallotExtractor.extract(specimenText);
		Ballot ballot = ballots.get(0);
		assertEquals(1, ballots.size());
		assertEquals("005", ballot.getPrecinctNo());
		assertEquals("ATGLEN", ballot.getPrecinctName());
		assertEquals("005_ATGLEN", ballot.getPrecinctNoName());
		assertEquals(expected, ballots.get(0).getRawText());
	}

	void testSpecimenOnePageThreeBallots() throws IOException {
		// specimen-2026-primary-ATGLEN-AVONDALE-BIRMINGHAM.txt
		// specimen-2026-primary-ATGLEN.txt
		String specimenText = Files.readString(Path.of(testPath + "specimen-2026-primary-ATGLEN-AVONDALE-BIRMINGHAM-1.txt"));
		List<Ballot> ballots = BallotExtractor.extract(specimenText);
		assertEquals(3, ballots.size());
		List<String> expected = List.of("005_ATGLEN", "010_AVONDALE", "014_BIRMINGHAM_1");
		for (int i = 0; i < ballots.size(); i++) {
			assertEquals(expected.get(i), ballots.get(i).getPrecinctNoName());			
		}		
	}
	
	
}
