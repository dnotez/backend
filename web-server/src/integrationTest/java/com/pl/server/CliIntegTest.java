package com.pl.server;

import com.pl.api.CliApi;
import com.pl.client.ApiClientBuilder;
import com.pl.dsl.GetByKeyRequest;
import com.pl.dsl.IdResponse;
import com.pl.dsl.PagedResponse;
import com.pl.dsl.Pong;
import com.pl.dsl.article.Article;
import com.pl.dsl.article.ArticleResult;
import com.pl.dsl.cli.SaveCmdRequest;
import com.pl.dsl.cli.SaveCmdResponse;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 01/12/14.
 */
public class CliIntegTest extends BaseServerIntegTest {

    protected CliApi cliApi;

    @Override
    protected void prepareApi(String url) {
        ApiClientBuilder apiClientBuilder = ApiClientBuilder.create().withUrl(url);
        cliApi = apiClientBuilder.cmdLineApi();
    }

    @Test
    public void testPing() throws Exception {
        Pong pong = cliApi.ping();
        assertNotNull(pong);
        assertTrue(pong.isOkay());
    }

    @Test
    public void testSaveCmdRequest() throws Exception {
        SaveCmdRequest request = new SaveCmdRequest();
        try {
            cliApi.saveCmd(request);
            Assert.fail("Should not reach this line.");
        } catch (Exception e) {
            //its okay;
        }
        request = new SaveCmdRequest("user", "label", "body");
        SaveCmdResponse response = cliApi.saveCmd(request);
        verifyResponse(response);
    }

    protected void verifyResponse(SaveCmdResponse response) {
        assertNotNull(response);
        assertNotNull(response.getUrl());
        assertNotNull(response.getId());
    }

    @Test
    public void testDelete() throws Exception {
        esHelper.deleteAllDocs();
        SaveCmdRequest request = new SaveCmdRequest("user", "label", "body");
        SaveCmdResponse response = cliApi.saveCmd(request);
        verifyResponse(response);
        esHelper.ensureIndexed(1);

        IdResponse idResponse = cliApi.deleteItem(response.getId());
        assertNotNull(idResponse);
        assertThat(idResponse.getId(), equalTo(response.getId()));
    }

    @Test
    public void testGet() throws Exception {
        esHelper.deleteAllDocs();
        String label = "update-npm";
        SaveCmdRequest request = new SaveCmdRequest("user", label, "body");
        SaveCmdResponse response = cliApi.saveCmd(request);
        verifyResponse(response);
        esHelper.ensureIndexed(1);
        PagedResponse<ArticleResult> pagedResponse = cliApi.getArticle(response.getId(), null, 1);
        verifyGetResponse(pagedResponse, label, 1, "body");

        //now get it by label
        pagedResponse = cliApi.getArticle(label, GetByKeyRequest.KeyType.LABEL.name(), 1);
        verifyGetResponse(pagedResponse, label, 1, "body");

        //now save another one with same label
        request = new SaveCmdRequest("user", label, "body1");
        response = cliApi.saveCmd(request);
        verifyResponse(response);
        esHelper.ensureIndexed(2);
        pagedResponse = cliApi.getArticle(label, GetByKeyRequest.KeyType.LABEL.name(), 1);
        verifyGetResponse(pagedResponse, label, 2, "body1");
    }

    protected void verifyGetResponse(PagedResponse<ArticleResult> pagedResponse, String label, int totalCount, String expectedBody) {
        assertNotNull(pagedResponse);
        assertNotNull(pagedResponse.getResults());
        assertThat(pagedResponse.getResults().size(), equalTo(1));
        assertThat(pagedResponse.getTotal(), equalTo((long) totalCount));
        assertNotNull(pagedResponse.getResults().get(0));
        Article fetched = pagedResponse.getResults().get(0).getItem();
        assertNotNull(fetched);
        assertThat(fetched.getText(), equalTo(expectedBody));
        assertThat(fetched.getLabel(), equalTo(label));
    }

    //todo: test other cli REST api method
}
