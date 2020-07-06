package engineer.kovalev.judgmentless;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseLine {
    private final ResponseCode responseCode;
    private final String meta;
    private static final Pattern pattern = Pattern.compile("(\\d\\d) (.*)");
    public ResponseLine(String line) {
        Matcher m = pattern.matcher(line);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid response from server:\n" + line);
        }
        String code = m.group(1);
        meta = m.group(2);

        responseCode = switch (code.charAt(0)) {
            case '1' -> ResponseCode.INPUT;
            case '2' -> ResponseCode.SUCCESS;
            case '3' -> ResponseCode.REDIRECT;
            case '4' -> ResponseCode.TEMP_FAILURE;
            case '5' -> ResponseCode.PERM_FAILURE;
            case '6' -> ResponseCode.CLIENT_CERTIFICATE_REQUIRED;
            default -> throw new IllegalArgumentException("Wrong response code: " + code + "\nFull response\n" + line);
        };
        if (meta.length() > 1024) {  // todo magic numbers
            throw new IllegalArgumentException("Meta string is too long");
        }
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public String getMeta() {
        return meta;
    }
}
