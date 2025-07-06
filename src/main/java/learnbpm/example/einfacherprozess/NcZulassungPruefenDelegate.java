package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.*;
import java.util.*;

public class NcZulassungPruefenDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(">>> NC-Zulassungsprüfung gestartet...");

        Integer aktuellerAntragId = (Integer) execution.getVariable("antrag_id");
        if (aktuellerAntragId == null) {
            throw new IllegalArgumentException("Prozessvariable 'antrag_id' fehlt.");
        }

        String url = "jdbc:mysql://camunda-mysql:3306/immatrikulation";
        String user = "root";
        String password = "root";

        boolean istZugelassen = false; // wird später als Camunda-Prozessvariable gesetzt

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            String sqlStudiengaenge = "SELECT id, max_anzahl_zulassen FROM studiengang WHERE zulassungstyp = 'nc'";
            try (PreparedStatement stmtStudiengaenge = conn.prepareStatement(sqlStudiengaenge);
                 ResultSet rsStudiengaenge = stmtStudiengaenge.executeQuery()) {

                while (rsStudiengaenge.next()) {
                    int studiengangId = rsStudiengaenge.getInt("id");
                    int maxZulassungen = rsStudiengaenge.getInt("max_anzahl_zulassen");

                    // Anzahl bereits zugelassener Bewerber
                    int bereitsZugelassen = 0;
                    String sqlZugelassen = "SELECT COUNT(*) FROM immatrikulationsantrag WHERE studiengang_id = ? AND zulassung = 1";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlZugelassen)) {
                        stmt.setInt(1, studiengangId);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            bereitsZugelassen = rs.getInt(1);
                        }
                    }

                    int nochVerfuegbar = maxZulassungen - bereitsZugelassen;
                    if (nochVerfuegbar <= 0) {
                        System.out.println("!! Keine Plätze mehr verfügbar für Studiengang " + studiengangId);
                        continue;
                    }

                    // Alle Bewerber mit diesem Studiengang, Zulassung noch nicht gesetzt
                    String sqlBewerber = "SELECT id, hzb_note FROM immatrikulationsantrag WHERE studiengang_id = ? AND zulassung IS NULL";
                    try (PreparedStatement stmtBewerber = conn.prepareStatement(sqlBewerber)) {
                        stmtBewerber.setInt(1, studiengangId);
                        ResultSet rsBewerber = stmtBewerber.executeQuery();

                        List<Bewerber> bewerberList = new ArrayList<>();
                        while (rsBewerber.next()) {
                            int antragId = rsBewerber.getInt("id");
                            double note = rsBewerber.getDouble("hzb_note");
                            bewerberList.add(new Bewerber(antragId, note));
                        }

                        Collections.shuffle(bewerberList); // Gleichstand fair behandeln
                        bewerberList.sort(Comparator.comparingDouble(b -> b.note)); // Beste Note zuerst

                        for (int i = 0; i < bewerberList.size(); i++) {
                            Bewerber b = bewerberList.get(i);
                            boolean zugelassen = (i < nochVerfuegbar);

                            // Datenbank-Update
                            String sqlUpdate = "UPDATE immatrikulationsantrag SET zulassung = ? WHERE id = ?";
                            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                                stmtUpdate.setInt(1, zugelassen ? 1 : 0);
                                stmtUpdate.setInt(2, b.antragId);
                                stmtUpdate.executeUpdate();
                            }

                            System.out.println(" → Antrag " + b.antragId + (zugelassen ? " ZUGELASSEN" : " ABGELEHNT") + " (Note: " + b.note + ")");

                            // Setze Prozessvariable nur für den aktuellen Antrag
                            if (b.antragId == aktuellerAntragId) {
                                istZugelassen = zugelassen;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler bei NC-Zulassungsprüfung: " + e.getMessage(), e);
        }

        // ✅ Prozessvariable setzen
        execution.setVariable("nc_zugelassen", istZugelassen);
        System.out.println(">> Prozessvariable 'nc_zugelassen' = " + istZugelassen);
        System.out.println(">>> NC-Zulassungsprüfung abgeschlossen.");
    }

    private static class Bewerber {
        int antragId;
        double note;

        public Bewerber(int antragId, double note) {
            this.antragId = antragId;
            this.note = note;
        }
    }
}
