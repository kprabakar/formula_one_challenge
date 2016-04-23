package org.race;

import org.race.error.NotInRaceException;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class NitroCar implements Car {
  public static final double HF = 0.8; // handling factor

  public final int TEAM_NUMBER;
  public final double TOP_SPEED;
  public final double ACCELERATION;

  private double remainingDistance;
  private boolean isNitroAvailable = true;
  private double currentSpeed = 0.0;

  private long totalRaceTime;

  NitroCar(int teamNumber, double trackLength, int totalTeams) {
    if (teamNumber < 1 || teamNumber > totalTeams) {
      throw new IllegalArgumentException("org.race.Car number can only from 1 to " + totalTeams);
    }

    this.TEAM_NUMBER = teamNumber;
    TOP_SPEED = (150 + 10.0 * teamNumber) * 1000.0 / (60.0 * 60.0); //speed in m/s
    ACCELERATION = 2 * teamNumber;
    remainingDistance = trackLength + 200 * (teamNumber - 1); //distance to cover at the start
  }

  /**
   * When nitro is applied, it is assumed that the speed increases instantly there by no distance
   * is travelled by the car during the interim time when the speed changes
   */
  public void applyBoost() {
    if (isFinished()) {
      return;
    }
    if (isNitroAvailable) {
      currentSpeed = (currentSpeed * 2 > TOP_SPEED) ? TOP_SPEED : currentSpeed * 2;
      isNitroAvailable = false;
    }
  }

  public void applyHF() {
    if (!isFinished()) {
      currentSpeed *= NitroCar.HF;
    }
  }

  @Override
  public void race(long duration) throws NotInRaceException {
    if (isFinished()) {
      throw new NotInRaceException();
    }
    /**
     * before inspection: calculate remainingDistance & currentSpeed (preAssessmentRace)
     * d = v1 * t + 1/2 * a * t^2, where t=2s always
     */
    remainingDistance -= currentSpeed * duration + ACCELERATION * duration * duration / 2;

    //vf = vi + at
    double newSpeed = currentSpeed + ACCELERATION * 2;
    if (newSpeed < TOP_SPEED) {
      currentSpeed = newSpeed;
    } else {
      currentSpeed = TOP_SPEED;
    }
    totalRaceTime += duration;
  }

  /**
   * assessment: speed adjustment with HF; HF is applied instantly
   */
  @Override
  public void assess() {
    applyHF();
  }

  @Override
  public boolean isFinished() {
    return remainingDistance <= 0;
  }

  @Override
  public double getSpeed() {
    return currentSpeed;
  }

  @Override
  public double getDistanceLeft() {
    return remainingDistance;
  }


  @Override
  public long getDuration() {
    return totalRaceTime;
  }

  @Override
  public int getNumber() {
    return TEAM_NUMBER;
  }

  public boolean isNitroAvailable() {
    return isNitroAvailable;
  }

  @Override
  public boolean equals(Object other) {
    if(other instanceof NitroCar && other != null) {
      return TEAM_NUMBER == ((NitroCar) other).TEAM_NUMBER;
    }
    return false;
  }

  public int hashCode() {
    return TEAM_NUMBER;
  }

}
