package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.StudiengangNameErmittelnDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StudiengangNameErmittelnDelegateTest {

    @Test
    public void testExecuteThrowsWhenStudiengangIdMissing() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("studiengang_id")).thenReturn(null);

        StudiengangNameErmittelnDelegate delegate = new StudiengangNameErmittelnDelegate();
        assertThrows(IllegalArgumentException.class, () -> delegate.execute(execution));
    }
}
