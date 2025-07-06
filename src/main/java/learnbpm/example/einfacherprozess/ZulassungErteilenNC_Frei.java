package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ZulassungErteilenNC_Frei implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> ZulassungErteilenDelegate gestartet");

        // Antrag-ID abrufen
        Object antragIdObj = execution.getVariable("antrag_id");
        if (antragIdObj == null) {
            throw new IllegalStateException("Prozessvariable 'antrag_id' fehlt.");
        }

        int antragId = (Integer) antragIdObj;

        // DB-Verbindung herstellen
        String url = "jdbc:mysql://camunda-mysql:3306/immatrikulation";
        String user = "root";
        String password = "root";

        String updateSql = "UPDATE immatrikulationsantrag SET zulassung = 1 WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setInt(1, antragId);
            int affected = stmt.executeUpdate();

            if (affected > 0) {
                System.out.println(">>> Zulassung erfolgreich erteilt für Antrag-ID: " + antragId);
            } else {
                throw new RuntimeException(">>> Keine Zeile aktualisiert. Prüfe, ob Antrag-ID existiert: " + antragId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Aktualisieren der Zulassung: " + e.getMessage(), e);
        }
    }
}
