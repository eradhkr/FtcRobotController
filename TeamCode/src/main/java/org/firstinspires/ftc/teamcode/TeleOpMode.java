package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "TestSlides", group = "AbsolutePriority")// Name and Group
public class TeleOpMode extends LinearOpMode {
    Gamepad currentGamepad1 = null;
    Gamepad previousGamepad1 = null;
    Gamepad currentGamepad2 = null;
    Gamepad previousGamepad2 = null;

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
    int FLIP_DIRECTION_UP = 6;
    int FLIP_DIRECTION_DOWN = 7;
    double FLIP_MOTOR_POWER = 1;
    double slowPower = 0.5;
    double slowReversePower = -0.5;
    double zeroPower = 0;
    double fastPower = 1;

    int slidePosition = 0;
    double linkPosition = 0;

    int MIN_LINK_POSITION = 0;
    int MAX_LINK_POSITION = 180;

    int MIN_FLIP_POSITION = 40;
    int MAX_FLIP_POSITION = 40;
    int FLIP_POSITION_INCREASE_AMOUNT = 5;



    double LINK_POSITION_INCREASE_AMOUNT = 0.1;

    int MAX_SLIDE_POSITION = 1750;
    int SLIDE_POSITION_INCREASE_AMOUNT = 30;

    int gamepad1_Button_X = 0;
    int gamepad1_Button_Y = 0;
    int gamepad1_Button_A = 0;
    int gamepad1_Button_B = 0;
    int gamepad2_Button_X = 0;
    int gamepad2_Button_Y = 0;
    int gamepad2_Button_A = 0;
    int gamepad2_Button_B = 0;
    DcMotor frontLeftMotor = null;
    DcMotor backLeftMotor = null;
    DcMotor frontRightMotor = null;
    DcMotor backRightMotor = null;
    DcMotor rightSlide = null;
    DcMotor leftSlide = null;
    DcMotor flipDownMotor = null;
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

    void moveFlipMotor(int flipMotorPosition, double flipMotorPower) {
        flipDownMotor.setTargetPosition(flipMotorPosition);
        flipDownMotor.setPower(flipMotorPower);
        flipDownMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    void flipMotor(int flipDirection) {
        int flipMotorPosition = 0;
        double flipMotorPower = 0;
        if (flipDirection == FLIP_DIRECTION_UP) {
            if (flipDownMotor.getCurrentPosition() <= MAX_FLIP_POSITION) {
                flipMotorPosition += FLIP_POSITION_INCREASE_AMOUNT;
            }
            if (flipMotorPosition >= MAX_FLIP_POSITION) {
                flipMotorPosition = MAX_FLIP_POSITION;
            }
            flipMotorPower = FLIP_MOTOR_POWER;
        } else if(flipDirection == FLIP_DIRECTION_DOWN) {
            flipMotorPosition -= FLIP_POSITION_INCREASE_AMOUNT;
            if (flipMotorPosition < MIN_FLIP_POSITION) {
                flipMotorPosition = MIN_FLIP_POSITION;
            }
            flipMotorPower = -FLIP_MOTOR_POWER;
        }

        moveFlipMotor(flipMotorPosition,flipMotorPower);

        flipDownMotor.setPower(-FLIP_MOTOR_POWER);
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
        flipDownMotor = hardwareMap.dcMotor.get("flipDownMotor");

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
        flipDownMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        rightSlide.setDirection(DcMotorSimple.Direction.FORWARD);

        currentGamepad1 = new Gamepad();
        previousGamepad1 = new Gamepad();
        currentGamepad2 = new Gamepad();
        previousGamepad2 = new Gamepad();


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
            telemetry.addData("Era Test", slidePosition);
            telemetry.update();

            //Copy  Button Values from previous iteration into Previous GamePad Button Values
            previousGamepad1.copy(currentGamepad1);
            //Store  Button Values from current iteration into currentGamePad
            //this way, we can copy CurrentGamePad to PreviousGamePad for next iteration
            currentGamepad1.copy(gamepad1);

            gamepad1_Button_X = getButtonStatus(currentGamepad1.x, previousGamepad1.x);
            gamepad1_Button_Y = getButtonStatus(currentGamepad1.y, previousGamepad1.y);
            gamepad1_Button_A = getButtonStatus(currentGamepad1.a, previousGamepad1.a);
            gamepad1_Button_B = getButtonStatus(currentGamepad1.b, previousGamepad1.b);
            gamepad2_Button_X = getButtonStatus(currentGamepad2.x, previousGamepad2.x);
            gamepad2_Button_Y = getButtonStatus(currentGamepad2.y, previousGamepad2.y);
            gamepad2_Button_A = getButtonStatus(currentGamepad2.a, previousGamepad2.a);
            gamepad2_Button_B = getButtonStatus(currentGamepad2.b, previousGamepad2.b);



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
             * Here are the current button functions
             * Gamepad1 Button Y- flip up intake
             * Gamepad1 Button A- flip down intake
             * Gamepad1 Button B- no action
             * Gamepad1 Button X- no action
             * Gamepad1 Dpad Up- Slides up
             * Gamepad1 Dpad Down- Slides down
             * Gamepad1 Dpad Left- linkage extend
             * Gamepad1 Dpad Down- linkage retract
             * Gamepad1 Joysticks- Wheel movement
             *
             * Gamepad2 Button Y- no action
             * Gamepad2 Button A- no action
             * Gamepad2 Button B- no action
             * Gamepad2 Button X- no action
             * Gamepad2 Dpad Up- no action
             * Gamepad2 Dpad Down- no action
             * Gamepad2 Dpad Left- no action
             * Gamepad2 Dpad Down- no action
             * Gamepad1 Joysticks- no action
             */

            if (isButtonPressedFirstTime(gamepad1_Button_Y)) {
                // If button y is pressed for first time
                // do nothing

            }

            // If button y is no longer being pressed
            if (isButtonReleased(gamepad1_Button_Y)) {
                //stop the motors completely
                stopMotor();
            }

            // If button y is continuously pressed
            if (isButtonContinuouslyPressed(gamepad1_Button_Y)) {
                flipMotor(FLIP_DIRECTION_UP);
                //moveForward(fastPower);
            }


            if (isButtonPressedFirstTime(gamepad1_Button_X)) {
                // If button is pressed for first time
                // do nothing
            }
            // If button is no longer being pressed
            if (isButtonReleased(gamepad1_Button_X)) {
                // do nothing
            }

            // If button is continuously pressed
            if (isButtonContinuouslyPressed(gamepad1_Button_X)) {
                //strafeLeft(fastPower);

            }
            /******* Button A **********/
            if (isButtonPressedFirstTime(gamepad1_Button_A)) {
                // If button y is pressed for first time
                // do nothing
            }
            // If button y is no longer being pressed
            if (isButtonReleased(gamepad1_Button_A)) {
                //stop the motors completely
                stopMotor();
            }

            // If button y is continuously pressed
            if (isButtonContinuouslyPressed(gamepad1_Button_A)) {
                flipMotor(FLIP_DIRECTION_DOWN);
            }

            /******* Button B **********/
            if (isButtonPressedFirstTime(gamepad1_Button_B)) {
                // If button y is pressed for first time
                // do nothing
            }
            // If button y is no longer being pressed
            if (isButtonReleased(gamepad1_Button_B)) {
                //stop the motors completely
                stopMotor();
            }

            // If button y is continuously pressed
            if (isButtonContinuouslyPressed(gamepad1_Button_B)) {
                strafeRight(fastPower);
            }

        }
    }
}







