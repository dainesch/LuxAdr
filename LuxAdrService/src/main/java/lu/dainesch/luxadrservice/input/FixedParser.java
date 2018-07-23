package lu.dainesch.luxadrservice.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedParser implements AutoCloseable {

    public static final Logger LOG = LoggerFactory.getLogger(FixedParser.class);

    public static final String BOOL_TRUE = "O";
    public static final String NULL_VAL = "?";
    public static final String DATE_FORMAT = "dd.MM.yyyy";

    private final BufferedReader reader;
    private final int[] colEnd;

    private boolean done = false;
    private String currentLine = null;

    public FixedParser(InputStream input, int... colSpec) {
        this.colEnd = new int[colSpec.length];

        int pos = 0;
        for (int i = 0; i < colSpec.length; i++) {
            if (colSpec[i] <= 0) {
                throw new IllegalArgumentException("Colum size must be greater than zero");
            }
            pos += colSpec[i];
            this.colEnd[i] = pos;
        }

        this.reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.ISO_8859_1));
    }

    public boolean hasNext() throws IOException {
        if (done || currentLine != null) {
            return !done;
        }

        currentLine = reader.readLine();
        if (currentLine == null) {
            done = true;
            return false;
        }
        if (currentLine.length() != colEnd[colEnd.length - 1]) {
            throw new IOException("Invalid line length for col spec. Wanted " + colEnd[colEnd.length - 1] + " got " + currentLine.length());
        }
        return !done;
    }

    public ParsedLine next() throws IOException {
        if (currentLine == null) {
            hasNext();
        }
        if (done) {
            throw new NoSuchElementException("No line left");
        }

        ParsedLine ret = new ParsedLine(colEnd.length);
        int start = 0;
        for (int i = 0; i < colEnd.length; i++) {
            ret.setValue(i, currentLine.substring(start, colEnd[i]).trim());
            start = colEnd[i];
        }
        currentLine = null;
        return ret;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public static class ParsedLine {

        private final String[] values;

        public ParsedLine(int count) {
            this.values = new String[count];
        }

        void setValue(int pos, String val) {
            values[pos] = val;
        }

        public String getString(int pos) {
            return values[pos];
        }

        public Long getLong(int pos) {
            String s = values[pos];
            if (s.isEmpty()) {
                return null;
            }
            try {
                return Long.valueOf(s);
            } catch (NumberFormatException ex) {
                LOG.warn("Invalid number string " + s, ex);
                return null;
            }
        }

        public Integer getInteger(int pos) {
            String s = values[pos];
            if (s.isEmpty()) {
                return null;
            }
            try {
                return Integer.valueOf(s);
            } catch (NumberFormatException ex) {
                LOG.warn("Invalid number string " + s, ex);
                return null;
            }
        }

        public boolean getBoolean(int pos) {
            String s = values[pos];
            if (s.isEmpty()) {
                return false;
            }
            try {
                return BOOL_TRUE.equals(s);
            } catch (NumberFormatException ex) {
                LOG.warn("Invalid bool value " + s, ex);
                return false;
            }
        }

        public Locale getLanguage(int pos) {
            String s = values[pos];
            if (s.isEmpty()) {
                return null;
            }
            try {
                switch (s) {
                    case "L":
                        return new Locale("LU");
                    case "D":
                    case "A":
                        return Locale.GERMAN;
                    case "F":
                        return Locale.FRENCH;
                    default:
                        LOG.warn("Unsupported language '" + s + "'");
                        return null;
                }
            } catch (NumberFormatException ex) {
                LOG.warn("Invalid number string " + s, ex);
                return null;
            }
        }

        public Date getDate(int pos) {
            String s = values[pos];
            if (s.length() < DATE_FORMAT.length()) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(s));
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                return cal.getTime();
            } catch (ParseException ex) {
                LOG.warn("Invalid date string " + s, ex);
                return null;
            }

        }

        public boolean isNull(int pos) {
            String s = values[pos];
            if (s.isEmpty()) {
                return false;
            }
            return NULL_VAL.equals(s);
        }

        @Override
        public String toString() {
            return Arrays.toString(values);
        }

    }

}
