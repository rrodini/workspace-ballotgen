package com.rodini.ballotgen;

import static com.rodini.ballotgen.BallotGenOutput.BOTH;
import static com.rodini.ballotgen.BallotGenOutput.PRECINCT;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.ballotprocessor.BallotProcessor;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotprocessor.model.Election;
//import com.rodini.ballotgen.endorsement.Endorsement;
//import com.rodini.ballotgen.endorsement.EndorsementFactory;
//import com.rodini.ballotgen.endorsement.EndorsementProcessor;
import com.rodini.ballotgen.writein.Writein;
import com.rodini.ballotgen.writein.WriteinFactory;
import com.rodini.ballotgen.writein.WriteinProcessor;
import com.rodini.ballotutils.ElectionType;
import com.rodini.ballotutils.Party;
import com.rodini.ballotutils.Utils;
import com.rodini.zoneprocessor.Zone;
import com.rodini.zoneprocessor.ZoneProcessor;
/** 
 * Initialize class gets the program ready to generate sample ballots.
 * It attempts to validate critical inputs and FAIL EARLY if things
 * are amiss.
 * 
 * Note: Many property values are now read by voteforprocessor component.
 * 
 * @author Bob Rodini
 *
 */
public class Initialize {
	private static final Logger logger = LogManager.getLogger(Initialize.class);
	// Global variables
	
	// From zoneprocessor store:
	private static Map<String, Zone> precinctToZoneMap;	
	// From ballotprocessor store:
	public static Election election;
	public static ElectionType elecType;	// PRIMARY or GENERAL
	public static Party endorsedParty;		// Democratic (or NULL)
	public static List<Ballot> ballots; 	// List of Ballot objects
//  public static EndorsementProcessor endorsementProcessor;
//  static Map<String, List<Endorsement>> candidateEndorsements;
    public static WriteinProcessor writeinProcessor;
    static Map<String,List<Writein>> precinctWriteins;
	
	public static String msWordPrecinctTemplateFile = "";	// MS Word precinct template file
	public static String msWordUniqueTemplateFile = "";	    // MS Word unique template file
	public static Properties ballotgenProps;
	public static String electionDirPath;	// election dir (derived from ballotprocessor.property)
	public static String ballotSummaryFile; // output from BallotProcessor
	public static boolean writeInDisplay;
	public static boolean pageBreakDisplay;
	public static String pageBreakType;
	public static String  pageBreakWording; // Typically "Vote Both Sides"
	public static List<String> columnBreaksBefore;  // Generate column breaks BEFORE these contest names
	public static List<String> columnBreaksAfter;  // Generate column breaks AFTER these contest names
	public static BallotGenOutput ballotGenOutput;  // output generation directive
//	public static List<String> uniqueFirstBallotFile; // ballot file that triggers unique_ballot_xx.docx
//	public static Map<Integer, List<String>> uniqueBallotFiles; // precinctNoNames that belong to a unique ballot.
	

	static final String BALLOTPROCESSOR_PROPS_FILE = "../ballotprocessor/resources/ballotprocessor.properties";
	static final String ZONEPROCESSOR_PROPS_FILE = "../zoneprocessor/resources/zoneprocessor.properties";
	static final String BALLOTGEN_PROPS_FILE = "./resources/ballotgen.properties";
//	Property names - must match names within ballotgen.properties
	private static final String PROP_BALLOTGEN_OUTPUT = "ballotgen.output";
	private static final String PROP_BALLOT_SUMMARY_FILE = "ballot.summary.file";
	private static final String PROP_ENDORSEMENTS_FILE = "endorsements.file";
	private static final String PROP_WRITE_INS_FILE = "write.ins.file";
	private static final String PROP_WORD_TEMPLATE_DEFAULT = "word.template.default";
	private static final String PROP_WORD_TEMPLATE_UNIQUE = "word.template.unique";
	private static final String PROP_PAGE_BREAK_DISPLAY = "page.break.display"; // True => display wording below
	private static final String PROP_PAGE_BREAK_TYPE = "page.break.type"; // None / PAGE / COLUMN
	private static final String PROP_PAGE_BREAK_WORDING = "page.break.wording";
	private static final String PROP_COlUMN_BREAK_BEFORE_CONTEST_NAME = "column.break.before.contest.name";
	private static final String PROP_COlUMN_BREAK_AFTER_CONTEST_NAME = "column.break.after.contest.name";
	public  static final String PROP_WRITE_IN_DISPLAY = "write.in.display"; // True => display wording below
	public  static final String CONTEST_PAGE_BREAK = "CONTEST_PAGE_BREAK"; // pseudo contest name

//  BallotProcessor properties and constants
	private final static String PROP_BALLOT_STORE = "ballot.store";
//  ZoneProcessor properties and constants
	final static String PROP_ZONE_STORE = "precinct.to.zone.store";
	
