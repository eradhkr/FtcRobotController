package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "TeleOpMode", group = "AbsolutePriority")// Name and Group
public class TeleOpMode extends LinearOpMode {
    Gamepad currentGamepad1 = null;
    Gamepad previousGamepad1 = null;

    /*
    enum ButtonStatus {
        BUTTON_PRESSED_FIRST_TIME.
        BUTTON_CONTINUOUSLY_PRESSED,
        BUTTON_RELEASED,
        BUTTON_STATUS_UNDETERMINED
    }
    */

    int BUTTON_PRESSED_FIRST_TIME = 2;
    int BUTTON_CONTINUOUSLY_PRESSED = 3;
    int BUTTON_RELEASED = 4;
    int BUTTON_STATUS_UNDETERMINED = 5;
    double slowPower = 0.5;
    double slowReversePower = -0.5;
    double zeroPower = 0;
    double fastPower = 1;

    int slidePosition = 0;
    double linkPosition = 0;

    int MIN_LINK_POSITION = 0;
    int MAX_LINK_POSITION = 180;
    double LINK_POSITION_INCREASE_AMOUNT = 0.1;

    int MAX_SLIDE_POSITION = 1750;
    int SLIDE_POSITION_INCREASE_AMOUNT = 30;

    int Button_X = 0;
    int Button_Y = 0;
    int Button_A = 0;
    int Button_B = 0;

    DcMotor frontLeftMotor = null;
    DcMotor backLeftMotor = null;
    DcMotor frontRightMotor = null;
    DcMotor backRightMotor = null;
    DcMotor rightSlide = null;
    DcMotor leftSlide = null;
    Servo linkServo = null;

    int getButtonStatus(boolean currentButtonValue, boolean previousButtonValue)
    {
        if (currentButtonValue && !previousButtonValue)
        {
            return BUTTON_PRESSED_FIRST_TIME;
        } else if (currentButtonValue && previousButtonValue)
        {
            return BUTTON_CONTINUOUSLY_PRESSED;
        }
        else if (!currentButtonValue && previousButtonValue)
        {
            return BUTTON_RELEASED;
        }
        else
        {
            return BUTTON_STATUS_UNDETERMINED;
        }
    }

