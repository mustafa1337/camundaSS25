package learnbpm.example.einfacherprozess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ImmatrikulationsantragSpeichernDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DriverManager.getConnection(
            "jdbc:mysql://camunda-mysql:3306/immatrikulation?user=root&password=root"
        );

        String sql = "INSERT INTO immatrikulationsantrag " +
                     "(nachname, vorname, geburtsdatum, geburtsort, staatsangehoerigkeit, adresse, email, telefonnummer, " +
                     "studiengang_id, abschluss, hochschulsemester, " +
                     "hzb_zeugnis_name, krankenversicherung_name) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = connection.prepareStatement(sql);

        stmt.setString(1, (String) execution.getVariable("nachname"));
        stmt.setString(2, (String) execution.getVariable("vorname"));
        stmt.setString(3, (String) execution.getVariable("geburtsdatum"));
        stmt.setString(4, (String) execution.getVariable("geburtsort"));
        stmt.setString(5, (String) execution.getVariable("staatsangehoerigkeit"));
        stmt.setString(6, (String) execution.getVariable("adresse"));
        stmt.setString(7, (String) execution.getVariable("email"));
        stmt.setString(8, (String) execution.getVariable("telefonnummer"));
        stmt.setInt(9, (Integer) execution.getVariable("studiengang_id"));
        stmt.setString(10, (String) execution.getVariable("abschluss"));
        stmt.setInt(11, (Integer) execution.getVariable("hochschulsemester"));
        stmt.setString(12, (String) execution.getVariable("hzb_zeugnis_name"));
        stmt.setString(13, (String) execution.getVariable("krankenversicherung_name"));

        stmt.executeUpdate();

        connection.close();
    }
}
