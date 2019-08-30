package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

public class TestAssignment {
  
  private Random rand = new Random(System.currentTimeMillis());
  
  private Employee currentEmployee;
  private Dat currentDat;

  public void validateEmployeePreferences(List<Employee> employees) {
    // asdf TODO
  }
  
  public Variable[][][] generate(List<Employee> employees) {
    
    Variable[][][] variables = new Variable[5][5][Assigner.NUM_POSITIONS];
    
    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        for( int position = 0; position < variables[week][day].length; position++ ) {
          String nameofday = Day.getNameofDay(day);
          Variable var = new Variable(week + ":" + nameofday + ":" + position );
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
    
    // clean out the locked positions
    for( int day = 0; day < variables[0].length; day++ ) {
      List<Employee> lockedEmployees = hasLockedPositions(employees, day);
      for(Employee locked : lockedEmployees) {
        for( int week = 0; week < variables.length; week++ ) {
          variables[week][day][locked.getLockedPosition(day)].removeAllFromDomainExcept(locked);
          for( int position = 0; position < variables[week][day].length; position++ ) {
            if( position != locked.getLockedPosition(day) ) {
              variables[week][day][position].removeFromDomain(locked);
            }
          }
        }
      }
    }
    System.out.println("After pruning locked positions:");
    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        for( int position = 0; position < variables[week][day].length; position++ ) {
          System.out.println(variables[week][day][position]);
        }
      }
    }
    
    Variable[][][] result = depthFirst(variables, 0);
    this.printVariables(result);
    return result;
  }
  
  private List<Employee> hasLockedPositions(List<Employee> employees, int day) {
    LinkedList<Employee> locked = new LinkedList<>();
    for(Employee e : employees) {
      if(e.isPositionLocked(day)) {
        locked.add(e);
      }
    }
    return locked;
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
      Dat dat = selectVariableToAssign(copy);
      Variable var = copy[dat.week][dat.day][dat.position];
      Variable oldVar = findVariable(variables, var);
      
      Employee emp = currentEmployee;
      if( emp == null ) {
        List<Employee> domain = var.getDomain();
        emp = selectValueToAssign(copy, var, domain);
      }

      //System.out.println("                    Assigning " + emp + " to " + var);
      var.assign(emp);
      
      currentEmployee = emp;
      currentDat = dat;
      
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
    // Commented out edges disallowing employee with the same position multiple times in a week
    
    //Edge[] edges = new Edge[variables.length*Assigner.NUM_POSITIONS + variables.length*variables[0].length];
    Edge[] edges = new Edge[variables.length*variables[0].length];
    
//    for( int week = 0; week < variables.length; week++ ) {
//      for( int position = 0; position < variables[week][0].length; position++ ) {
//        AllDiffEdge edge = new AllDiffEdge("Week " + week + " Position " + position);
//        List<Variable> list = new ArrayList<Variable>();
//        for( int day = 0; day < variables[week].length; day++ ) {
//          list.add(variables[week][day][position] );
//        }
//        edge.setVariables(list);
//        edges[week*Assigner.NUM_POSITIONS + position] = edge;
//      }
//    }
    
    for( int week = 0; week < variables.length; week++ ) {
      for( int day = 0; day < variables[week].length; day++ ) {
        AllDiffEdge edge = new AllDiffEdge("Week " + week + " Day " + day);
        List<Variable> list = new ArrayList<Variable>();
        for( int position = 0; position < variables[week][0].length; position++ ) {
          list.add(variables[week][day][position] );
        }
        edge.setVariables(list);
        //edges[variables.length*Assigner.NUM_POSITIONS + week*5 + day] = edge;
        edges[week*5 + day] = edge;
      }
    }
    return edges;
  }
  
  public Employee selectValueToAssign(Variable[][][] variables, Variable var, List<Employee> domain) {

    return domain.get(0);
  }
  
  public Dat selectVariableToAssign(Variable[][][] variables) {

    
    if( currentEmployee != null) {
      for(int day = currentDat.day + 1; day < variables[0].length; day++) {
        if(!variables[currentDat.week][day][currentDat.position].isAssigned() ) {
          if(variables[currentDat.week][day][currentDat.position].getDomain().contains(currentEmployee)) {
            return new Dat(currentDat.week, day, currentDat.position);
          }
        }
      }
      currentEmployee = null;
    }
    List<Dat> unassigned = new ArrayList<Dat>();
    
    for( int day = 0; day < variables[0].length; day++ ) {
      for( int week = 0; week < variables.length; week++ ) {
        for( int position = 0; position < variables[week][day].length; position++ ) {
          if( !variables[week][day][position].isAssigned() ) {
            unassigned.add(new Dat(week, day, position));
            //unassigned.add(variables[week][day][position]);
            //return variables[week][day][position];
          }
        }
      }
      if( unassigned.size() > 0 ) {
        return unassigned.get( rand.nextInt(unassigned.size()) );
      }
    }
    if( unassigned.size() == 0 ) {
      System.err.println("Error, 0 unassigned employees remaining");
      JOptionPane.showMessageDialog(null, "Oops, an error occurred. Please Generate again.");
      return null;
    }
    else {
      return unassigned.get( rand.nextInt(unassigned.size()) );
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
