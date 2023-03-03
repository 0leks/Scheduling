package ok.schedule;

import java.io.*;
import java.util.*;

import ok.schedule.model.Day;
import ok.schedule.model.Employee;

public class HtmlWriter {
  
  private static final String NORMAL_SPACE = "<a href=\"https://youtu.be/dQw4w9WgXcQ\" style=\"text-decoration: none\"> </a>";

  private static final String JACKOLANTERN = "http://clipart-library.com/images/pc58gx99i.png";
	private static final String SPARKLER = "http://clipart-library.com/image_gallery/284123.png";
	private static final String CLOVER = "http://clipart-library.com/images_k/clover-transparent/clover-transparent-18.png";
	private static final String HEART = "http://clipart-library.com/images_k/heart-clipart-transparent/heart-clipart-transparent-18.png";
	private static final String FIREWORK = "http://clipart-library.com/data_images/284680.gif";
	private static final String TURKEY = "http://clipart-library.com/img/756162.png";
	private static final String EXTRA_STYLE = " style=\"background-color: rgba(255,255,255,0.45); background-blend-mode: lighten; background-position: right; background-size: contain; background-repeat: no-repeat; background-image: url(";
	private static final String EXTRA_STYLE_AFTER = ")\" ";
	private static String addExtra(int year, String month, int week, int day, int dayofmonth) {
		// TODO add solstice/equinox?
		// make thanksgiving autocompute
		String url = null;
		HashMap<String, HashMap<Integer, String>> events = new HashMap<>();
		
		for(String monthName : Utils.monthStrings) {
			events.put(monthName, new HashMap<>());
		}
		
		events.get("February").put(14, HEART);
		events.get("March").put(17, CLOVER);
		events.get("July").put(4, FIREWORK);
		events.get("October").put(31, JACKOLANTERN);
		events.get("December").put(31, SPARKLER);
		
		HashMap<Integer, String> currentEventMonth = events.get(month);
		for(Integer eventDay : currentEventMonth.keySet()) {
			if(dayofmonth == eventDay || 
					(day == 4 && (dayofmonth+1 == eventDay || dayofmonth+2 == eventDay))) {
				url = currentEventMonth.get(eventDay);
				break;
			}
		}
		if(month.equals("November") && (
				(year == 2021 && dayofmonth == 25)
				|| (year == 2022 && dayofmonth == 24)
				|| (year == 2023 && dayofmonth == 23))) {
			url = TURKEY;
		}
		if(url != null) {
			return EXTRA_STYLE + url + EXTRA_STYLE_AFTER;
		}
		return "";
	}

	public static boolean writeToFile(String fileName, int year, String month, Day[][] days, boolean useOrderedList) {
		PrintWriter fileOut;
		try {
			fileOut = new PrintWriter(new FileWriter(fileName, false));
			fileOut.print("<html>\n");
			fileOut.print("<head>\n");
			fileOut.print("<title>" + month + " " + year);
			fileOut.print("</title>\n");
			fileOut.print("<style type=\"text/css\">\n");
			fileOut.print("th, td { padding:1px; padding-right: 20px; width: 230px; font-weight: bold;font-size: 18px; vertical-align: top;}\n");
			fileOut.print("li { font-weight: normal;font-size: 16px; }\n");
			fileOut.print("td { height: 139px;}\n");
			fileOut.print("h2 {text-align: center;}\n");
			fileOut.print("ol, ul { -webkit-margin-before: 5px; -webkit-margin-after: 5px; margin-block-start: 0.1em; margin-block-end: 0.1em;}\n");
			fileOut.print("table, th, td {border: 1px solid black;border-collapse: collapse;}\n");
			fileOut.print("</style>\n");
			fileOut.print("</head>\n");

			fileOut.print("\n<body>\n");

			fileOut.print("<h2>Noon Supervisors" + NORMAL_SPACE + "Schedule</h2>\n");
			fileOut.print("<h2>" + month + " " + year);
			fileOut.print("</h2>\n");

			fileOut.print("<table border=\"1\">\n");
			fileOut.print("<tr><th>Monday</th><th>Tuesday</th><th>Wednesday</th><th>Thursday</th><th>Friday</th></tr>\n");

			fileOut.print("<tr>\n");
			String lastprinted = "";
			for (int week = 0; week < days.length; week++) {
				for (int day = 0; day < days[week].length; day++) {
					fileOut.print("\t<td" + addExtra(year, month, week, day, days[week][day].getOfficialDate()) + "> ");
					fileOut.print(days[week][day].getOfficialDate() + " ");
					if (!lastprinted.equals(days[week][day].getMonth())) {
						fileOut.print(days[week][day].getMonth());
						lastprinted = days[week][day].getMonth();
					}

					if (days[week][day].isHoliday()) {
						fileOut.print("<ul> " + days[week][day].getText() + "</ul> ");
					} else if (!days[week][day].isUnused()) {
						fileOut.print(" " + days[week][day].getText());
						fileOut.print(useOrderedList ? "<ol> " : "<ul> ");
						List<Employee> assigned = days[week][day].getAssignments();
						for (int position = 0; position < assigned.size(); position++) {
							Employee e = assigned.get(position);
							fileOut.print("<li>");
							fileOut.print(e.getName());
							fileOut.print("</li> ");
						}
						fileOut.print(useOrderedList ? "</ol> " : "</ul> ");
					}

					fileOut.print("</td>\n");
				}
				fileOut.print("</tr>\n<tr>\n");
			}

			fileOut.print("</table>\n");
			fileOut.print("</body>\n");
			fileOut.print("</html>\n");
			fileOut.close();
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return false;
	}
}
