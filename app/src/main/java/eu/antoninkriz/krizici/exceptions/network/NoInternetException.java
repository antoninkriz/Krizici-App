package eu.antoninkriz.krizici.exceptions.network;

import eu.antoninkriz.krizici.exceptions.CustomExceptions;

public class NoInternetException extends CustomExceptions {
    public NoInternetException() {
        super();
    }

    public NoInternetException(String message) {
        super(message);
    }

    public NoInternetException(int code) {
        super(code);
    }

    public NoInternetException(String message, int code) {
        super(message, code);
    }
}
