package com.rodini.ballotprocessor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rodini.ballotprocessor.*;
import com.rodini.ballotprocessor.model.Election;
import com.rodini.ballotprocessor.model.Ballot;

import com.rodini.ballotutils.Utils;

class TestBallotProcessorClient {

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
		Properties props = Utils.loadProperties("./resources/ballotprocessor.properties");
		BallotProcessor.start(props);
		DataRoot ballotRoot = BallotProcessor.getDataRoot();
		Election election = ballotRoot.getElection();
		if (election != null) {
			System.out.println(String.format("Election title: %s", election.getTitle()));
			System.out.println(String.format("Ballots  #: %d", election.getBallots().size()));
		} else {
			System.out.println("Election object is null.");
		}
		BallotProcessor.stop();	
	}

}
