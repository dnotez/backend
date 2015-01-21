package com.pl.store.es;

import com.google.common.base.Optional;
import com.pl.dsl.GetByKeyRequest;
import com.pl.dsl.IdResponse;
import com.pl.dsl.PagedRequest;
import com.pl.dsl.PagedResponse;
import com.pl.dsl.note.Note;
import com.pl.dsl.note.NoteResult;
import com.pl.dsl.note.SuggestionResponse;
import com.pl.dsl.extension.GetByUrlRequest;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author mamad
 * @since 13/11/14.
 */
public interface NoteStore {

    /**
     * Save the code note
     *
     * @param object the object to be saved
     * @return response of the save action, contains id of successfully saved note.
     */
    IdResponse save(Note object) throws IOException, StoreActionFailedException;

    void asyncSave(Note note, Consumer<IdResponse> onSuccess, Consumer<Throwable> onError);
    IdResponse update(Note object);

    IdResponse delete(String id);

    void asyncDelete(String id, Consumer<IdResponse> onSuccess, Consumer<Throwable> onError);

    Optional<Note> findById(String id) throws IOException, StoreActionFailedException;

    /**
     * The url by itself can not identify an note uniquely because:
     * 1. URL could have session/request based parameters or proxied.
     * 2. Partial parts of the page represented by URL may be saved.
     * URL can be use to alert the user that they already saved an note from the page.
     * <p>
     * <p>
     * So we use md5 checksum of the note (or parts of the note) as a way to identify duplicates.
     *
     * @param md5 md5 checksum of the note
     * @return Optional note
     * @throws IOException
     * @throws StoreActionFailedException
     */
    Optional<Note> findByMd5(String md5) throws IOException, StoreActionFailedException;

    PagedResponse<NoteResult> list(PagedRequest request) throws IOException, StoreActionFailedException;

    /**
     * Handle request asynchronously
     *
     * @param request    the list request
     * @param onResponse consumer to handle response page
     * @param onFailure  consumer to handler error
     */
    void asyncList(PagedRequest request, Consumer<PagedResponse<NoteResult>> onResponse, Consumer<Throwable> onFailure);

    void asyncGet(GetByKeyRequest request, Consumer<PagedResponse<NoteResult>> onResponse, Consumer<Throwable> onFailure);

    /**
     * Asynchronously checks the requested urls are saved in the system or not.
     *
     * @param request    the check request
     * @param onResponse consumer to handle response page
     * @param onFailure  consumer to handler error
     */
    void asyncGet(GetByUrlRequest request, Consumer<PagedResponse<NoteResult>> onResponse, Consumer<Throwable> onFailure);

    /**
     * Similar to list but handle suggestions request
     *
     * @param request    suggestion request
     * @param onResponse consumer to handle response page
     * @param onFailure  consumer to handler error
     */
    void asyncSuggestion(PagedRequest request, Consumer<SuggestionResponse> onResponse, Consumer<Throwable> onFailure);
}
