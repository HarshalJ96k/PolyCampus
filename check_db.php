<?php
$host = "localhost";
$dbname = "polycampusstudentdb";
$username = "root";
$password = "";

try {
    $Polyconnection = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $Polyconnection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $stmt = $Polyconnection->query("DESCRIBE studentdetailstbl");
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($result, JSON_PRETTY_PRINT);
} catch(PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>
