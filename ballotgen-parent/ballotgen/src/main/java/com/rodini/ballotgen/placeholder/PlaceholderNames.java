package com.rodini.ballotgen.placeholder;

import java.util.List;

public class PlaceholderNames {
	// Placeholders are predefined strings that may be embedded in the template file
	// so they can be located programmatically by this program.
	public static final String PLACEHOLDER_CONTESTS = "Contests";
	public static final String PLACEHOLDER_REFERENDUMS = "Referendums";
	public static final String PLACEHOLDER_RETENTIONS = "Retentions";
	public static final String PLACEHOLDER_PRECINCT_NO = "PrecinctNo";
	public static final String PLACEHOLDER_PRECINCT_NAME = "PrecinctName";
	public static final String PLACEHOLDER_PRECINCT_NO_NAME = "PrecinctNoName";
	public static final String PLACEHOLDER_ZONE_NO= "ZoneNo";	
	public static final String PLACEHOLDER_ZONE_NAME = "ZoneName";	
	public static final String PLACEHOLDER_ZONE_LOGO = "ZoneLogo";	
	public static final String PLACEHOLDER_ZONE_URL = "ZoneUrl";	
	public static final String PLACEHOLDER_ZONE_CHUNK = "ZoneChunk";	
	public static List<String> placeholderNames = List.of (PLACEHOLDER_CONTESTS, PLACEHOLDER_REFERENDUMS,
			PLACEHOLDER_RETENTIONS, PLACEHOLDER_PRECINCT_NO, PLACEHOLDER_PRECINCT_NAME, PLACEHOLDER_PRECINCT_NO_NAME,
			PLACEHOLDER_ZONE_NO, PLACEHOLDER_ZONE_NAME, PLACEHOLDER_ZONE_LOGO, PLACEHOLDER_ZONE_URL, PLACEHOLDER_ZONE_CHUNK);

}
