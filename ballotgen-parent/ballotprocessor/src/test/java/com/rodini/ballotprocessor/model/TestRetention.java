package com.rodini.ballotprocessor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestRetention {
	Ballot ballot; // dummy object
	Retention retention;
	String title = "Superior Court Retention\nElection Question";
	String text = "Shall Jack Panella be retained for an\nadditional term as Judge of the\nSuperior Court of the Commonwealth\nof Pennsylvania?";
	String judgeName = "Jack Panella";
	@Test
	void testConstructor() {
		retention = new Retention(ballot, title, text, judgeName);
		assertEquals(title, retention.getTitle());
		assertEquals(text, retention.getRetText());
		assertEquals(judgeName, retention.getJudgeName());
	}
	@Test
	void testToString() {
		String expected = "Retention:\ntitle: " + title + " judge: " + judgeName;
		retention = new Retention(ballot, title, text, judgeName);
		assertEquals(expected, retention.toString());
	}

}
