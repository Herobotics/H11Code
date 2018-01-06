package org.usfirst.frc.team2500.autonomus;

import org.usfirst.frc.team2500.autonomusSubCommands.AutoSubCommand;

import edu.wpi.first.wpilibj.command.Command;


public class AutoTemplate extends Command {
	
	AutoSubCommand[] commands;
	int currentCommand;
	boolean finished;
	
	/**
     * This function is run once each time the robot enters autonomous mode
     */	
    public void initialize() {
		commands = new AutoSubCommand[0];
		currentCommand = 0;
		finished = false;
    }

    /**
     * This function is called periodically during autonomous
     */
    public void execute() {
		if(currentCommand == commands.length){
			finished = true;
		}
		if(commands[currentCommand].run()){
			currentCommand++;
		}
    }
    
    /**
     * This function is used to end the program
     * When it returns true the command finishes
     * Make sure it does this so it dosnt talk up cup power by running this command over and over again
     */
	@Override
	protected boolean isFinished() {
		return finished;
	}
}