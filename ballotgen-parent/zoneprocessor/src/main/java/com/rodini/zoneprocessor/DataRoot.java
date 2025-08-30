package com.rodini.zoneprocessor;

import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.rodini.ballotutils.Utils.ATTN;

/**
 * DataRoot class is the object graph root for EclipseStore.
 * EclipseStore will NOT persist static data. This has a major impact on the application.
 */
public class DataRoot {
	static final Logger logger = LogManager.getLogger(DataRoot.class);

	// zoneNo (key)  Zone object (value)
	Map<String, Zone> zoneNoZoneMap = new TreeMap<>();
	// PrecinctNo (key) Zone object (value)
	// 020              Zone7
	Map<String, Zone> precinctNoZoneMap = new TreeMap<>();
	// precinctNo (key) Precinct object (value)
	Map<String, Precinct> precinctNoPrecinctMap = new TreeMap<>();

	public Map<String, Zone> getZoneNoZoneMap() {
		return zoneNoZoneMap;
	}
	// Use only for testing!
	public void clearZoneNoZoneMap() {
		zoneNoZoneMap = new TreeMap<>();
	}
	public Map<String, Zone> getPrecinctNoZoneMap() {
		return precinctNoZoneMap;
	}
	// Use only for testing!
	public void clearPrecinctNoZoneMap() {
		precinctNoZoneMap = new TreeMap<>();
	}
	public Map<String, Precinct> getPrecinctNoPrecinctMap() {
		return precinctNoPrecinctMap;
	}
	// Use only for testing!
	public void clearPrecinctNoPrecinctMap() {
		precinctNoPrecinctMap = new TreeMap<>();
	}
	// Log a summary of the zone/precinct data
	public void logRootContent() {
		String message;
		message = String.format("# zones:     %3d", zoneNoZoneMap.keySet().size());
		logger.log(ATTN, message);	
		message = String.format("# precincts: %3d", precinctNoPrecinctMap.keySet().size());
		logger.log(ATTN, message);	
	}
}
