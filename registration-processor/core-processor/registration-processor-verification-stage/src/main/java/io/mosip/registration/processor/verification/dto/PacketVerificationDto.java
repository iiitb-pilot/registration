package io.mosip.registration.processor.verification.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class PacketVerificationDto {
	private boolean isValid = false;
	private boolean isTransactionSuccessful;
	private String packetValidatonStatusCode="";
	private String packetValidaionFailureMessage = "";
}
