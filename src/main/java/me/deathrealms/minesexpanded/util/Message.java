package me.deathrealms.minesexpanded.util;

public enum Message {
    PREFIX("prefix"),
    RELOAD_SUCCESS("mines.reload.success"),
    CREATE_NO_SELECTION("mines.create.no_selection"),
    CREATE_EXISTS("mines.create.exists"),
    CREATE_SUCCESS("mines.create.success"),
    REMOVE_NO_MINE("mines.remove.no_mine"),
    REMOVE_SUCCESS("mines.remove.success"),
    EDIT_NO_MINE("mines.edit.no_mine");

    private final String key;

    Message(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public String getMessage(Object... args) {
        return MessageUtil.getMessage(this, args);
    }
}
