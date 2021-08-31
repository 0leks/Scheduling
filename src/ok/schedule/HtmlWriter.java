package ok.schedule;

import java.io.*;
import java.util.*;

import javax.swing.*;

import main.*;

public class HtmlWriter {

	public static boolean writeToFile(String fileName, int year, String month, Day[][] days) {
		PrintWriter fileOut;
		try {
			fileOut = new PrintWriter(new FileWriter(fileName, false));

			// filename = 'Mohr_' + month[0:3] + '_' + month2[0:3] + '_' + yearstr +
			// '_Schedule.html'

			fileOut.print("<html>\n");
			fileOut.print("<head>\n");
			String monthHeader = "<title>" + month + " " + year;
			fileOut.print(monthHeader);
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

			fileOut.print("<h2>Noon Supervisor Schedule</h2>\n");
			monthHeader = "<h2>" + month + " " + year;
			fileOut.print(monthHeader);
			fileOut.print("</h2>\n");

			fileOut.print("<table border=\"1\">\n");
			fileOut.print("<tr><th>Monday</th><th>Tuesday</th><th>Wednesday</th><th>Thursday</th><th>Friday</th></tr>\n");

			fileOut.print("<tr>\n");
			String lastprinted = "";
			for (int week = 0; week < days.length; week++) {
				for (int day = 0; day < days[week].length; day++) {
					fileOut.print("<td>\n");
					fileOut.print("\t" + days[week][day].getOfficialDate() + " ");
					if (!lastprinted.equals(days[week][day].getMonth())) {
						fileOut.print(days[week][day].getMonth());
						lastprinted = days[week][day].getMonth();
					}
					fileOut.print("\n");

					if (days[week][day].isHoliday()) {
						fileOut.print("\t<ul>\n");
						fileOut.print("\t\t" + days[week][day].getText());
						fileOut.print("\t</ul>\n");
					} else if (!days[week][day].isUnused()) {
						fileOut.print("\t<ol>\n");
						List<Employee> assigned = days[week][day].getAssignments();
						for (int position = 0; position < assigned.size(); position++) {
							Employee e = assigned.get(position);
							fileOut.print("\t\t<li>");
							fileOut.print(e.getName());
							position++;
							if (assigned.size() > position) {
								Employee b = assigned.get(position);
								if(!b.getName().equals("")) {
									fileOut.print(", " + b.getName());
								}
							}
							fileOut.print("</li>\n");
						}
						fileOut.print("\t</ol>\n");
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
