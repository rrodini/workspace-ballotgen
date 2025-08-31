package com.rodini.zoneprocessor;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

import com.rodini.ballotutils.Utils;

/**
 * ZoneProcessor class generates the zone/precinct EclipseStore store.
 * 
 * @author Bob Rodini
 *
 */
public class ZoneProcessor {
	private static final  Logger logger = LogManager.getLogger(ZoneProcessor.class);
	private static final  String PROP_PRECINCT_ZONE_FOLDER = "precinct.to.zone.folder";
	private static final  String ENV_BALLOTGEN_VERSION = "BALLOTGEN_VERSION";
	private static        String precinctZoneCsvFilePath;
	private static        String storeDirPath;
	private static DataRoot dataRoot;
	private static EmbeddedStorageManager storageManager;


	// Disable constructor
	private ZoneProcessor() {}
	
	/**
	 * main is the entry point for creating the store with zone/precinct data.
	 * 
	 * @param args args[0] path to precincts to zones CSV file.
	 *             args[1] path to directory with zone store
	 */
	public static void main(String [] args) {
		Utils.setLoggingLevel(LogManager.getRootLogger().getName());
		String version = Utils.getEnvVariable(ENV_BALLOTGEN_VERSION, true);
		String message = String.format("Start of ZoneProcessor app. Version: %s", version);
		Utils.logAppMessage(logger, message, true);		
		initialize(args);
		dataRoot = new DataRoot();
		// Process CSV file into three data structures
		//  precinctNoZoneMap (TreeMap) key: precinct No (String) value: zone (Zone object)
		//  zoneNoZoneMap (TreeMap) key: zone No (String) value: zone (Zone Object)
		//  precinctNoPrecinct (TreeMap) key: precinct No (String) value: precinct (Precinct object).
		String csvText = Utils.readTextFile(precinctZoneCsvFilePath);
		processCSVText(dataRoot, csvText);
		terminate();
		message = "End of ZoneProcessor app";
		Utils.logAppErrorCount(logger);
		Utils.logAppMessage(logger, message, true);
	}
	
	// Perform main initialization by validating CLI arguments.
	public static void initialize(String [] args) {
		// check the # command line arguments
		if (args.length != 2) {
			Utils.logFatalError("incorrect CLI arguments:\n" +
					"args[0]: path to precincts to zones CSV file.\n" +
					"args[1]: path to directory with zone store");
		} else {
			String msg0 = String.format("CSV file:  %s", args[0]);
			String msg1 = String.format("store dir: %s", args[1]);
			System.out.println(msg0);
			System.out.println(msg1);
			logger.info(msg0);
			logger.info(msg1);
		}
		// Check that args[0] exists and is a CSV file.
		precinctZoneCsvFilePath = args[0];
		if (!Files.exists(Path.of(precinctZoneCsvFilePath), NOFOLLOW_LINKS)) {
			Utils.logFatalError("can't find \"" + precinctZoneCsvFilePath + "\" file.");
		}
		if (!precinctZoneCsvFilePath.endsWith("csv")) {
			Utils.logFatalError("file \"" + precinctZoneCsvFilePath + "\" doesn't end with CSV extension.");
		}
		// Check that args[1] is a directory.
		storeDirPath = args[1];
		if (!Files.isDirectory(Path.of(storeDirPath))) {
			Utils.logFatalError("invalid args[1] value, store dir doesn't exist: " + storeDirPath);
		}		
	}
	// Write zone/precinct data to EclipseStore.
	static void terminate() {
		storageManager = EmbeddedStorage.start(dataRoot, Paths.get(storeDirPath));
		storageManager.storeRoot();
		storageManager.shutdown();
		// Give a short report
		dataRoot.logRootContent();
	}
	
