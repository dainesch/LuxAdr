package lu.dainesch.luxadrservice.base;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ImportException extends Exception {

    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }

}
