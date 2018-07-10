package eu.antoninkriz.krizici.exceptions.network;

import eu.antoninkriz.krizici.exceptions.CustomExceptions;

public class FailedDownloadException extends CustomExceptions {
    public FailedDownloadException() {
        super();
    }

    public FailedDownloadException(String message) {
        super(message);
    }

    public FailedDownloadException(int code) {
        super(code);
    }

    public FailedDownloadException(String message, int code) {
        super(message, code);
    }
}
