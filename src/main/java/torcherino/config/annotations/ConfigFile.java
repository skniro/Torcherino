package torcherino.config.annotations;

import torcherino.config.ConfigManager;
import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface ConfigFile
{
	String name();

	String extension() default ConfigManager.DEFAULT_EXTENSION;
}
