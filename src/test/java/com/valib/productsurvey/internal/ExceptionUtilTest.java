package com.valib.productsurvey.internal;

import com.google.common.util.concurrent.UncheckedExecutionException;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class ExceptionUtilTest {
    @Test
    public void extractCauseFromExecutionException() {
        // WHEN
        ExecutionException ee = new ExecutionException(new IllegalArgumentException());

        // THEN
        assertEquals(IllegalArgumentException.class.getName(), ExceptionUtil.toRuntimeException(ee).getClass().getName());
    }

    @Test
    public void extractCauseFromUncheckedExecutionException() {
        // WHEN
        UncheckedExecutionException uee = new UncheckedExecutionException(new IllegalArgumentException());

        // THEN
        assertEquals(IllegalArgumentException.class.getName(), ExceptionUtil.toRuntimeException(uee).getClass().getName());
    }

    @Test
    public void wrapNonRuntimeExceptionWithRuntimeException() {
        // WHEN
        CloneNotSupportedException cnse = new CloneNotSupportedException();

        // THEN
        RuntimeException re = ExceptionUtil.toRuntimeException(cnse);
        assertEquals(RuntimeException.class.getName(), re.getClass().getName());
        assertEquals(CloneNotSupportedException.class.getName(), re.getCause().getClass().getName());
    }

    @Test
    public void doNotWrapRuntimeException() {
        // WHEN
        NullPointerException npe = new NullPointerException();

        // THEN
        RuntimeException re = ExceptionUtil.toRuntimeException(npe);
        assertEquals(NullPointerException.class.getName(), re.getClass().getName());
    }
}
