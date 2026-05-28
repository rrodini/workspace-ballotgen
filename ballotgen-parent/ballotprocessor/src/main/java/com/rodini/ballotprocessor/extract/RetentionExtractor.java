package com.rodini.ballotprocessor.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotprocessor.model.Retention;

/** 
 * RetentionExtractor processes all ballots and extracts the retentions
 * from each.
 * 
 * @author Bob Rodini
 *
 */
public class RetentionExtractor {
	// prevent instantiation.
	private RetentionExtractor() {}
	
	static final Logger logger = LogManager.getLogger(RetentionExtractor.class);

	/**
	 * extractRetentions loops thru the pageText applying ballot retention regex to parse a retention.
	 * The algorithm relies on VS formatting each retention on the page like this:
	 * 
	 * +----------------+
	 * | title          |          |
	 * | text of        |
	 * | retention      |
	 * | YES            |
	 * | NO             |
	 * +----------------+
	 * 
	 * Note:
	 * 1) A second regex is used to obtain the judges's name.
	 *    The judge's name is used for endorsements.
	 * @param ballot Ballot object.
	 * @param pageText Ballot's page2 text.
	 * 
	 * @return List of Retention objects.
	 */
	public static List<Retention> extractRetentions(Ballot ballot, String pageText) {
		logger.info(String.format("extract retentions precinctNoName: %s", ballot.getPrecinctNoName()));
		List<Retention> retentions = new ArrayList<>();
		Pattern textRegex = Initialize.retentionTextRegex;
		Pattern nameRegex = Initialize.retentionNameRegex;
		// There may be more than one retention.
		Matcher m1 = textRegex.matcher(pageText);
		while (m1.find()) {
			String title = m1.group("title");
			String question = m1.group("question");
//			title += "\n";
			Matcher m2 = nameRegex.matcher(question);
			logger.info(String.format("retention extraction: %s title: %s", ballot.getPrecinctNoName(), title));
			if (m2.find()) {
				Retention ret = new Retention(ballot, title, question, m2.group("name"));
				retentions.add(ret);
			} else {
				logger.error("judge name not found in retention question:");
			}
		}
		return retentions;
	}
	
	
	
}
