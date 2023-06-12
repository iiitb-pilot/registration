package io.mosip.registration.processor.print.stage.impl;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.stage.PrintPartnerService;
import org.json.simple.JSONObject;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class PrintPartnerServiceImpl implements PrintPartnerService {

    private static final Logger regProcLogger = RegProcessorLogger.getLogger(PrintPartnerServiceImpl.class);
    private static final String PRINT_ISSUERS = "mosip.registration.processor.print.issuers";
    private static final String PRINT_ISSUER_ATTRIBUTE = "mosip.registration.processor.print.issuer.identification.attribute";
    private static final String DEFAULT_ISSUER = "mosip.registration.processor.print.issuer.default";
    private static final String COMMON_ISSUER = "mosip.registration.processor.print.issuer.common";

    @Autowired
    private Environment env;

    @Autowired
    private Utilities utilities;

    @Autowired
    @Qualifier("varresolver")
    private VariableResolverFactory functionFactory;

    @Override
    public Set<String> getPrintPartners(String regId, JSONObject identity) {

        regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                regId, "PrintPartnerServiceImpl::getPrintPartners()::entry");

        Set<String> filteredPartners = new HashSet<>();
        String configuredPrintIssuers = env.getProperty(PRINT_ISSUERS);
        if (!StringUtils.hasText(configuredPrintIssuers)) {
            regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
                    LoggerFileConstant.REGISTRATIONID.toString(), regId,
                    PlatformErrorMessages.RPR_PRT_ISSUER_NOT_FOUND_IN_PROPERTY.name());
            return filteredPartners;
        }
        regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                regId, "PrintPartnerServiceImpl::getPrintPartners()::" + configuredPrintIssuers);

        var identityValues = utilities.getIdJsonByAttribute(identity, env.getProperty(PRINT_ISSUER_ATTRIBUTE));

        Map<String, Object> context = new HashMap<>();
        context.put("printIssuers", configuredPrintIssuers);
        context.put("attributeValues", identityValues);
        context.put("commonPartner", env.getProperty(COMMON_ISSUER));

        VariableResolverFactory myVarFactory = new MapVariableResolverFactory(context);
        myVarFactory.setNextFactory(functionFactory);
        Serializable serializable = MVEL.compileExpression("getConfiguredPartners(printIssuers, attributeValues, commonPartner);");
        MVEL.executeExpression(serializable, context, myVarFactory, Set.class);
        filteredPartners = (Set<String>) myVarFactory.getNextFactory().getVariableResolver("filteredPartners").getValue();
        if (StringUtils.hasText(env.getProperty(DEFAULT_ISSUER))) {
            filteredPartners.add(env.getProperty(DEFAULT_ISSUER));
        }
        return filteredPartners;
    }

}
