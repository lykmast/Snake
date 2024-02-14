package com.lykmast.Snake;

import java.util.List;
import java.util.Random;

public abstract class ListTools<T> {
  public static <T> T getRandomElement(List<T> l){
    Random rnd = new Random();
    int i = rnd.nextInt(l.size());
    return l.get(i);
  }
}
