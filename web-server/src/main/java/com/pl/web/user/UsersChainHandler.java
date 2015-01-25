package com.pl.web.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pl.web.user.note.NotesChainHandler;
import ratpack.func.Action;
import ratpack.handling.Chain;

/**
 * @author mamad
 * @since 22/01/15.
 */
@Singleton
public class UsersChainHandler implements Action<Chain> {

    @Inject
    public UsersChainHandler() {
    }

    @Override
    public void execute(Chain chain) throws Exception {
        chain.prefix(":user/notes", chain.getRegistry().get(NotesChainHandler.class));
        //chain.prefix(":user/profile", chain.getRegistry().get(ProfileChainHandler.class));
    }
}
