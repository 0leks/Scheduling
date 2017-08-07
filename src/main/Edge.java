package main;

import java.util.ArrayList;
import java.util.List;

public abstract class Edge {

  protected List<Variable> variables;
  private String name;
  
  public Edge(String name ) {
    this.name = name;
    variables = new ArrayList<Variable>();
  }
  
  public void setVariables(List<Variable> variables) {
    this.variables.addAll(variables);
  }
  
  /** 
   * 
   * @return true if a domain was reduced to size 0
   */
  public abstract boolean updateDomains();

  @Override
  public String toString() {
    String string = name + " [";
    for( Variable v : variables ) {
      string += v + ", ";
    }
    string += "]";
    return string;
  }
  
}
