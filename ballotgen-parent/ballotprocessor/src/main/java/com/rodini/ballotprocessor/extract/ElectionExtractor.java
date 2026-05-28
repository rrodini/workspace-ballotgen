package com.rodini.ballotprocessor.extract;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rodini.ballotprocessor.BallotProcessor;
import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.Election;
import com.rodini.ballotutils.Utils;
import static com.rodini.ballotutils.ElectionType.*;

public class ElectionExtractor {
	static final Logger logger = LogManager.getLogger(ElectionExtractor.class);
	
	public static Election extract(String specimenText) {
		logger.info(String.format("election extraction:"));
		// apply electionTextRegex to BallotProcessor.specimenText
		String title = extractTitle(specimenText);
		// extract the date from the title
		String date  = extractDate(title);
		// check for misconfiguration of election type.
		checkElectionType(title);
		Election election = new Election(title,
				date,
				BallotProcessor.electionType,
				BallotProcessor.endorsedParty,
				BallotProcessor.ballots);
		return election;
	}
	
	static String extractTitle(String specimenText) {
		// apply electionTextRegex to BallotProcessor.specimenText
		String title = "ELECTION";
		Pattern pat = Initialize.electionTextRegex;
		Matcher match = pat.matcher(specimenText);
		if (match.find()) {
			title = match.group("title");
		} else {
			logger.error(String.format("no match for %s", Initialize.PROP_BALLOT_ELECTION_TEXT_REGEX));
		}
		return title;	
	}
	
	static void checkElectionType(String title) {
		// Primary => "Primary" must be present in title.
		// General => "Primary" must NOT be present in title.
		Pattern pat = Utils.compileRegex("(?mi).*Primary.*");
		Matcher match = pat.matcher(title);
		boolean found = match.find();	
		if (BallotProcessor.electionType == PRIMARY ) {
			if (!found) {
				logger.error(String.format("ballotprocessor configured for PRIMARY but title: %s", title));
			}
		} else if (BallotProcessor.electionType == GENERAL ){
			if (found) {
				logger.error(String.format("ballotprocessor configured for GENERAL but title: %s", title));
			}
		}
	}
	
	
	static String extractDate(String title) {
		// Primary in May / General in November
		String date = "Month dd, yyyy";
		Pattern pat = Utils.compileRegex("(?mi)^(?<date>(May|November) (\\d)*, (\\d)*)\n");
		Matcher match = pat.matcher(title);
		if (match.find()) {
			date = match.group("date");
		} else {
			logger.error(String.format("no match for election date: %s", title));
		}
		return date;
	}

}
