package body;

import com.dz.OkClientBuilder;
import com.dz.fetch.FetchResponse;
import com.dz.fetch.NoteBodyFetcher;
import com.dz.fetch.NoteBodyFetcherImpl;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLHighlighter;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;

/**
 * @author mamad
 * @since 07/02/15.
 */
public class MainBodyExtractor {
    public static void main(String[] args) throws Exception {
        NoteBodyFetcher fetcher = new NoteBodyFetcherImpl(OkClientBuilder.create().get());
        //String url = "https://code.google.com/p/boilerpipe/";
        //String url = "http://www.bbc.co.uk/news/";
        //String url = "http://www.bbc.co.uk/news/world-europe-31185027";
        String url = "http://www.elasticsearch.org/blog/numeric-aggregations-an-exploration-of-uk-housing-data/";
        FetchResponse response = fetcher.fetchBody(url);
        System.out.println(response.isValid());
        String body = response.getBody();
        TextDocument doc = new BoilerpipeSAXInput(new InputSource(
                new StringReader(body))).getTextDocument();
        //ArticleExtractor.INSTANCE.process(doc);
        HTMLHighlighter htmlHighlighter = HTMLHighlighter.newExtractingInstance();
        final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
        extractor.process(doc);
        String main = htmlHighlighter.process(doc, body);
        System.out.println(main);
        File out = new File("main.html");
        if (!out.exists()) {
            out.createNewFile();
        }
        Files.write(main.getBytes(Charsets.UTF_8), out);
    }
}
