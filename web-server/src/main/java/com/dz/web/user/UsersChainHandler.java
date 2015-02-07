package com.dz.web.user;

import com.dz.web.user.note.NotesChainHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
