package learnbpm.example.test.einfacherprozess;

import learnbpm.example.einfacherprozess.StudentLoeschenDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StudentLoeschenDelegateTest {

    @Test
    public void testExecuteThrowsWhenStudentIdMissing() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("student_id")).thenReturn(null);

        StudentLoeschenDelegate delegate = new StudentLoeschenDelegate();
        assertThrows(IllegalArgumentException.class, () -> delegate.execute(execution));
    }
}
