package com.csgroup.reprodatabaseline.rules;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.MILLIS;
import java.time.ZonedDateTime;

public class RuleUtilities {

	public static ZonedDateTime getMeanTime(ZonedDateTime start, ZonedDateTime end) {
		long diff_half = SECONDS.between(start, end) / 2;
		return start.plusSeconds(diff_half);
	}
	
	public static long getDiff(ZonedDateTime start, ZonedDateTime end) {
		long diff = SECONDS.between(start, end);
		return diff;
	}
	
}
