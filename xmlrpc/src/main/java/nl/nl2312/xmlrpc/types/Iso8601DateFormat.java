package nl.nl2312.xmlrpc.types;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Provide a fast thread-safe formatter/parser DateFormat for ISO8601 dates ONLY.
 * It was mainly done to be used with Jackson JSON Processor.
 * <p>
 * Watch out for clone implementation that returns itself.
 * <p>
 * All other methods but parse and format and clone are undefined behavior.
 * <p>
 * Taken under Apache License 2.0 from https://github.com/FasterXML/jackson-databind
 *
 * @see Iso8601Utils
 */
public class Iso8601DateFormat extends DateFormat {

    private static final long serialVersionUID = 1L;

    // those classes are to try to allow a consistent behavior for hascode/equals and other methods
    private static Calendar CALENDAR = new GregorianCalendar();
    private static NumberFormat NUMBER_FORMAT = new DecimalFormat();

    public Iso8601DateFormat() {
        this.numberFormat = NUMBER_FORMAT;
        this.calendar = CALENDAR;
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        String value = Iso8601Utils.format(date);
        toAppendTo.append(value);
        return toAppendTo;
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        try {
            return Iso8601Utils.parse(source, pos);
        } catch (ParseException e) {
            return null;
        }
    }

    //supply our own parse(String) since pos isn't updated during parsing,
    //but the exception should have the right error offset.
    @Override
    public Date parse(String source) throws ParseException {
        return Iso8601Utils.parse(source, new ParsePosition(0));
    }

    @Override
    public Object clone() {
        /* Jackson calls clone for every call. Since this instance is
         * immutable (and hence thread-safe)
         * we can just return this instance
         */
        return this;
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

}
