package com.timgroup.blondin.diagnostics;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.timgroup.blondin.config.BlondingDiagnosticsConfiguration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class ExternalRecorderTest {

    private final DummyRecordingLogHandler handler = new DummyRecordingLogHandler();
    private final BlondingDiagnosticsConfiguration config = new BlondingDiagnosticsConfiguration("", "", 1, 1, "");
    private final ExternalRecorder externalRecorder = new ExternalRecorder(config);

    @Before
    public void addHandler() {
        Logger.getLogger("").addHandler(handler);
    }

    @After
    public void removeHandler() {
        Logger.getLogger("").removeHandler(handler);
    }

    @Test public void
    logs_errors() {
        final Throwable throwable = new NullPointerException();
        
        externalRecorder.logError(ExternalRecorderTest.class, "foo", throwable);
        
        assertThat(handler.records.size(), is(1));
        assertThat(handler.records.get(0).getLevel().getName(), is("SEVERE"));
        assertThat(handler.records.get(0).getMessage(), is("foo"));
        assertThat(handler.records.get(0).getThrown(), is(throwable));
    }

    @Test public void
    logs_warnings() {
        final Throwable throwable = new NullPointerException();
        
        externalRecorder.logWarning(ExternalRecorderTest.class, "foo", throwable);
        
        assertThat(handler.records.size(), is(1));
        assertThat(handler.records.get(0).getLevel().getName(), is("WARNING"));
        assertThat(handler.records.get(0).getMessage(), is("foo"));
        assertThat(handler.records.get(0).getThrown(), is(throwable));
    }

    @Test public void
    logs_warnings_without_throwable() {
        externalRecorder.logWarning(ExternalRecorderTest.class, "foo");
        
        assertThat(handler.records.size(), is(1));
        assertThat(handler.records.get(0).getLevel().getName(), is("WARNING"));
        assertThat(handler.records.get(0).getMessage(), is("foo"));
        assertThat(handler.records.get(0).getThrown(), is(nullValue()));
    }

    private static final class DummyRecordingLogHandler extends Handler {
        private final List<LogRecord> records = Lists.newArrayList();
        
        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }
        
        @Override
        public void flush() { }
        
        @Override
        public void close() throws SecurityException { }
    }
}
