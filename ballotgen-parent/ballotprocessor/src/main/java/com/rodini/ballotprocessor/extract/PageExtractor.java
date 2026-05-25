package com.rodini.ballotprocessor.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotprocessor.model.Contest;
import com.rodini.ballotprocessor.model.Referendum;
import com.rodini.ballotprocessor.model.Retention;
/**
 * PageExtractor has the responsibility for isolating the page text for each Ballot object.
 * This isolation helps with the creation of VoteFor objects.
 * 
 * @author Bob Rodini
 *
 */
public class PageExtractor {
	static final Logger logger = LogManager.getLogger(PageExtractor.class);
	// prevent instantiation.
	private PageExtractor() {}
	/**
	 * extract isolates the page1 and page2 text from the ballot objects' rawText field
	 * and updates the fields within each ballot object.
	 * 
	 * @param ballots list of Ballot objects.
	 */
	public static void extract(List<Ballot> ballots) {
		for (Ballot ballot: ballots) {
			extractPages(ballot);
		}
	}
	/**
	 * extractPages isolates the page1 and page2 text for one ballot object.
	 * It is important to isolate page1 from page2 for several reasons.
	 * 1. Two page ballots require a PAGE BREAK (e.g. Vote Both Sides) blurb.
	 * 2. Referendum questions and retention questions are always placed on page2
	 *     by Chesco Voter Services.
	 *     
	 * @param ballot Ballot object.
	 */
	static void extractPages(Ballot ballot) {
		String rawText = ballot.getRawText();
		String precinctNoName = ballot.getPrecinctNoName();
		String page1Text = "";
		String page2Text = "";
		if (extractPageCount(ballot, rawText) == 1) {
			// Use just one regex.
			page1Text = extractPage(precinctNoName, rawText, Initialize.onePageTextRegex, 1);
		} else {
			// Use two regexes.
			page1Text = extractPage(precinctNoName, rawText, Initialize.twoPageTextP1Regex, 1);
			page2Text = extractPage(precinctNoName, rawText, Initialize.twoPageTextP2Regex, 2);
		}	
		ballot.setPage1Text(page1Text);
		ballot.setPage2Text(page2Text);
		// Don't need raw text anymore.
		ballot.discardRawText();
		// Now extract Contests, Referendums, and Retentions.
		extractVoteFors(ballot);
	}
	/**
	 * Use the precinctPageBreakRegex to recognize a two-page ballot.
	 * Recent VS specimens use "Vote Both Sides" as a marker for a two pager.
	 * @param rawText complete precinct ballot text
	 * @return 1 or 2.
	 */
	static int extractPageCount(Ballot ballot, String rawText) {
		int pageCount = 1;
		Pattern pageBreakRegex = Initialize.pageBreakRegex;
		Matcher m = pageBreakRegex.matcher(rawText);
		if (m.find()) {
			pageCount = 2;
		}
		logger.debug(String.format("ballot: %s has %d pages", ballot.getPrecinctNoName(), pageCount));
		return pageCount;
	}
	
	/**
	 * extractPage isolates a page of contest text from the ballot. The regex to do
	 * this must be pre-tested and correctly entered as a property value.
	 * 
	 * It was recently discovered (General election of 2024) that some ballots have
	 * two pages of text and others only one page of text. This now (v1.7.0) requires
	 * a precise regex for one page ballots and two precise regexes for two page
	 * ballots.
	 * 
	 * @param precinctNoName ballot identifier.
	 * @param rawText the raw text of the ballot.
	 * @param pageRegex regex designed to isolate the given page.
	 * @param pageNo 1 or 2.
	 * @return text of page 1 or 2.
	 */
	static String extractPage(String precinctNoName, String rawText, Pattern pageRegex, int pageNo) {
		String pageText = "";		
		Matcher m = pageRegex.matcher(rawText);
		if (!m.find()) {
			String msg = String.format("no match for precinctNoName: %s precinct page %d.  regex: %s", precinctNoName, pageNo, pageRegex.pattern());
			logger.error(msg);
		} else {
			try {
				pageText = m.group("page");
				logger.info(String.format("page extraction: %s page #: %d", precinctNoName, pageNo));
				logger.debug("page lines:");
				logger.debug(pageText);
				logger.debug("---------");
			} catch (Exception e) {
				String msg = String.format("no match for precinctNoName: %s precinct page %d.  regex: %s reason: %s", precinctNoName, pageNo, pageRegex.pattern(), e.getMessage());
				logger.error(msg);
			}
		}
		return pageText;
	}

	static void extractVoteFors(Ballot ballot) {
		String page1Text = ballot.getPage1Text();
		String page2Text = ballot.getPage2Text();
		// Contest extraction.
		List<Contest> contests;
		// Page 1.
		contests = ContestExtractor.extractContests(ballot, page1Text);
		extendContests(ballot, contests);
		// Page 2.
		contests.clear();		
		if (page2Text.isEmpty()) {
			logger.debug(String.format("page 2 empty. %s", ballot.getPrecinctNoName()));
		}
		contests = ContestExtractor.extractContests(ballot, page2Text);
		// If there are page 2 contests, need to generate a page break here.
		if (contests.size() > 0) {
			Contest pageBreakContest = new Contest(ballot, Initialize.CONTEST_PAGE_BREAK, null, null, null);
			extendContests(ballot, List.of(pageBreakContest));
		}
		extendContests(ballot, contests);
		// Referendum extraction.
		List<Referendum> referendums;
		referendums = ReferendumExtractor.extractReferendums(ballot, page2Text);
		extendReferendums(ballot, referendums);
		// Retention extraction.
		List<Retention> retentions;
		retentions = RetentionExtractor.extractRetentions(ballot, page2Text);
		extendRetentions(ballot, retentions);
	}
	
	static void extendContests(Ballot ballot, List<Contest> contests) {
		for (Contest contest: contests) {
			ballot.extendVoteFors(contest);
		}		
	}
	static void extendReferendums(Ballot ballot, List<Referendum> referendums) {
		for (Referendum referendum: referendums) {
			ballot.extendVoteFors(referendum);
		}		
	}
	static void extendRetentions(Ballot ballot, List<Retention> retentions) {
		for (Retention retention: retentions) {
			ballot.extendVoteFors(retention);
		}		
	}

}
