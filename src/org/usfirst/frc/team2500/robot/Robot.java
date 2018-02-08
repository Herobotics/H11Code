package org.usfirst.frc.team2500.robot;

//wpi imports
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//auto imports
import org.usfirst.frc.team2500.autonomus.AutoBaseLine;
import org.usfirst.frc.team2500.autonomus.AutoCentor;
import org.usfirst.frc.team2500.autonomus.AutoLeftSwitch;
import org.usfirst.frc.team2500.autonomus.AutoRightSwitch;
//driverstaion imports
import org.usfirst.frc.team2500.driverStation.Controller;
import org.usfirst.frc.team2500.subSystemCommands.DriveChassis;
import org.usfirst.frc.team2500.subSystems.Chassis;
import org.usfirst.frc.team2500.subSystems.Climber;
import org.usfirst.frc.team2500.subSystems.Lift;
import org.usfirst.frc.team2500.subSystems.Unloader;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Command autonomousCommand;
	SendableChooser<Command> autonomousChooser = new SendableChooser<>();

	public static DriveChassis chassis;
	public static Climber climber ;
	public static Lift lift;
	public static Unloader unloader;
	
	//Command dataLogger;
	
	UsbCamera camera1;
	UsbCamera camera2;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		Controller.initialize();
		
		Climber.initialize();
		
		unloader = new Unloader();
		
		autonomousChooser.addDefault("Default Auto", new AutoBaseLine());
		autonomousChooser.addObject("Base Line", new AutoBaseLine());
		autonomousChooser.addObject("Left Switch", new AutoLeftSwitch());
		autonomousChooser.addObject("Centor", new AutoCentor());
		autonomousChooser.addObject("Right Switch", new AutoRightSwitch());
		SmartDashboard.putData("Auto mode", autonomousChooser);

//		camera1 = CameraServer.getInstance().startAutomaticCapture(0);
//		camera2 = CameraServer.getInstance().startAutomaticCapture(1);
	}
	
	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		autonomousCommand.cancel();
	}
	
	@Override
	public void autonomousInit() {
		autonomousCommand = autonomousChooser.getSelected();

		// schedule the autonomous command (example)
		if (autonomousCommand != null){
			autonomousCommand.start();
		}
		else{
			System.out.println("Something went wrong in starting auto");
		}
	}
	
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		
		chassis.start();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}
}
