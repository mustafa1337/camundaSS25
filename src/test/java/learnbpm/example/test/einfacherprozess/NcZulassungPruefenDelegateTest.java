package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.NcZulassungPruefenDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class NcZulassungPruefenDelegateTest {

    @Test
    public void testExecuteThrowsWhenAntragIdMissing() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("antrag_id")).thenReturn(null);

        NcZulassungPruefenDelegate delegate = new NcZulassungPruefenDelegate();
        assertThrows(IllegalArgumentException.class, () -> delegate.execute(execution));
    }
}
