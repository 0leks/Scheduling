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
    if(failedDays.size() == 0) {
      return null;
    }
    else {
        return null;
      //return "Not enough employees available for: " + combineListEnglish(failedDays) + ".";
    }
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
    
    TestAssignment test = new TestAssignment();
    Variable[][][] result = test.generate(employees);
    for( int week = 0; week < days.length; week++ ) {
      for( int day = 0; day < result[0].length; day++ ) {
        days[week][day].clearAssignments();
      }

      for( int day = 0; day < result[0].length; day++ ) {
        for( int position = 0; position < result[0][day].length; position++ ) {
          Employee e = result[week][day][position].getAssignment();
          days[week][day].assign(e);
//          for( int day2 = day+1; day2 < result[0].length; day2++) {
//            days[week][day2].assign(e);
//          }
//            days[week][day].assign(result[0][day][(position + week*3 )%result[0][day].length].getAssignment());
//          }
        }
      }
    }
    return days;
    
//    // Initialize the nodes of the map
//    Node[][] map = new Node[5][5];
//    for( int week = 0; week < 5; week++ ) {
//      for( int day = 0; day < 5; day++ ) {
//        Node node = new Node();
//        if( days[week][day].isHoliday() ) {
//          node.assignAll(NOBODY);
//        }
//        map[week][day] = node;
//      }
//    }
//    
//    for( int day = 0; day < 5; day++ ) {
//      List<Employee> available = new ArrayList<Employee>();
//      for( Employee e : employees ) {
//        if( e.available(day) ) {
//          available.add(e);
//        }
//      }
//      int numAvailable = available.size();
//
//      for( int week = 0; week < 5; week++ ) {
//        
//        if( !days[week][day].isHoliday() ) {
//          List<Employee> assign = new ArrayList<Employee>();
//          List<Employee> reassign = new ArrayList<Employee>();
//          
//          if( numAvailable < 5 ) {
//            JOptionPane.showMessageDialog(null, "Only " + numAvailable + " people available on " + Day.getNameofDay(day), "ERROR!", JOptionPane.ERROR_MESSAGE);
//          }
//          assign.addAll(available);
//          while( assign.size() > 5 ) {
//            assign.remove((int)(Math.random()*assign.size()));
//          }
//          
//          for( int i = 0; i < 5; i++ ) {
//            Employee toAssign = assign.remove((int)(Math.random()*assign.size()));
//            int indexOf = employees.indexOf(toAssign);
//            
//            while( !assignmentAllowed(indexOf, week, day, map)  && assign.size() > 0) {
//              reassign.add(toAssign);
//              toAssign = assign.remove((int)(Math.random()*assign.size()));
//              indexOf = employees.indexOf(toAssign);
//            } 
//            map[week][day].assign(indexOf);
//            assign.addAll(reassign);
//            reassign.clear();
//          }
//        }
//      }
//    }
//    
//    
//    Day[][] newDays = new Day[5][5];
//    for( int week = 0; week < 5; week++ ) {
//      for( int day = 0; day < 5; day++ ) {
//        Day oldDay = days[week][day];
//        Day newDay = new Day(oldDay);
//        if( !oldDay.isHoliday() ) {
//          //System.out.println("Transferring");
//          for( int i = 0; i < 5; i++ ) {
//            int id = map[week][day].assignments[i];
//            Employee assignee = employees.get(id);
//            newDay.assign(assignee);
//          }
//        }
//        newDays[week][day] = newDay;
//      }
//    }
//    return newDays;
    
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
