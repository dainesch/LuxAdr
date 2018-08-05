package lu.dainesch.luxadrservice.base;

public enum ConfigType {
    NONE(null),
    //
    CORS_ORIGIN("*"),
    ADMIN_ACCESS_CHECK("true"),
    ADMIN_ACCESS_KEY("ufnDMuLS7dOO51JlAtuSoszHxl0Wico4sPqR96FiNdf3reLClQNp6QFhIVPbS6Vi"),
    BATCH_SIZE("1000"),
    MAX_SEARCH_RES("20"),
    MAX_DIST_KM("0.2");
    ;
    
    private final String defaultValue;

    private ConfigType(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

}
