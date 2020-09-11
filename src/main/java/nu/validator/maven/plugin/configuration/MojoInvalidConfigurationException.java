package nu.validator.maven.plugin.configuration;

import org.apache.maven.plugin.MojoFailureException;

public class MojoInvalidConfigurationException extends MojoFailureException {

    public MojoInvalidConfigurationException(final String message) {
        super("Configuration error: " + message);
    }
}
