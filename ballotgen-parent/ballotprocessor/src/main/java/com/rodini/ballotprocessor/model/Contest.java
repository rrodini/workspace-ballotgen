package com.rodini.ballotprocessor.model;

import java.util.List;
import static java.util.stream.Collectors.joining;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Contest extends VoteFor {
	private static final Logger logger = LogManager.getLogger(Contest.class);
//	private String title; // e.g. "Justice of the\nSupreme Court"

	// use this to avoid null values
	public final static Contest GENERIC_CONTEST = new Contest(null, "Title", "", "", null);
	
	private String term; // e.g. "6 Year term" or "Vote for no more than EIGHT" or ""
	private String instructions; // e.g. "VOTE for one"
	private List<Candidate> candidates;

	public Contest(Ballot ballot, String title, String term, String instructions, List<Candidate> candidates) {
		super(ballot, title);
		this.term = term;
		this.instructions = instructions;
		this.candidates = candidates;
	}
	public String getTerm() {
		return term;
	}
	public String getInstructions() {
		return instructions;
	}
	public List<Candidate> getCandidates() {
		return candidates;
	}
	@Override
	public String toString() {
		// shorten to essential info
		StringBuilder sb = new StringBuilder("Contest: ");
		sb.append(title + "\n");
		if (!term.isEmpty()) {
			sb.append(term + "\n");
		}
		sb.append(instructions + "\n");
		sb.append("Candidates: ");
		String names = candidates.stream().map( candidate -> candidate.toString()).collect(joining(","));
		sb.append(names);
		String contestText = sb.toString();
		// strip off the final ", " from last candidate
		return contestText;
	}

}