    boolean isButtonPressedFirstTime (int buttonStatus)
    {
        if (buttonStatus == BUTTON_PRESSED_FIRST_TIME)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    boolean isButtonContinuouslyPressed (int buttonStatus)
    {
        if (buttonStatus == BUTTON_CONTINUOUSLY_PRESSED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    boolean isButtonReleased (int buttonStatus)
    {
        if (buttonStatus == BUTTON_RELEASED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    void setPower(double power)
    {
        frontLeftMotor.setPower(power);
        backLeftMotor.setPower(power);
        frontRightMotor.setPower(power);
        backRightMotor.setPower(power);
    }

    void moveForward(double power)
    {
        setPower(power);
    }

    void moveBackward(double power)
    {
        setPower(-power);
    }

    void stopMotor()
    {
        setPower(zeroPower);
    }

    void strafeLeft(double power)
    {
        frontLeftMotor.setPower(-power);
        backLeftMotor.setPower(power);
        frontRightMotor.setPower(power);
        backRightMotor.setPower(-power);
    }

    void strafeRight(double power)
    {
        frontLeftMotor.setPower(power);
        backLeftMotor.setPower(-power);
        frontRightMotor.setPower(-power);
        backRightMotor.setPower(power);
    }

    void turnRight(double power)
    {
        frontLeftMotor.setPower(power);
        backLeftMotor.setPower(power);
        frontRightMotor.setPower(-power);
        backRightMotor.setPower(-power);

    }

    void turnLeft(double power)
    {
        frontLeftMotor.setPower(-power);
        backLeftMotor.setPower(-power);
        frontRightMotor.setPower(power);
        backRightMotor.setPower(power);

    }

    void moveSlides(int slidePosition)
    {
        rightSlide.setTargetPosition(slidePosition);
        rightSlide.setPower(-1);
        leftSlide.setTargetPosition(slidePosition);
        leftSlide.setPower(1);
        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    @Override
    public void runOpMode() throws InterruptedException
    {
        // Initialize Hardware
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        rightSlide = hardwareMap.dcMotor.get("rightViperSlide");
        leftSlide = hardwareMap.dcMotor.get("leftViperSlide");
        linkServo = hardwareMap.servo.get("linkageServo");

        // Set Modes For Certain Motors
        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Reverse Motor Directions
        frontLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        rightSlide.setDirection(DcMotorSimple.Direction.FORWARD);

        currentGamepad1 = new Gamepad();
        previousGamepad1 = new Gamepad();

        waitForStart();// Wait for Play to Be Clicked

        if (isStopRequested()) return;// If Stop Is Clicked At Any Time, STOP

        while (opModeIsActive()) {// When Program is Running
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1; // Correct For Imperfect Strafing
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);// Math Stuff
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            // Set Wheel Power
            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            telemetry.addData("Slide Position", rightSlide.getCurrentPosition());
            telemetry.addData("Slide Position", leftSlide.getCurrentPosition());
            telemetry.addData("Servo link Position", linkServo.getPosition());
            telemetry.addData("SlidePos", slidePosition);
            telemetry.update();

            //Copy  Button Values from previous iteration into Previous GamePad Button Values
            previousGamepad1.copy(currentGamepad1);
            //Store  Button Values from current iteration into currentGamePad
            //this way, we can copy CurrentGamePad to PreviousGamePad for next iteration
            currentGamepad1.copy(gamepad1);

            Button_X = getButtonStatus(currentGamepad1.x, previousGamepad1.x);
            Button_Y = getButtonStatus(currentGamepad1.y, previousGamepad1.y);
            Button_A = getButtonStatus(currentGamepad1.a, previousGamepad1.a);
            Button_B = getButtonStatus(currentGamepad1.b, previousGamepad1.b);

            if (gamepad1.dpad_up && rightSlide.getCurrentPosition() <= MAX_SLIDE_POSITION) {
                slidePosition += 30;
            } else if (gamepad1.dpad_down) {
                slidePosition -= 30;
            }

            if (slidePosition < 0) {
                slidePosition = 0;
            }

            if (slidePosition != 0) {
                moveSlides(slidePosition);
            }

            if (gamepad1.dpad_right) {
                linkPosition += LINK_POSITION_INCREASE_AMOUNT;
            } else if (gamepad1.dpad_left) {
                linkPosition -= LINK_POSITION_INCREASE_AMOUNT;
            }

            if (linkPosition < MIN_LINK_POSITION) {
                linkPosition = MIN_LINK_POSITION;
            }
            if (linkPosition >= MAX_LINK_POSITION) {
                linkPosition = MAX_LINK_POSITION;
            }
            linkServo.setPosition(linkPosition);

            telemetry.addData("Slide Position", rightSlide.getCurrentPosition());
            telemetry.addData("Slide Position", leftSlide.getCurrentPosition());
            telemetry.addData("SlidePos", slidePosition);
            telemetry.update();

            /**
             * Button Y
             */
            if (isButtonPressedFirstTime(Button_Y)) {
                // If button y is pressed for first time
                // do nothing
            }

            // If button y is no longer being pressed
            if (isButtonReleased(Button_Y)) {
                //stop the motors completely
                stopMotor();
            }

            // If button y is continuously pressed
            if (isButtonContinuouslyPressed(Button_Y)) {
                moveForward(fastPower);
            }

            /**
             * Button X
             */

            if (isButtonPressedFirstTime(Button_X)) {
                // If button is pressed for first time
                // do nothing
            }
            // If button is no longer being pressed
            if (isButtonReleased(Button_X)) {
                // do nothing
            }

            // If button is continuously pressed
            if (isButtonContinuouslyPressed(Button_X)) {
                strafeLeft(fastPower);
            }
            /******* Button A **********/
            if (isButtonPressedFirstTime(Button_A)) {
                // If button y is pressed for first time
                // do nothing
            }
            // If button y is no longer being pressed
            if (isButtonReleased(Button_A)) {
                //stop the motors completely
                stopMotor();
            }

            // If button y is continuously pressed
            if (isButtonContinuouslyPressed(Button_A)) {
                moveBackward(fastPower);
            }

            /******* Button B **********/
            if (isButtonPressedFirstTime(Button_B)) {
                // If button y is pressed for first time
                // do nothing
            }
            // If button y is no longer being pressed
            if (isButtonReleased(Button_B)) {
                //stop the motors completely
                stopMotor();
            }

            // If button y is continuously pressed
            if (isButtonContinuouslyPressed(Button_B)) {
                strafeRight(fastPower);
            }
        }
    }
}







