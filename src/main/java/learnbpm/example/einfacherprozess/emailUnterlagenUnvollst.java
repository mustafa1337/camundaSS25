package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

public class emailUnterlagenUnvollst implements JavaDelegate {

    // SMTP-Konfiguration für den Mailversand
    private static final String HOST = "smtp.ethereal.email";
    private static final String USER = "tyree.okon39@ethereal.email";
    private static final String PWD = "pZSs4NXJsjBf42zz2T";
    private static final Integer PORT = 587;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Prozessvariablen abrufen
        String vorname = (String) execution.getVariable("vorname");
        String nachname = (String) execution.getVariable("nachname");
        String kommentar = (String) execution.getVariable("kommentar");
        String emailEmpfaenger = (String) execution.getVariable("email"); // echte Bewerber-Mail

        // Fallback bei leerem Kommentar
        if (kommentar == null || kommentar.trim().isEmpty()) {
            kommentar = "Keine weiteren Hinweise angegeben.";
        }

        // E-Mail-Inhalt formulieren
        String nachricht =
            "Sehr geehrte/r " + vorname + " " + nachname + ",\n\n" +
            "vielen Dank für die Einreichung Ihres Immatrikulationsantrags an der Hochschule XYZ.\n\n" +
            "Leider konnten Ihre Unterlagen nicht vollständig geprüft werden. Bitte reichen Sie die fehlenden oder korrigierten Dokumente so bald wie möglich nach.\n\n" +
            "Kommentar der Sachbearbeitung:\n" +
            kommentar + "\n\n" +
            "Bei Rückfragen stehen wir Ihnen gerne zur Verfügung.\n\n" +
            "Mit freundlichen Grüßen\n" +
            "Ihr Studierendensekretariat\n" +
            "Hochschule XYZ";

        // E-Mail vorbereiten und senden
        Email email = new SimpleEmail();
        email.setCharset("utf-8");
        email.setHostName(HOST);
        email.setAuthentication(USER, PWD);
        email.setSmtpPort(PORT);
        email.setStartTLSEnabled(true);
        email.setSSLOnConnect(false);
        email.setFrom(USER, "Studierendensekretariat Hochschule XYZ");
        email.setSubject("Ihr Immatrikulationsantrag – Unterlagen erforderlich");
        email.setMsg(nachricht);

        // Empfänger setzen
        email.addTo(emailEmpfaenger);

        email.send();
        System.out.println("E-Mail erfolgreich versendet an: " + emailEmpfaenger);
    }
}
