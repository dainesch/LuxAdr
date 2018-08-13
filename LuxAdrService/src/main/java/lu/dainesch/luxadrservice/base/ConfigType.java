package lu.dainesch.luxadrservice.base;

public enum ConfigType {
    NONE(null, false),
    //
    CORS_ORIGIN("*", true),
    //
    ADMIN_ACCESS_CHECK("true", true),
    ADMIN_ACCESS_KEY("ufnDMuLS7dOO51JlAtuSoszHxl0Wico4sPqR96FiNdf3reLClQNp6QFhIVPbS6Vi", true),
    //
    BATCH_SIZE("1000", true),
    MAX_SEARCH_RES("20", true),
    MAX_DIST_KM("0.2", true),
    //
    LUCENE_ENABLED("true", true),
    LUCENE_DATA_DIR("", false);

    private final String defaultValue;
    private final boolean autocreate;

    private ConfigType(String defaultValue, boolean auto) {
        this.defaultValue = defaultValue;
        this.autocreate = auto;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isAutocreate() {
        return autocreate;
    }

}
