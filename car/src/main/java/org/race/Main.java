package org.race;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class Main {
  public static void main(String[] args) {
    int teamCount = 40;
    double trackLength = -250;
    Race race = new Race(teamCount, trackLength);
    race.setCars(createCars(teamCount, trackLength));//dependency inversion
    race.doRace();
  }

  public static Car[] createCars(int teamCount, double trackLength) {
    Car[] allCars = new Car[teamCount];

    for (int i = 0; i < teamCount; i++) {
      allCars[i] = new NitroCar(i + 1, trackLength, teamCount);
    }
    return allCars;
  }
}
