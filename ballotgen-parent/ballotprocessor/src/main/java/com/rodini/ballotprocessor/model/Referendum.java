package com.rodini.ballotprocessor.model;

/**
 * Referendum is a simple representation of a referendum on a ballot.
 * 
 * @author Bob Rodini
 *
 */
public class Referendum extends VoteFor {
//  private String title: // e.g. "Honey Brook Township:\nReferendum for Additional\nTownship Supervisors"
	String refText; // e.g. "Should two additional..."
		
	public Referendum(Ballot ballot, String title, String refText) {
		super(ballot, title);
		this.refText = refText;
	}

	public String getRefText() {
		return refText;
	}
	
	public String toString() {
		return String.format("Referendum:%ntitle: %s%ntext: %s", title, refText);
	}

}
