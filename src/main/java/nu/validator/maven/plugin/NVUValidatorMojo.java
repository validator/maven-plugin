/*
 * Copyright (c) 2020 Nicolai Ehemann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package nu.validator.maven.plugin;

import nu.validator.maven.plugin.configuration.FileSetCfg;
import nu.validator.maven.plugin.configuration.FilterCfg;
import nu.validator.maven.plugin.configuration.MojoInvalidConfigurationException;
import nu.validator.maven.plugin.configuration.ValidatorCfg;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Validates HTML / CSS / SVG files
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
public class NVUValidatorMojo extends AbstractMojo {

  /**
   * The Maven Project Object
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  /**
   * The fileset configuration
   */
  @Parameter(required = true)
  private List<FileSetCfg> filesets;

  /**
   * Fail on first error
   */
  @Parameter
  private boolean failfast;

  /**
   * Use ASCII quotes instead of unicode quotes (default true)
   */
  @Parameter
  private boolean asciiquotes = true;

  /**
   * Validator configuration
   */
  @Parameter
  private ValidatorCfg validator = new ValidatorCfg();

  private final FileSystem fileSystem;

  NVUValidatorMojo(final FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public NVUValidatorMojo() {
    this(FileSystems.getDefault());
  }

  @Override
  public final void execute() throws MojoFailureException {
    final NVUValidatorConfiguration configuration = prepareConfiguration();

    final NVUValidator nvuValidator = new NVUValidator(this.project, this.fileSystem, this.getLog(), configuration);
    nvuValidator.validate();
  }

  private NVUValidatorConfiguration prepareConfiguration() throws MojoFailureException {
    validateFilesetConfguration();
    compileRegexes(validator.getFilters());
    final NVUValidatorConfiguration configuration = new NVUValidatorConfiguration();
    configuration.setFailfast(failfast);
    configuration.setAsciiquotes(asciiquotes);
    configuration.setValidatorCfg(validator);
    configuration.setFilesets(filesets);

    return configuration;
  }

  private void compileRegexes(final List<FilterCfg> filters) throws MojoFailureException {
    for (FilterCfg filter : filters) {
      filter.compilePattern();
    }
  }

  private void validateFilesetConfguration() throws MojoInvalidConfigurationException {
    for (FileSetCfg fileset : filesets) {
      fileset.validate();
    }
  }

  public final void setProject(final MavenProject project) {
    this.project = project;
  }

  public final void setFilesets(final List<FileSetCfg> filesets) {
    this.filesets = filesets;
  }

  public final void setFailfast(final boolean failfast) {
    this.failfast = failfast;
  }

  public final void setAsciiquotes(final boolean asciiquotes) {
    this.asciiquotes = asciiquotes;
  }

  public final void setValidator(final ValidatorCfg validator) {
    this.validator = validator;
  }
}
