package main;

import java.util.*;

public class Assigner {
  
  private static final int NOBODY = -1;
  
  public static int NUM_POSITIONS = 12;

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
  
  class Bag {
    public List<Employee> employees;
    public Bag() {
      employees = new LinkedList<>();
    }
  }
  
//Function to return the next random number  
  static int getNum(ArrayList<Integer> v)  
  {  
      // Size of the vector  
      int n = v.size();  
    
      // Make sure the number is within  
      // the index range  
      int index = (int)(Math.random() * n);  
    
      // Get random number from the vector  
      int num = v.get(index);  
    
      // Remove the number from the vector  
      v.set(index, v.get(n - 1)); 
      v.remove(n - 1);  
    
      // Return the removed number  
      return num;  
  }  
//Function to generate n  
  // non-repeating random numbers  
  static LinkedList<Integer> generateRandomPermutation(int n)  
  {  
      ArrayList<Integer> v = new ArrayList<>(n);  
    
      // Fill the vector with the values  
      // 1, 2, 3, ..., n  
      for (int i = 0; i < n; i++)  
          v.add(i);  

      LinkedList<Integer> toret = new LinkedList<>();  
      // While vector has elements  
      // get a random number from the vector and print it  
      while (v.size() > 0)  
      {  
        toret.add(getNum(v));
      }  
      return toret;
  }  
  
  public List<Integer> getPossibleDays(Employee e, Bag[] bag, Bag[][] assignments, int position, int EMPLOYEES_PER_POSITION) {
    List<Integer> possDays = new LinkedList<>();
    for(int day = 0; day < bag.length; day++) {
      if(assignments[day][position].employees.contains(e) || assignments[day][position].employees.size() < EMPLOYEES_PER_POSITION) {
        possDays.add(day);
      }
    }
    return possDays;
  }
  public void assignToBestPosition(Employee e, Bag[] bag, Bag[][] assignments, int EMPLOYEES_PER_POSITION) {
    // find which position works the most
    int maxNum = -1;
    int bestPosition = -1;
    List<Integer> positions = generateRandomPermutation(assignments[0].length);
    while(!positions.isEmpty()) {
      int position = positions.remove((int)(Math.random()*positions.size()));
      int num = getPossibleDays(e, bag, assignments, position, EMPLOYEES_PER_POSITION).size();
      if(num > maxNum) {
        maxNum = num;
        bestPosition = position;
      }
    }
    System.out.println("assigning to " + bestPosition);
    for(int day = 0; day < bag.length; day++) {
      if(bag[day].employees.contains(e) && assignments[day][bestPosition].employees.size() < EMPLOYEES_PER_POSITION) {
        System.out.println("day " + day);
        assignments[day][bestPosition].employees.add(e);
        bag[day].employees.remove(e);
      }
    }
  }
  
  public Day[][] generateSchedule(Day[][] days, List<Employee> employees) {
    int NUM_DAYS = 5;
    Bag[] bag = new Bag[NUM_DAYS];
    for(int dayIndex = 0; dayIndex < NUM_DAYS; dayIndex++) {
      bag[dayIndex] = new Bag();
    }
    for(Employee e : employees) {
      for(int dayIndex = 0; dayIndex < NUM_DAYS; dayIndex++) {
        if(e.available(dayIndex)) {
          bag[dayIndex].employees.add(e);
        }
      }
    }
    int NUM_POSITIONS = Assigner.NUM_POSITIONS/2;
    int EMPLOYEES_PER_POSITION = 2;
    Bag[][] assignments = new Bag[NUM_DAYS][NUM_POSITIONS];
    for(int dayIndex = 0; dayIndex < NUM_DAYS; dayIndex++) {
      for(int position = 0; position < NUM_POSITIONS; position++) {
        assignments[dayIndex][position] = new Bag();
      }
    }
    // First assign employees that have a preferred position
    for(int day = 0; day < assignments.length; day++) {
      for(int employeeIndex = bag[day].employees.size()-1; employeeIndex >= 0; employeeIndex--) {
        Employee e = bag[day].employees.get(employeeIndex);
        if(e.isPositionLocked(day)) {
          assignments[day][e.getLockedPosition(day)].employees.add(e);
          bag[day].employees.remove(e);
          System.out.println("Assigned " + e.getName() + " to position " + e.getLockedPosition(day));
        }
      }
    }
    // then assign the rest of the employees to random positions
    List<Integer> possibleDays = new LinkedList<>();
    for(int i = 0; i < 5; i++) {
      possibleDays.add(i);
    }
    while(!possibleDays.isEmpty()) {
      int day = possibleDays.remove((int)(Math.random()*possibleDays.size()));
      System.out.println("Handling day " + day);
      while(!bag[day].employees.isEmpty()) {
        Employee e = bag[day].employees.get((int)(Math.random()*bag[day].employees.size()));
        System.out.println("Handling employee " + e.getName());
        assignToBestPosition(e, bag, assignments, EMPLOYEES_PER_POSITION);
      }
    }

    for( int week = 0; week < days.length; week++ ) {
      for( int day = 0; day < days[week].length; day++ ) {
        days[week][day].clearAssignments();
      }
    }
    for( int day = 0; day < assignments.length; day++ ) {
      for( int position = 0; position < assignments[day].length; position++ ) {
        for( int week = 0; week < days.length; week++ ) {
          for(Employee e : assignments[day][position].employees) {
            days[week][day].assign(e);
          }
          for(int extra = 0; extra < 2 - assignments[day][position].employees.size(); extra++) {
            days[week][day].assign(new Employee(""));
          }
        }
      }
    }
    
    
//    TestAssignment test = new TestAssignment();
//    Variable[][][] result = test.generate(employees);
//    for( int week = 0; week < days.length; week++ ) {
//      for( int day = 0; day < result[0].length; day++ ) {
//        days[week][day].clearAssignments();
//      }
//
//      for( int day = 0; day < result[0].length; day++ ) {
//        for( int position = 0; position < result[0][day].length; position++ ) {
//          Employee e = result[week][day][position].getAssignment();
//          days[week][day].assign(e);
////          for( int day2 = day+1; day2 < result[0].length; day2++) {
////            days[week][day2].assign(e);
////          }
////            days[week][day].assign(result[0][day][(position + week*3 )%result[0][day].length].getAssignment());
////          }
//        }
//      }
//    }
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
