package org.usfirst.frc.team818.robot.commands;

public class DriveCommand extends CommandBase {
  
    public DriveCommand() {
       	requires(drive);
    }

    protected void initialize() {
    	drive.setBoth(0);
    }

    protected void execute() {
        drive.setBoth(oi.getLeftY()*0.8, oi.getRightY()*0.8);
    }

    protected boolean isFinished() {
        return false;
    }

    protected void end() {
    	drive.setBoth(0);
    }

    protected void interrupted() {
    	drive.setBoth(0);
    }
}
