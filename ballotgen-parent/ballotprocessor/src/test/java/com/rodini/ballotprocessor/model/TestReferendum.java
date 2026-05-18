package com.rodini.ballotprocessor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestReferendum {
	Ballot ballot; // dummy object
	Referendum referendum;
	String title = "Honey Brook Township:\nReferendum for Additional\nTownship Supervisors";
	String text = "Should two additional supervisors be\nelected to serve in this township?";
	@Test
	void testConstructor() {
		referendum = new Referendum(ballot, title, text);
		assertEquals(title, referendum.getTitle());
		assertEquals(text, referendum.getRefText());
	}
	@Test
	void testToString() {
		String expected = "Referendum:\ntitle: " + title + "\ntext: " + text;
		referendum = new Referendum(ballot, title, text);
		assertEquals(expected, referendum.toString());
	}

}
