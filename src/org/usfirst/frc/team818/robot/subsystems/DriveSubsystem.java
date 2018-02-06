package org.usfirst.frc.team818.robot.subsystems;

import org.usfirst.frc.team818.robot.Constants;
import org.usfirst.frc.team818.robot.commands.DriveCommand;
import org.usfirst.frc.team818.robot.utilities.DoublePIDOutput;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

public class DriveSubsystem extends Subsystem {

	TalonSRX[] leftMotors, rightMotors;
	AnalogGyro driveGyro;
	Encoder leftEncoder, rightEncoder;
	Accelerometer accelerometer;

	private static final double[] DYNAMIC_BREAKING_PID_VALUES = { 0.01, 0.001, 0 };
	private static final double[] DYNAMIC_BREAKING_PID_RANGE = { -1, 1 };
	private static final double[] GYRO_PID_VALUES = { 0.05, 0, 0.1 };
	private static final double[] GYRO_PID_RANGE = { -0.4, 0.4 };
	private static final double GYRO_PID_TOLERANCE = 1;
	private static final double[] SPEEDLIMIT_PID_VALUES = { 0.01, 0.001, 0 };
	private static final double[] SPEEDLIMIT_PID_RANGE = { -Constants.speedLimit, Constants.speedLimit };

	private PIDController dynamicBreakingControllerLeft, dynamicBreakingControllerRight, gyroController, speedLimitControllerLeft, speedLimitControllerRight;
	private DoublePIDOutput pidOutputGryo, pidOutputRight, pidOutputLeft, pidOutputSpeedLimitLeft, pidOutputSpeedLimitRight;

	private boolean driveEnabled;

