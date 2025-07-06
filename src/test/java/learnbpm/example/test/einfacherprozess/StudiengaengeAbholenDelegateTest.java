package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.StudiengaengeAbholenDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StudiengaengeAbholenDelegateTest {

    @Test
    public void testExecuteFailsDueToMissingDatabase() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);

        StudiengaengeAbholenDelegate delegate = new StudiengaengeAbholenDelegate();
        assertThrows(Exception.class, () -> delegate.execute(execution));
    }
}
