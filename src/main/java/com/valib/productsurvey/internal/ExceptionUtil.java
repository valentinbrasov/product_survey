package com.valib.productsurvey.internal;

import com.google.common.util.concurrent.UncheckedExecutionException;

import java.util.concurrent.ExecutionException;

public class ExceptionUtil {
    private ExceptionUtil() {
    }

    public static RuntimeException toRuntimeException(Throwable t) {
        Throwable cause;
        if (t instanceof ExecutionException && t.getCause() != null) {
            cause = t.getCause();
        } else if (t instanceof UncheckedExecutionException && t.getCause() != null) {
            cause = t.getCause();
        } else {
            cause = t;
        }

        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        }

        return new RuntimeException(cause);
    }
}
