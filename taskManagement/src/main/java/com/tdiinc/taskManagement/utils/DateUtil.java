package com.tdiinc.taskManagement.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Hedi Ben Said (hedi.bensaid@gmai.com) Some utilities to handle date
 *         operations
 *
 */
public class DateUtil {

	/**
	 * Returns the date object regarding the given string
	 * 
	 * @param dueDate: the due date can be written in format
	 *        [dd/MM/yyyy][dd-MM-yyyy] or can be written as follows TOMORROW, TODAY,
	 *        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	 * @return
	 */
	public Date computeDueDate(String dueDate) {
		Date computedDay;
		dueDate = dueDate.toUpperCase();
		switch (dueDate) {
		case "TODAY":
			computedDay = new Date();
			break;
		case "MONDAY":
		case "TUESDAY":
		case "WEDNESDAY":
		case "THURSDAY":
		case "FRIDAY":
		case "SATURDAY":
		case "SUNDAY":
			computedDay = computeToDayOfWeek(dueDate);
			break;
		case "TOMORROW":
			computedDay = computeToNextDayOfWeek();
			break;
		case "NEXTWEEK":
			computedDay = computeToDayOfWeek("MONDAY");
			break;
		default:
			computedDay = parseDate(dueDate);
		}

		return computedDay;
	}

	/**
	 * returns the next date of the current date
	 * 
	 * @return
	 */
	public Date computeToNextDayOfWeek() {

		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);

		c.add(Calendar.DAY_OF_MONTH, 1);

		return c.getTime();
	}

	/**
	 * Returns the date for the given Day Of Week
	 * 
	 * @param dueDate: represented as TOMORROW, TODAY, MONDAY, TUESDAY, WEDNESDAY,
	 *        THURSDAY, FRIDAY, SATURDAY, SUNDAY
	 * @return
	 */
	public Date computeToDayOfWeek(String dueDate) {

		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);

		c.add(Calendar.DAY_OF_MONTH, 1);

		while (getDayOfWeek(c.getTime()).toString() == null ? dueDate != null
				: !getDayOfWeek(c.getTime()).toString().equals(dueDate.toUpperCase())) {
			c.add(Calendar.DAY_OF_MONTH, 1);
		}

		return c.getTime();
	}

	/**
	 * Returns the Day Of Week for the given date
	 * 
	 * @param refDate
	 * @return
	 */
	public DayOfWeek getDayOfWeek(Date refDate) {
		LocalDate today = refDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return today.getDayOfWeek();
	}

	/**
	 * parse the date of pattern [dd/MM/yyyy][dd-MM-yyyy]
	 *
	 * @param dueDate
	 * @return
	 */
	public Date parseDate(String dueDate) {
		Date computedDay;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd/MM/yyyy][dd-MM-yyyy]");
		LocalDate realDueDate = LocalDate.parse(dueDate, formatter);
		computedDay = Date.from(realDueDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return computedDay;
	}

}
