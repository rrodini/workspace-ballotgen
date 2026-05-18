package com.rodini.ballotprocessor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.ballotutils.ElectionType;

public class Election {
	private static final Logger logger = LogManager.getLogger(Election.class);
	private ElectionType electionType;  // from property or title?
	private String title;
	private String date;
	private List<Ballot> ballots;

	
	public Election(String title, String date) {
		super();
		this.title = title;
		this.date = date;
		this.ballots = new ArrayList<>();
	}
	public String getTitle() {
		return title;
	}
	public String getDate() {
		return date;
	}
	public void extendBallots(Ballot ballot) {
		ballots.add(ballot);
	}
	public List<Ballot> getBallots() {
		return ballots;
	}
}
