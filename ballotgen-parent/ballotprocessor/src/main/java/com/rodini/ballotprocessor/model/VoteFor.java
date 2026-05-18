package com.rodini.ballotprocessor.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class VoteFor {
	private static final Logger logger = LogManager.getLogger(VoteFor.class);

	protected Ballot ballot;	// parent object to obtain precinctNo, precinctName
	protected String title;		// One of Contest.title, Retention.question, Referendum.question
	
	public VoteFor(Ballot ballot, String title) {
		this.ballot = ballot;
		this.title = title;
	}

	public Ballot getBallot() {
		return ballot;
	}

	public String getTitle() {
		return title;
	}
	
	public String normalizeTitle() {
		return title.replace("\n", " ");
	}
	
	
}
