package org.race;

import org.race.error.NotInRaceException;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public interface Car {

  /**
   * org.race.Race the car for the given amount of time. This can be called repeatedly with different t.
   * Throws org.race.error.NotInRaceException when called on a car that has crossed the finish line already
   *
   * @param t - time in seconds
   */
  void race(long t) throws NotInRaceException;

  void assess();

  void applyBoost();

  int getNumber();

  /**
   * Says whether the car has crossed the finish line or not
   *
   * @return true - finished the race
   */
  boolean isFinished();

  double getSpeed();

  double getDistanceLeft();

  /**
   * Duration for which the car has been racing
   *
   * @return - time in seconds
   */
  long getDuration();
}
