package org.usfirst.frc.team6518.robot.commands;

import org.usfirst.frc.team6518.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArcadeDrive extends Command {
	
	private boolean bDriveAssisted = false;
	private boolean bGyroSetpoint = false;
	
	private double gyroDriveAngle = -6000.0;
	private double gyroSetpoint = -6000.0;

	public ArcadeDrive() {
		// TODO Auto-generated constructor stub
		requires(Robot.myDriveBase);
	}
	@Override
	protected void initialize() {
		SmartDashboard.putNumber("Gyro Turn Angle", 0);
		
	}

	@Override
	protected void execute() {
		// TODO: Hook up throttle
		// double throttle = (1.0-Robot.m_oi.stick.getRawAxis(3))/-2.0;
		//update kalman filter
		Robot.fltr_upd();
		
		if (Robot.m_oi.getButtonBPressed() ) {
			gyroSetpoint = (Robot.xhat)%360;
			bGyroSetpoint = true;
		}
		
		if (Robot.m_oi.getButtonAPressed() ) {
			
			gyroDriveAngle = bGyroSetpoint ? gyroSetpoint :  bDriveAssisted ? gyroDriveAngle : Robot.xhat%360;
			bDriveAssisted = true;
		}
		else 
			bDriveAssisted = false;
		
		
		double throttle = 1.0;
		if (!bDriveAssisted) {
			Robot.myDriveBase.setDriveSpeed(Robot.m_oi.getStick0_Y() * throttle, Robot.m_oi.getStick0_X() * throttle );
			SmartDashboard.putNumber("Gyro Turn Angle", 0);
		}
		else
		{
			double stick_position = Robot.m_oi.getStick0_Y();
			double angle_delta = gyroDriveAngle - (Robot.xhat)%360;
			angle_delta = Math.copySign(angle_delta, stick_position);
			double  v_delta = (angle_delta % 360) / 360 ;
			// v_delta = Math.copySign(v_delta, angle_delta);
			
			if (Math.abs(angle_delta) < 1.0 )
				v_delta = 0;
			else if (v_delta < 0 && v_delta > -0.4)
				v_delta = - 0.4;
			else if (v_delta > 0 && v_delta < 0.4)
				v_delta = 0.4;

			
			SmartDashboard.putNumber("Gyro Turn Angle", angle_delta);
			SmartDashboard.putNumber("Gyro VDelta", v_delta);
			Robot.myDriveBase.setDriveSpeed(stick_position, v_delta);
			//predict gyro values with kalman filter
			Robot.fltr_pred(v_delta,stick_position);
		}
		Robot.fltr_pred(Robot.m_oi.getStick0_X(), Robot.m_oi.getStick0_Y());
	}

	@Override
	protected boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override 
	protected void interrupted() {
		// TODO Auto-generated method stub
		super.interrupted();
	}

}
