package main;

import java.util.*;

public class Assigner {
  
  private static final int NOBODY = -1;
  
  public static int NUM_POSITIONS = 6;

  public Assigner() {
    
  }

  public String checkIfPossible(Day[][] days, List<Employee> employees) {
    int[] numAvailablePerDay = new int[5];
    for(Employee employee : employees) {
      for(int day = 0; day < numAvailablePerDay.length; day++) {
        if(employee.available(day)) {
          numAvailablePerDay[day]++;
        }
      }
    }
    LinkedList<String> failedDays = new LinkedList<>();
    for(int day = 0; day < numAvailablePerDay.length; day++) {
      if(numAvailablePerDay[day] < Assigner.NUM_POSITIONS) {
        failedDays.add(days[0][day].getName());
      }
    }
    return null;
  }
  public String combineListEnglish(List<String> list) {
    if(list.isEmpty()) {
      return "";
    }
    if(list.size() == 1) {
      return list.get(0);
    }
    if(list.size() == 2) {
      return list.get(0) + " and " + list.get(1);
    }
    String combined = "";
    while(list.size() > 1) {
      combined += list.remove(0) + ", ";
    }
    combined += "and " + list.remove(0);
    return combined;
  }
  
    public Day[][] generateSchedule(Day[][] days, List<Employee> employees) {
        for (int week = 0; week < days.length; week++) {
            for (int day = 0; day < days[0].length; day++) {
                days[week][day].clearAssignments();
            }
        }
        for (int day = 0; day < days[0].length; day++) {
            for (Employee employee : employees) {
                if (employee.available(day)) {
                    for (int week = 0; week < days.length; week++) {
                        days[week][day].assign(employee);
                    }
                }
            }
        }
        int maxPositions = 0;
        for (int day = 0; day < days[0].length; day++) {
            maxPositions = Math.max(maxPositions, days[1][day].getAssignments().size());
        }
        NUM_POSITIONS = maxPositions;
        return days;
    }
  
  public boolean assignmentAllowed(int id, int week, int day, Node[][] map) {
    int start = 0;
    int end = 5;
    if( day <=3 ) {
      end = 4;
    }
    if( day > 3 ){
      start = 1;
    }
    for( int d = start; d < end; d++ ) {
      if( map[week][d].getAssignment(map[week][day].numAssigned) == id ) {
        System.out.println("Blocked 2 in row position");
        return false;
      }
    }
    return true;
  }
  
  public class Node {
    int[] assignments;
    int numAssigned;
    
    public Node() {
      assignments = new int[NUM_POSITIONS];
      for( int i = 0; i < assignments.length; i++ ) {
        assignments[i] = NOBODY;
      }
      
      numAssigned = 0;
    }
    
    public void assign(int id) {
      assignments[numAssigned] = id;
      numAssigned++;
    }
    
    public void assignAll(int id) {
      for( numAssigned = 0; numAssigned < NUM_POSITIONS; numAssigned++ ) {
        assignments[numAssigned] = id;
      }
    }
    public int getAssignment(int position) {
      return assignments[position];
    }
  }
}
