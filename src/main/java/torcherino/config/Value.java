package torcherino.config;

public @interface Value
{
	/**
	 *  @return the name of the field when serialized or deserialized.
	 */
	String name();

	/**
	 * @return the comment to be displayed when serialized.
	 */
	String[] comment() default {};
}
