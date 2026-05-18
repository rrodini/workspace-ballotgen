package com.rodini.ballotprocessor;

import java.util.List;
import java.util.regex.Pattern;

import com.rodini.ballotutils.ElectionType;
import com.rodini.ballotutils.Party;


public class Initialize {

	public static ElectionType elecType;
	public static Party endorsedParty;
	public static List<String> titlesOfTicketContests;
	public static List<String> titlesOfLocalContests;
	public static List<String> titlesOfLocalContestsExceptions;
	public static String writeIn;

	// All regexes are compiled after loading
	public static Pattern electionTextRegex;
	public static Pattern ballotTextRegex;
	public static Pattern pageBreakRegex;
	public static Pattern onePageTextRegex;
	public static Pattern twoPageTextP1Regex;
	public static Pattern twoPageTextP2Regex;

	public static Pattern [] contestTextRegex;
	public static Pattern referendumTextRegex;
	public static Pattern retentionTextRegex;
	public static Pattern retentionNameRegex;

	
//	public static final String PROP_ = "";
	public static final String PROP_BALLOT_WRITE_IN = "write.in";
	public static final String PROP_BALLOT_PAGE_PAGE_BREAK_REGEX = "ballot.page.break.regex";
	public static final String PROP_BALLOT_ELECTION_TEXT_REGEX = "ballot.election.text.regex";
	public static final String PROP_BALLOT_TEXT_REGEX = "ballot.text.regex";
	public static final String PROP_BALLOT_ONEPAGE_TEXT_REGEX = "ballot.onepage.text.regex";
	public static final String PROP_BALLOT_TWOPAGE_TEXT_P1_REGEX = "ballot.twopage.text.p1.regex";
	public static final String PROP_BALLOT_TWOPAGE_TEXT_P2_REGEX = "ballot.twopage.text.p2.regex";
	public static final String PROP_BALLOT_CONTEST_TEXT_PREFIX = "ballot.contest.text.regex";
	public static final String PROP_BALLOT_REFERENDUM_TEXT_REGEX = "ballot.referendum.text.regex";
	public static final String PROP_BALLOT_RETENTION_TEXT_REGEX = "ballot.retention.text.regex";
	public static final String PROP_BALLOT_RETENTION_NAME_REGEX = "ballot.retention.name.regex";	
	public static final String CONTEST_PAGE_BREAK = "CONTEST_PAGE_BREAK"; // pseudo contest name

	
}
