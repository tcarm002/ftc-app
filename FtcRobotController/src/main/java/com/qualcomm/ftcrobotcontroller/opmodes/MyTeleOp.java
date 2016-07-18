/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.ftccommon.DbgLog;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class MyTeleOp extends OpMode {

	//Manouvering the Bot
	DcMotor motorRight;
	DcMotor motorLeft;

	//Arm extension & Arm Up/Down
	//DcMotor motorExtension;
	//DcMotor motorUpDown;

	//Servo_1 = Claw Grip, Servo_2 = Claw Rotation
	Servo servo_1;
	Servo servo_2;

	private ServoController sc;
	private double servoPosition = 0.0;

	//current claw grip position
	double current_pos1;

	//current claw rotation position
	double current_pos2;

	/**
	 * Constructor
	 */
	public MyTeleOp() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {


		/*
		 * Use the hardwareMap to get the dc motors and servos by name. Note
		 * that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */

		/*
		 * For the demo Tetrix K9 bot we assume the following,
		 *   There are two motors "motor_1" and "motor_2"
		 *   "motor_1" is on the right side of the bot.
		 *   "motor_2" is on the left side of the bot and reversed.
		 */

		motorLeft = hardwareMap.dcMotor.get("motor_2");
		motorRight = hardwareMap.dcMotor.get("motor_1");
		//motorExtension = hardwareMap.dcMotor.get("motor_3");
		//motorUpDown = hardwareMap.dcMotor.get("motor_4");

		motorLeft.setDirection(DcMotor.Direction.REVERSE);
		DbgLog.msg("TSC - Activating motors");

		servo_1 = hardwareMap.servo.get("servo_1");
		servo_2 = hardwareMap.servo.get("servo_2");

		current_pos1 = 0.02;
		current_pos2 = 0;
	}

	/*
	 * This method will be called repeatedly in a loop
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		/*
		 * Gamepad 1
		 *
		 * Gamepad 1 controls the motors via the left stick
		 */

		if (gamepad1.back) {flipDirection();}

		// throttle: left_stick_y ranges from -1 to 1, where -1 is full up, and
		// 1 is full down
		// direction: left_stick_x ranges from -1 to 1, where -1 is full left
		// and 1 is full right
		float throttle = -gamepad1.left_stick_y;
		float direction = gamepad1.left_stick_x;
		float right = throttle - direction;
		float left = throttle + direction;


		//To control claw grip
		if (gamepad2.left_bumper)
		{
			current_pos1 = 0.05;
			servo_1.setPosition(current_pos1);
		}

		if (gamepad2.right_bumper)
		{
			current_pos1 += 0.02;
			if (current_pos1 >= 0.0 && current_pos1 <=1.0) {
				servo_1.setPosition(current_pos1);
			}
		}

		//To control claw rotation
		if(gamepad2.a){
			current_pos2 -= 0.01;
			if (current_pos2 >= 0.0 && current_pos2 <=1.0) {
				servo_2.setPosition(current_pos2);
			}
			else{
				current_pos2 = 0;
		}
		}

		if(gamepad2.b){
			current_pos2 += 0.01;
			if (current_pos2 >= 0.0 && current_pos2 <=1.0) {
				servo_2.setPosition(current_pos2);
			}
			else{
				current_pos2 = 1.0;
			}
		}

		//For testing purposes
		if(current_pos1 >= 0.0 && current_pos1 <=1.0) {
			telemetry.addData("Current hand position", "" + current_pos1);
		}
		else {
			telemetry.addData("Current hand position", "OUT OF BOUNDS");
		}

		if(current_pos2 >= 0.0 && current_pos2 <=1.0) {
			telemetry.addData("Current rotation", "" + current_pos2);
		}
		else {
			telemetry.addData("Current hand rotation", "OUT OF BOUNDS");
		}

		//telemetry.addData("Maximum position possible","" + servo_1.MAX_POSITION);
		//telemetry.addData("Minimum position possible","" + servo_1.MIN_POSITION);

		//For GamePad2
		boolean rise = gamepad2.dpad_up;
		boolean lower = gamepad2.dpad_down;
		double armSpeed = 0.50;

		// clip the right/left values so that the values never exceed +/- 1
		right = Range.clip(right, -1, 1);
		left = Range.clip(left, -1, 1);

		// scale the joystick value to make it easier to control
		// the robot more precisely at slower speeds.
		right = (float)scaleInput(right);
		left =  (float)scaleInput(left);

//		//Arm's up and down movement
//		if (rise) {
//			servoMotor1.setDirection(DcMotor.Direction.FORWARD);
//			servoMotor1.setPower(armSpeed);
//		}
//		else if (lower) {
//			servoMotor1.setDirection(DcMotor.Direction.REVERSE);
//			servoMotor1.setPower(armSpeed);
//		}

		// write the values to the motors
		motorRight.setPower(left);
		motorLeft.setPower(right);


		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */
//        telemetry.addData("T1", "*** looping***");
//		DbgLog.msg("TSC - Looping");
//        telemetry.addData("left tgt pwr",  "left  pwr: " + Boolean.toString(gamepad1.right_bumper));
       // telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", right));
		//telemetry.addData("Button Pressed:", Boolean.toString(gamepad1.right_bumper));


	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}

	public void flipDirection() {
		telemetry.addData("T2","Flipping");
		DbgLog.msg("TSC - flipping");
		if (motorLeft.getDirection().equals(DcMotor.Direction.REVERSE)) {
			motorLeft.setDirection(DcMotor.Direction.FORWARD);
			motorRight.setDirection(DcMotor.Direction.REVERSE);
		}
		else {
			motorLeft.setDirection(DcMotor.Direction.REVERSE);
			motorRight.setDirection(DcMotor.Direction.FORWARD);
		}
	}

//	public void servoIniPosi(double this_ini_pos, Servo this_servo){
//		double ini_pos = this_ini_pos;
//		Servo servo = this_servo;
//		servo.setPosition(ini_pos);
//	}
//
//	public void servoIncrease(double this_current_pos, Servo this_servo){
//		double current_pos = this_current_pos;
//		Servo servo = this_servo;
//		current_pos += 0.02;
//		if (current_pos >= 0.0 && current_pos <=1.0) {
//			servo.setPosition(current_pos);
//		}
//	}

	/*
	 * This method scales the joystick input so for low joystick values, the
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
	double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
				0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

		// get the corresponding index for the scaleInput array.
		int index = (int) (dVal * 16.0);

		// index should be positive.
		if (index < 0) {
			index = -index;
		}

		// index cannot exceed size of array minus 1.
		if (index > 16) {
			index = 16;
		}

		// get value from the array.
		double dScale = 0.0;
		if (dVal < 0) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}

		// return scaled value.
		return dScale;
	}

}
