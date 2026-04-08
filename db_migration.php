<?php
$host = "localhost";
$dbname = "polycampusstudentdb";
$username = "root";
$password = "";

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // 1. Create or Upgrade study_material table to include all required fields
    $sql = "CREATE TABLE IF NOT EXISTS study_material (
        id INT(11) NOT NULL AUTO_INCREMENT,
        branch VARCHAR(255) NOT NULL,
        sem VARCHAR(255) NOT NULL,
        title VARCHAR(255) NOT NULL,
        description TEXT,
        doc VARCHAR(255) DEFAULT NULL,
        date DATE DEFAULT NULL,
        PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    
    $pdo->exec($sql);
    echo "✓ Table 'study_material' created or verified successfully.\n";

    // 2. Migration: Copy data from studymaterialtbl if it exists and study_material is empty
    $checkOld = $pdo->query("SHOW TABLES LIKE 'studymaterialtbl'");
    if ($checkOld->rowCount() > 0) {
        $checkNew = $pdo->query("SELECT COUNT(*) FROM study_material");
        if ($checkNew->fetchColumn() == 0) {
            $pdo->exec("INSERT INTO study_material (sem, title, description, doc, branch, date) 
                       SELECT semester, subject, mode, study_material, 'Computer Engineering', CURDATE() FROM studymaterialtbl");
            echo "✓ Migrated records from legacy 'studymaterialtbl' to 'study_material'.\n";
        }
    }

} catch(PDOException $e) {
    echo "❌ Error: " . $e->getMessage() . "\n";
}
?>
