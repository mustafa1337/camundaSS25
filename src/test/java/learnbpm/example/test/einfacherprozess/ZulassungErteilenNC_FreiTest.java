package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.ZulassungErteilenNC_Frei;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ZulassungErteilenNC_FreiTest {

    @Test
    public void testExecuteThrowsWhenAntragIdMissing() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("antrag_id")).thenReturn(null);

        ZulassungErteilenNC_Frei delegate = new ZulassungErteilenNC_Frei();
        assertThrows(IllegalStateException.class, () -> delegate.execute(execution));
    }
}
