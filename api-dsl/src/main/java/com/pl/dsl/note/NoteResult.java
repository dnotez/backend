package com.pl.dsl.note;

import com.pl.dsl.Result;

import java.util.Objects;

/**
 * @author mamad
 * @since 15/11/14.
 */
public class NoteResult implements Result<Note> {
    private float score;
    private Note item;

    public NoteResult(float score, Note item) {
        this.score = Objects.equals(score, Float.NaN) ? Float.MIN_VALUE : score;
        this.item = item;
    }

    @Override
    public Note getItem() {
        return item;
    }

    @Override
    public float getScore() {
        return score;
    }
}
