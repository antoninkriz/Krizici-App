package eu.antoninkriz.krizici.exceptions;

public class UnknownException extends CustomExceptions {
    UnknownException() {
        super();
    }

    UnknownException(String message) {
        super(message);
    }

    public UnknownException(String message, int code) {
        super(message, code);
    }
}
