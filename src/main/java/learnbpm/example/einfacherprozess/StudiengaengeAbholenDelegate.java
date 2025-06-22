package learnbpm.example.einfacherprozess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.Variables.SerializationDataFormats;

public class StudiengaengeAbholenDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> StudiengaengeAbholenDelegate gestartet");

        // Verbindung zur MySQL-Datenbank
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://camunda-mysql:3306/immatrikulation?user=root&password=root");

        Statement stmt = connection.createStatement();
        String sql = "SELECT id, name FROM studiengang";

        ResultSet res = stmt.executeQuery(sql);

        // Map für Studiengänge: id (als String) → name
        Map<String, String> studiengangMap = new LinkedHashMap<>();

        while (res.next()) {
            String id = String.valueOf(res.getInt("id"));
            String name = res.getString("name");
            studiengangMap.put(id, name);
        }

        res.close();
        stmt.close();
        connection.close();

        // Als JSON-Variable speichern
        execution.setVariable("ALLE_STUDIENGAENGE", Variables
            .objectValue(studiengangMap)
            .serializationDataFormat(SerializationDataFormats.JSON)
            .create());

        System.out.println(">>> Studiengänge erfolgreich geladen und als Prozessvariable gesetzt.");
    }
}
