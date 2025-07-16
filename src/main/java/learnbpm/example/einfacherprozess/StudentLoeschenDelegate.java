package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.*;

public class StudentLoeschenDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> StudentLoeschenDelegate gestartet");

        Integer studentId = (Integer) execution.getVariable("student_id");
        if (studentId == null) {
            throw new IllegalArgumentException("Prozessvariable 'student_id' ist nicht gesetzt.");
        }

        String url = "jdbc:mysql://camunda-mysql:3306/immatrikulation";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            conn.setAutoCommit(false); // Transaktion starten

            Integer antragId = null;

            // 1. Antrag-ID des Studenten ermitteln
            String selectSql = "SELECT antrag_id FROM studenten WHERE id = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, studentId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        antragId = rs.getObject("antrag_id", Integer.class);
                    }
                }
            }

            // 2. Student löschen
            String deleteStudentSql = "DELETE FROM studenten WHERE id = ?";
            try (PreparedStatement stmtStudent = conn.prepareStatement(deleteStudentSql)) {
                stmtStudent.setInt(1, studentId);
                int studentRows = stmtStudent.executeUpdate();

                if (studentRows == 0) {
                    conn.rollback();
                    throw new RuntimeException("Kein Student mit ID " + studentId + " gefunden oder bereits gelöscht.");
                }

                System.out.println("→ Student mit ID " + studentId + " erfolgreich gelöscht.");
            }

            // 3. Falls vorhanden: zugehörigen Antrag löschen
            if (antragId != null) {
                String deleteAntragSql = "DELETE FROM immatrikulationsantrag WHERE id = ?";
                try (PreparedStatement stmtAntrag = conn.prepareStatement(deleteAntragSql)) {
                    stmtAntrag.setInt(1, antragId);
                    int antragRows = stmtAntrag.executeUpdate();

                    if (antragRows > 0) {
                        System.out.println("→ Antrag mit ID " + antragId + " erfolgreich gelöscht.");
                    } else {
                        System.out.println("→ Kein Antrag mit ID " + antragId + " gefunden.");
                    }
                }
            } else {
                System.out.println("→ Kein Antrag mit dem Studenten verknüpft (antrag_id ist NULL).");
            }

            conn.commit(); // Transaktion erfolgreich abschließen

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Löschen des Studenten und zugehörigen Antrags: " + e.getMessage(), e);
        }
    }
}
