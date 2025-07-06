package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.emailUnterlagenUnvollst;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EmailUnterlagenUnvollstDelegateTest {

    @Test
    public void testExecuteThrowsExceptionWhenEmailMissing() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("vorname")).thenReturn("Max");
        when(execution.getVariable("nachname")).thenReturn("Mustermann");
        when(execution.getVariable("kommentar")).thenReturn("Fehlt");
        when(execution.getVariable("email")).thenReturn(null);

        emailUnterlagenUnvollst delegate = new emailUnterlagenUnvollst();
        assertThrows(Exception.class, () -> delegate.execute(execution));
    }
}
