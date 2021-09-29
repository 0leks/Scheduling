package ok.schedule;

import java.io.*;
import java.util.*;

import main.*;

public class HtmlWriter {
	private static final String EXTRA_STYLE = " style=\"background-color: rgba(255,255,255,0.45); background-blend-mode: lighten; background-position: right; background-size: contain; background-repeat: no-repeat; background-image: url(";
	private static final String EXTRA_STYLE_AFTER = ")\" ";
	private static String addExtra(int year, String month, int week, int day) {
		// TODO add new year, thanksgiving, st patrick, valentine, 4th of july
		String url = null;
		if((year == 2021 && month.equals("October") && week == 4 && day == 4)
			|| (year == 2022 && month.equals("October") && week == 4 && day == 0)) {
			url = "http://clipart-library.com/images/pc58gx99i.png";
		}
		if(url != null) {
			return EXTRA_STYLE + url + EXTRA_STYLE_AFTER;
		}
		return "";
	}

	public static boolean writeToFile(String fileName, int year, String month, Day[][] days) {
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
			fileOut.print("ol { -webkit-margin-before: 5px; -webkit-margin-after: 5px; margin-block-start: 0.1em; margin-block-end: 0.1em;}\n");
			fileOut.print("table, th, td {border: 1px solid black;border-collapse: collapse;}\n");
			fileOut.print("</style>\n");
			fileOut.print("</head>\n");

			fileOut.print("\n<body>\n");

			fileOut.print("<h2>Noon Supervisors Schedule</h2>\n");
			fileOut.print("<h2>" + month + " " + year);
			fileOut.print("</h2>\n");

			fileOut.print("<table border=\"1\">\n");
			fileOut.print("<tr><th>Monday</th><th>Tuesday</th><th>Wednesday</th><th>Thursday</th><th>Friday</th></tr>\n");

			fileOut.print("<tr>\n");
			String lastprinted = "";
			for (int week = 0; week < days.length; week++) {
				for (int day = 0; day < days[week].length; day++) {
					fileOut.print("\t<td" + addExtra(year, month, week, day) + "> ");
					fileOut.print(days[week][day].getOfficialDate() + " ");
					if (!lastprinted.equals(days[week][day].getMonth())) {
						fileOut.print(days[week][day].getMonth());
						lastprinted = days[week][day].getMonth();
					}
					fileOut.print(" ");

					if (days[week][day].isHoliday()) {
						fileOut.print("<ul> " + days[week][day].getText() + "</ul> ");
					} else if (!days[week][day].isUnused()) {
						fileOut.print("<ol> ");
						List<Employee> assigned = days[week][day].getAssignments();
						for (int position = 0; position < assigned.size(); position++) {
							Employee e = assigned.get(position);
							fileOut.print("<li>");
							fileOut.print(e.getName());
							position++;
							if (assigned.size() > position) {
								Employee b = assigned.get(position);
								if(!b.getName().equals("")) {
									fileOut.print(", " + b.getName());
								}
							}
							fileOut.print("</li> ");
						}
						fileOut.print("</ol> ");
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
