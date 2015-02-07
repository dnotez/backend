package com.dz.dsl.note;

import com.dz.dsl.meta.NoteSubmitter;
import com.google.common.base.MoreObjects;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author mamad
 * @since 10/11/14.
 */
public class Note {
    private String id;
    private String url;
    private String title;
    private String body;
    //plain text version of the body
    private String text;
    private String mimeType;
    private String md5;
    private String label;

    //the DateTime that note saved, in milliseconds, must be in UTC, displayed as local DateTime based on user's timezone
    private long saveDate;

    private Type type;

    //type of the application submitted this note, could be user, extension, cli, etc.
    private NoteSubmitter savedBy;

    public Note() {
    }

    public Note(String id, String url, String title, String body) {
        this.id = checkNotNull(id);
        this.url = checkNotNull(url);
        this.title = checkNotNull(title);
        this.body = checkNotNull(body);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public NoteSubmitter getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(NoteSubmitter savedBy) {
        this.savedBy = savedBy;
    }

    public long getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(long saveDate) {
        this.saveDate = saveDate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Note other = (Note) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("url", url)
                .add("title", title)
                .add("mimeType", mimeType)
                .add("type", type)
                .toString();
    }
}
