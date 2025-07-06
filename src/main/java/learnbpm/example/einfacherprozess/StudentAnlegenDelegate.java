package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StudentAnlegenDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> StudentAnlegenDelegate gestartet");

        // 1. Antrag-ID aus dem Prozesskontext holen
        Integer antragId = (Integer) execution.getVariable("antrag_id");
        if (antragId == null) {
            throw new IllegalArgumentException("Prozessvariable 'antrag_id' ist nicht gesetzt.");
        }

        // 2. Datenbankverbindung
        String url = "jdbc:mysql://camunda-mysql:3306/immatrikulation";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // 3. Prüfen, ob Antrag überhaupt zugelassen wurde
            String checkSql = "SELECT zulassung FROM immatrikulationsantrag WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, antragId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    throw new RuntimeException("Kein Antrag mit ID " + antragId + " gefunden.");
                }

                int zulassung = rs.getInt("zulassung");
                if (zulassung != 1) {
                    System.out.println("Antrag wurde nicht zugelassen. Kein Student wird angelegt.");
                    return;
                }
            }

            // 4. Prozessvariablen für Studentendaten abrufen
            String vorname = (String) execution.getVariable("vorname");
            String nachname = (String) execution.getVariable("nachname");
            int studiengangId = Integer.parseInt((String) execution.getVariable("studiengang_id"));
            int semester = (Integer) execution.getVariable("hochschulsemester");

            // 5. Matrikelnummer generieren
            String prefix = String.format("%02d", studiengangId);
            String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            int zufall = (int) (Math.random() * 9000) + 1000;
            String matrikelnummer = prefix + datePart + zufall;

            // 6. Student in Datenbank einfügen inklusive antrag_id
            String insertSql = "INSERT INTO studenten (nachname, vorname, matrikelnummer, studiengang_id, semester, zurückgemeldet_bis, antrag_id) " +
                               "VALUES (?, ?, ?, ?, ?, NULL, ?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, nachname);
                insertStmt.setString(2, vorname);
                insertStmt.setString(3, matrikelnummer);
                insertStmt.setInt(4, studiengangId);
                insertStmt.setInt(5, semester);
                insertStmt.setInt(6, antragId); // <-- Verknüpfung über Foreign Key

                int rows = insertStmt.executeUpdate();

                if (rows == 0) {
                    throw new SQLException("Einfügen fehlgeschlagen, keine Zeile wurde erstellt.");
                }

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int studentId = generatedKeys.getInt(1);
                        execution.setVariable("student_id", studentId);
                        execution.setVariable("matrikelnummer", matrikelnummer);

                        System.out.println("Student erfolgreich angelegt mit ID: " + studentId + " und Matrikelnummer: " + matrikelnummer);
                    } else {
                        throw new SQLException("Einfügen erfolgreich, aber keine ID zurückgegeben.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Anlegen des Studenten: " + e.getMessage(), e);
        }
    }
}
