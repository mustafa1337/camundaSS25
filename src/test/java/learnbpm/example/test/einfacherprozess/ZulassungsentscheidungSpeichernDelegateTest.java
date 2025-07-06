package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.ZulassungsentscheidungSpeichernDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ZulassungsentscheidungSpeichernDelegateTest {

    @Test
    public void testExecuteThrowsWhenVariablesMissing() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("antrag_id")).thenReturn(null);
        when(execution.getVariable("zulassung_bestanden")).thenReturn(null);

        ZulassungsentscheidungSpeichernDelegate delegate = new ZulassungsentscheidungSpeichernDelegate();
        assertThrows(IllegalArgumentException.class, () -> delegate.execute(execution));
    }
}
