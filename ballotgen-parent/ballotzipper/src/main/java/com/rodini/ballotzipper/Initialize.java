package com.rodini.ballotzipper;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.zoneprocessor.ZoneProcessor;
import com.rodini.ballotutils.Utils;

/**
 * Initialize class performs all initializations needed trying
 * to detect misconfigurations as soon as possible (i.e. fail-fast).
 * 
 * @author Bob Rodini
 *
 */
public class Initialize {
	static final Logger logger = LogManager.getLogger(Initialize.class);
	// Global variables
	static String csvFilePath;
	static String inDirPath;
	static String outDirPath;
	// Prevent instantiation.
	private Initialize() {
	}
	// Perform main initialization.
	static void initialize(String [] args, String zoneProcessorPropsFile) {
		// check the # command line arguments
		if (args.length != 2) {
			Utils.logFatalError("incorrect CLI arguments:\n" +
					"args[0]: path to directory with municipal level \"NNN_name.docx\" files." +
					"args[1]: path to directory for zoneNN.zip files.");
		} else {
			String msg0 = String.format("DOCX dir: %s", args[0]);
			String msg1 = String.format("ZIP dir:  %s", args[1]);
			System.out.println(msg0);
			System.out.println(msg1);
			logger.info(msg0);
			logger.info(msg1);
		}
		// Check that args[0] is a directory and has files.
		inDirPath = args[0];
		if (!Files.isDirectory(Path.of(inDirPath))) {
			Utils.logFatalError("invalid args[0] value, DOCX dir doesn't exist: " + inDirPath);
		}		
		// Check that args[1] is a directory.
		outDirPath = args[1];
		if (!Files.isDirectory(Path.of(outDirPath))) {
			Utils.logFatalError("invalid args[1] value, ZIP dir doesn't exist: " + outDirPath);
		}
		// Clear out args[1] directory of ZIP files since inDir may equal outDir.
		File outDir = new File(outDirPath);
		File [] zipFiles = outDir.listFiles(file -> file.getName().endsWith(".zip"));
		for (File file: zipFiles) {
			file.delete();
		}
		// Now start the component zoneprocessor in order to get the Eclipse store for zone/precinct data.
		Properties zoneProcessorProps = Utils.loadProperties(zoneProcessorPropsFile);
		ZoneProcessor.start(zoneProcessorProps);
	}

}
