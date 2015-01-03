package com.pl.web.cli;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.pl.dsl.cli.SaveCmdRequest;
import com.pl.string.Ellipsizer;

import java.util.Iterator;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * @author mamad
 * @since 13/12/14.
 */
@Singleton
public class FirstLineSaveCmdTitleBuilder implements SaveCmdTitleBuilder {
    public static final String MAX_TITLE_LENGTH = "MAX_TITLE_LENGTH";
    public static final String PREPEND_LABEL = "PREPEND_LABEL";
    private static final Splitter SPLITTER = Splitter.on('\n').trimResults().omitEmptyStrings();
    private final int maxTitleLength;
    private final boolean prependLabel;

    @Inject
    public FirstLineSaveCmdTitleBuilder(@Named(MAX_TITLE_LENGTH) int maxTitleLength, @Named(PREPEND_LABEL) boolean prependLabel) {
        this.maxTitleLength = maxTitleLength;
        this.prependLabel = prependLabel;
    }

    @Override
    public String titleOf(SaveCmdRequest request) {
        String body = nullToEmpty(request.getBody());
        String label = nullToEmpty(request.getLabel());
        Iterator<String> iterator = SPLITTER.split(body).iterator();
        if (iterator.hasNext()) {
            String firstLine = iterator.next();
            if (firstLine.length() > maxTitleLength) {
                firstLine = Ellipsizer.ellipsize(firstLine, maxTitleLength);
            }
            return prependLabel(label, firstLine);
        }
        return prependLabel(label, body);
    }

    protected String prependLabel(String label, String firstLine) {
        if (prependLabel && label.length() > 0) {
            return firstLine.length() > 0 ? String.format("%s:%s", label, firstLine) : label;
        } else {
            return firstLine;
        }
    }
}
