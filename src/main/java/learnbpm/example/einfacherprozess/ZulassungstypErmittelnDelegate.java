package learnbpm.example.einfacherprozess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ZulassungstypErmittelnDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> ZulassungstypErmittelnDelegate gestartet");

        // Studiengang-ID abrufen
        String studiengangIdStr = (String) execution.getVariable("studiengang_id");
        if (studiengangIdStr == null || studiengangIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("studiengang_id ist nicht gesetzt oder leer.");
        }

        int studiengangId = Integer.parseInt(studiengangIdStr);
        String zulassungstyp = null;

        // Verbindung zur Datenbank
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://camunda-mysql:3306/immatrikulation?user=root&password=root");
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT zulassungstyp FROM studiengang WHERE id = ?")) {

            stmt.setInt(1, studiengangId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    zulassungstyp = rs.getString("zulassungstyp");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Abrufen des Zulassungstyps: " + e.getMessage(), e);
        }

        if (zulassungstyp == null || zulassungstyp.trim().isEmpty()) {
            throw new IllegalStateException("Kein Zulassungstyp für den Studiengang gefunden.");
        }

        // Zulassungstyp bereinigen und ggf. vereinheitlichen
        zulassungstyp = zulassungstyp.trim().toLowerCase();

        // Abbildung auf gültige Gateway-Werte
        switch (zulassungstyp) {
            case "nc":
                execution.setVariable("zulassungstyp", "nc");
                break;
            case "unbeschraenkt":
                execution.setVariable("zulassungstyp", "unbeschraenkt");
                break;
            case "zulassungstest":
            case "test":
                execution.setVariable("zulassungstyp", "test");
                break;
            default:
                throw new IllegalArgumentException("Unbekannter Zulassungstyp: " + zulassungstyp);
        }

        System.out.println(">>> Zulassungstyp gesetzt: " + execution.getVariable("zulassungstyp"));
    }
}
