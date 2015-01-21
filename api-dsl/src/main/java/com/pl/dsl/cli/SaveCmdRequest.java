package com.pl.dsl.cli;

import com.google.common.base.MoreObjects;

/**
 * A bash command to be saved.
 * <p>
 * This will be converted to an note with proper field values and then stored in the system.
 * <p>
 * The url for the bash command will be automatically generated.
 * Title and body usually the same.
 * Type is Type.BASH_CMD and the submitter is CLI
 *
 * @author mamad
 * @since 11/12/14.
 */
public class SaveCmdRequest {

    /**
     * The actual command, usually a single line command but could be a long, multi line text.
     * It also could be also output of another command, example:
     * <p>
     * <code>
     * tail -n 5 /var/log/syslog | pl save -
     * </code>
     * </p>
     */
    private String body;

    /**
     * a label to be assigned to command for quick search.
     * For example, user can save a command like this
     * <code>
     * pl save -l u-update "sudo apt-get update;sudo apt-get upgrade"
     * </code>
     * <p>
     * and later search for it by:
     * <code>
     * pl search -l u-_
     * </code>
     */
    private String label;

    /**
     * Name of user on the local machine, for example root.
     * This could be different from the account name or could be the same
     */
    private String user;


    public SaveCmdRequest() {
    }

    public SaveCmdRequest(String user, String label, String body) {
        this.user = user;
        this.label = label;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("body", body)
                .add("label", label)
                .add("user", user)
                .toString();
    }
}
