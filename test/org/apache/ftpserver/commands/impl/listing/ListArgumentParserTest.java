package org.apache.ftpserver.commands.impl.listing;

import junit.framework.TestCase;
import org.apache.ftpserver.command.impl.listing.ListArgument;
import org.apache.ftpserver.command.impl.listing.ListArgumentParser;

public class ListArgumentParserTest extends TestCase {

    public void testParseOnlyFile() {
        ListArgument arg = ListArgumentParser.parse("foo");
        assertEquals("foo", arg.getFile());
        assertNull(arg.getPattern());
        assertEquals(0, arg.getOptions().length);
    }

    public void testParseOnlyFileWithDir() {
        ListArgument arg = ListArgumentParser.parse("bar/foo");
        assertEquals("bar/foo", arg.getFile());
        assertNull(arg.getPattern());
        assertEquals(0, arg.getOptions().length);
    }

    public void testParseOnlyPatternWithDir() {
        ListArgument arg = ListArgumentParser.parse("bar/foo*");
        assertEquals("bar/", arg.getFile());
        assertEquals("foo*", arg.getPattern());
        assertEquals(0, arg.getOptions().length);
    }

    public void testParseFileWithSpace() {
        ListArgument arg = ListArgumentParser.parse("foo bar");
        assertEquals("foo bar", arg.getFile());
        assertNull(arg.getPattern());
        assertEquals(0, arg.getOptions().length);
    }

    public void testParseWithTrailingOptions() {
        ListArgument arg = ListArgumentParser.parse("foo -la");
        assertEquals("foo -la", arg.getFile());
        assertNull(arg.getPattern());
        assertEquals(0, arg.getOptions().length);
    }

    public void testParseNullArgument() {
        ListArgument arg = ListArgumentParser.parse(null);
        assertEquals("./", arg.getFile());
        assertNull(arg.getPattern());
        assertEquals(0, arg.getOptions().length);
    }

    public void testParseFileAndOptions() {
        ListArgument arg = ListArgumentParser.parse("-la foo");
        assertEquals("foo", arg.getFile());
        assertNull(arg.getPattern());
        assertEquals(2, arg.getOptions().length);
        assertTrue(arg.hasOption('l'));
        assertTrue(arg.hasOption('a'));
    }

    public void testParseOnlyOptions() {
        ListArgument arg = ListArgumentParser.parse("-la");
        assertEquals("./", arg.getFile());
        assertNull(arg.getPattern());
        assertEquals(2, arg.getOptions().length);
        assertTrue(arg.hasOption('l'));
        assertTrue(arg.hasOption('a'));
    }

    public void testPatternDetection() {
        assertNull(ListArgumentParser.parse("foo").getPattern());
        assertNotNull(ListArgumentParser.parse("foo*").getPattern());
        assertNotNull(ListArgumentParser.parse("f*oo").getPattern());
        assertNotNull(ListArgumentParser.parse("*foo").getPattern());
        assertNotNull(ListArgumentParser.parse("?foo").getPattern());
        assertNotNull(ListArgumentParser.parse("f?oo").getPattern());
        assertNotNull(ListArgumentParser.parse("foo?").getPattern());
        assertNotNull(ListArgumentParser.parse("foo[").getPattern());
        assertNotNull(ListArgumentParser.parse("[foo").getPattern());
    }

    public void testParseSimplePattern() {
        ListArgument arg = ListArgumentParser.parse("foo*");
        assertEquals("./", arg.getFile());
        assertEquals("foo*", arg.getPattern());
        assertEquals(0, arg.getOptions().length);
    }

    public void testParseDirAndPattern() {
        ListArgument arg = ListArgumentParser.parse("bar/foo*");
        assertEquals("bar/", arg.getFile());
        assertEquals("foo*", arg.getPattern());
        assertEquals(0, arg.getOptions().length);
    }

    public void testParsePatternInDir() {
        try {
            ListArgumentParser.parse("bar*/foo");
            fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
    }
}
