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
    LUCENE_DATA_DIR("", false),
    LUCENE_GEO_SEARCH("true", true),
    //
    DATA_PUBLIC_ADR_URL("https://data.public.lu/fr/datasets/r/af76a119-2bd1-462c-a5bf-23e11ccfd3ee",true),
    DATA_PUBLIC_GEO_URL("https://data.public.lu/fr/datasets/r/7b58cf20-cbb0-4970-83f7-53a277f691b8", true);

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
