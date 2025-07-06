package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.*;
import java.time.LocalDate;

public class TEST_DB_INSERT_DATA implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> Camunda Delegate wird ausgeführt.");
        insertTestdaten();
    }

    public static void main(String[] args) {
        System.out.println(">>> Manuelle Ausführung (ohne Camunda)");

        try {
            insertTestdaten();
            System.out.println(">>> Testdaten wurden erfolgreich eingefügt.");
        } catch (Exception e) {
            System.err.println("Fehler beim Einfügen der Testdaten:");
            e.printStackTrace();
        }
    }

    private static void insertTestdaten() throws SQLException {
        String url = "jdbc:mysql://localhost:3307/immatrikulation";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            String sql = "INSERT INTO immatrikulationsantrag " +
                    "(nachname, vorname, geburtsdatum, geburtsort, staatsangehoerigkeit, adresse, email, telefonnummer, studiengang_id, hochschulsemester, hzb_note, zulassung) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int counter = 1;

                for (int studiengangId = 1; studiengangId <= 6; studiengangId++) {
                    // Zwei mit Zulassung = 1
                    for (int i = 0; i < 2; i++) {
                        addEintrag(stmt, counter++, studiengangId, 1);
                    }
                    // Zwei mit Zulassung = NULL
                    for (int i = 0; i < 2; i++) {
                        addEintrag(stmt, counter++, studiengangId, null);
                    }
                }

                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private static void addEintrag(PreparedStatement stmt, int counter, int studiengangId, Integer zulassung) throws SQLException {
        stmt.setString(1, "Mustermann" + counter);
        stmt.setString(2, "Max" + counter);
        stmt.setDate(3, Date.valueOf(LocalDate.of(2000, 1, 1).plusDays(counter)));
        stmt.setString(4, "Frankfurt am Main");
        stmt.setString(5, "DE");
        stmt.setString(6, "Beispielstraße 5, 60311 Frankfurt");
        stmt.setString(7, "max" + counter + "@example.com");
        stmt.setString(8, "0151-123456" + counter);
        stmt.setInt(9, studiengangId);
        stmt.setInt(10, 1 + (counter % 6));
        stmt.setDouble(11, 1.0 + (counter % 5) * 0.3);
        if (zulassung != null) {
            stmt.setInt(12, zulassung);
        } else {
            stmt.setNull(12, Types.TINYINT);
        }
        stmt.addBatch();
    }
}