	/**
	 * validateCommandLineArgs checks that there are at least 1 CLI arg.
	 * args[0] - ballotsDirPath path directory.
	 * 
	 * @param args command line arguments
	 */
	/* private */
	static void validateCommandLineArgs(String [] args) {
		// check for 1 command argument.
		if (args.length < 1) {
			Utils.logFatalError("missing CLI arguments:\n" +
					"args[0]: path to directory for generated sample ballots \"NNN_XYZ.docx\" files.\n");
		} else {
			String msg0 = String.format("_ballots dir: %s", args[0]);
			System.out.println(msg0);
			logger.info(msg0);
		}
		String ballotsDirPath = args[0];
		if (!Utils.checkDirExists(ballotsDirPath)) {
			Utils.logFatalError(String.format("invalid args[0] value, ballots dir doesn't exist: ", ballotsDirPath));
		}
		BallotGen.ballotsDirPath = ballotsDirPath;
	}
	/**
	 * validateWordTemplate checks that there is a MS Word template (.dotx) file.
	 * @param templateFile .dotx file
	 * @param which PRECINC or UNIQUE template
	 */
	static void validateWordTemplate(String templateFile, String which) {
		if (templateFile.isEmpty()) {
			Utils.logFatalError(String.format("MS Word %s template file not specified (blank)", which));
		}
		if (!Files.exists(Path.of(templateFile))) {
			Utils.logFatalError(String.format("MS Word %s template file does not exist: %s", which, templateFile));
		}
		if (!templateFile.endsWith(".dotx")) {
			Utils.logFatalError(String.format("MS Word %s template file should end with \"dotx\": %s", which, templateFile));
		}
	}
	
	/**
	 * validateWordTemplates validates the existence of the Word template files.
	 */
	static void validateWordTemplates(Properties props) {
		msWordPrecinctTemplateFile = Utils.getPropValue(props, PROP_WORD_TEMPLATE_DEFAULT);
		msWordUniqueTemplateFile = Utils.getPropValue(props, PROP_WORD_TEMPLATE_UNIQUE);
		logger.info("msWordPrecinctTemplateFile: " + msWordPrecinctTemplateFile);
		logger.info("msWordUniqueTemplateFile: " + msWordUniqueTemplateFile);
		if (ballotGenOutput == PRECINCT || ballotGenOutput == BOTH) {
			validateWordTemplate(msWordPrecinctTemplateFile, "PRECINCT");
		}
		if (ballotGenOutput == PRECINCT || ballotGenOutput == BOTH) {
			validateWordTemplate(msWordUniqueTemplateFile, "UNIQUE");
		}
	}
	/**
	 * initializeZoneProcessor initializes the zone processor component.
	 */
	static void validateZoneProcessor(String propsPath) {
		Utils.checkFileExists(propsPath);
		Properties zoneprocessorProps = Utils.loadProperties(propsPath);
		ZoneProcessor.start(zoneprocessorProps);
		precinctToZoneMap = ZoneProcessor.getDataRoot().getPrecinctNoZoneMap();
	}
	
	static void validateBallotProcessor(String propsPath) {
		Utils.checkFileExists(propsPath);
		Properties ballotprocessorProps = Utils.loadProperties(propsPath);
//		String storeDirPath = Utils.getPropValue(ballotprocessorProps, PROP_BALLOT_STORE);
//		Path electionPath = Paths.get(BallotProcessor.storeDirPath).getParent();
//		electionDirPath = electionPath.toString();
		BallotProcessor.start(ballotprocessorProps);
		election = BallotProcessor.getDataRoot().getElection();
		elecType = election.getElectionType();
		endorsedParty = election.getEndorsedParty();
		ballots = election.getBallots();
	}
	
	static boolean validateInputFile( String fileProp, String fileName) {
		boolean exists = true;
		logger.info(String.format("%s: %s", fileProp, fileName));
		if (!Utils.checkFileExists(fileName)) {
			logger.info(String.format("%s does not exist: %s ", fileProp, fileName));
			exists = false;
		} else {
			logger.info(String.format("%s: %s ", fileProp, fileName));
		}
		return exists;
	}
	
