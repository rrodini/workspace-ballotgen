package com.rodini.ballotprocessor;

import static org.apache.logging.log4j.Level.DEBUG;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import com.rodini.ballotprocessor.*;
import com.rodini.ballotutils.ElectionType;
import com.rodini.ballotutils.Party;
import com.rodini.ballotutils.Utils;

//import static com.rodini.ballotprocessor.*;

/**
 * Initialize class has the job of validating program inputs as thoroughly
 * as possible.  This includes CLI parameters and Property file values.
 * 
 * @author Bob Rodini
 *
 */

public class Initialize {
	static final Logger logger = LogManager.getLogger(Initialize.class);
	public static final String RESOURCE_PATH = "./resources/";
	public static final String PROPS_FILE = "ballotprocessor.properties";
	public static final String CONTEST_PAGE_BREAK = "CONTEST_PAGE_BREAK"; // pseudo contest name
	public static Properties ballotprocessorProps;
	//  Global variables here.
//	public static String specimenText; // text of the Voter Services specimen.
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
	
	public static String endorsementsAllFileName;

	
//	public static final String PROP_ = "";
	public static final String PROP_ELECTION_TYPE = "election.type";
	public static final String PROP_ENDORSED_PARTY = "endorsed.party";
	public static final String PROP_BALLOT_WRITE_IN = "ballot.write.in";
	public static final String PROP_BALLOT_PAGE_BREAK_REGEX = "ballot.page.break.regex";
	public static final String PROP_BALLOT_ELECTION_TEXT_REGEX = "ballot.election.text.regex";
	public static final String PROP_BALLOT_TEXT_REGEX = "ballot.text.regex";
	public static final String PROP_BALLOT_ONEPAGE_TEXT_REGEX = "ballot.onepage.text.regex";
	public static final String PROP_BALLOT_TWOPAGE_TEXT_P1_REGEX = "ballot.twopage.text.p1.regex";
	public static final String PROP_BALLOT_TWOPAGE_TEXT_P2_REGEX = "ballot.twopage.text.p2.regex";
	public static final String PROP_BALLOT_CONTEST_TEXT_PREFIX = "ballot.contest.text.regex";
	public static final String PROP_BALLOT_REFERENDUM_TEXT_REGEX = "ballot.referendum.text.regex";
	public static final String PROP_BALLOT_RETENTION_TEXT_REGEX = "ballot.retention.text.regex";
	public static final String PROP_BALLOT_RETENTION_NAME_REGEX = "ballot.retention.name.regex";	
	public static final String PROP_TICKET_CONTEST_TITLES = "ticket.contest.titles";
	public static final String PROP_LOCAL_CONTEST_TITLES = "local.contest.titles";
	public static final String PROP_LOCAL_CONTEST_EXCEPTION_TITLES = "local.contest.exception.titles";
	public static final String PROP_ENDORSEMENTS_ALL_FILE_NAME = "endorsements.all.file.name";

