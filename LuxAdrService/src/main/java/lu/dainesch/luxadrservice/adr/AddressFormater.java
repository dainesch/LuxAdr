package lu.dainesch.luxadrservice.adr;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lu.dainesch.luxadrdto.entity.PostCodeType;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.entity.HouseNumber;


public class AddressFormater {

    private static final Pattern NUMBER_PAT = Pattern.compile("(^| )[0-9]{1,3}[A-Za-z]?(,| |$)");
    private static final Pattern POSTCODE_PAT = Pattern.compile("[0-9]{4,4}");
    private static final Pattern LOC_PAT = Pattern.compile("[A-Za-z]{3,}( |-)*[A-Za-z]*");

    private AddressFormater() {

    }

    public static String formatNormal(Building build, HouseNumber num) {
        if (build == null || (build.getPostalCode() != null && build.getPostalCode().getType() == PostCodeType.Mailbox)) {
            return null;
        }
        StringBuilder b = new StringBuilder();
        if (num != null) {
            b.append(num.getNumber()).append(", ");
        }
        if (build.getStreet() != null) {
            b.append(build.getStreet().getName()).append(" ");
        }
        if (build.getPostalCode() != null) {
            b.append("L-").append(build.getPostalCode().getCode()).append(" ");
        }
        if (build.getStreet() != null && build.getStreet().getLocality() != null) {
            b.append(build.getStreet().getLocality().getName());
        }
        return b.toString();
    }

    public static List<String> formatPostalBoxes(Building build) {
        if (build == null || build.getPostalCode() == null || build.getPostalCode().getType() == PostCodeType.Normal) {
            return null;
        }
        List<String> ret = new ArrayList<>();
        for (int i = build.getPostalCode().getMinMailbox(); i <= build.getPostalCode().getMaxMailbox(); i++) {
            StringBuilder b = new StringBuilder();
            b.append("BP ").append(i).append(" ");
            b.append("L-").append(build.getPostalCode().getCode()).append(" ");
            if (build.getStreet() != null && build.getStreet().getLocality() != null) {
                b.append(build.getStreet().getLocality().getName());
            } else if (build.getQuarter() != null && build.getQuarter().getLocality() != null) {
                b.append(build.getQuarter().getLocality().getName());
            }
            ret.add(b.toString());
        }

        return ret;
    }

    public static String extractNumber(String in) {
        Matcher m = NUMBER_PAT.matcher(in);
        if (m.find()) {
            return m.group().replace(",", "").trim();
        }
        return null;
    }

    public static String extractPostcode(String in) {
        Matcher m = POSTCODE_PAT.matcher(in);
        if (m.find()) {
            return m.group().trim();
        }
        return null;
    }

    public static boolean isNumber(String in) {
        return NUMBER_PAT.matcher(in).matches();
    }

    public static boolean isPostCode(String in) {
        return POSTCODE_PAT.matcher(in).matches();
    }

    public static boolean isPossibleLocality(String in) {
        return LOC_PAT.matcher(in).matches();
    }

}