	public DriveSubsystem(int[] leftMotorPorts, int[] rightMotorPorts, int gyroPort, int[] leftEncoderPorts,
			int[] rightEncoderPorts, boolean driveEnabled) {

		this.driveEnabled = driveEnabled;

		if (driveEnabled) {
			leftMotors = new WPI_TalonSRX[leftMotorPorts.length];
			rightMotors = new WPI_TalonSRX[rightMotorPorts.length];

			leftEncoder = new Encoder(leftEncoderPorts[0], leftEncoderPorts[1]);
			rightEncoder = new Encoder(rightEncoderPorts[0], rightEncoderPorts[1]);

			for (int i = 0; i <leftMotorPorts.length; i++)
				leftMotors[i] = new WPI_TalonSRX(leftMotorPorts[i]);

			for (int i = 0; i <rightMotorPorts.length; i++)
				rightMotors[i] = new WPI_TalonSRX(rightMotorPorts[i]);

			driveGyro = new AnalogGyro(gyroPort);
			accelerometer = new BuiltInAccelerometer();
		}

		pidOutputRight = new DoublePIDOutput();
		pidOutputLeft = new DoublePIDOutput();
		pidOutputGryo = new DoublePIDOutput();

		dynamicBreakingControllerRight = new PIDController(DYNAMIC_BREAKING_PID_VALUES[0], DYNAMIC_BREAKING_PID_VALUES[1],
				DYNAMIC_BREAKING_PID_VALUES[2], rightEncoder, pidOutputRight);
		dynamicBreakingControllerRight.setOutputRange(DYNAMIC_BREAKING_PID_RANGE[0], DYNAMIC_BREAKING_PID_RANGE[1]);
		dynamicBreakingControllerRight.setInputRange(Double.MAX_VALUE, Double.MIN_VALUE);
		dynamicBreakingControllerRight.setSetpoint(0);
		dynamicBreakingControllerRight.setContinuous(false);

		dynamicBreakingControllerLeft = new PIDController(DYNAMIC_BREAKING_PID_VALUES[0], DYNAMIC_BREAKING_PID_VALUES[1],
				DYNAMIC_BREAKING_PID_VALUES[2], leftEncoder, pidOutputLeft);
		dynamicBreakingControllerLeft.setOutputRange(DYNAMIC_BREAKING_PID_RANGE[0], DYNAMIC_BREAKING_PID_RANGE[1]);
		dynamicBreakingControllerLeft.setInputRange(Double.MAX_VALUE, Double.MIN_VALUE);
		dynamicBreakingControllerLeft.setSetpoint(0);
		dynamicBreakingControllerLeft.setContinuous(false);

		gyroController = new PIDController(GYRO_PID_VALUES[0], GYRO_PID_VALUES[1], GYRO_PID_VALUES[2],
				driveGyro, pidOutputGryo);
		gyroController.setOutputRange(GYRO_PID_RANGE[0], GYRO_PID_RANGE[1]);
		gyroController.setInputRange(Double.MAX_VALUE, Double.MIN_VALUE);
		gyroController.setAbsoluteTolerance(GYRO_PID_TOLERANCE);
		gyroController.setContinuous();
		gyroController.setSetpoint(0);
		
		speedLimitControllerLeft = new PIDController(SPEEDLIMIT_PID_VALUES[0], SPEEDLIMIT_PID_VALUES[1], SPEEDLIMIT_PID_VALUES[2],
				leftEncoder, pidOutputSpeedLimitLeft);
		speedLimitControllerLeft.setOutputRange(SPEEDLIMIT_PID_RANGE[0], SPEEDLIMIT_PID_RANGE[1]);
		speedLimitControllerLeft.setInputRange(Double.MIN_VALUE, Double.MAX_VALUE);
		speedLimitControllerLeft.setContinuous(false);
		speedLimitControllerLeft.setSetpoint(0);

		speedLimitControllerRight = new PIDController(SPEEDLIMIT_PID_VALUES[0], SPEEDLIMIT_PID_VALUES[1], SPEEDLIMIT_PID_VALUES[2],
				rightEncoder, pidOutputSpeedLimitRight);
		speedLimitControllerRight.setOutputRange(SPEEDLIMIT_PID_RANGE[0], SPEEDLIMIT_PID_RANGE[1]);
		speedLimitControllerRight.setInputRange(Double.MIN_VALUE, Double.MAX_VALUE);
		speedLimitControllerRight.setContinuous(false);
		speedLimitControllerRight.setSetpoint(0);
		
		leftEncoder.setDistancePerPulse(Constants.cycleDistance);
		rightEncoder.setDistancePerPulse(Constants.cycleDistance);
	}

	public void initDefaultCommand() {
		setDefaultCommand(new DriveCommand());
	}

	public void setLeft(double speed) {
		if (driveEnabled) {
			for (int i = 0; i < leftMotors.length; i++)
				leftMotors[i].set(ControlMode.PercentOutput,-speed);
		}
	}

	public void setRight(double speed) {
		if (driveEnabled) {
			for (int i = 0; i < rightMotors.length; i++)
				rightMotors[i].set(ControlMode.PercentOutput,speed);
		}
	}

	public void setBoth(double speedLeft, double speedRight) {
		if (driveEnabled) {
			setLeft(speedLeft);
			setRight(speedRight);
		}
	}

	public void setBoth(double speed) {
		if (driveEnabled) {
			setLeft(speed);
			setRight(speed);
		}
	}

	public void resetGyro() {
		if (driveEnabled) {
			driveGyro.reset();
		}
	}

	public double getAngle() {
		if (driveEnabled) {
			return driveGyro.getAngle() % 360;
		} else
			return 0;
	}

	public int getLeftRotation() {
		if (driveEnabled) {
			return leftEncoder.get();
		} else
			return 0;
	}

	public int getRightRotation() {
		if (driveEnabled) {
			return rightEncoder.get();
		} else
			return 0;
	}

	public void resetBothEncoders() {
		if (driveEnabled) {
			rightEncoder.reset();
			leftEncoder.reset();
		}
	}

