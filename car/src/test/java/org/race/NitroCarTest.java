package org.race;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.race.error.NotInRaceException;
import org.race.util.TestConstants;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class NitroCarTest {

  @Test
  public void shouldNotBeAbleToCreateACarWithIllegalTeamNumber() {
    testIllegalTeamNumber(0, 1);
    testIllegalTeamNumber(-1, 1);
    testIllegalTeamNumber(2, 1);
  }

  /**
   * illegal team count is captured by the code that validates the team number because a legal team
   * number will always be greater than illegal team count (0 or -ve numbers)
   */
  @Test
  public void shouldNotBeAbleToCreateACarWithIllegalTeamCount() {
    testIllegalTeamCount(0);
    testIllegalTeamCount(-1);
  }

  @Test
  public void shouldSetCorrectTopSpeed() {
    NitroCar c = new NitroCar(1, 1, 1);
    assertEquals(c.TOP_SPEED, (150 + 10.0 * 1) * 1000.0 / (60.0 * 60.0),
        TestConstants.DOUBLE_PRECISION);
  }

  @Test
  public void shouldSetCorrectAcceleration() {
    NitroCar c = new NitroCar(2, 1, 2);
    assertEquals(c.ACCELERATION, 2 * 2, TestConstants.DOUBLE_PRECISION);
  }

  @Test
  public void shouldSetCorrectInitialDistance() {
    NitroCar c = new NitroCar(2, 1, 2);
    assertEquals(c.getDistanceLeft(), 1 + 200 * (2 - 1), TestConstants.DOUBLE_PRECISION);
  }

  @Test
  public void shouldHaveNitroAvailableInitially() {
    NitroCar c = new NitroCar(1, 1, 1);
    assertTrue(c.isNitroAvailable());
  }

  @Test
  public void shouldNotHaveNitroAfterOneUse() {
    NitroCar c = new NitroCar(1, 1, 1);
    c.applyBoost();
    assertFalse(c.isNitroAvailable());
  }

  @Test
  public void shouldNotApplyNitroWhenOutOfRace() {
    NitroCar c = new NitroCar(1, 0, 1); //0 trackLength makes the car out of race
    c.applyBoost();
    assertTrue(c.isNitroAvailable());
  }

  @Test
  public void shouldDoubleTheSpeedWhenTopSpeedIsGreaterWhenNitroIsApplied()
      throws NotInRaceException {
    NitroCar c = new NitroCar(1, 10000, 2);
    c.race(2);
    double currentSpeed = c.getSpeed();
    c.applyBoost();
    assertEquals(currentSpeed * 2, c.getSpeed(), TestConstants.DOUBLE_PRECISION);
  }

  @Test
  public void shouldAttainTopSpeedWhenTopSpeedIsLessThanDoubledSpeedWhenNitroIsApplied()
      throws NotInRaceException {
    NitroCar c = new NitroCar(1, 1000, 2);
    while (c.getSpeed() * 2 < c.TOP_SPEED) {
      c.race(2);
    }
    c.applyBoost();
    assertEquals(c.TOP_SPEED, c.getSpeed(), TestConstants.DOUBLE_PRECISION);
    c.race(2); //race again which shouldn't increase the speed anymore
    assertEquals(c.TOP_SPEED, c.getSpeed(), TestConstants.DOUBLE_PRECISION);
  }

  @Test
  public void shouldNotApplyHFWhenNotInRace() throws NotInRaceException {
    NitroCar c = new NitroCar(1, 0, 1);
    double oldSpeed = c.getSpeed();
    c.applyHF();
    assertEquals(oldSpeed, c.getSpeed(), TestConstants.DOUBLE_PRECISION);
    c.assess();
    assertEquals(oldSpeed, c.getSpeed(), TestConstants.DOUBLE_PRECISION);
  }

  @Test
  public void shouldReduceSpeedWhenHFIsApplied() throws NotInRaceException {
    NitroCar c = new NitroCar(1, 1000, 1);
    c.race(2);
    c.race(2);
    double oldSpeed = c.getSpeed();
    c.applyHF();
    assertEquals(oldSpeed * NitroCar.HF, c.getSpeed(), TestConstants.DOUBLE_PRECISION);
  }

  @Test
  public void shouldNotRaceWhenCarIsAlreadyFinished() {
    Car c = createOutOfRaceCar();
    try {
      c.race(2);
      fail();
    } catch (Exception e) {
      assertTrue(e instanceof NotInRaceException);
    }

    c = createNewCar();
    try {
      c.race(1); //exhaust the distance to cover (make it out of race)
      c.race(2);
      fail();
    } catch (Exception e) {
      assertTrue(e instanceof NotInRaceException);
    }
  }

  @Test
  public void shouldReduceRemainingDistanceCorrectlyWhenRacing() throws NotInRaceException {
    NitroCar c = new NitroCar(1, 1000, 1);
    c.race(2);
    double oldDistance = c.getDistanceLeft();
    double oldSpeed = c.getSpeed();
    c.race(2);
    assertEquals(oldDistance - (oldSpeed * 2 + c.ACCELERATION * 2 * 2 / 2),
        c.getDistanceLeft(), TestConstants.DOUBLE_PRECISION);
  }

  @Test
  public void shouldIncreseSpeedCorrectlyWhenRacing() throws NotInRaceException {
    NitroCar c = new NitroCar(1, 1000, 1);
    c.race(2);
    double oldSpeed = c.getSpeed();
    c.race(2);
    assertEquals(oldSpeed + c.ACCELERATION * 2, c.getSpeed(), TestConstants.DOUBLE_PRECISION);
  }

  @Test
  public void shouldIncreseRaceTimeCorrectlyWhenRacing() throws NotInRaceException {
    NitroCar c = new NitroCar(1, 1000, 1);
    c.race(2);
    assertEquals(2, c.getDuration());
    c.race(3);
    assertEquals(5, c.getDuration());
  }

  @Test
  public void shouldReturnTrueWhenZeroOrNegativeDistanceToBeCovered() {
    NitroCar c = new NitroCar(1, 0, 1);
    assertTrue(c.isFinished());
    c = new NitroCar(1, -1, 1);
    assertTrue(c.isFinished());
    c = new NitroCar(1, 2, 1);
    assertFalse(c.isFinished());
  }

  @Test
  public void shouldReturnTrueForSameTeamCarForEquals() {
    NitroCar c1 = new NitroCar(1, 0, 1);
    NitroCar c2 = new NitroCar(1, 10, 2);
    assertTrue(c1.equals(c2));
    assertFalse(c1.equals(new NitroCar(2, 0, 2)));
  }

  @Test
  public void shouldReturnSameHashcodeForSameTeamCars() {
    NitroCar c1 = new NitroCar(1, 0, 1);
    NitroCar c2 = new NitroCar(1, 10, 2);
    assertEquals(c1.hashCode(), c2.hashCode());
  }

  private void testIllegalTeamNumber(int teamNumber, int teamCount) {
    try {
      new NitroCar(teamNumber, -1, teamCount);
      fail();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  private void testIllegalTeamCount(int teamCount) {
    try {
      new NitroCar(1, -1, teamCount);
      fail();
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
    }
  }

  private Car createNewCar() {
    return new NitroCar(1, 1, 1);
  }

  private Car createOutOfRaceCar() {
    return new NitroCar(1, 0, 1); //0 track length is OutOfRaceCar
  }
}
