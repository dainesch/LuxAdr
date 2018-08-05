package lu.dainesch.luxadrservice.base;

import java.util.Comparator;

public enum ImportStep {
    POSTALCODE(1, "CODEPT", "postcode", false),
    DISTRICT(2, "DISTRICT", "district", false),
    CANTON(3, "CANTON", "canton", false),
    COMMUNE(4, "COMMUALL", "commune", false),
    LOCALITY(5, "LOCALITE", "locality", false),
    LOCALITY_ALT(6, "ALIAS.LOCALITE", "locality name", true),
    QUARTER(7, "QUARTIER", "quarter", false),
    STREET(8, "RUE", "street", false),
    STREET_ALT(9, "ALIAS.RUE", "street name", true),
    BUILDING(10, "IMMEUBLE", "building", false),
    BUILDING_DES(11, "IMMDESIG", "building name", true),
    //
    GEODATA(12, "addresses.geojson", "geodata", false);

    private final int order;
    private final String file;
    private final String stepName;
    private final boolean alt;

    ImportStep(int order, String file, String stepName, boolean alt) {
        this.order = order;
        this.file = file;
        this.stepName = stepName;
        this.alt = alt;
    }

    public String getFile() {
        return file;
    }

    public String getStepName() {
        return stepName;
    }

    public int getOrder() {
        return order;
    }

    public boolean isAlt() {
        return alt;
    }

    public static Comparator<ImportStep> comparator() {
        return (o1, o2) -> o1.order - o2.order;
    }

    public static ImportStep getStepFromFile(String file) {
        for (ImportStep s : values()) {
            if (s.getFile().equalsIgnoreCase(file)) {
                return s;
            }
        }
        return null;
    }
}
