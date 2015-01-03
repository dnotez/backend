package com.pl.store.es;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pl.dsl.article.Article;
import com.pl.extraction.PlainTextExtraction;
import com.pl.extraction.PlainTextExtractor;
import com.pl.extraction.TextExtractionException;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.io.InputStream;

import static com.pl.dsl.article.ArticleFields.*;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author mamad
 * @since 13/11/14.
 */
@Singleton
public class IndexableArticleComposerImpl implements IndexableArticleComposer {
    private final PlainTextExtractor plainTextExtractor;
    private final ArticleSuggestionComposer completionSuggestionComposer;

    @Inject
    public IndexableArticleComposerImpl(PlainTextExtractor plainTextExtractor, ArticleSuggestionComposer completionSuggestionComposer) {
        this.plainTextExtractor = plainTextExtractor;
        this.completionSuggestionComposer = completionSuggestionComposer;
    }

    @Override
    public XContentBuilder compose(Article article) throws IOException, StoreActionFailedException {
        //todo: move the text extraction to another class
        InputStream stream = ByteSource.wrap(article.getBody().getBytes(Charsets.UTF_8)).openStream();
        try {
            PlainTextExtraction textExtraction = plainTextExtractor.extract(stream, ImmutableMap.of("Content-Type", "text/html"));
            article.setText(textExtraction.getPlainText());
            article.setMimeType(textExtraction.getMimeType().asString());
        } catch (TextExtractionException e) {
            throw new StoreActionFailedException(StoreActionFailedException.Action.CREATE, "Error in extracting text", e);
        }

        return jsonBuilder()
                .startObject()
                .field(TITLE, article.getTitle())
                .field(BODY, article.getBody())
                .field(TEXT, article.getText())
                .field(MD5, article.getMd5())
                .field(MIME_TYPE, article.getMimeType())
                .field(SAVE_DATE, article.getSaveDate())
                .field(LABEL, article.getLabel())
                .field(URL, article.getUrl())
                        //for completion suggestion
                .field(SUGGEST, completionSuggestionComposer.createSuggestionObject(article))
                .field(SUGGEST_PHRASE, article.getTitle())
                .endObject();
    }
}
