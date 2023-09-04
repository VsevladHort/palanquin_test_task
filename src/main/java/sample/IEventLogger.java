package sample;

import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This class should be implemented to provide event logging capabilities.
 * <p>
 * Objective:
 * Implement methods to log events and get a list of all logged events in reverse order.
 * <p>
 * Example:
 * 1. log("Event1")
 * 2. log("Event2")
 * 3. log("Event3")
 * 4. getEventsInReverse() should return ["Event3", "Event2", "Event1"]
 */
public class IEventLogger {

    private final LinkedList<String> loggedEvents = new LinkedList<>();
    private OutputStream outputStream;
    private final Object OutputStreamMonitor = new Object();
    private OutputStreamWriter outputStreamWriter;
    private String charset;

    public IEventLogger() {
        outputStream = System.out;
        outputStreamWriter = new OutputStreamWriter(outputStream);
    }

    public IEventLogger(OutputStream stream, String charset) throws UnsupportedEncodingException {
        outputStream = stream;
        outputStreamWriter = new OutputStreamWriter(outputStream, charset);
    }

    public void log(String event) throws IOException {
        var time = new Date();
        synchronized (this) {
            loggedEvents.addFirst(event);
        }
        synchronized (OutputStreamMonitor) {
            outputStreamWriter.write(time.toString());
            outputStreamWriter.write(" : ");
            outputStreamWriter.write(event);
            outputStreamWriter.write(System.lineSeparator());
            outputStreamWriter.flush();
        }
    }

    /*
     * Given the way events are stored in-memory, strictly speaking, it is permissible to just return a reference to
     * our loggedEvents LinkedList object. However, that runs the risk of it being modified elsewhere, hence why I went
     * with returning a shallow copy of the list, at the expense of time and space.
     * This may be a concern, given it is a Logger class, since our log may get rather large, but it is safe from the
     * point of view of data integrity, as we can be sure this log is not being unintentionally tampered with.
     */
    public List<String> getEventsInReverse() {
        synchronized (this) {
            return (List<String>) loggedEvents.clone();
        }
    }

    public void setOutputStream(OutputStream outputStream) throws UnsupportedEncodingException {
        synchronized (OutputStreamMonitor) {
            this.outputStream = outputStream;
            outputStreamWriter = new OutputStreamWriter(outputStream, charset);
        }
    }

    public synchronized void setCharset(String charset) throws UnsupportedEncodingException {
        synchronized (OutputStreamMonitor) {
            this.charset = charset;
            outputStreamWriter = new OutputStreamWriter(outputStream, charset);
        }
    }
}