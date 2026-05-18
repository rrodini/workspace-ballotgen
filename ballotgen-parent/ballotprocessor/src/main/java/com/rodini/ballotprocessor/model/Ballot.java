package com.rodini.ballotprocessor.model;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.ballotutils.Utils;

public class Ballot {
	private static final Logger logger = LogManager.getLogger(Ballot.class);
	private String precinctNoName;	// e.g. 301_HONEY_BROOK_TOWNSHIP 2
	private String precinctNo;		// e.g. 301
	private String precinctName;	// e.g. HONEY_BROOK_TOWNSHIP 2
	private String rawText;			// to be refined, than discarded.
	private String page1Text;		// to be refined into VoteFor objects
	private String page2Text;		// to be refined into VoteFor objects
	private List<VoteFor> voteFors;	// List of everything you vote for (or against).

	public Ballot(String precinctNoName, String rawText) {
		this.precinctNoName = precinctNoName;
		this.precinctNo = Utils.getPrecinctNo(precinctNoName);
		this.precinctName = Utils.getPrecinctName(precinctNoName);
		this.rawText = rawText;
		this.page1Text = "";
		this.page2Text = "";
		this.voteFors = new ArrayList<> ();
	}
	public String getPrecinctNo() {
		return precinctNo;
	}
	public String getPrecinctName() {
		return precinctName;
	}
	public String getPrecinctNoName() {
		return precinctNoName;
	}
	public String getRawText() {
		return rawText;
	}
	public void discardRawText() {
		rawText = null;
	}
	public String getPage1Text() {
		return page1Text;
	}
	public void setPage1Text(String text) {
		page1Text = text;
	}
	public String getPage2Text() {
		return page2Text;
	}
	public void setPage2Text(String text) {
		page2Text = text;
	}
	public void extendVoteFors(Contest contest) {
		voteFors.add(contest);
	}
	public void extendVoteFors(Referendum referendum) {
		voteFors.add(referendum);
	}
	public List<VoteFor> getVoteFors() {
		return voteFors;
	}


}
