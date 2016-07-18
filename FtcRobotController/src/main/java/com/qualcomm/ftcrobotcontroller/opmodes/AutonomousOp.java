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
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.ftccommon.DbgLog;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * AutonomousOp Mode
 * <p>
 * Enables autonomous control of the bot using the encoders
 */

public class AutonomousOp extends OpMode {

    //Declare motors
    DcMotor motorLeft;
    DcMotor motorRight;

    //Declare servos
//	Servo clawServo;
//	Servo rotationServo;

    public AutonomousOp() {

    }

    /*
     * Code to run when the op mode is first enabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init() {

        //Initializing Motors
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorRight = hardwareMap.dcMotor.get("motor_2");

        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        //Initializing Servos
//		clawServo = hardwareMap.servo.get("servo_1");
//		rotationServo = hardwareMap.servo.get("servo_2");
//
//		clawServo.setPosition(0.05);
//		rotationServo.setPosition(0.00);

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        driveForwardDistance(0.25, 2000);

        //Finish
        stop();
    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {

    }

    //Code for driving forward with the distance and power given
    public void driveForwardDistance(double power, int distance){
        //Reset Encoders
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        //Set Target Position
        motorLeft.setTargetPosition(distance);
        motorRight.setTargetPosition(distance);

        //Run to the Position set in the Target Position
        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        //Indicate the power
        driveForward(power);

        while(motorLeft.isBusy() && motorRight.isBusy()){
            //Wait until both motors have reached their target.
        }

        //After motors are not busy, stop driving
        stopDriving();

        //Make sure, to set it again to run using the encoders
        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

    }

    //Code for turning left with the distance and power given
    public void turnLeftDistance(double power, int distance){
        //Reset Encoders
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        //Set Target Position
        motorLeft.setTargetPosition(-distance);
        motorRight.setTargetPosition(distance);

        //Run to the Position set in the Target Position
        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        //Indicate the power
        turnLeft(power);

        while(motorLeft.isBusy() && motorRight.isBusy()){
            //Wait until both motors have reached their target.
        }

        //After motors are not busy, stop driving
        stopDriving();

        //Make sure, to set it again to run using the encoders
        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

    }

    //Code for turning right with the distance and power given
    public void turnRightDistance(double power, int distance){
        turnLeftDistance(-power, -distance);
    }

    //Power: Driving Forward
    public void driveForward(double power){
        motorLeft.setPower(power);
        motorRight.setPower(power);
    }

    //Power: Turning Left
    public void turnLeft(double power){
        motorLeft.setPower(-power);
        motorRight.setPower(power);
    }

    //Power: Turning Right
    public void turnRight(double power){
        turnLeft(-power);
    }

    //Finish moving
    public void stopDriving(){
        driveForward(0);
    }

}
