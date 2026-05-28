package com.rodini.ballotprocessor.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotprocessor.model.Referendum;

/**
 * ReferendumExtractor processes all ballots and extracts the referendums from
 * each.
 * 
 * Note: The wording of referendums varies greatly between different precincts.
 * This means that one regex may NOT match all of the referendum titles.
 * The solution is to use a number of regexes (like contest regexes)
 * but this is NOT IMPLEMENTED at present.
 * 
 * @author Bob Rodini
 *
 */
public class ReferendumExtractor {

	static final Logger logger = LogManager.getLogger(ReferendumExtractor.class);

	// prevent instantiation
	private ReferendumExtractor() {
	}
	
	/**
	 * extractReferendumss loops thru the pageText applying ballot referendum regex to parse a referendum.
	 * The algorithm relies on VS formatting each referendum on the page like this:
	 * +----------------+
	 * | title/question |          |
	 * | text of        |
	 * | referendum     |
	 * | YES            |
	 * | NO             |
	 * +----------------+
	 * 
	 * Notes:
	 * 1) WARNING. One regex may not work for multiple Referendum titles due to variation in title.
	 *    Must test for both primary and general elections.
	 * 
	 * @param ballot Ballot object.
	 * @param pageText Ballot's page2 text.
	 * 
	 * @return List of Referendum objects.
	 */
	public static List<Referendum> extractReferendums(Ballot ballot, String pageText) {
		logger.info(String.format("extract referendums precinctNoName: %s", ballot.getPrecinctNoName()));
		List<Referendum> referendumList = new ArrayList<>();
		Pattern regex = Initialize.referendumTextRegex;
		Matcher m = regex.matcher(pageText);
		// There may be more than one referendum question.
		while (m.find()) {
			// TODO: Add try/catch
			String title = m.group("title");
			if (title.endsWith("\n")) {
				title = title.substring(0, title.length() - 1);
			}
			String text = m.group("text");
			logger.info(
					String.format("referendum title: %s precinctNoName: %s", title, ballot.getPrecinctNoName()));
			Referendum ref = new Referendum(ballot, title, text);
			referendumList.add(ref);
		}
		return referendumList;
	}

}
