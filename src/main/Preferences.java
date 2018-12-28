package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class Preferences {
  
  private static PrintWriter fileOut;
  private static Scanner fileIn;
  private static final String PREFERENCES_LOCATION = "preferences2.txt";
  
  private static final String YES = "Y";
  private static final String NO = "N";
  
  private static void initOutput() {
    if( fileOut != null ) {
      fileOut.close();
    }
    try {
      fileOut = new PrintWriter(new FileWriter(PREFERENCES_LOCATION, false));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static void initInput() {
    if( fileIn != null ) {
      fileIn.close();
    }
    try {
      fileIn = new Scanner(new File(PREFERENCES_LOCATION));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  private static void closeInput() {
    fileIn.close();
  }
  
  private static void closeOutput() {
    fileOut.close();
  }
  
  public static void readEmployees(List<Employee> list) {
    initInput();
    int day = 0;
    Employee e = new Employee("Temp");
    while(fileIn.hasNext()) {
      String token = fileIn.next();
      if( token.equals(YES) ) {
        
      }
      else if( token.equals(NO) ) {
        e.toggleAvailable(day);
      }
      day++;
      if( day == 5 ) {
        String name = fileIn.nextLine();
        name = name.trim();
        e.setName(name);
        list.add(e);
        day = 0;
      }
    }
    closeInput();
  }
  
  public static void writeEmployees(List<Employee> list) {
    initOutput();
    for( Employee e : list ) {
      writeEmployee(e);
    }
    closeOutput();
  }
  
  private static void writeEmployee(Employee e) {
    
    for( int day = 0; day < 5; day++ ) {
      if( e.available(day) ) {
        fileOut.print(YES + " ");
      }
      else {
        fileOut.print(NO + " ");
      }
    }
    fileOut.println(e.getName());
  }
}
