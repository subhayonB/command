package org.usfirst.frc.team6518.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team6518.robot.RobotMap;
import org.usfirst.frc.team6518.robot.commands.ArcadeDrive;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveBase extends Subsystem {
	
	private Spark leftSpark;
	private Spark rightSpark;
	private DifferentialDrive myRobotDrive;
	

	public DriveBase() {
		// TODO Auto-generated constructor stub
		leftSpark = new Spark(RobotMap.DRIVE_MOTOR_LEFT);
		rightSpark = new Spark(RobotMap.DRIVE_MOTOR_RIGHT);
		myRobotDrive = new DifferentialDrive(leftSpark, rightSpark);
		
	}

	public void setDriveSpeed(double y, double x) {
		myRobotDrive.arcadeDrive(y, x);
	}
	
	public void stopMotor() {
		myRobotDrive.stopMotor();
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		setDefaultCommand(new ArcadeDrive());
	}

}
