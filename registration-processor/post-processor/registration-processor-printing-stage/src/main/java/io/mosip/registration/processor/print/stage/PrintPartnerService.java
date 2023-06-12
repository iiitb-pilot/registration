package io.mosip.registration.processor.print.stage;

import org.json.simple.JSONObject;

import java.util.Set;

/**
 * * This service helps to get configured print partners to whom the print credentials to be shared.
 * *
 */
public interface PrintPartnerService {
    /**
     * Gets list of print partners to send the credential print request.
     * @param regId
     * @param identity
     * @return Set of print partner Ids.
     */
    Set<String> getPrintPartners(String regId, JSONObject identity);
}
