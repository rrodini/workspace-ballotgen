package com.rodini.ballotprocessor;

import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

import com.rodini.ballotutils.ElectionType;
import com.rodini.ballotutils.Party;
import com.rodini.ballotutils.Utils;
import com.rodini.ballotprocessor.extract.BallotExtractor;
import com.rodini.ballotprocessor.extract.ElectionExtractor;
import com.rodini.ballotprocessor.extract.PageExtractor;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotprocessor.model.Election;
/**
 * BallotProcessor is both a standalone program and a component of other BallotGen programs.
 * As a standalone program it processes the text of the Voter Services specimen into 
 * BallotGen objects and stores them in an EclipseStore data store.
 * As a component it provides an API to the EclipseStore data store.
 */
public class BallotProcessor {
	private static final  Logger logger = LogManager.getLogger(BallotProcessor.class);
	public  static String specimenText; // text of the Voter Services specimen.
	private static Election election;
	public  static ElectionType electionType;
	public  static Party endorsedParty;
	public  static List<Ballot> ballots;
	public  static String storeDirPath;
	private static final String PROP_BALLOT_STORE = "ballot.store";
	private static final String ENV_BALLOTGEN_VERSION = "BALLOTGEN_VERSION";
	private static DataRoot dataRoot;
	private static EmbeddedStorageManager storageManager;

	/**
	 * 
	 * @param args args[0] - file path to VS specimen text file.
	 *             args[1] - path to directory with ballot store.
	 */
	public static void main(String [] args) {
		Utils.setLoggingLevel(LogManager.getRootLogger().getName());
		String version = Utils.getEnvVariable(ENV_BALLOTGEN_VERSION, true);
		String message = String.format("Start of BallotProcessor app. Version: %s", version);
		Utils.logAppMessage(logger, message, true);		
		initialize(args);
		dataRoot = new DataRoot();
		// Create raw Ballot objects here
		ballots = BallotExtractor.extract(specimenText);
		// Update each ballot object with VoteFor objects here.
		PageExtractor.extract(ballots);
		// Create Election object here.
		election = ElectionExtractor.extract(specimenText);
		dataRoot.setElection(election);
		terminate();
		message = "End of BallotProcessor app";
		Utils.logAppErrorCount(logger);
		Utils.logAppMessage(logger, message, true);	
	}

	
	// Initialize and validate the CLI arguments and all property values.
	public static void initialize(String [] args) {
		Initialize.init(args);
	}
	
	// Write BallotProcessor data to EclipseStore.
	public static void terminate() {
		logger.info("BallotProcessor.terminate() called.");
		// Wipe out previous contents of store to avoid legacy object issues.
		Utils.deleteDir(storeDirPath);
		storageManager = EmbeddedStorage.start(dataRoot, Paths.get(storeDirPath));
		storageManager.storeRoot();
		storageManager.shutdown();
		// Generate the summary report.
		GenerateBallotSummary.generate(election);
	}

	// clients of BallotProcessor use start() at startup.
	static public void start(Properties props) {
		String storeDirPath = props.getProperty(PROP_BALLOT_STORE);
		if (storeDirPath == null) {
			logger.error(String.format("BallotProcessor property does not exist: %s", PROP_BALLOT_STORE));
		}
		String absStoreDirPath = Paths.get(storeDirPath).toAbsolutePath().toString();
		logger.info(String.format("ballot store abs. path: %s", absStoreDirPath));
		if (!Utils.checkDirExists(storeDirPath)) {
			logger.error(String.format("folder does not exist: %s%n", storeDirPath));
		}
		dataRoot = new DataRoot();
		storageManager = EmbeddedStorage.start(dataRoot, Paths.get(storeDirPath));
	    dataRoot = (DataRoot) storageManager.root();
	    if (storageManager.root() != null) {
	        dataRoot = (DataRoot) storageManager.root();
	        logger.info("BallotProcessor.start() loaded existing DataRoot from store");
	    } else {
	        logger.error("BallotProcessor.start() created new DataRoot");
	    }	}
	// clients of BallotProcessor need access to dataRoot.
	public static DataRoot getDataRoot() {
		logger.info(String.format("BallotProcessor.getDataRoot()"));
		if (dataRoot == null) {
			logger.error(String.format("BallotProcessor dataRoot is null."));
		}
		return dataRoot;
	}
	
	// clients of BallotProcessor use stop() at end.
	static public void stop() {
		logger.info(String.format("BallotProcessor.stop() called EmbeddedStorage.shutdown()"));
		storageManager.shutdown();
	}

	
}
