package com.rodini.zoneprocessor;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import com.rodini.ballotutils.Utils;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

public class ZoneProcessorDumpStore {
	private static String storeDirPath;
	private static DataRoot dataRoot;
//	private static EmbeddedStorageManager storageManager;

	public static void main(String[] args) {
		// check the # command line arguments
		if (args.length != 1) {
			Utils.logFatalError("incorrect CLI arguments:\n" +
					"args[0]: path to directory with zone store");
		} else {
			String msg0 = String.format("store dir: %s", args[0]);
			System.out.println(msg0);
		}
		// Check that args[0] is a directory.
		storeDirPath = args[0];
		if (!Files.isDirectory(Path.of(storeDirPath))) {
			Utils.logFatalError("invalid args[1] value, store dir doesn't exist: " + storeDirPath);
		}
		dataRoot = new DataRoot();
		EmbeddedStorageManager storageManager = EmbeddedStorage.start(dataRoot, Paths.get(storeDirPath));
		dumpZoneNoZoneMap(dataRoot.getZoneNoZoneMap());
		dumpPrecinctNoPrecinctMap(dataRoot.getPrecinctNoPrecinctMap());
		dumpPrecinctNoZoneMap(dataRoot.getPrecinctNoZoneMap());
		storageManager.shutdown();
	}

	private static void dumpPrecinctNoZoneMap(Map<String, Zone> precinctNoZoneMap) {
		
		System.out.println("precinctNoZoneMap");
		Set<String> precinctNos = precinctNoZoneMap.keySet();
		for (String precinctNo: precinctNos) {
			System.out.printf("%s: %s%n", precinctNo, precinctNoZoneMap.get(precinctNo));
		}
		System.out.println();
	}

	private static void dumpPrecinctNoPrecinctMap(Map<String, Precinct> precinctNoPrecinctMap) {
		System.out.println("precinctNoPrecinctMap");
		Set<String> precinctNos = precinctNoPrecinctMap.keySet();
		for (String precinctNo: precinctNos) {
			System.out.printf("%s: %s%n", precinctNo, precinctNoPrecinctMap.get(precinctNo));
		}
		System.out.println();
	}

	private static void dumpZoneNoZoneMap(Map<String, Zone> zoneNoZoneMap) {
		System.out.println("zoneNoZoneMap");
		Set<String> zoneNos = zoneNoZoneMap.keySet();
		for (String zoneNo: zoneNos) {
			System.out.printf("%s: %s%n", zoneNo, zoneNoZoneMap.get(zoneNo));
		}
		System.out.println();
		
	}
	
	

}
