package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.EmailUnterlagenVollstaendigDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EmailUnterlagenVollstaendigDelegateTest {

    @Test
    public void testExecuteThrowsExceptionWhenEmailMissing() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("vorname")).thenReturn("Max");
        when(execution.getVariable("nachname")).thenReturn("Mustermann");
        when(execution.getVariable("email")).thenReturn(null);

        EmailUnterlagenVollstaendigDelegate delegate = new EmailUnterlagenVollstaendigDelegate();
        assertThrows(Exception.class, () -> delegate.execute(execution));
    }
}
