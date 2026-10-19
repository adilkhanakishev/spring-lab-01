package kz.iitu.springlab.catalog;

public class InvalidSortFieldException extends RuntimeException {

    private final String field;

    public InvalidSortFieldException(String field) {
        super("Unknown sort field: " + field);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
