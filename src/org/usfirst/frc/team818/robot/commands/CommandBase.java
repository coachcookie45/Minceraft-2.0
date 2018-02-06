package org.usfirst.frc.team818.robot.commands;

import org.usfirst.frc.team818.robot.Constants;
import org.usfirst.frc.team818.robot.OI;
import org.usfirst.frc.team818.robot.subsystems.DriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;


public abstract class CommandBase extends Command {

	public static OI oi;
	
	public static DriveSubsystem drive;
	
	public static void init() {
		
		drive = new DriveSubsystem(Constants.leftMotorPorts, Constants.rightMotorPorts, Constants.gyroDrivePort, 
				Constants.leftEncoderPorts, Constants.rightEncoderPorts, Constants.driveEnabled);
		
		oi = new OI();
	}
	
	public static void disable() {
		drive.setBoth(0);
	}
	
}