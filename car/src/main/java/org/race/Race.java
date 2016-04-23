package org.race;

import org.race.error.NotInRaceException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Assumptions:
 * 1. Applying HF factor reduces speed instantly (no distance is travelled while the speed changes)
 * 2. Applying nitro increases the speed instantly
 * 3. Based on assumption 1, determining the last car in the race can be done either before
 * applying HF factor or after because the distance wouldn't have changed
 * <p>
 * 4. Cars that crossed the finish line already, will not apply HF factor or nitro
 * 5. No chance for more than one cars to be at the last position
 * 6. By the time Nitro is applied, that car's speed is already reduced by HF
 * 7. For 0 or negative track length, the race still happens computationally and the race ends
 * for first car at least after first 2 seconds
 *
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class Race {
  private Car[] cars;
  private int teamCount;
  private double trackLength;

  Race(int teamCount, double trackLength) {
    this.teamCount = teamCount;
    this.trackLength = trackLength;
  }

  void doRace() {
    Set<Car> carsInRace = new HashSet<Car>();
    carsInRace.addAll(Arrays.asList(this.cars));

    while (!carsInRace.isEmpty()) {

      Iterator<Car> iterator = carsInRace.iterator();
      Car iCar = null;

      //parallel stream
      Set<Car> exitedCars = new HashSet<>();
      carsInRace.parallelStream().forEach(c -> {
        try {
          c.race(2);
        } catch (NotInRaceException e) {
          e.printStackTrace();
        }
      });
      while (iterator.hasNext()) { //TODO - do this using parallel stream
        iCar = iterator.next();
        try {
          iCar.race(2); //race for 2 seconds every time
        } catch (NotInRaceException e) {
          iterator.remove();
          continue;
        }
        if(iCar.getDistanceLeft() <= 0) {
          iterator.remove();
        }
      }

      /**
       * do the assessment after 2sec racing so that cars that finished need not assess themselves
       */
      carsInRace.forEach(c -> c.assess());

      /**
       * find last car (findLastCar) - use Comparator on remainingDistance since HF
       * is applied instantly and will not impact the current distance travelled, you can
       * find the last car before applying HF or after applying it; it doesn't matter.
       */
      Car lastCar = findLastCar(carsInRace);

      /**
       * assessment: after driver notices that his car is last (which means his speed has
       * reduced already because of HF factor), then only he applies nitro
       */
      if (lastCar != null) {
        lastCar.applyBoost();
      }
    }

    for (Car car : cars) {
      System.out.println(String.format("org.race.Car %d finshed with Speed=%f m/s in %d seconds", car
          .getNumber(), car.getSpeed(), car.getDuration()));
    }
  }

  public void setCars(Car[] cars) {
    this.cars = cars;
  }

  public Car[] getCars() {
    return this.cars;
  }

  private static Car findLastCar(Set<Car> cars) {
    PriorityQueue<Car> queue = new PriorityQueue<Car>(new LastInRaceComparator());
    queue.addAll(cars);
    return queue.poll();
  }

  static class LastInRaceComparator implements Comparator<Car> {
    public int compare(Car car1, Car car2) {
      //car last in the race comes first in sorting
      return (int) (car2.getDistanceLeft() - car1.getDistanceLeft());
    }
  }
}
