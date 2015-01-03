package com.pl.web.cli;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pl.dsl.article.Article;
import com.pl.dsl.article.Type;
import com.pl.dsl.cli.SaveCmdRequest;
import com.pl.dsl.cli.SaveCmdResponse;
import com.pl.store.es.ArticleStore;
import com.pl.store.es.UUIDGenerator;

import java.util.function.Consumer;

/**
 * @author mamad
 * @since 13/12/14.
 */
@Singleton
public class SaveCmdPersisterImpl implements SaveCmdPersister {
    private final ArticleStore articleStore;
    private final SaveCmdTitleBuilder titleBuilder;
    private final SaveCmdHtmlBodyBuilder bodyBuilder;
    private final SaveCmdUrlBuilder urlBuilder;
    private final UUIDGenerator uuidGenerator;

    @Inject
    public SaveCmdPersisterImpl(ArticleStore articleStore, SaveCmdTitleBuilder titleBuilder,
                                SaveCmdHtmlBodyBuilder bodyBuilder, SaveCmdUrlBuilder urlBuilder,
                                UUIDGenerator uuidGenerator) {
        this.articleStore = articleStore;
        this.titleBuilder = titleBuilder;
        this.bodyBuilder = bodyBuilder;
        this.urlBuilder = urlBuilder;
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public void persist(SaveCmdRequest request, Consumer<SaveCmdResponse> onSuccess, Consumer<Throwable> onError) {
        //convert request to article
        Article article = new Article();
        String id = uuidGenerator.newId();
        article.setId(id);
        article.setBody(bodyBuilder.bodyOf(request));
        article.setMimeType("text/plain");
        article.setType(Type.BASH_CMD);
        article.setTitle(titleBuilder.titleOf(request));
        String url = urlBuilder.urlOf(request, id);
        article.setUrl(url);
        article.setLabel(request.getLabel());
        articleStore.asyncSave(article, response -> onSuccess.accept(new SaveCmdResponse(url, id)), onError);
    }
}
