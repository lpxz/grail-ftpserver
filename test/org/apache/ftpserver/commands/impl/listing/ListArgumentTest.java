package org.apache.ftpserver.commands.impl.listing;

import java.util.Arrays;
import junit.framework.TestCase;
import org.apache.ftpserver.command.impl.listing.ListArgument;

public class ListArgumentTest extends TestCase {

    private static final char[] OPTIONS = new char[] { 'a', 'b', 'c' };

    private static final char[] OPTIONS_EMPTY = new char[] {};

    public void testFilePatternAndOptions() {
        ListArgument arg = new ListArgument("bar", "foo", OPTIONS);
        assertEquals("bar", arg.getFile());
        assertEquals("foo", arg.getPattern());
        assertTrue(Arrays.equals(OPTIONS, arg.getOptions()));
    }

    public void testArgumentAndEmptyOptions() {
        ListArgument arg = new ListArgument("bar", "foo", OPTIONS_EMPTY);
        assertEquals(0, arg.getOptions().length);
    }

    public void testArgumentAndNullOptions() {
        ListArgument arg = new ListArgument("bar", "foo", null);
        assertNotNull(arg.getOptions());
        assertEquals(0, arg.getOptions().length);
    }

    public void testNullFile() {
        ListArgument arg = new ListArgument(null, "foo", null);
        assertNull(arg.getFile());
        assertEquals("foo", arg.getPattern());
    }

    public void testNullPattern() {
        ListArgument arg = new ListArgument("bar", null, null);
        assertEquals("bar", arg.getFile());
        assertNull(arg.getPattern());
    }
}
