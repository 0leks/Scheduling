package main;

import java.util.ArrayList;
import java.util.List;

public class AllDiffEdge extends Edge {
  
  public AllDiffEdge(String name) {
    super(name);
  }
  
  /** 
   * 
   * @return true if a domain was reduced to size 0
   */
  @Override
  public boolean updateDomains() {
    for( Variable v : variables ) {
      if( v.isAssigned() ) {
        
        for( Variable affected : variables ) {
          if( affected != v ) {
            affected.removeFromDomain( v.getAssignment() );
            
            if( affected.isImpossible() ) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
}
