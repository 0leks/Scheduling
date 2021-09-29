package main;

public class AllDiffEdge extends Edge {
  
  public AllDiffEdge(String name) {
    super(name);
  }
  
  /** 
   * 
   * @return true if a domain was reduced to size 0
   */
  @Override
  public boolean updateVariables() {
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
