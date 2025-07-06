package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.*;
import java.io.InputStream;

public class ImmatrikulationsantragSpeichernDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        System.out.println("SPEICHER-DELEGATE AUFGERUFEN");

        // === Daten aus dem Prozess abrufen ===
        String nachname = (String) execution.getVariable("nachname");
        String vorname = (String) execution.getVariable("vorname");
        String geburtsdatumStr = (String) execution.getVariable("geburtsdatum");
        Date geburtsdatum = Date.valueOf(geburtsdatumStr);

        String geburtsort = (String) execution.getVariable("geburtsort");
        String staatsangehoerigkeit = (String) execution.getVariable("staatsangehoerigkeit");
        String adresse = (String) execution.getVariable("adresse");
        String email = (String) execution.getVariable("email");
        String telefonnummer = (String) execution.getVariable("telefonnummer");
        int studiengangId = Integer.parseInt((String) execution.getVariable("studiengang_id"));
        Integer hochschulsemester = (Integer) execution.getVariable("hochschulsemester");

        // HZB-Note verarbeiten
        Object hzbNoteObj = execution.getVariable("hzb_note");
        double hzbNote = 0.0;
        if (hzbNoteObj != null) {
            String hzbNoteStr = hzbNoteObj.toString().replace(",", ".");
            hzbNote = Double.parseDouble(hzbNoteStr);
        } else {
            throw new IllegalArgumentException("HZB-Note wurde nicht übergeben!");
        }

        // Dateien
        FileValue hzbFile = (FileValue) execution.getVariableTyped("hzb_zeugnis");
        FileValue krankFile = (FileValue) execution.getVariableTyped("krankenversicherung");

        if (hzbFile == null || krankFile == null || hzbFile.getValue() == null || krankFile.getValue() == null) {
            throw new IllegalArgumentException("Dateiupload fehlt oder ist leer!");
        }

        InputStream hzbInput = hzbFile.getValue();
        InputStream krankInput = krankFile.getValue();

        // Verbindungsinformationen
        String url = "jdbc:mysql://camunda-mysql:3306/immatrikulation";
        String user = "root";
        String password = "root";

        // Prüfen, ob bereits ein Antrag existiert
        Object antragIdObj = execution.getVariable("antrag_id");
        boolean isUpdate = (antragIdObj != null);
        String sql;

        if (isUpdate) {
            sql = "UPDATE immatrikulationsantrag SET " +
                    "nachname = ?, vorname = ?, geburtsdatum = ?, geburtsort = ?, staatsangehoerigkeit = ?, " +
                    "adresse = ?, email = ?, telefonnummer = ?, studiengang_id = ?, hochschulsemester = ?, " +
                    "hzb_note = ?, hzb_zeugnis = ?, krankenversicherung = ? " +
                    "WHERE id = ?";
        } else {
            sql = "INSERT INTO immatrikulationsantrag " +
                    "(nachname, vorname, geburtsdatum, geburtsort, staatsangehoerigkeit, adresse, email, telefonnummer, " +
                    "studiengang_id, hochschulsemester, hzb_note, hzb_zeugnis, krankenversicherung) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Parameter setzen
            stmt.setString(1, nachname);
            stmt.setString(2, vorname);
            stmt.setDate(3, geburtsdatum);
            stmt.setString(4, geburtsort);
            stmt.setString(5, staatsangehoerigkeit);
            stmt.setString(6, adresse);
            stmt.setString(7, email);
            stmt.setString(8, telefonnummer);
            stmt.setInt(9, studiengangId);
            stmt.setInt(10, hochschulsemester);
            stmt.setDouble(11, hzbNote);
            stmt.setBlob(12, hzbInput);
            stmt.setBlob(13, krankInput);

            if (isUpdate) {
                stmt.setInt(14, (Integer) antragIdObj);
                stmt.executeUpdate();
                System.out.println("Antrag aktualisiert mit ID: " + antragIdObj);
            } else {
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int antragId = rs.getInt(1);
                    execution.setVariable("antrag_id", antragId);

                    // Download-Links erzeugen
                    String baseUrl = "http://localhost:8080/u-einfacher-prozess";
                    String hzbLink = baseUrl + "/download/hzb?id=" + antragId;
                    String krankLink = baseUrl + "/download/kv?id=" + antragId;

                    execution.setVariable("hzb_download_link", hzbLink);
                    execution.setVariable("krankenversicherung_download_link", krankLink);
                    execution.setVariable("hzb_zeugnis_name", hzbFile.getFilename());
                    execution.setVariable("krankenversicherung_name", krankFile.getFilename());

                    System.out.println("Antrag erfolgreich gespeichert mit ID: " + antragId);
                    System.out.println("HZB-Link: " + hzbLink);
                    System.out.println("KV-Link: " + krankLink);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Speichern/Aktualisieren in der Datenbank: " + e.getMessage(), e);
        }
    }
}
