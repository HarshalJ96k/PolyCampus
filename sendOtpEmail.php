<?php
// sendOtpEmail.php
// Updated to be copy-paste ready for XAMPP htdocs/PolyCampusAPI/
header("Content-Type: application/json");

// Ensure PHPMailer exists in the same folder as this script
// You should have a 'PHPMailer' folder here containing the 'src' folder
$exceptionPath = 'PHPMailer/src/Exception.php';
$mailerPath = 'PHPMailer/src/PHPMailer.php';
$smtpPath = 'PHPMailer/src/SMTP.php';

if (!file_exists($exceptionPath) || !file_exists($mailerPath) || !file_exists($smtpPath)) {
    echo json_encode([
        "success" => "0", 
        "message" => "PHPMailer missing! Please download it and place the 'src' files in a 'PHPMailer' folder inside PolyCampusAPI."
    ]);
    exit;
}

require $exceptionPath;
require $mailerPath;
require $smtpPath;

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = isset($_POST['Email']) ? $_POST['Email'] : null;
    $otp = isset($_POST['otp']) ? $_POST['otp'] : null;

    if (!$email || !$otp) {
        echo json_encode(["success" => "0", "message" => "Email or OTP missing in POST request"]);
        exit;
    }
    
    $mail = new PHPMailer(true);

    try {
        // Server settings
        $mail->isSMTP();
        $mail->Host       = 'smtp.gmail.com';
        $mail->SMTPAuth   = true;
        
        // --- IMPORTANT: CHANGE THESE TWO LINES ---
        $mail->Username   = 'harshdyp96@gmail.com'; 
        // Use your 16-digit Google App Password here (Settings -> Security -> 2FA -> App Passwords)
        $mail->Password   = 'REPLACE_WITH_YOUR_16_DIGIT_APP_PASSWORD'; 
        // ------------------------------------------

        $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
        $mail->Port       = 587;

        // Recipients
        $mail->setFrom('harshdyp96@gmail.com', 'PolyCampus Auth');
        $mail->addAddress($email);

        // Content
        $mail->isHTML(false);
        $mail->Subject = "PolyCampus - Your Registration OTP";
        $mail->Body    = "Welcome to PolyCampus!\n\nYour One Time Password (OTP) for verifying your registration is:\n\n" . $otp . "\n\nPlease enter this code in the Android App to complete your registration process.\n\nDo not share this code with anyone.";

        $mail->send();
        echo json_encode(["success" => "1", "message" => "OTP sent successfully to $email"]);
    } catch (Exception $e) {
        echo json_encode(["success" => "0", "message" => "Mailer Error: {$mail->ErrorInfo}"]);
    }
} else {
    echo json_encode(["success" => "0", "message" => "Invalid request method. Use POST."]);
}
?>