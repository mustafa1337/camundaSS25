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

                    // Alle Bewerber dieses Studiengangs inklusive bisheriger Zulassungswerte
                    String sqlBewerber = "SELECT id, hzb_note, zulassung FROM immatrikulationsantrag WHERE studiengang_id = ?";
                    try (PreparedStatement stmtBewerber = conn.prepareStatement(sqlBewerber)) {
                        stmtBewerber.setInt(1, studiengangId);
                        ResultSet rsBewerber = stmtBewerber.executeQuery();

                        List<Bewerber> bewerberList = new ArrayList<>();
                        while (rsBewerber.next()) {
                            int antragId = rsBewerber.getInt("id");
                            Double noteObj = rsBewerber.getObject("hzb_note", Double.class);
                            Integer status = (Integer) rsBewerber.getObject("zulassung");
                            bewerberList.add(new Bewerber(antragId, noteObj, status));
                        }

                        // Zufällige Reihenfolge vor Sortierung für faire Behandlung bei gleichen Noten
                        Collections.shuffle(bewerberList);
                        bewerberList.sort(Comparator.comparing((Bewerber b) -> b.note, Comparator.nullsLast(Double::compare)));

                        for (int i = 0; i < bewerberList.size(); i++) {
                            Bewerber b = bewerberList.get(i);

                            boolean inTop = i < maxZulassungen;
                            int neuerStatus;
                            if (inTop) {
                                neuerStatus = 1;
                            } else {
                                // Bewerber war zuvor zugelassen, rutscht aber nun aus dem Kontingent
                                if (b.status != null && b.status == 1) {
                                    neuerStatus = 2;
                                } else {
                                    neuerStatus = 0;
                                }
                            }

                            if (b.status == null || b.status != neuerStatus) {
                                String sqlUpdate = "UPDATE immatrikulationsantrag SET zulassung = ? WHERE id = ?";
                                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                                    stmtUpdate.setInt(1, neuerStatus);
                                    stmtUpdate.setInt(2, b.antragId);
                                    stmtUpdate.executeUpdate();
                                }
                            }

                            System.out.println(" → Antrag " + b.antragId + (neuerStatus == 1 ? " ZUGELASSEN" : " ABGELEHNT") + " (Note: " + b.note + ")");

                            if (b.antragId == aktuellerAntragId) {
                                istZugelassen = (neuerStatus == 1);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler bei NC-Zulassungsprüfung: " + e.getMessage(), e);
        }

        // Prozessvariable setzen
        execution.setVariable("nc_zugelassen", istZugelassen);
        System.out.println(">> Prozessvariable 'nc_zugelassen' = " + istZugelassen);
        System.out.println(">>> NC-Zulassungsprüfung abgeschlossen.");
    }

    private static class Bewerber {
        int antragId;
        Double note;
        Integer status;

        public Bewerber(int antragId, Double note, Integer status) {
            this.antragId = antragId;
            this.note = note;
            this.status = status;
        }
    }
}