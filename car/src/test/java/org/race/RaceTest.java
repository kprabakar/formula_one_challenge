package org.race;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.race.Car;
import org.race.Main;
import org.race.NitroCar;
import org.race.Race;
import org.race.util.TestConstants;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class RaceTest {

  @Test
  public void shouldReturnCorrectDistanceExitSpeedAndCompletionTimeForSingleCarRace() {
    Race race = createRaceWith(1, 0); //zero distance
    race.doRace();
    Car[] cars = race.getCars();
    assertEquals(cars[0].getDistanceLeft(), 0, TestConstants.DOUBLE_PRECISION);
    assertEquals(cars[0].getSpeed(), 0, TestConstants.DOUBLE_PRECISION);
    assertEquals(cars[0].getDuration(), 0, TestConstants.DOUBLE_PRECISION);

    race = createRaceWith(1, 1); //distance 1
    race.doRace();
    cars = race.getCars();
    assertTrue(cars[0].getDistanceLeft() <= 0);
    assertEquals(cars[0].getSpeed(), ((NitroCar) cars[0]).ACCELERATION * 2,
        TestConstants.DOUBLE_PRECISION);
    assertEquals(cars[0].getDuration(), 2, TestConstants.DOUBLE_PRECISION);

    race = createRaceWith(1, 100); //distance 100
    race.doRace();
    cars = race.getCars();
    assertTrue(cars[0].getDuration() > 2);
  }

  @Test
  public void testLastInRaceComparator() {
    Race race = createRaceWith(2, 100);
    Car[] cars = race.getCars();
    assertTrue(new Race.LastInRaceComparator().compare(cars[0], cars[1]) > 0);
  }

  private Race createRaceWith(int teamCount, double trackLength) {
    Race race = new Race(teamCount, trackLength);
    race.setCars(Main.createCars(teamCount, trackLength));
    return race;
  }
}
