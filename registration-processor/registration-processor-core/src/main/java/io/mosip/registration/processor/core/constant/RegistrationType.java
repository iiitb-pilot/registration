package io.mosip.registration.processor.core.constant;

/**
 * The Enum RegistrationType for different types of Registration.
 * 
 * @author Pranav Kumar
 * @since 0.10.2
 *
 */
public enum RegistrationType {

	/** The new. */
	NEW("new"),

	/** The CRVS_NEW. */
	CRVS_NEW("crvs_new"),

	/** The CRVS_UPDATE. */
	CRVS_UPDATE("crvs_update"),

	/** The CRVS_DEATH. */
	CRVS_DEATH("crvs_death"),

	/** The update. */
	UPDATE("update"),

	/** The res update. */
	RES_UPDATE("res_update"),

	/** The correction. */
	CORRECTION("correction"),

	/** The activated. */
	ACTIVATED("activated"),

	/** The deactivated. */
	DEACTIVATED("deactivated"),

	/** The lost */
	LOST("lost"),

	RES_REPRINT("res_reprint");

	public String regType;

	private RegistrationType(String regType) {
		this.regType = regType;
	}

	@Override
	public String toString() {
		return regType;
	}
}
