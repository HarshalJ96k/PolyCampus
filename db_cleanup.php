<?php
$host = "localhost";
$dbname = "polycampusstudentdb";
$username = "root";
$password = "";

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // 1. Drop the legacy studymaterialtbl
    $pdo->exec("DROP TABLE IF EXISTS studymaterialtbl");
    echo "✓ Legacy table 'studymaterialtbl' deleted successfully.\n";

    // 2. Drop the unused subjectwisestudymaterialtbl
    $pdo->exec("DROP TABLE IF EXISTS subjectwisestudymaterialtbl");
    echo "✓ Unused table 'subjectwisestudymaterialtbl' deleted successfully.\n";

    // 3. Drop any other potential typos from previous turns (if any existed)
    // For example, if 'studymaterailtbl' existed
    $pdo->exec("DROP TABLE IF EXISTS studymaterailtbl");

    echo "🧹 Cleanup complete! Your database now only uses 'study_material'.\n";

} catch(PDOException $e) {
    echo "❌ Error during cleanup: " . $e->getMessage() . "\n";
}
?>
