/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6518.robot;

import org.usfirst.frc.team6518.robot.subsystems.DriveBase;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;

// import edu.wpi.first.wpilibj.ADXRS450_Gyro;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static OI m_oi;
	public static DriveBase myDriveBase;
	public static ADXRS450_Gyro myGyro;
	
	//public static  myPDP pdp ;
	public static PowerDistributionPanel pdp;
	public static BuiltInAccelerometer accel ;
	public static SmartDashboard dashboard;
	private static boolean b_initmode = false;
	private static double totalDrift = 0;
	public static double avgDrift = 0;
	private static double lastAngle = 0;
	private static double currAngle = 0;
	private static int n_cycles = 1;
	private static int s_cycles = 0;
	public static double predicted_drift = 0;

	public static double x = 0.0000001;
	public static double r = 200;
	public static double a = 1 ;
	public static double b = .2 ;

	public static double c = 2.0 ; 
	public static double p =0.0000001 ;
	public static double g  ;
	public static double xhat  ;


	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		// Init subsystems and OI
		myDriveBase=new DriveBase();
//		m_chooser.addDefault("Default Auto", new ExampleCommand());
//		// chooser.addObject("My Auto", new MyAutoCommand());
//		SmartDashboard.putData("Auto mode", m_chooser);
		m_oi = new OI();
		
		// Init sensors 
		myGyro = new ADXRS450_Gyro();
		pdp = new myPDP();
		accel = new BuiltInAccelerometer();
		
		// Set SmartDashboard Entries
		SmartDashboard.putBoolean("AutoAssist", false);
		SmartDashboard.putNumber("Gyro Turn Angle", 0);
		SmartDashboard.putNumber("Gyro VDelta", 0);
		SmartDashboard.putBoolean("JoyA",  m_oi.getButtonAPressed());
		SmartDashboard.putBoolean("JoyB",  m_oi.stick.getRawButton(2));
		SmartDashboard.putNumber("JoyLT",  m_oi.stick.getRawAxis(2));
		SmartDashboard.putNumber("JoyRT",  m_oi.stick.getRawAxis(3));
		SmartDashboard.putNumber("Gyro",  myGyro.getAngle());
		
		
		// myGyro.calibrate();
		System.out.println("=== Robot init completed ***");
		b_initmode = true;
		
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		b_initmode = false;
		m_autonomousCommand = m_chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		
		s_cycles = n_cycles % 50 == 0 ? s_cycles + 1 : s_cycles ;
		predicted_drift = avgDrift * s_cycles; 
		SmartDashboard.putNumber("predicted drift", predicted_drift);
		n_cycles++;
	}

	@Override
	public void teleopInit() {
		b_initmode = false;
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}

		System.out.println("=== Teleop init completed ***");
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		SmartDashboard.putBoolean("JoyA",  m_oi.getButtonAPressed());
		SmartDashboard.putBoolean("JoyB",  m_oi.stick.getRawButton(2));
		SmartDashboard.putNumber("JoyLT",  m_oi.stick.getRawAxis(2));
		SmartDashboard.putNumber("JoyRT",  m_oi.stick.getRawAxis(3));
		SmartDashboard.putNumber("Gyro",  myGyro.getAngle());
		SmartDashboard.putNumber("jyx", m_oi.stick.getRawAxis(0));
		SmartDashboard.putNumber("jyy", m_oi.stick.getRawAxis(1));
		
		s_cycles = n_cycles % 50 == 0 ? s_cycles + 1 : s_cycles ;
		predicted_drift = avgDrift * s_cycles; 
		SmartDashboard.putNumber("predicted drift", predicted_drift);
		n_cycles++;
		
		Scheduler.getInstance().run();
		
			
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
	}

	@Override 
	public void robotPeriodic() {
		// super.robotPeriodic();
		if (b_initmode) {
			
		
			double  drift = currAngle - lastAngle;
			totalDrift += drift;
			
			
			avgDrift = n_cycles % 50 == 0 ? totalDrift/n_cycles * 50 : avgDrift ;

			

			lastAngle = currAngle;
			SmartDashboard.putNumber("Average Drift", avgDrift);
			SmartDashboard.putNumber("Drift", drift);
			

			fltr_upd();
			
			fltr_pred(0,0);
			

		}
		
		s_cycles = n_cycles % 50 == 0 ? s_cycles + 1 : s_cycles ;
		predicted_drift = avgDrift * s_cycles; 
		SmartDashboard.putNumber("predicted drift", predicted_drift);
		n_cycles++;
	}
	
	public static void fltr_upd(){
		currAngle = myGyro.getAngle();
		g=(p*c)/(c*p*c+r);
		xhat+=g*(currAngle-(c*xhat));
		p=(1-g)*p;
	}

	public static void fltr_pred(double u, double v){
		// x*=a;
		u = Math.abs(u) < 0.48? 0: u;
		v = Math.abs(v) < 0.48? 0: v;

		xhat = xhat*a + 0.6*u + .001*v ;
		p*=a*a;
		
		SmartDashboard.putNumber("X^", xhat);
	}
}


