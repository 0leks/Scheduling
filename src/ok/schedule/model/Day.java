package ok.schedule.model;

import java.util.ArrayList;
import java.util.List;

import ok.schedule.Utils;

public class Day {

  private String name;
  private String text;
  private boolean blank;
  
  private boolean holiday;
  private boolean unused;
  
  private int officialDate;
  private int id;
  private String month;
  private int year;
  
  private List<Employee> assignments;
  
  public Day(String name, int officialDate, int id) {
    this.name = name;
    this.text = "";
    this.officialDate = officialDate;
    this.id = id;
    this.blank = false;
  }
  
  public Day(Day day) {
    this(day.getName(), day.getOfficialDate(), day.getID());
    this.month = (day.getMonth());
    this.setYear(day.getYear());
    this.holiday = day.holiday;
    this.setText(day.getText());
  }

  public Day() {
    this.blank = true;
    this.text = "";
    this.name = "blank";
    this.month = "MONTH";
  }
  
  public Day(int dayofweek, int officialDate, int id) {
    this(Utils.getNameofDay(dayofweek), officialDate, id);
  }
  
  public void clearAssignments() {
    if( assignments != null ) {
      assignments.clear();
    }
  }
  public List<Employee> getAssignments() {
    return assignments;
  }
  
  public boolean hasAssignments() {
    return assignments != null;
  }
  
  public void assign(Employee e) {
    if(assignments == null ) {
      assignments = new ArrayList<Employee>();
    }
    assignments.add(e);
  }
  
  public void setIsHoliday(boolean isHoliday) {
    holiday = isHoliday;
  }
  public boolean isUnused() {
    return unused; 
  }
  public void setUnused(boolean state) {
    unused = state;
  }
  
  public boolean isHoliday() {
    return holiday;
  }
  
  public boolean isBlank() {
    return blank;
  }
  
  public String getName() {
    return name;
  }
  public int getOfficialDate() {
    return officialDate;
  }
  public int getID() {
    return id;
  }
  
  public void setMonth(int month) {
    this.month = Utils.getNameofMonth(month);
  }
  
  public String getMonth() {
    return month;
  }
  
  public int getYear() {
    return year;
  }
  public void setYear(int year) {
    this.year = year;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  public String getText() {
    return text;
  }
}
