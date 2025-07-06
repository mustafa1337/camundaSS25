package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.ZulassungstypErmittelnDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ZulassungstypErmittelnDelegateTest {

    @Test
    public void testExecuteThrowsWhenStudiengangIdMissing() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("studiengang_id")).thenReturn(null);

        ZulassungstypErmittelnDelegate delegate = new ZulassungstypErmittelnDelegate();
        assertThrows(IllegalArgumentException.class, () -> delegate.execute(execution));
    }
}
