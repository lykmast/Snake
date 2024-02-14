package com.lykmast.Snake;

enum Direction {
  NORTH,
  SOUTH,
  WEST,
  EAST;

  static Direction reverse(Direction direction){
    switch (direction) {
      case NORTH:
        return Direction.SOUTH;
      case EAST:
        return Direction.WEST;
      case SOUTH:
        return Direction.NORTH;
      case WEST:
        return Direction.EAST;
      default:
        throw new IllegalArgumentException("Unknown fifth direction: " + direction);
    }
  }
}