package com.rodini.ballotgen;

import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.logging.log4j.LogManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.ballotprocessor.BallotProcessor;
//import com.rodini.ballotgen.common.GenDocxBallot;
//import com.rodini.ballotgen.common.Initialize;
//import static com.rodini.ballotgen.common.BallotGenOutput.*;
//import com.rodini.ballotgen.endorsement.Endorsement;
import com.rodini.ballotutils.Utils;
import com.rodini.zoneprocessor.ZoneProcessor;

import static com.rodini.ballotutils.Utils.ATTN;
/**
 * BallotGen is the program that generates the precinct level .docx (Word) files.
 * It also generates the unique ballot .docx (Word) files.
 * 
 * It is dependent on an upstream programs ZoneProcessor and BallotProcessor
 * to have generated EclipseStores for their respective objects.
 * 
 * CLI arguments:
 * args[0] - path to directory for generated DOCX files (e.g "NNN_XYZ.docx").
 *
 * ENV variables:
 * BALLOTGEN_VERSION version # of BallotGen (e.g. "2.0.0")
 * 
 * @author Bob Rodini
 *
 */
public class BallotGen {
	
	private static final Logger logger = LogManager.getRootLogger();
	static final String ENV_BALLOTGEN_VERSION = "BALLOTGEN_VERSION";
	static final String UNIQUE_TITLE = "UNIQUE_";  // e.g. unique_01.docx
	static       int precinctBallotCount = 0;
	static       int uniqueBallotCount = 0;
	static       String ballotsDirPath;  // docx directory
	
	public static void main(String[] args){
		// Get the logging level from JVM parameter on command line.
		Utils.setLoggingLevel(LogManager.getRootLogger().getName());
		String version = Utils.getEnvVariable(ENV_BALLOTGEN_VERSION, true);
		String msg = String.format("Start of BallotGen app. Version: %s", version);
		Utils.logAppMessage(logger, msg, true);
		Initialize.init(args);
//		if (Initialize.ballotGenOutput == PRECINCT || Initialize.ballotGenOutput == BOTH) {
//			// Generate all precinct ballots.
//			genPrecinctBallotFiles(Initialize.msWordPrecinctTemplateFile);
//		}
//		if (Initialize.ballotGenOutput == UNIQUE || Initialize.ballotGenOutput == BOTH) {
//			// Generate unique ballots.
//			genUniqueBallotFiles(Initialize.msWordUniqueTemplateFile);
//		}
		terminate();
		msg = String.format("Generated %d precinct ballots", precinctBallotCount);
		Utils.logAppMessage(logger, msg, false);
		msg = String.format("Generated %d unique ballots", uniqueBallotCount);
		Utils.logAppMessage(logger, msg, false);
		Utils.logAppErrorCount(logger);
		msg = "End of BallotGen app";
		Utils.logAppMessage(logger, msg, true);
	}
	/** 
	 * genPrecinctBallotFiles generates PRECINCT ballots.
	 */
	private static void genPrecinctBallotFiles(String msWordTemplate) {
//		for (String ballotFile: Initialize.ballotFiles) {
//			String precinctBallotFile = Utils.getPrecinctNoName(ballotFile);
//			GenDocxBallot gdb = new GenDocxBallot(
//					msWordTemplate, 
//					ballotFile,  // ./chester-output/NNN_municipal_XYZ_VS.txt
//					precinctBallotFile, // NNN_municipal_XYZ
//					Initialize.endorsementProcessor,
//					Initialize.writeinProcessor);								
//			gdb.generate();
//			precinctBallotCount++;
//		}
	}
	
	/** 
	 * genUniqueBallotFiles generates UNIQUE ballots (see _elections/<specific election>/ballot-summary.txt).
	 * After this report is parsed, the data structure Initialize.uniqueFirstBallotFile triggers
	 * the generation of the sample ballot that is shared between precincts.
	 * Initialize.ballotFiles list.
	 */
	private static void genUniqueBallotFiles(String msWordTemplate) {
//		logger.info("Generating Unique ballot files.");
//		// The logic works because ballotFile names are unique across the county.
//		for (String ballotFile: Initialize.ballotFiles) {
//			logger.info(String.format("genUniqueBallotFiles: %s%n", ballotFile));
//			String precinctBallotFile = Utils.getPrecinctNoName(ballotFile);		
//			if (Initialize.uniqueFirstBallotFile.contains(precinctBallotFile)) {
//				// TBD - set values for uniquePrecinctNos, uniquePrecinctNames, uniquePrecinctNoNames
//				logger.info(String.format("genUniqueBallotFiles: first match: %s%n", ballotFile));
//				int uniqueNo = Initialize.uniqueFirstBallotFile.indexOf(precinctBallotFile);
//				String uniqueBallotFile = UNIQUE_TITLE + 
//						com.rodini.ballotutils.Utils.normalizeNo(uniqueNo, 2) +
//						"_" + precinctBallotFile;
//				GenDocxBallot gdb = new GenDocxBallot(
//						msWordTemplate,
//						ballotFile,  // same as chester-contests/NNN_municipal_XYZ_contests.txt and chester-output/NNN_municipal_XYZ_VS.txt
//						uniqueBallotFile, // unique_NN
//						Initialize.endorsementProcessor,
//						Initialize.writeinProcessor);								
//				gdb.generate();
//				uniqueBallotCount++;
//			}
//		}
	}

	/**
	 * Terminate ends the BallotGen with summary information.
	 */
	private static void terminate() {
		ZoneProcessor.stop();
		BallotProcessor.stop();
		// Generate list of endorsed candidates as a means for checking for errors
		// The first loop just shows how many cumulative endorsements any candidate received.
		// The second loop checks that a candidate with and explicit endorsement was
		// endorsed on any ballot.  If the answer is "no" the candidate's name is probably misspelled.		
//		Set<String> names = GenDocxBallot.endorsedCandidates.keySet();
//		String line = "Endorsed Candidate      No. endorsements";
//		//             STEPHANIE GIBSON WILLIAMS     <= long name
//		logger.info(line);
//		// First loop - messages always recorded.
//		for (String name: names) {
//			line = String.format("%-25s %5d", name, GenDocxBallot.endorsedCandidates.get(name));
//			logger.log(ATTN, line);
//		}
//		Map<String,List<Endorsement>> candidateEndorsements = Initialize.endorsementProcessor.getCandidateEndorsements();
//		names = candidateEndorsements.keySet();
//		// Second loop - ERROR messages.
//		for (String name: names) {
//			if (GenDocxBallot.endorsedCandidates.get(name) == null) {
//				
//				line = String.format("%s received endorsements (below) but did not appear on any ballot:", name);
//				logger.error(line);
//				System.out.println(line);
//				for (Endorsement end: candidateEndorsements.get(name)) {
//					line = "   " + end.toString();
//					logger.error(line);
//					System.out.println(line);
//				}
//			}
//		}
//
	}
}
