package com.rodini.ballotprocessor;

import com.rodini.ballotprocessor.model.Election;
/**
 * DataRoot is the class mandated by EclipsStore. Here is holds the root
 * object of the data store, namely the Election object.
 */
public class DataRoot {
	
	Election election; // data root used by BallotGen.

	public void setElection(Election election) {
		this.election = election;
	}

	
	public Election getElection() {
		return election;
	}

}
