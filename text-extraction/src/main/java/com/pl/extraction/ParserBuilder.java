package com.pl.extraction;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;

/**
 * @author mamad
 * @since 22/11/14.
 */
public class ParserBuilder {
    public static ParserBuilder create() {
        return new ParserBuilder();
    }

    public Parser defaultAutoDetectParser() {
        return new AutoDetectParser();
    }
}
