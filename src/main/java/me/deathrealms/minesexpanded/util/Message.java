package me.deathrealms.minesexpanded.util;

public enum Message {
    PREFIX("prefix"),
    RELOAD_SUCCESS("mines.reload.success"),
    CREATE_NO_SELECTION("mines.create.no_selection"),
    CREATE_EXISTS("mines.create.exists"),
    CREATE_SUCCESS("mines.create.success");

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
