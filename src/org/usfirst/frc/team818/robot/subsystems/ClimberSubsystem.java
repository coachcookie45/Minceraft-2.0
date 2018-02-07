/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team818.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ClimberSubsystem extends Subsystem {

	private Talon cMotor;
	private int cMotorPort;
	private DoubleSolenoid piston;
	private PowerDistributionPanel pdp;
	private boolean climberEnabled;

	public ClimberSubsystem(int climberMotorPort, int[] climberPistonPort, boolean climberEnabled) {
	
		this.climberEnabled = climberEnabled;

		if (climberEnabled) {

			cMotor = new Talon(climberMotorPort);
			piston = new DoubleSolenoid(climberPistonPort[0], climberPistonPort[1]);
			pdp = new PowerDistributionPanel();

		}

		cMotorPort = climberMotorPort;

	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}
	
	public void open() {
		if (climberEnabled) {
			piston.set(DoubleSolenoid.Value.kForward);
		}
	}

	public void close() {
		if (climberEnabled) {
			piston.set(DoubleSolenoid.Value.kReverse);
		}
	}

	public void off() {
		if (climberEnabled) {
			piston.set(DoubleSolenoid.Value.kOff);
		}
	}

	public void setSpeed() {
		if (climberEnabled) {
			cMotor.set(1);
		}
	}

	public void setSpeedReverse() {
		if (climberEnabled) {
			cMotor.set(-0.25);
		}
	}

	public void setSpeedOff() {
		if (climberEnabled) {
			cMotor.set(0);
		}
	}
	
	//This may need to be (and if not needed, should be) switched to CAN
	public double getClimberCurrent() {
		return (climberEnabled) ? Math.abs(pdp.getCurrent(cMotorPort)) : -1;
	}

	
}
