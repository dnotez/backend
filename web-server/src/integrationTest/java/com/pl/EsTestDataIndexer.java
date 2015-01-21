package com.pl;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pl.dsl.note.Note;
import com.pl.dsl.note.NoteFields;
import com.pl.store.es.NoteStore;
import org.junit.Assume;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.Assume.*;

/**
 * Helper class to test some notes.
 *
 * @author mamad
 * @since 29/11/14.
 */
public class EsTestDataIndexer implements BulkIndexer {


    public static EsTestDataIndexer create() {
        return new EsTestDataIndexer();
    }

    @SuppressWarnings("unchecked")
    public int indexStackoverflow(Consumer<Note> answerConsumer) {
        int count = 0;
        try (InputStream sofJsonStream = this.getClass().getResourceAsStream("/stackoverflow_answers.json")) {
            assumeNotNull(sofJsonStream);
            Gson gson = new GsonBuilder().create();
            Map sof = gson.fromJson(new InputStreamReader(sofJsonStream, Charsets.UTF_8), Map.class);
            assumeTrue(sof.containsKey("stackoverflow"));
            assumeTrue(sof.get("stackoverflow") instanceof List);
            List<Map> answers = (List<Map>) sof.get("stackoverflow");
            assumeFalse(answers.isEmpty());
            for (Map answer : answers) {
                answerConsumer.accept(toNote(answer));
                count++;
            }
        } catch (IOException e) {
            Assume.assumeNoException("IO Error in reading stackoverflow samples json", e);
        }
        return count;
    }

    private Note toNote(Map map) {
        assumeTrue(map.containsKey(NoteFields.TITLE));
        assumeTrue(map.containsKey(NoteFields.URL));
        assumeTrue(map.containsKey(NoteFields.BODY));
        return new Note(UUID.randomUUID().toString(), map.get(NoteFields.URL).toString(),
                map.get(NoteFields.TITLE).toString(), map.get(NoteFields.BODY).toString());
    }

    @Override
    public int index(NoteStore store) {
        return indexStackoverflow(answer -> {
            try {
                store.save(answer);
            } catch (Exception e) {
                Assume.assumeNoException(e);
            }
        });
    }
}