	/**
	 * validateCommandLineArgumenst checks the CLI args as follows:
	 * 1. Number are correct.
	 * 2. Paths are valid and of the right type.
	 * 
	 * @param args CLI args.
	 */
	static void validateCommandLineArgs(String [] args) {
		// check the # command line arguments
		if (args.length != 2) {
			Utils.logFatalError("incorrect CLI arguments:\n" +
					"args[0]: path to VS specimen text file.\n" +
					"args[1]: path to directory with ballot store");
		} else {
			String msg0 = String.format("specimen file:  %s", args[0]);
			String msg1 = String.format("store dir: %s", args[1]);
			System.out.println(msg0);
			System.out.println(msg1);
			logger.info(msg0);
			logger.info(msg1);
		}
		// check args[0] is present and is a TXT file
		String specimenFilePath = args[0];
		Utils.checkFileExists(specimenFilePath);
		if (!specimenFilePath.endsWith("txt")) {
			Utils.logFatalError("file \"" + specimenFilePath + "\" doesn't end with TXT extension.");
		}
		BallotProcessor.specimenText = Utils.readTextFile(specimenFilePath);
		// Check that args[1] is a directory.
		String storeDirPath = args[1];		
		if (!Files.isDirectory(Path.of(storeDirPath))) {
			Utils.logFatalError("invalid args[1] value, store dir doesn't exist: " + storeDirPath);
		}
		String absStoreDirPath = Paths.get(storeDirPath).toAbsolutePath().toString();
		logger.info(String.format("ballot store abs. path: %s", absStoreDirPath));
		BallotProcessor.storeDirPath = storeDirPath;
	}
	/**
	 * validateRegexProperty looks for the given property by name and
	 * validates that the string value can be compiled as a regex Pattern.
	 * 
	 * @param props Properties object
	 * @param propName property name
	 * @return property value compiled as Pattern.
	 */
	static Pattern validateRegexProperty(Properties props, String propName) {
		Pattern pat = null;
		String propVal = Utils.getPropValue(props, propName);
		if (propVal == null || propVal.isBlank()) {
			logger.error(String.format("property %s is missing.", propName));
		} else {
			pat = Utils.compileRegex(propVal);
		}
		return pat;
	}
	/**
	 * validateOrderedRegexProperties matches Utils.getPropOrderedValues logic
	 * which finds a sequence of property names that start with the same prefix.
	 * there is not a whole lot of validation, but the regexes must compile.
	 * 
	 * @param props Properties object
	 * @param propPrefix String prefix for sequence of related regex properties
	 * @return List of compile Pattern objects.
	 */
	static Pattern [] validateOrderedRegexProperties(Properties props, String propPrefix) {
		List<String> regexList = Utils.getPropOrderedValues(props, propPrefix);
		Pattern [] patList = new Pattern [regexList.size()];
		for (int i = 0; i < regexList.size(); i++) {
			patList[i] = Utils.compileRegex(regexList.get(i));
		}
		return patList;
	}
	/**
	 * Used by CandidateFactory (of all things).
	 */
	static void validateTicketAndLocalContestNames(Properties props) {
		String 	titles;
		titles = Utils.getPropValue(props, PROP_TICKET_CONTEST_TITLES);
		titlesOfTicketContests = Arrays.asList(titles.split(","));
		titles = Utils.getPropValue(props, PROP_LOCAL_CONTEST_TITLES);
		titlesOfLocalContests = Arrays.asList(titles.split(","));
		titles = Utils.getPropValue(props, PROP_LOCAL_CONTEST_EXCEPTION_TITLES);
		titlesOfLocalContestsExceptions = Arrays.asList(titles.split(","));
	}

	public static void validateProperties(Properties props) {
		String elecType = Utils.getPropValue(props, PROP_ELECTION_TYPE);
		BallotProcessor.electionType = ElectionType.toEnum(elecType);
		String party = Utils.getPropValue(props, PROP_ENDORSED_PARTY);
		BallotProcessor.endorsedParty = Party.toEnum(party);
		electionTextRegex = validateRegexProperty(props, PROP_BALLOT_ELECTION_TEXT_REGEX);
	    ballotTextRegex = validateRegexProperty(props, PROP_BALLOT_TEXT_REGEX);
	    pageBreakRegex = validateRegexProperty(props, PROP_BALLOT_PAGE_BREAK_REGEX);
	    onePageTextRegex = validateRegexProperty(props, PROP_BALLOT_ONEPAGE_TEXT_REGEX);
	    twoPageTextP1Regex = validateRegexProperty(props, PROP_BALLOT_TWOPAGE_TEXT_P1_REGEX);
	    twoPageTextP2Regex = validateRegexProperty(props, PROP_BALLOT_TWOPAGE_TEXT_P2_REGEX);
	    contestTextRegex = validateOrderedRegexProperties(props, PROP_BALLOT_CONTEST_TEXT_PREFIX);
	    referendumTextRegex = validateRegexProperty(props, PROP_BALLOT_REFERENDUM_TEXT_REGEX);
	    retentionTextRegex = validateRegexProperty(props, PROP_BALLOT_RETENTION_TEXT_REGEX);
	    retentionNameRegex = validateRegexProperty(props, PROP_BALLOT_RETENTION_NAME_REGEX);
	    writeIn = Utils.getPropValue(props, PROP_BALLOT_WRITE_IN);
	    endorsementsAllFileName = Utils.getPropValue(props, PROP_ENDORSEMENTS_ALL_FILE_NAME);
	    validateTicketAndLocalContestNames(props);
	}

	public static void init(String [] args) {
		validateCommandLineArgs(args);
		ballotprocessorProps = Utils.loadProperties(RESOURCE_PATH + PROPS_FILE);
		Utils.logProperties(logger, DEBUG, ballotprocessorProps);
		validateProperties(	ballotprocessorProps);
		
	}
	public static void term() {
		// Right now, nothing to do.
	}
}
