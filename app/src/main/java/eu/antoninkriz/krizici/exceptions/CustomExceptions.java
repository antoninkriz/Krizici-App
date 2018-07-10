package eu.antoninkriz.krizici.exceptions;

public abstract class CustomExceptions extends Exception {
    private final String message;
    private final int code;

    protected CustomExceptions() {
        this.message = null;
        this.code = 0;
    }

    protected CustomExceptions(String message) {
        this.message = message;
        this.code = 0;
    }

    protected CustomExceptions(int code) {
        this.message = null;
        this.code = code;
    }

    protected CustomExceptions(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.getClass().getName() + "; Message: " + message + "; Code" + code;
    }
}


