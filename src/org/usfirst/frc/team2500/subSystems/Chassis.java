package org.usfirst.frc.team2500.subSystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Chassis extends Subsystem{

	private final int LEFT_ENCODER_PORT1 = 0;
	private final int LEFT_ENCODER_PORT2 = 1;
	private final int LEFT_ENCODER_PORT3 = 1;
	private Encoder leftSideEndoder;
	
	private final int RIGHT_ENCODER_PORT1 = 2;
	private final int RIGHT_ENCODER_PORT2 = 3;
	private final int RIGHT_ENCODER_PORT3 = 3;
	private Encoder rightSideEndoder;
	
	
	
	private AHRS gyro;
	
	
	
	private final int LEFT_MOTOR_PORT1 = 6;
	private final int LEFT_MOTOR_PORT2 = 7;
	private Victor leftSideMotor1,leftSideMotor2;
	
	private final int RIGHT_MOTOR_PORT1 = 8;
	private final int RIGHT_MOTOR_PORT2 = 9;
	private Victor rightSideMotor1,rightSideMotor2;
	
	
	
	private final int SHIFTER_PORT = 0;
	private Solenoid shifter;
	
	private boolean shiftTarget;
	
	private final double MAX_HIGH_GEAR_SPEED = 0;
	private final double MAX_LOW_GEAR_SPEED = 0;
	private final double LOW_GEAR_SHIFT_PERCENT_HIGH = 0.9;
	private final double LOW_GEAR_SHIFT_PERCENT_LOW = 0.8;
	
	public static Chassis instance;
	
	public static Chassis getInstance()
    {
		if (instance == null)
		   instance = new Chassis();
	
		return instance;
    }
	
	private Chassis(){

		leftSideEndoder = new Encoder(LEFT_ENCODER_PORT1,LEFT_ENCODER_PORT2,LEFT_ENCODER_PORT3);
		rightSideEndoder = new Encoder(RIGHT_ENCODER_PORT1,RIGHT_ENCODER_PORT2,RIGHT_ENCODER_PORT3);
		leftSideEndoder.setDistancePerPulse(1);
		leftSideEndoder.reset();
		rightSideEndoder.setDistancePerPulse(1);
		rightSideEndoder.reset();
		
		gyro = new AHRS(SPI.Port.kMXP);
		gyro.reset();
		
		leftSideMotor1 = new Victor(LEFT_MOTOR_PORT1);
		leftSideMotor2 = new Victor(LEFT_MOTOR_PORT2);

		rightSideMotor1 = new Victor(RIGHT_MOTOR_PORT1);
		rightSideMotor2 = new Victor(RIGHT_MOTOR_PORT2);
		
		shifter = new Solenoid(SHIFTER_PORT);
		
		shiftTarget = false;
	}

	// call to change the power given to the motor
	public void ChangePower(double powerL,double powerR){
		leftSideMotor1.set(powerL);
		leftSideMotor2.set(powerL);
		
		rightSideMotor1.set(powerR);
		rightSideMotor2.set(powerR);
	}

	public void shift(boolean setShift){
		shiftTarget = setShift;
	}
	
	public void shift(){
		shiftTarget = !shiftTarget;
	}
	
	public void arcadeDrive(double turnValue, double moveValue){
		//This is the speed we want the left and the right wheels to go at when this fun
	    double leftTargetSpeed = 0.0;
	    double rightTargetSpeed = 0.0;
	    
	    //Math to convert the forword and  rotaion to left and right
	    if (turnValue > 0.0) {
	        if (moveValue > 0.0) {
	          leftTargetSpeed = turnValue - moveValue;
	          rightTargetSpeed = Math.max(turnValue, moveValue);
	        } else {
	          leftTargetSpeed = Math.max(turnValue, -moveValue);
	          rightTargetSpeed = turnValue + moveValue;
	        }
	      } else {
	        if (moveValue > 0.0) {
	          leftTargetSpeed = -Math.max(-turnValue, moveValue);
	          rightTargetSpeed = turnValue + moveValue;
	        } else {
	          leftTargetSpeed = turnValue - moveValue;
	          rightTargetSpeed = -Math.max(-turnValue, -moveValue);
	        }
	    }
	    
	    ChangePower(leftTargetSpeed, rightTargetSpeed);
	}
	
	private long lastTimeSpeed;
	private double skp = 1, ski = 1, skd = 1;

	private double errSumLeftSpeed, lastErrLeftSpeed;
	private double errSumRightSpeed, lastErrRightSpeed;
	public void ChangeSpeed(double leftSpeed, double rightSpeed)
	{
		/*How long since we last calculated*/
		long now = System.currentTimeMillis();
		double timeChange = (double)(now - lastTimeSpeed);
	  
	   	double eCodeReadingLeft = leftSideEndoder.getRate();
	   	/*Compute all the working error variables*/
	   	double error = getSpeedError(eCodeReadingLeft,leftSpeed);
	   	double dErr = 0;
		errSumLeftSpeed += (error * timeChange);
		dErr = (error - lastErrLeftSpeed) / timeChange;
		
		lastErrLeftSpeed = error;
	  
		/*Compute PID Output*/
		double leftPower = skp * error + ski * errSumLeftSpeed + skd * dErr;
	   
		double eCodeReadingRight = rightSideEndoder.getRate();
		/*Compute all the working error variables*/
		error = getSpeedError(eCodeReadingRight,rightSpeed);
		dErr = 0;
		errSumRightSpeed += (error * timeChange);
		dErr = (error - lastErrRightSpeed) / timeChange;
	  
		/*Remember some variables for next time*/
		lastErrRightSpeed = error;
		lastTimeSpeed = now;

		double rightPower = skp * error + ski * errSumRightSpeed + skd * dErr;
		
		autoShift((eCodeReadingLeft + eCodeReadingRight)/2);
		
		ChangePower(leftPower, rightPower);
	}
	
	private void autoShift(double speed){
		if(shiftTarget){
			if(speed > MAX_LOW_GEAR_SPEED * LOW_GEAR_SHIFT_PERCENT_HIGH){
				shifter.set(true);
			}
			else if(speed > MAX_LOW_GEAR_SPEED * LOW_GEAR_SHIFT_PERCENT_LOW) {
				shifter.set(false);
			}
		}
		else{
			shifter.set(false);
		}
	}
	
	private double getSpeedError(double currentSpeed, double targetSpeed){
		double maxSpeed = (shiftTarget)? MAX_LOW_GEAR_SPEED : MAX_HIGH_GEAR_SPEED;
		return (currentSpeed-(targetSpeed * maxSpeed))/maxSpeed;
	}
	
	public double getLeftRate(){
		return leftSideEndoder.getRate();
	}
	
	public double getRightRate(){
		return rightSideEndoder.getRate();
	}
	
	public double getLeftDist(){
		return leftSideEndoder.getDistance();
	}
	
	public double getRightDist(){
		return rightSideEndoder.getDistance();
	}
	
	public double getAngle(){
		return gyro.getAngle();
	}
	
	public void resetEncoders(){
		leftSideEndoder.reset();
		rightSideEndoder.reset();
	}
	
	public void resetGyro(){
		gyro.reset();
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
}