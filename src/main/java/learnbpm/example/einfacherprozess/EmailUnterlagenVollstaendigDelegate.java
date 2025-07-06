package learnbpm.example.einfacherprozess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

public class EmailUnterlagenVollstaendigDelegate implements JavaDelegate {

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
        String emailEmpfaenger = (String) execution.getVariable("email");

        // Nachrichtentext
        String nachricht =
            "Sehr geehrte/r " + vorname + " " + nachname + ",\n\n" +
            "vielen Dank für die Einreichung Ihres Immatrikulationsantrags an der Hochschule XYZ.\n\n" +
            "Wir freuen uns, Ihnen mitteilen zu können, dass Ihre Unterlagen vollständig sind und erfolgreich geprüft wurden.\n\n" +
            "Sie erhalten in Kürze weitere Informationen zum weiteren Ablauf der Immatrikulation.\n\n" +
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
        email.setSubject("Ihr Immatrikulationsantrag – Unterlagen vollständig");
        email.setMsg(nachricht);
        email.addTo(emailEmpfaenger);

        email.send();
        System.out.println("Bestätigungs-E-Mail erfolgreich versendet an: " + emailEmpfaenger);
    }
}
