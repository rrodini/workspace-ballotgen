package com.rodini.ballotprocessor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rodini.ballotprocessor.BallotProcessor;

class TestBallotProcessorStandalone {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		String specimenFile   = "./src/test/java/specimen-2026-primary-ATGLEN-AVONDALE-BIRMINGHAM-1.txt";
		String storeDirectory = "./src/test/java/_elections/2026-primary-dems/store";
		String [] args = {specimenFile, storeDirectory};
		BallotProcessor.main(args);
	}

}
