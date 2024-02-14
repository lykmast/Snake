package com.lykmast.Snake;

import java.util.ArrayList;
import java.util.Collection;


class Board {
  private Position dimensions;
  
  Board (int dimX, int dimY){
    dimensions = new Position(dimX, dimY);
  }

  Position getRandomPositionExcluding(Collection<Position> exclude) {    
    ArrayList<Position> ps = cartesianPositions(dimensions.x, dimensions.y);
    ps.removeAll(new ArrayList<>(exclude));
    return ListTools.getRandomElement(ps);
  }


  Position getCenter(){
    return new Position(dimensions.x / 2, dimensions.y / 2); 
  }

  boolean inBounds(Position pos){
     return (
         pos.x < dimensions.x 
      && pos.y < dimensions.y
      && pos.x >= 0
      && pos.y >= 0
      );
  }
  
  private ArrayList<Position> cartesianPositions(int xx, int yy) {
    ArrayList<Position> res = new ArrayList<Position>();
    for (int i = 0; i < xx; i++) {
      for (int j = 0; j < yy; j++) {
        res.add(new Position(i, j));
      }
      
    }
    return res;

  }
}
