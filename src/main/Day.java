package main;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

public class Day {

  private String name;
  private String text;
  private boolean blank;
  
  private boolean holiday;
  
  private JTextField field;
  
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
    blank = false;
  }
  
  public Day(Day day) {
    this(day.getName(), day.getOfficialDate(), day.getID());
    this.month = (day.getMonth());
    this.setYear(day.getYear());
    this.holiday = day.holiday;
    this.setText(day.getText());
  }
  
  public Day(int dayofweek, int officialDate, int id) {
    this(getNameofDay(dayofweek), officialDate, id);
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
  
  public void absorbTextField() {
    if( field != null ) {
      text = field.getText();
      field = null;
    }
  }
  
  public JTextField getTextField() {
    return field;
  }
  
  public void setTextField(JTextField field) {
    this.field = field;
  }
  public void toggleHoliday() {
    holiday = !holiday;
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
  
  public static String getNameofDay(int dayofweek) {
    if( dayofweek == 0 ) {
      return "Monday";
    }
    else if( dayofweek == 1 ) {
      return "Tuesday";
    }
    else if( dayofweek == 2 ) {
      return "Wednesday";
    }
    else if( dayofweek == 3 ) {
      return "Thursday";
    }
    else if( dayofweek == 4 ) {
      return "Friday";
    }
    return "";
  }
  
  public static String getNameofMonth(int monthofyear) {
    String[] monthStrings = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
    "September", "October", "November", "December" };
    return monthStrings[monthofyear];
  }
  public Day() {
    blank = true;
    this.text = "";
    this.name = "blank";
    this.month = "MONTH";
  }
  
  public void setMonth(int month) {
    this.month = getNameofMonth(month);
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
