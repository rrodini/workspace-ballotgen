package com.rodini.ballotgen.writein;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rodini.ballotgen.writein.Writein;

class TestWritein {
	Writein writein;
	String candName = "Jane Doe";
	String contestTitle = "Township Supervisor\nBirmingham";
	int zoneNo = 6;
	List<String> muniStrs = List.of("014", "015");
	
	@BeforeEach
	void setUp() throws Exception {
		writein = new Writein(
					candName,
					contestTitle,
					zoneNo,
					muniStrs);
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	@Test
	void testGetCandidateName() {
		assertEquals(candName, writein.getCandidateName());
	}	
	@Test
	void testGetContestTitle() {
		assertEquals(contestTitle, writein.getContestTitle());
	}
	@Test
	void testGetZoneNo() {
		assertEquals(zoneNo, writein.getZoneNo());
	}	
	@Test
	void testGetMuniNos() {
		assertEquals(muniStrs, writein.getMuniNos());
	}
	@Test
	void testToString() {
		String expected = "Writein: " +
				candName + ", " +
				contestTitle + ", " +
				Integer.toString(zoneNo) + ", " +
				muniStrs.get(0) + ",...," + muniStrs.get(1);
		assertEquals(expected, writein.toString());
	}
}
