package org.usfirst.frc.team6518.robot;
/**
 * 
 */


import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.SensorUtil;

/**
 * @author acerbix
 *
 */
public class myPDP extends PowerDistributionPanel {

	/**
	 * 
	 */
	public myPDP() {
		// TODO Auto-generated constructor stub
		// Some spurious merge comment
		super();
	}

	/**
	 * @param module
	 */
	public myPDP(int module) {
		super(module);
		// TODO Auto-generated constructor stub
	}
	
	// Changing method sig here:
	@Override
	public double getTotalCurrent() {
		return 0;
	}
	
	@Override
	  public void initSendable(SendableBuilder builder) {
	    builder.setSmartDashboardType("PowerDistributionPanel");
	    for (int i = 0; i < SensorUtil.kPDPChannels; ++i) {
	      final int chan = i;
	      builder.addDoubleProperty("Chan" + i, () -> getCurrent(chan), null);
	    }
	    builder.addDoubleProperty("Voltage", this::getVoltage, null);
	    builder.addDoubleProperty("TotalCurrent", this::getTotalCurrent, null);
	    builder.addDoubleProperty("Temperature", this::getTemperature, null);
	  }

}
