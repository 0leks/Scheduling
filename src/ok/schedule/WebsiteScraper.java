package ok.schedule;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ok.schedule.model.Day;
import ok.schedule.model.MyCalendar;

public class WebsiteScraper {
  
  private static final String schoolCalendarQuery = "https://mohr.pleasantonusd.net/apps/events/%d/%d/?id=0";

  public static void log(String format, Object...args) {
    System.out.printf(format + "\n", args);
  }
  public static void querySchoolCalendar(MyCalendar calendar, int targetYear, int targetMonth) {
    try {
      String urlString = schoolCalendarQuery.formatted(targetYear, targetMonth);
      System.out.println(urlString);
      Document doc = Jsoup.connect(urlString).get();
      log(doc.title());
      Elements newsHeadlines = doc.select(".event-day");
      System.out.println("Found " + newsHeadlines.size() + " event days");
      for (Element headline : newsHeadlines) {
        if(headline.classNames().contains("empty")) {
          continue;
        }
        Element dayDateBox = headline.selectFirst(".day-date-box");
        String month = dayDateBox.selectFirst(".month").text();
        int date = Integer.parseInt(dayDateBox.selectFirst(".date").text());
        Day day = Utils.getDayByDate(calendar.days, date);
        if(day == null) {
          continue;
        }
        for(Element titleElem : headline.select(".event-title")) {
          String eventTitle = titleElem.text().trim();
          String lowered = eventTitle.toLowerCase();
          if (lowered.contains("spirit friday")) {
            continue;
          }
          boolean isHoliday = lowered.contains("break") || lowered.contains("no school");
          log("event on %s, %d, %s with title:'%s'", 
              month, 
              date, 
              dayDateBox.selectFirst(".day").text(),
              eventTitle);
          day.setText(day.getText() + " " + eventTitle);
          day.setIsHoliday(day.isHoliday() || isHoliday);
          System.out.println("updated day: " + day.getText());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
