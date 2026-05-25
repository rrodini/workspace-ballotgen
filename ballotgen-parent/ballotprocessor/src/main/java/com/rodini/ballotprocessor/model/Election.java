package com.rodini.ballotprocessor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.ballotutils.ElectionType;
import com.rodini.ballotutils.Party;

public class Election {
	private static final Logger logger = LogManager.getLogger(Election.class);

	private String title;
	private ElectionType electionType;  // checked against title
	private Party endorsedParty;
	private String date;
	private List<Ballot> ballots;

	
	public Election(String title, String date, ElectionType type, Party endorsedParty, List<Ballot> ballots) {
		this.title = title;
		this.date = date;
		this.electionType = type;
		this.endorsedParty = endorsedParty;
		this.ballots = ballots;
	}
	
	public String getTitle() {
		return title;
	}	
	public ElectionType getElectionType() {
		return electionType;
	}
	public Party getEndorsedParty() {
		return endorsedParty;
	}
	public String getDate() {
		return date;
	}
	public List<Ballot> getBallots() {
		return ballots;
	}
}
