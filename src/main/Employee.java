package main;

public class Employee {
  
  private String name;
  
  private boolean[] available;
  
  /**
   * size 5 array denoting which position this employee requires for each day of the week.
   * -1 indicates no preference.
   */
  private int[] lockedPositions;

  public Employee(String name) {
    this.name = name;
    available = new boolean[]{true, true, true, true, true};
    lockedPositions = new int[] {-1, -1, -1, -1, -1};
  }

  public boolean available(int day) {
    return available[day];
  }
  
  public void toggleAvailable(int day) {
    available[day] = !available[day];
    if(!available[day]) {
      clearLockedPosition(day);
    }
  }
  
  public void lockedPosition(int day, int position) {
    lockedPositions[day] = position;
  }
  public void lockedPositions(int[] days) {
    for(int day = 0; day < days.length; day++ ) {
      lockedPosition(day, days[day]);
    }
  }
  public void clearLockedPosition(int day) {
    lockedPosition(day, -1);
  }
  
  public boolean isPositionLocked(int day) {
    return lockedPositions[day] != -1;
  }
  public int getLockedPosition(int day) {
    return lockedPositions[day];
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return name;
  }
 
}