	public boolean getLeftDirection() {
		if (driveEnabled) {
			return leftEncoder.getDirection();
		} else
			return false;

	}

	public boolean getRightDirection() {
		if (driveEnabled) {
			return rightEncoder.getDirection();
		} else
			return false;

	}

	public void enablePID(String pidType) {
		if (driveEnabled) {
			if (pidType.equals("straight")) {
				leftEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
				rightEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
				if (gyroController.isEnabled())
					gyroController.disable();
				if(speedLimitControllerLeft.isEnabled())
					speedLimitControllerLeft.disable();
				if(speedLimitControllerRight.isEnabled())
					speedLimitControllerRight.disable();
				if (!dynamicBreakingControllerRight.isEnabled())
					dynamicBreakingControllerRight.enable();
				if (!dynamicBreakingControllerLeft.isEnabled())
					dynamicBreakingControllerLeft.enable();
				
				
			} else if (pidType.equals("rotate")) {
				if (dynamicBreakingControllerRight.isEnabled())
					dynamicBreakingControllerRight.disable();
				if (dynamicBreakingControllerLeft.isEnabled())
					dynamicBreakingControllerLeft.disable();
				if(speedLimitControllerLeft.isEnabled())
					speedLimitControllerLeft.disable();
				if(speedLimitControllerRight.isEnabled())
					speedLimitControllerRight.disable();
				if (!gyroController.isEnabled())
					gyroController.enable();
			} else if(pidType.equals("speedLimit")){
				leftEncoder.setPIDSourceType(PIDSourceType.kRate);
				rightEncoder.setPIDSourceType(PIDSourceType.kRate);
				if (gyroController.isEnabled())
					gyroController.disable();
				if (dynamicBreakingControllerRight.isEnabled())
					dynamicBreakingControllerRight.disable();
				if (dynamicBreakingControllerLeft.isEnabled())
					dynamicBreakingControllerLeft.disable();
				if (!speedLimitControllerRight.isEnabled())
					speedLimitControllerRight.enable();
				if (!speedLimitControllerLeft.isEnabled())
					speedLimitControllerLeft.enable();
			}
		}
	}

	public void disablePID() {
		if (driveEnabled) {
			if (gyroController.isEnabled())
				gyroController.disable();
			if (dynamicBreakingControllerRight.isEnabled())
				dynamicBreakingControllerRight.disable();
			if (dynamicBreakingControllerLeft.isEnabled())
				speedLimitControllerLeft.disable();
			if (speedLimitControllerRight.isEnabled())
				speedLimitControllerRight.disable();
			if (speedLimitControllerLeft.isEnabled())
				speedLimitControllerLeft.disable();
		}
	}

	public void setRotatePoint(double angle) {
		if (driveEnabled)
			gyroController.setSetpoint(angle);
	}
	
	public void setVelocity(double velocityLeft, double velocityRight) {
		if (driveEnabled){
			speedLimitControllerLeft.setSetpoint(velocityLeft);
			speedLimitControllerRight.setSetpoint(velocityRight);
		}
	}

	public boolean rotateOnTarget() {
		return (driveEnabled) ? gyroController.onTarget() : true;
	}

	public double getPIDOutputGyro() {
		return (driveEnabled) ? pidOutputGryo.get() : 0;
	}

	public double getPIDOutputLeft() {
		return (driveEnabled) ? pidOutputLeft.get() : 0;
	}

	public double getPIDOutputRight() {
		return (driveEnabled) ? pidOutputRight.get() : 0;
	}
	
	public double getPIDOutputSpeedLimitLeft() {
		return (driveEnabled) ? pidOutputSpeedLimitLeft.get() : 0;
	}

	public double getPIDOutputSpeedLimitRight() {
		return (driveEnabled) ? pidOutputSpeedLimitRight.get() : 0;
	}

	public void setRotatePID(double p, double i, double d) {
		gyroController.setPID(p, i, d);
	}

}