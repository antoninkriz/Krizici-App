package eu.antoninkriz.krizici.utils;

import eu.antoninkriz.krizici.exceptions.UnknownException;
import eu.antoninkriz.krizici.exceptions.network.FailedDownloadException;

public class JsonHelper {
    public enum DOWNLOADFILE {
        TIMETABLES("json"),
        CONTACTS("contacts");

        private String file;

        DOWNLOADFILE(String name) {
            this.file = name;
        }
    }

    public static String getJson(DOWNLOADFILE downloadfile) throws FailedDownloadException, UnknownException {
        String url = "https://files.antoninkriz.eu/apps/krizici/" + downloadfile.file + ".json";
        Network.Result result = Network.downloadString(url);

        if (result.success) {
            return result.result;
        } else {
            if (result.exception != null) {
                throw result.exception;
            } else {
                throw new UnknownException("Unknown error while downloading file '" + url + "'", 0);
            }
        }
    }
}
