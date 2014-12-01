// -------------------------------------------------------------------------------------
// Calendar HTML generation functions
// -------------------------------------------------------------------------------------

// Calls the calendar generation sub-functions
function atgCalendar_getCalendarHTML(calendarInstance)
{
	var calendarHTML = "";
	
	calendarHTML += _atgCalendar_generateCalendarHeader(calendarInstance);
	calendarHTML += _atgCalendar_generateCalendarMonth(calendarInstance);
	calendarHTML += _atgCalendar_generateCalendarBody(calendarInstance);
	calendarHTML += _atgCalendar_generateCalendarFooter(calendarInstance);
	
	return calendarHTML;
}

// Generates the calendar header
function _atgCalendar_generateCalendarHeader(calendarInstance)
{
	var calendarHeader = "";
	
	calendarHeader += "<table class=\""+calendarStyles.Container+"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n";
	calendarHeader += "<tr>\n";
	calendarHeader += "<td class=\""+calendarStyles.BorderTopLeft+"\"></td>\n";
	calendarHeader += "<td class=\""+calendarStyles.BorderTopMiddle+"\"></td>\n";
	calendarHeader += "<td class=\""+calendarStyles.BorderTopRight+"\"></td>\n";
	calendarHeader += "</tr>\n";
		
	return calendarHeader;
}

// Generates the calendar month and year labels
function _atgCalendar_generateCalendarMonth(calendarInstance)
{
	var newDate = calendarInstance.displayDate;
	var calendarMonth = "";
	
	calendarMonth += "<tr>\n";
	calendarMonth += "<td nowrap=\"nowrap\" class=\""+calendarStyles.Left+"\"></td>\n";
	calendarMonth += "<td class=\""+calendarStyles.Middle+"\">\n";
	calendarMonth += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td colspan=\"7\" class=\""+calendarStyles.MonthContainer+"\">\n";
	calendarMonth += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td class=\""+calendarStyles.PreviousMonthRow+"\" nowrap=\"nowrap\"><span class=\""+calendarStyles.IconArrowLeft+"\" title=\"previous month\" onclick=\"atgCalendar_showPreviousMonth(calendarInstance)\">&nbsp;&nbsp;&nbsp;</span></td>\n";
	calendarMonth += "<td class=\""+calendarStyles.MonthRow+"\" nowrap=\"nowrap\"><span id=\""+calendarInstance.calendarInstanceId+"_currentMonth\" class=\""+calendarStyles.CurrentMonth+"\">"
	calendarMonth += atgCalendar_getSelectedMonth(newDate);
	calendarMonth += " ";
	calendarMonth += atgCalendar_getSelectedYear(newDate)+"\n";
	calendarMonth += "<td class=\""+calendarStyles.NextMonthRow+"\" nowrap=\"nowrap\"><span class=\""+calendarStyles.IconArrowRight+"\" title=\"next month\" onclick=\"atgCalendar_showNextMonth(calendarInstance)\">&nbsp;&nbsp;&nbsp;</span></td>\n";
	calendarMonth += "</tr>\n";
	calendarMonth += "</table>\n";
	calendarMonth += "</td>\n";
	calendarMonth += "</tr>\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td align=\"center\">\n";
	calendarMonth += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabelsWeekend+"\">"+calendarI18n.daySymbols[0]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[1]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[2]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[3]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[4]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[5]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabelsWeekend+"\">"+calendarI18n.daySymbols[6]+"</td>\n";
	calendarMonth += "</tr>\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td colspan=\"7\"><hr style=\"width:100%;color:#CCCCCC\" size=\"1\" /></td>\n";
	calendarMonth += "</tr>\n";
	
	return calendarMonth;
}

