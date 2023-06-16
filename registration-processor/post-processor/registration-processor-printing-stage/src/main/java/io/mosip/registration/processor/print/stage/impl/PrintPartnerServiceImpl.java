package io.mosip.registration.processor.print.stage.impl;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.stage.PrintPartnerService;
import org.json.simple.JSONObject;
import org.mvel2.MVEL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class PrintPartnerServiceImpl implements PrintPartnerService {

    private static final Logger regProcLogger = RegProcessorLogger.getLogger(PrintPartnerServiceImpl.class);
    private static final String IDENTITY_ATTRIBUTE = "mosip.identification.attribute";
    private static final String NO_MATCH_ISSUER = "mosip.registration.processor.print.issuer.noMatch";

    @Autowired
    private Environment env;

    @Autowired
    private Utilities utilities;

    @Value("#{${mosip.registration.processor.print.issuer.config-map:{}}}")
    private Map<String, String> printPartnerExpression;

    @Override
    public Set<String> getPrintPartners(String regId, JSONObject identity) {

        regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                regId, "PrintPartnerServiceImpl::getPrintPartners()::entry");

        Set<String> filteredPartners = new HashSet<>();
        if (printPartnerExpression.isEmpty()) {
            regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
                    LoggerFileConstant.REGISTRATIONID.toString(), regId,
                    PlatformErrorMessages.RPR_PRT_ISSUER_NOT_FOUND_IN_PROPERTY.name());
            return filteredPartners;
        }
        regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                regId, "PrintPartnerServiceImpl::getPrintPartners()::" + printPartnerExpression.toString());

        String identityValue = utilities.getIdJsonByAttribute(identity, env.getProperty(IDENTITY_ATTRIBUTE));

        Map<String, Object> context = new HashMap<>();
        context.put(env.getProperty(IDENTITY_ATTRIBUTE), identityValue);
        boolean printIssuerFound = false;
        regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                regId, "PrintPartnerServiceImpl::PrintPartnerExpression::" + printPartnerExpression.toString());

        for(Map.Entry<String, String> entry : printPartnerExpression.entrySet()) {
            Boolean result = (Boolean) MVEL.eval(entry.getValue(), context);
            if (result) {
                filteredPartners.add(entry.getKey());
                if (!ObjectUtils.nullSafeToString(entry.getValue()).equalsIgnoreCase("true")) {
                    printIssuerFound = true;
                }
            }
        }
        if (StringUtils.hasText(env.getProperty(NO_MATCH_ISSUER)) && !printIssuerFound) {
            filteredPartners.add(env.getProperty(NO_MATCH_ISSUER));
        }
        regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                regId, "PrintPartnerServiceImpl::FilteredPartners::" + filteredPartners.toString());

        return filteredPartners;
    }
}
