package com.dz.dsl;

import com.dz.dsl.note.Note;
import com.dz.dsl.note.NoteResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author mamad
 * @since 15/11/14.
 */
public class GsonHelper {
    public static final Type NOTE_RESULT_TYPE = new TypeToken<Result<Note>>() {
    }.getType();
    public static final Type NOTE_PAGED_RESPONSE_TYPE = new TypeToken<PagedResponse<NoteResult>>() {
    }.getType();

    public static GsonHelper create() {
        return new GsonHelper();
    }

    public Gson buildDefault() {
        return new GsonBuilder()
                .registerTypeAdapter(NOTE_RESULT_TYPE, new NoteResultTypeAdapter())
                .create();
    }


    private static class NoteResultTypeAdapter implements InstanceCreator<Result<Note>> {
        @Override
        public Result<Note> createInstance(Type type) {
            return new NoteResult(0, null);
        }
    }

}