	static void validateBallotSummaryFile(Properties props) {
		String ballotSummaryFile = Utils.getPropValue(props, PROP_BALLOT_SUMMARY_FILE);
		if (validateInputFile(PROP_BALLOT_SUMMARY_FILE, ballotSummaryFile)) {
		}
	}
	/**
	 * validateEndorsementsFile validates the existence of the endorsements file.
	 */
	static void validateEndorsementsFile(Properties props) {
		String endorsementsFile = Utils.getPropValue(props, PROP_ENDORSEMENTS_FILE);
		String endorsementsCSVText = "";
		if (validateInputFile(PROP_ENDORSEMENTS_FILE, endorsementsFile)) {
			endorsementsCSVText = Utils.readTextFile(endorsementsFile);
		}
//		EndorsementFactory.processCSVText(endorsementsCSVText);
//		candidateEndorsements = EndorsementFactory.getCandidateEndorsements();
	}
	/**
	 * validateWriteinsFile validates the existence of the Write-ins file.
	 */
	static void validateWriteinsFile(Properties props) {
		String writeinsFile = Utils.getPropValue(props, PROP_WRITE_INS_FILE);
		String writeinsCSVText = "";
		if (validateInputFile(PROP_WRITE_INS_FILE, writeinsFile)) {
			writeinsCSVText = Utils.readTextFile(writeinsFile);
		}
		// Must set this map BEFORE processing CSV file.
		WriteinFactory.setPrecinctToZones(precinctToZoneMap);
		WriteinFactory.processCSVText(writeinsCSVText);
		precinctWriteins = WriteinFactory.getPrecinctWriteins();
	}
	/**
	 * validateColumnBreakContestName reads/displays the COlUMN_BREAK_CONTEST_NAME property value.
	 */
	static void validateColumnBreakContestName(Properties props) {
		String value = Utils.getPropValue(props, PROP_COlUMN_BREAK_BEFORE_CONTEST_NAME);
		columnBreaksBefore = new ArrayList<String>();
		if (value != null) {
			columnBreaksBefore = Arrays.asList(value.split(","));
		}
		logger.info(String.format("%s: %s",  PROP_COlUMN_BREAK_BEFORE_CONTEST_NAME, columnBreaksBefore));
		
		columnBreaksAfter = new ArrayList<String>();
		value = Utils.getPropValue(props, PROP_COlUMN_BREAK_AFTER_CONTEST_NAME);
		if (value != null) {
			columnBreaksAfter = Arrays.asList(value.split(","));
		}
		logger.info(String.format("%s: %s",  PROP_COlUMN_BREAK_AFTER_CONTEST_NAME, columnBreaksAfter));
	}
	static void validatePageBreak(Properties props) {
		boolean display = true;
		String strDisplay = Utils.getPropValue(props,PROP_WRITE_IN_DISPLAY);
		if (strDisplay != null) {
			display = Boolean.valueOf(strDisplay);
		}
		writeInDisplay = display;
		strDisplay = Utils.getPropValue(props,PROP_PAGE_BREAK_DISPLAY);
		if (strDisplay != null) {
			display = Boolean.valueOf(strDisplay);
		}
		pageBreakDisplay = display;
		pageBreakType = Utils.getPropValue(props,PROP_PAGE_BREAK_TYPE);
		if (pageBreakType == null) {
			pageBreakType = "NONE";
		}
		pageBreakDisplay = display;
		logger.info(String.format("%s: %s", "pageBreakDisplay", pageBreakDisplay));
		logger.info(String.format("%s: %s", "pageBreakType", pageBreakType));
		String value = Utils.getPropValue(props, PROP_PAGE_BREAK_WORDING);
		if (value == null) {
			value = "Vote Both Sides";
		}
		pageBreakWording = value;
		logger.info(String.format("%s: %s", "pageBreakWording", pageBreakWording));
	}

	static void validateBallotGenOutput(Properties props) {
		String propValue;
		propValue = Utils.getPropValue(props, PROP_BALLOTGEN_OUTPUT);
		ballotGenOutput = BallotGenOutput.toEnum(propValue);
		if (ballotGenOutput == null) {
			ballotGenOutput = PRECINCT;
		}
		logger.info(String.format("%s: %s", PROP_BALLOTGEN_OUTPUT, ballotGenOutput));
	}
	/**
	 * validateBallotReport ensures that the ballot-summary.txt file exists.
	 */
	static void validateBallotSummary(Properties props) {
		String ballotSummaryFile = Utils.getPropValue(props, PROP_BALLOT_SUMMARY_FILE);
		if (!Utils.checkFileExists(ballotSummaryFile)) {
			Utils.logFatalError("Can't find file: " + ballotSummaryFile);
		}
		// Report is there, so parse it.
//		BallotReportParser.parseBallotReport(ballotSummaryPath);
	}
	
	/**
	 * start begins the initialization process.
	 * @param args CLI arguments
	 */
	public static void init(String [] args) {
		validateCommandLineArgs(args);
		ballotgenProps = Utils.loadProperties(BALLOTGEN_PROPS_FILE);
		validateZoneProcessor(ZONEPROCESSOR_PROPS_FILE);
		validateBallotProcessor(BALLOTPROCESSOR_PROPS_FILE);
		validateBallotGenOutput(ballotgenProps);
		// ballotGenOutput must be valued by now.
		validateWordTemplates(ballotgenProps);
		validateEndorsementsFile(ballotgenProps);
		validateWriteinsFile(ballotgenProps);
		
		// the ballot processor must be started.
//		elecType = com.rodini.voteforprocessor.extract.Initialize.elecType;
//		endorsedParty = com.rodini.voteforprocessor.extract.Initialize.endorsedParty;
//		endorsementProcessor  = new EndorsementProcessor(elecType, endorsedParty,
//				candidateEndorsements, precinctToZoneMap);
//		// create the write-in processor
		writeinProcessor = new WriteinProcessor(precinctWriteins);
		validateColumnBreakContestName(ballotgenProps);
		validatePageBreak(ballotgenProps);
		validateBallotSummary(ballotgenProps);
	}
	
	

}
