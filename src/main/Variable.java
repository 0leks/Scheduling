package main;

import java.util.ArrayList;
import java.util.List;

public class Variable {

  private List<Employee> domain;
  private String name;
  private List<Employee> removed;
  private int id;
  private static int idcounter = 0;
  
  public Variable( String name ) {
    domain = new ArrayList<Employee>();
    removed = new ArrayList<Employee>();
    this.name = name;
    id = idcounter++;
  }
  
  public Variable copy() {
    Variable var = new Variable(name);
    var.fillDomain(domain);
    var.id = this.id;
    return var;
  }
  
  public int getId() {
    return id;
  }
  
  public void addToDomain( Employee e ) {
    domain.add(e);
  }
  
  public void fillDomain(List<Employee> employees) {
    domain.addAll(employees);
  }
  
  public Employee getAssignment() {
    return domain.get(0);
  }
  
  public List<Employee> getDomain() {
    return domain;
  }
  
  public void removeFromDomain( Employee e ) {
    domain.remove(e);
  }
  
  public void unAssign() {
    domain.clear();
    domain.addAll(removed);
    removed.clear();
  }
  public void assign(Employee e) {
    removed.clear();
    removed.addAll(domain);
    domain.clear();
    domain.add(e);
  }
  
  public boolean isImpossible() {
    return domain.size() == 0;
  }
  
  public boolean isAssigned() {
    return domain.size() == 1;
  }
  
  @Override
  public String toString() {
    String string = name + " [";
    boolean first = true;
    for( Employee e : domain ) {
      if( !first ) {
        string += ", ";
      }
      string += e;
      if( first ) {
        first = false;
      }
    }
    string += "]";
    return string;
  }
}
