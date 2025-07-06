package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.*;

public class StudentLoeschenDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> StudentLoeschenDelegate gestartet");

        // 1. ID aus Prozessvariable holen
        Integer studentId = (Integer) execution.getVariable("student_id");
        if (studentId == null) {
            throw new IllegalArgumentException("Prozessvariable 'student_id' ist nicht gesetzt.");
        }

        // 2. Datenbankverbindung
        String url = "jdbc:mysql://camunda-mysql:3306/immatrikulation";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            String deleteSql = "DELETE FROM studenten WHERE id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setInt(1, studentId);

                int rows = stmt.executeUpdate();

                if (rows == 0) {
                    throw new RuntimeException("Kein Student mit ID " + studentId + " gefunden oder bereits gelöscht.");
                }

                System.out.println("Student mit ID " + studentId + " erfolgreich aus der Tabelle 'studenten' gelöscht.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Löschen des Studenten: " + e.getMessage(), e);
        }
    }
}
