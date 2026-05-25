package com.rodini.ballotprocessor.model;

/**
 * Retention is a simple representation of a judge retention on a ballot.
 * 
 * @author Bob Rodini
 *
 */
public class Retention extends VoteFor{
//  private String title: // e.g. "Superior Court Retention\nElection Question"
	private String retText; // e.g. "Shall <judgeName> ... "
	private String judgeName; 

	public Retention(Ballot ballot, String title, String retText, String judgeName) {
		super(ballot, title);
		this.retText = retText;
		this.judgeName = judgeName;
	}

	public String getRetText() {
		return retText;
	}
	
	public String getJudgeName() {
		return judgeName;
	}
    
	public String toString() {
		return String.format("Retention:%ntitle: %s\ntext: %s", title, retText);	
	}
}
