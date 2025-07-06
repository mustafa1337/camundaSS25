package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ZulassungsentscheidungSpeichernDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> ZulassungsentscheidungSpeichernDelegate gestartet");

        // Prozessvariablen abrufen
        Integer antragId = (Integer) execution.getVariable("antrag_id");
        Boolean bestanden = (Boolean) execution.getVariable("zulassung_bestanden");

        if (antragId == null || bestanden == null) {
            throw new IllegalArgumentException("antrag_id oder zulassung_bestanden ist null!");
        }

        // Boolean in int (1/0) konvertieren
        int zulassungswert = bestanden ? 1 : 0;

        // DB-Verbindung
        String url = "jdbc:mysql://camunda-mysql:3306/immatrikulation";
        String user = "root";
        String password = "root";

        String updateSQL = "UPDATE immatrikulationsantrag SET zulassung = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setInt(1, zulassungswert);
            stmt.setInt(2, antragId);

            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Kein Antrag mit ID " + antragId + " gefunden.");
            }

            System.out.println("Zulassung für Antrag ID " + antragId + " erfolgreich gesetzt: " + zulassungswert);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Aktualisieren der Zulassung: " + e.getMessage(), e);
        }
    }
}
