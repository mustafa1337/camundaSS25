package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.*;

public class RueckmeldungSetzenDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> RueckmeldungSetzenDelegate gestartet");

        Integer studentId = (Integer) execution.getVariable("student_id");
        if (studentId == null) {
            throw new IllegalArgumentException("Prozessvariable 'student_id' ist nicht gesetzt.");
        }

        // Zielwert
        Date rueckmeldungBis = Date.valueOf("2026-03-30");

        // DB-Verbindung
        String url = "jdbc:mysql://camunda-mysql:3306/immatrikulation";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            String updateSql = "UPDATE studenten SET zurückgemeldet_bis = ? WHERE id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setDate(1, rueckmeldungBis);
                stmt.setInt(2, studentId);

                int rows = stmt.executeUpdate();

                if (rows == 0) {
                    throw new RuntimeException("Kein Student mit ID " + studentId + " gefunden oder nichts aktualisiert.");
                }

                System.out.println("Student mit ID " + studentId + " wurde erfolgreich bis zum " + rueckmeldungBis + " zurückgemeldet.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Setzen des Rückmeldedatums: " + e.getMessage(), e);
        }
    }
}
