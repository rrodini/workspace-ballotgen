package com.rodini.ballotprocessor.extract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rodini.ballotprocessor.Initialize;
import com.rodini.ballotprocessor.model.Ballot;
import com.rodini.ballotprocessor.model.Contest;
import com.rodini.ballotprocessor.model.Candidate;
import com.rodini.ballotutils.Utils;


public class ContestExtractor {
	static final Logger logger = LogManager.getLogger(ContestExtractor.class);
	// prevent instantiation.
	private ContestExtractor() {}
	
	public static void extract(List<Ballot> ballots) {
		List<Contest> contests;
		for (Ballot ballot: ballots) {
			// Page 1 text processing here.
			contests = extractContests(ballot, ballot.getPage1Text());
			for (Contest contest: contests) {
				ballot.extendVoteFors(contest);
			}
			// Page 2 text processing here.
			contests = extractContests(ballot, ballot.getPage2Text());
			// If there are page 2 contests, need to generate a page break here.
			if (contests.size() > 0) {
				ballot.extendVoteFors(new Contest(ballot, Initialize.CONTEST_PAGE_BREAK, null, null, null));		
			}
			for (Contest contest: contests) {
				ballot.extendVoteFors(contest);
			}
		}
	}
	/**
	 * extractContests loops thru the pageText applying ballot contest regexes to parse a contest.
	 * The algorithm relies on VS formatting each contest on the page like this:
	 * +----------------+
	 * | title          |
	 * | instructions   |
	 * | candidate list |
	 * | Write-in       |
	 * +----------------+
	 * The assumption is that the pageText starts with a contest title and
	 * the first literal "Write-in" is the end of its candidate list.
	 * 
	 * @param ballot Ballot object.
	 * @param pageText Ballot's page1 or page2 text.
	 * @return
	 */
	static List<Contest> extractContests(Ballot ballot, String pageText) {
		List<Contest> contests = new ArrayList<>();
		int start = 0;
		int end = 0;
		if (pageText.isEmpty()) {
			// Page1 should have contests, but Page2 may not.
			return contests;
		} else if (pageText.lastIndexOf(Initialize.writeIn) == -1) {
			// We're in trouble since there is no literal Write-in text.
			logger.error("lastIndexOf(" + Initialize.writeIn + ") is -1");
			logger.error(String.format("defective ballot: %s pageText: %s", ballot.getPrecinctNoName(), pageText));
			return contests;
		}
		// Lines below will eliminate text pertaining to Referendums and Retentions. They are critical
		// to ballots that have either of them.
		int lastWriteIn = pageText.lastIndexOf(Initialize.writeIn) + Initialize.writeIn.length();
		pageText = pageText.substring(0, lastWriteIn);
		//  Assume that the last "Write-in" line marks end of all contests.
		while (start < pageText.length()) {
			end = findContestEnd(pageText, start);
			String contestText = pageText.substring(start, end);
System.out.println("contestText:" + contestText);
			Contest contest = matchContest(ballot, contestText);
			if (contest != null) {
				contests.add(contest);
			}
			start = end;
		}
		return contests;
	}
	
	/**
	 * findContestEnd finds the index of the string past the "Write-in\n" entries for a contest.
	 * 
	 * @param start index into contestsTexts
	 * @return index past the next "Write-in\n" entries
	 */
	static int findContestEnd(String contestText, int start) {
		int len = Initialize.writeIn.length();
		int end = contestText.indexOf(Initialize.writeIn, start);
		// there should be at least one "Write-in" line.
		if (end == -1) {
			logger.error("Can't find \"" + Initialize.writeIn.trim() + "\"" );
			return contestText.length();
		}
		// Skip over multiple "Write-in" lines.
		while ( end + len <= contestText.length() &&
				contestText.substring(end, end+len).equals(Initialize.writeIn)) {
			end = end + len;
		}
		return end;
	}

	/**
	 * matchContest applies the contest regexes in precedence order.
	 * From experience it seems that two regexes (sometimes only one)
	 * cover all ballots issued by VS. The first regex is more stringent
	 * (harder) to match than the second regex.
	 * 
	 * Note:
	 * 1) There may be more than two regexes in the list, but the stringency
	 *    condition should still be met.
	 * 2) Referendum and Retention text is also submitted.
	 * 
	 * @param ballot Ballot object.
	 * @param contestText text for one contest.
	 * @return
	 */
	static Contest matchContest(Ballot ballot, String contestText) {
				Pattern [] regexes = Initialize.contestTextRegex;
				Contest contest = null;
				String title = "Unknown contest";
				// try all regexes for a contest. Note that these
				// are ordered from the most stringent to the
				// least stringent.
				int count = regexes.length;
				int i;
				Matcher m = null;
				for (i = 0; i < count; i++) {
					m = regexes[i].matcher(contestText);
					if (m.find()) {
						title = m.group("title").trim();
						break;
					}
				}
				if (i == count) {
					// No match!
					logger.error(String.format("no contest regex match. precinctNoName: %s text: %s", ballot.getPrecinctNoName(), contestText));					
				} else {
					logger.info(String.format("contest title: %s precinctNoName: %s", title, ballot.getPrecinctNoName()));
					contest = contestFactory(ballot, title, m);
				}
				return contest;
			}

	static Contest contestFactory(Ballot ballot, String title, Matcher m) {
		Contest contest = Contest.GENERIC_CONTEST;
		try {
			// "term" is optional
//			String term = getMatchGroup(m, "term");
			String term = "";
			String instructions = getMatchGroup(m, "instructions");
			String candidatesText = getMatchGroup(m, "candidates");
			CandidateFactory cf = new CandidateFactory(title, candidatesText, Initialize.elecType, Initialize.endorsedParty);
			List<Candidate> candidates = cf.getCandidates();
			contest = new Contest(ballot, title, term, instructions, candidates);
		} catch (Exception e) {
			String msg = e.getMessage();
			logger.error(msg);
		}
		if (contest == Contest.GENERIC_CONTEST) {
			logger.error(String.format("could not generate contest precinctNoName: %s title: %s", ballot.getPrecinctNoName(), title));
		}
		return contest;
	}

	/** 
	 * getMatchGroup attempts to get the value of the named match
	 * group.  There are some formats (regexes) that will not match.
	 * 
	 * @param m Matcher object
	 * @param groupName name for group
	 * @return value for group
	 */
	/* private */
	static String getMatchGroup(Matcher m, String groupName) {
		String value = "";
		try {
			value = m.group(groupName);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg.contains("<term>")) {
				// this is expected for some formats (regexes).
				logger.info(msg);
			} else {
				logger.error(msg);
			}
		}
		return value;
	}

	
}
