package main;

public class Employee {
  
  private String name;
  
  private boolean[] available;

  public Employee(String name) {
    this.name = name;
    available = new boolean[]{true, true, true, true, true};
  }

  public boolean available(int day) {
    return available[day];
  }
  
  public void toggleAvailable(int day) {
    available[day] = !available[day];
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
