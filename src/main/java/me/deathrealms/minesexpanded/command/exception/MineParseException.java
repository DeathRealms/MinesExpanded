package me.deathrealms.minesexpanded.command.exception;

public class MineParseException extends Exception {

    public MineParseException(String input) {
        super("Unable to find mine from input '" + input + "'");
    }
}
