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

package nu.validator.maven.plugin.configuration;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Nicolai Ehemann, 2020-09-03
 */
public class ValidatorCfg {

  public enum WarningConfig {
    WARN,
    ERROR,
    IGNORE
  }

  /**
   * Force document type. Possible values:
   * <ul>
   * <li>HTML</li>
   * <li>CSS</li>
   * <li>SVG</li>
   * <li>XHTML</li>
   * </ul>
   * @since 1.0.0
   */
  @Parameter
  private DocumentType forceType;

  /**
   * How to handle warnings
   * <dl>
   * <dt>WARN (default)</dt><dd>Show warning</dd>
   * <dt>IGNORE</dt><dd>Supress warnings</dd>
   * <dt>ERROR</dt><dd>Treat as errors</dd>
   * </dl>
   * @since 1.0.0
   */
  @Parameter
  private WarningConfig warnings = WarningConfig.WARN;

  /**
   * Filter errors to be filtered by regex
   */
  @Parameter
  private List<FilterCfg> filters = new ArrayList<>();

  public final DocumentType getForceType() {
    return forceType;
  }

  public final void setForceType(final DocumentType forceType) {
    this.forceType = forceType;
  }

  public final WarningConfig getWarnings() {
    return warnings;
  }

  public final void setWarnings(final WarningConfig warnings) {
    this.warnings = warnings;
  }

  public final List<FilterCfg> getFilters() {
    return filters;
  }

  public final void setFilters(final List<FilterCfg> filters) {
    this.filters = filters;
  }

  @Override
  public final String toString() {
    return "validator{warnings=" + warnings + ", filters=" + filters + '}';
  }

}
