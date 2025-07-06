package learnbpm.example.einfacherprozess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class StudiengangNameErmittelnDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> StudiengangNameErmittelnDelegate gestartet");

        // Studiengang-ID aus Prozessvariable lesen
        String studiengangIdStr = (String) execution.getVariable("studiengang_id");

        if (studiengangIdStr == null || studiengangIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("studiengang_id ist nicht gesetzt oder leer.");
        }

        int studiengangId = Integer.parseInt(studiengangIdStr);
        String studiengangName = "[Unbekannt]";

        // Datenbankverbindung herstellen
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://camunda-mysql:3306/immatrikulation?user=root&password=root");
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT name FROM studiengang WHERE id = ?")) {

            stmt.setInt(1, studiengangId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    studiengangName = rs.getString("name");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Abrufen des Studiengang-Namens: " + e.getMessage(), e);
        }

        // Studiengangsname als Prozessvariable setzen
        execution.setVariable("studiengang_name", studiengangName);
        System.out.println(">>> Studiengangsname gesetzt: " + studiengangName);
    }
}
