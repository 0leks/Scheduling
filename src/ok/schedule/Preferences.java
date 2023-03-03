package ok.schedule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import ok.schedule.model.Employee;
import ok.schedule.model.Settings;

public class Preferences {
	
  private static final int FILE_VERSION = 3;
  
  private static PrintWriter fileOut;
  private static Scanner fileIn;
  private static final String PREFERENCES_LOCATION = "preferences2.txt";
  
  private static final String YES = "Y";
  private static final String NO = "N";
  
  private static final String FILE_VERSION_KEY = "Preferences Version";
  private static final String NUMBERED_POSITIONS_KEY = "Numbered Positions";
  
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
    	File prefFile = new File(PREFERENCES_LOCATION);
    	if(!prefFile.exists()) {
    		try {
				prefFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
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
  
  private static boolean checkLineForLockPositionVersion(String line) {
	StringTokenizer st = new StringTokenizer(line);
    int day = 0;
    while(st.hasMoreTokens() && day < 5) {
      String token = st.nextToken();
      if( token.equals(YES) ) {
        String nextString = st.nextToken();
        try {
          Integer.parseInt(nextString);
          return true;
        }
        catch(NumberFormatException ee) {
          return false;
        }
      }
      day++;
    }
    return false;
  }
  
  private static int getVersion() {
    initInput();
    if (!fileIn.hasNextLine()) {
	    closeInput();
    	return 1;
    }
    String firstLine = fileIn.nextLine();
    if (firstLine.contains(FILE_VERSION_KEY)) {
    	String[] split = firstLine.split(":");
    	if (split.length < 2) {
    		JOptionPane.showMessageDialog(null, firstLine, "ERROR in preferences", JOptionPane.ERROR_MESSAGE);
    	}
    	String versionString = split[1].strip();
    	try {
    		return Integer.parseInt(versionString);
    	}
    	catch (NumberFormatException e) {
    		JOptionPane.showMessageDialog(null, firstLine, "NumberFormatException in preferences", JOptionPane.ERROR_MESSAGE);
    		throw e;
    	}
    }
    else {
    	if (checkLineForLockPositionVersion(firstLine)) {
    	    closeInput();
    		return 2;
    	}
        while(fileIn.hasNext()) {
        	if (checkLineForLockPositionVersion(fileIn.nextLine())) {
        	    closeInput();
        		return 2;
        	}
        }
        closeInput();
        return 1;
    }
  }
  
  public static void readSettings(Settings settings) {
    
	int version = getVersion();
    if (version < 3) {
    	initInput();
    }
    
    settings.useNumberedPositions = false;
    
    if (version >= 3) {
    	String numberedPositionsLines = fileIn.nextLine();
    	String[] split = numberedPositionsLines.split(":");
    	if (split[0].contains(NUMBERED_POSITIONS_KEY) && split.length == 2) {
    		settings.useNumberedPositions = split[1].contains(YES);
    	}
    }
    
    int day = 0;
    Employee e = null;
    while(fileIn.hasNext()) {
      if( day == 0 ) {
        e = new Employee("Temp");
      }
      String token = fileIn.next();
      if( token.equals(YES) ) {
        // employee constructor sets all days to available by default
        if( version >= 2 ) {
          String lockedString = fileIn.next();
          try {
            int lockedPosition = Integer.parseInt(lockedString);
            e.lockedPosition(day, lockedPosition);
          }
          catch(NumberFormatException ee) {
            ee.printStackTrace();
            JOptionPane.showMessageDialog(null, "num format error");
          }
        }
      }
      else if( token.equals(NO) ) {
        e.toggleAvailable(day);
      }
      day++;
      if( day == 5 ) {
        String name = fileIn.nextLine();
        name = name.trim();
        e.setName(name);
        settings.employees.add(e);
        day = 0;
      }
    }
    closeInput();
  }
  
  public static void writeSettings(Settings settings) {
    initOutput();
    fileOut.println(FILE_VERSION_KEY + ": " + FILE_VERSION);
    fileOut.println(NUMBERED_POSITIONS_KEY + ": " + (settings.useNumberedPositions ? YES : NO));
    for( Employee e : settings.employees ) {
      writeEmployee(e);
    }
    closeOutput();
  }
  
  private static void writeEmployee(Employee e) {
    
    for( int day = 0; day < 5; day++ ) {
      if( e.available(day) ) {
        fileOut.print(YES + " " + e.getLockedPosition(day) + " ");
      }
      else {
        fileOut.print(NO + " ");
      }
    }
    fileOut.println(e.getName());
  }
}