	// clients of ZoneProcessor use start() at startup.
	static public void start(Properties props) {
		storeDirPath = props.getProperty(PROP_PRECINCT_ZONE_FOLDER);
		if (storeDirPath == null) {
			logger.error(String.format("ZoneProcessor property does not exist: %s%n", PROP_PRECINCT_ZONE_FOLDER));
		}
		if (!Utils.checkDirExists(storeDirPath)) {
			logger.error(String.format("folder does not exist: %s%n",precinctZoneCsvFilePath));
		}
		dataRoot = new DataRoot();
		storageManager = EmbeddedStorage.start(dataRoot, Paths.get(storeDirPath));

	}
	// clients of ZoneProcessor need access to dataRoot.
	public static DataRoot getDataRoot() {
		return dataRoot;
	}
	// clients of ZoneProcessor use stop() at end.
	static public void stop() {
		storageManager.shutdown();
	}
	// old initialization API
	/** 
	 * processCSVText processed the contents of the precinct-zone CVS file into zone objects
	 * and Precinct objects.  It relies on its client to read the file from disk.
	 * 
	 * @param csvText contents of CSV file.
	 */
	static void processCSVText(DataRoot root, String csvText) {
		// Don't miss the last line of the precinctsText!
		if (!csvText.endsWith("\n")) {
			csvText += "\n";
		}
		logger.debug(String.format("Processing csvText:\n%s", csvText));
		if (csvText.isEmpty()) {
			Utils.logFatalError("precincts-zones CSV file is empty.");
		}
//		String regex = "(?mi)^Zones$\n(?<zonesdata>((.*\n)*))^Precincts$\n(?<precinctsdata>((.*\n)*))";
		String regex = "(?mi)^Zones$\\n(?<zonesdata>((.*\\n)*))^Precincts$\\n(?<precinctsdata>((.*\\n)*))";
		logger.debug("Regex: " + regex);
		// Since precincts-zones CSV is hard-coded, this regex can be hard-coded.
		Pattern pattern = Pattern
				.compile(regex);
		Matcher matcher = pattern.matcher(csvText);
		if (!matcher.find()) {
			Utils.logFatalError("precincts-zones CSV file does not match format");
		}
		String zonesText = matcher.group("zonesdata");
		String precinctsText = matcher.group("precinctsdata");
		ZoneDataProcessor.processZonesText(root, zonesText);
		PrecinctDataProcessor.processPrecinctsText(root, precinctsText);
		checkZones(root);
		checkPrecincts(root);
	}
	/**
	 * checkZones checks that each zone has at least one precinct.
	 */
	private static void checkZones(DataRoot root) {
		Set<String> zoneKeys = root.getZoneNoZoneMap().keySet();
		Map<String, Precinct> precinctMap = root.getPrecinctNoPrecinctMap();
		Set<String> precinctKeys = precinctMap.keySet();
		for (String zoneNo: zoneKeys) {
			boolean foundPrecinct = false;
//			System.out.printf("zoneNo: %s%n", zoneNo);
			for (String precinctNo: precinctKeys) {
				Precinct precinct = precinctMap.get(precinctNo);
//				System.out.printf("precinctNo: %s%n", precinctNo);
				if (precinct.getZoneNo().equals(zoneNo)) {
//					System.out.printf("precinctNo: %s belongs to zoneNo: %s%n", precinctNo, zoneNo);
					foundPrecinct = true;
					break;
				}
			}
			if (!foundPrecinct) {
				logger.error(String.format("zone: %s has no precincts.", zoneNo));
			}
		}
	}
	/**
	 * checkPrecincts checks that each precinct belongs to a zone.
	 * As a side-effect it creates the precinctNoZoneMap.
	 */
	private static void checkPrecincts(DataRoot root) {
		Map<String, Zone> zoneMap = root.getZoneNoZoneMap();
		Set<String> zoneKeys = zoneMap.keySet();
		Map<String, Precinct> precinctMap = root.getPrecinctNoPrecinctMap();
		Set<String> precinctKeys = precinctMap.keySet();
		for (String precinctNo: precinctKeys) {
			Precinct precinct = precinctMap.get(precinctNo);
			String zoneNo = precinct.getZoneNo();
			if (zoneKeys.contains(zoneNo)) {
				Zone zone = zoneMap.get(zoneNo);
				root.precinctNoZoneMap.put(precinctNo, zone);
			} else {
				logger.error(String.format("precinct: %s has no zone.", precinctNo));
			}
		}
	}
	/**
	 * zoneOwnsPrecinct checks that the given zone "owns" the given precinct.
	 * 
	 * @param zoneNo zone identity.
	 * @param precinctNo precinct identity.
	 * @return true => zone "owns" precinct.
	 */
	public static boolean zoneOwnsPrecinct(DataRoot root, String zoneNo, String precinctNo) {
		Zone zone = root.precinctNoZoneMap.get(precinctNo);
		if (zone == null) {
			return false;
		}
		return zone.getZoneNo().equals(zoneNo);
	}
}
