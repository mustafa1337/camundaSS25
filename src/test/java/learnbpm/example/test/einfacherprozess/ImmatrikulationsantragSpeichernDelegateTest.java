package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.ImmatrikulationsantragSpeichernDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ImmatrikulationsantragSpeichernDelegateTest {

    @Test
    public void testExecuteThrowsWhenFilesMissing() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("nachname")).thenReturn("Mustermann");
        when(execution.getVariable("vorname")).thenReturn("Max");
        when(execution.getVariable("geburtsdatum")).thenReturn("2000-01-01");
        when(execution.getVariable("geburtsort")).thenReturn("Berlin");
        when(execution.getVariable("staatsangehoerigkeit")).thenReturn("DE");
        when(execution.getVariable("adresse")).thenReturn("Adresse");
        when(execution.getVariable("email")).thenReturn("test@example.com");
        when(execution.getVariable("telefonnummer")).thenReturn("123");
        when(execution.getVariable("studiengang_id")).thenReturn("1");
        when(execution.getVariable("hochschulsemester")).thenReturn(1);
        when(execution.getVariable("hzb_note")).thenReturn("1.0");
        when(execution.getVariableTyped("hzb_zeugnis")).thenReturn(null);
        when(execution.getVariableTyped("krankenversicherung")).thenReturn(null);

        ImmatrikulationsantragSpeichernDelegate delegate = new ImmatrikulationsantragSpeichernDelegate();
        assertThrows(IllegalArgumentException.class, () -> delegate.execute(execution));
    }
}
