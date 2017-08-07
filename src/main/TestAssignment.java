package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TestAssignment {

  public Variable[][][] generate(List<Employee> employees) {
    
    Variable[][][] variables = new Variable[1][5][5];
    
    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        for( int position = 0; position < variables[week][day].length; position++ ) {
          String nameofday = Day.getNameofDay(day);
          Variable var = new Variable(week + ":" + nameofday + ":" + position );
          //var.fillDomain(employees);
          for( Employee e : employees ) {
            if( e.available(day) ) {
              var.addToDomain(e);
            }
          }
          variables[week][day][position] = var;
          System.out.println(var);
        }
      }
    }
    
    Variable[][][] result = depthFirst(variables, 0);
    this.printVariables(result);
    return result;
  }
  
  public TestAssignment() {
    
  }
  
  public Variable[][][] depthFirst( Variable[][][] variables, int layer ) {
    System.out.println("Layer " + layer);
    //this.printVariables(variables);
    if( this.checkComplete(variables) ) {
      return variables;
    }
    
    while( true ) {
      Variable[][][] copy = copy(variables);
      Variable var = selectVariableToAssign(copy);
      Variable oldVar = findVariable(variables, var);
      
      List<Employee> domain = var.getDomain();
      Employee emp = selectValueToAssign(copy, var, domain);

      //System.out.println("                    Assigning " + emp + " to " + var);
      var.assign(emp);
      
      Edge[] edges = createEdges(copy);
      
      if( updateDomains(edges) ) {
        // need to backtrack
        //System.out.println("ERROR! impossible to continue! removing assignment from domain");
        oldVar.removeFromDomain(emp);
        //printVariables(variables);
      }
      else {
        Variable[][][] result = depthFirst(copy, layer+1);
        if( result == null ) {
          //System.out.println("ERROR! impossible to continue! removing assignment from domain");
          oldVar.removeFromDomain(emp);
          //printVariables(variables);
        }
        else {
          //System.out.println("}");
          return result;
        }
      }
      Edge[] oldEdges = createEdges(variables);
      if( updateDomains(oldEdges) ) {
        return null;
      }
    }
//    System.out.println("}");
//    return null;
  }
  
  public Variable findVariable( Variable[][][] variables, Variable var ) {

    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        for( int position = 0; position < variables[week][day].length; position++ ) {
          if( variables[week][day][position].getId() == var.getId() ) {
            return variables[week][day][position];
          }
        }
      }
    }
    return null;
  }
  
  public boolean updateDomains(Edge[] edges) {
    for( int b = 0; b < edges.length; b++ ) {
      for( int a = 0; a < edges.length; a++ ) {
        if( edges[a].updateDomains() ) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Edge[] createEdges(Variable[][][] variables) {
    Edge[] edges = new Edge[variables.length*10];
    
    for( int week = 0; week < variables.length; week++ ) {
      for( int position = 0; position < variables[week][0].length; position++ ) {
        AllDiffEdge edge = new AllDiffEdge("Week " + week + " Position " + position);
        List<Variable> list = new ArrayList<Variable>();
        for( int day = 0; day < variables[week].length; day++ ) {
          list.add(variables[week][day][position] );
        }
        edge.setVariables(list);
        edges[week*5 + position] = edge;
      }
    }
    
    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        AllDiffEdge edge = new AllDiffEdge("Week " + week + " Day " + day);
        List<Variable> list = new ArrayList<Variable>();
        for( int position = 0; position < variables[week][0].length; position++ ) {
          list.add(variables[week][day][position] );
        }
        edge.setVariables(list);
        edges[edges.length/2 + week*5 + day] = edge;
      }
    }
    return edges;
  }
  
  public Employee selectValueToAssign(Variable[][][] variables, Variable var, List<Employee> domain) {

    return domain.get(0);
  }
  
  public Variable selectVariableToAssign(Variable[][][] variables) {

    List<Variable> unassigned = new ArrayList<Variable>();
    
    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        for( int position = 0; position < variables[week][day].length; position++ ) {
          if( !variables[week][day][position].isAssigned() ) {
            unassigned.add(variables[week][day][position]);
            //return variables[week][day][position];
          }
        }
      }
    }
    if( unassigned.size() == 0 ) {
      return null;
    }
    else {
      return unassigned.get( (int)( Math.random()*unassigned.size() ) );
    }
  }
  
  public boolean checkComplete(Variable[][][] variables) {

    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        for( int position = 0; position < variables[week][day].length; position++ ) {
          if( !variables[week][day][position].isAssigned() ) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  public void printVariables(Variable[][][] variables) {
    System.out.println("~~~~~~~~~~~~ Variables ~~~~~~~~~~~~~~~");
    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        for( int position = 0; position < variables[week][day].length; position++ ) {
          System.out.println(variables[week][day][position]);
        }
      }
    }
  }
    
  
  public void printEdges(Object[] edges) {
    for( int a = 0; a < edges.length; a++ ) {
      System.out.println(edges[a]);
    }
  }
  
  public Variable[][][] copy(Variable[][][] variables) {
    Variable[][][] copy = new Variable[variables.length][variables[0].length][variables[0][0].length];
    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        for( int position = 0; position < variables[week][day].length; position++ ) {
          Variable var = variables[week][day][position].copy();
          copy[week][day][position] = var;
        }
      }
    }
    return copy;
  }
}