// Generates the body of the calendar for the specific month
function _atgCalendar_generateCalendarBody(calendarInstance)
{
	var calendarBody = "";
	var newDate = calendarInstance.displayDate;
	var newYear = newDate.getFullYear();
	var newMonth = newDate.getMonth();
	var daysInMonth = atgCalendar_getDaysInMonth(newMonth, newYear);
	var firstDayOfWeek = atgCalendar_getFirstDayOfWeek(newDate);
	var dayCount=1;
	var counter=1;

	for (var weekCount=0; weekCount<6; weekCount++) 
	{
		calendarBody += "<tr>\n";
		for (var iterator=0; iterator<calendarI18n.dayNames.length; iterator++) 
		{
			if (counter <= firstDayOfWeek)
			{
				calendarBody += "<td class=\""+calendarStyles.NumericsOff+"\"></td>\n";
				counter ++;
			}
			else if (dayCount <= daysInMonth)
			{
				var dayStyle = calendarStyles.Numerics;
				
				// Check for weekend
				if ((iterator == 0)||(iterator == 6))
					dayStyle = calendarStyles.NumericsWeekend;
				
				// Check for current day
				var iterationDate = new Date(newDate);
				iterationDate.setDate(dayCount);

				if ((iterationDate.getFullYear() == today.getFullYear())
					&&(iterationDate.getMonth() == today.getMonth())
					&&(iterationDate.getDate() == today.getDate()))
				{
					if (dayStyle == calendarStyles.NumericsWeekend)
						dayStyle = calendarStyles.NumericsWeekendToday;
					else
						dayStyle = calendarStyles.NumericsToday;
				}
					
				calendarBody += "<td id=\""+calendarInstance.calendarInstanceId+"_"+dayCount+"\" class="+dayStyle+" onclick=\"atgCalendar_setDateInMonth("+dayCount+");atgCalendar_setSelectedDate();\">"+dayCount+"</td>\n";
				dayCount ++;
			}
		}
		calendarBody += "</tr>\n";
		if (dayCount > daysInMonth)
		{
			weekCount = 6;
		}
	}
	return calendarBody;
}


// Generates the bottom part of the calendar
function _atgCalendar_generateCalendarFooter(calendarInstance)
{
	var calendarFooter = "";

  calendarFooter += "</table>\n";
	calendarFooter += "</td>\n";
	calendarFooter += "</tr>\n";
	calendarFooter += "<tr>\n";
	calendarFooter += "<td colspan=\"7\" class=\""+calendarStyles.BottomSpacer+"\">\n";
	
	if ((calendarInstance.hasToday == true) || (calendarInstance.hasClose == true))
	{
		calendarFooter += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
		calendarFooter += "<tr>\n";
		if (calendarInstance.hasToday == true)
			calendarFooter += "<td class=\""+calendarStyles.AdditionalTodayRow+"\"><span onclick=\"atgCalendar_setNewDate(today);atgCalendar_setSelectedDate();\" class=\""+calendarStyles.TodayIcon+"\">"+calendarI18n.todayTextString+"</span></td>\n";
		else
			calendarFooter += "<td></td>";
			
		if (calendarInstance.hasClose == true)
			calendarFooter += "<td class=\""+calendarStyles.AdditionalCloseRow+"\"><span onclick=\"atgCalendar_hideCalendar()\" class=\""+calendarStyles.CloseIcon+"\">"+calendarI18n.closeTextString+"</span></td>\n";
		else
			calendarFooter += "<td></td>";
			
		calendarFooter += "</tr>\n";
		calendarFooter += "</table>\n";
	}
	
	calendarFooter += "</td>\n";
	calendarFooter += "</tr>\n";
	
	calendarFooter += "<tr>\n";
	calendarFooter += "<td colspan=\"3\" class=\""+calendarStyles.BottomSpacer+"\"></td>\n";
	calendarFooter += "</tr>\n";
	calendarFooter += "</table>\n";
	calendarFooter += "</td>\n";
	calendarFooter += "<td nowrap=\"nowrap\" class=\""+calendarStyles.Right+"\"></td>\n";
	calendarFooter += "</tr>\n";
	
	calendarFooter += "<tr>\n";
	calendarFooter += "<td class=\""+calendarStyles.BorderBottomLeft+"\"></td>\n";
	calendarFooter += "<td class=\""+calendarStyles.BorderBottomMiddle+"\"></td>\n";
	calendarFooter += "<td class=\""+calendarStyles.BorderBottomRight+"\"></td>\n";
	calendarFooter += "</tr>\n";
	calendarFooter += "</table>\n";
	
	return calendarFooter;
}
