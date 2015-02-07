package com.dz.dsl;

import com.dz.dsl.note.Note;
import com.dz.dsl.note.NoteResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 15/11/14.
 */
public class GsonHelperTest {
    @Test
    public void testDeserializePagedResponse() throws Exception {
        Gson gson = GsonHelper.create().buildDefault();
        NoteResult result = new NoteResult(1.2f, new Note("12", "u1", "t1", "b1"));
        String json = gson.toJson(result);
        assertTrue(json, json.contains("score"));
        List<NoteResult> results = Arrays.asList(result);
        PagedResponse<NoteResult> response = new PagedResponse<>(results, 1);
        json = gson.toJson(response, new TypeToken<PagedResponse<Note>>() {
        }.getType());
        assertTrue(json, json.contains("score"));

        PagedResponse<NoteResult> loaded = gson.fromJson(json, GsonHelper.NOTE_PAGED_RESPONSE_TYPE);
        assertNotNull(loaded);
        assertNotNull(loaded.getResults());
        assertEquals(1, loaded.getResults().size());
        NoteResult r = loaded.getResults().get(0);
        assertNotNull(r);
        assertNotNull(r.getItem());
        assertEquals(result.getScore(), r.getScore(), 0);
        assertEquals(result.getItem().getId(), r.getItem().getId());
    }

}
