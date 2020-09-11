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

import nu.validator.maven.plugin.ValidationError.Type;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Nicolai Ehemann, 2020-09-04
 */
public class FilterCfg {

  public enum FilterType {
    WARNING(Type.WARNING),
    ERROR(Type.ERROR),
    ALL;

    private final Type errorType;

    FilterType() {
      errorType = null;
    }

    FilterType(final Type errorType) {
      this.errorType = errorType;
    }

    public final Type getErrorType() {
      return errorType;
    }
  }

  /**
   * Type of validation messages to be filtered. Possible values:
   * <ul>
   * <li>WARN</li>
   * <li>ERROR</li>
   * <li>ALL (default)</li>
   * </ul>
   */
  @Parameter(defaultValue = "ALL")
  private FilterType type = FilterType.ALL;

  /**
   * Regex to be filtered
   */
  @Parameter(required = true)
  private String regex;

  private Pattern pattern;

  public final FilterType getType() {
    return type;
  }

  public final void setType(final FilterType type) {
    this.type = type;
  }

  public final String getRegex() {
    return regex;
  }

  public final void setRegex(final String regex) throws MojoFailureException {
    this.regex = regex;
  }

  public final Pattern getPattern() {
    return pattern;
  }

  public final void compilePattern() throws MojoInvalidConfigurationException {
    if (regex == null) {
      throw new MojoInvalidConfigurationException("Missing required <regex> in <filter>");
    }
    try {
      pattern = Pattern.compile(regex);
    } catch (PatternSyntaxException e) {
      throw new MojoInvalidConfigurationException("Invalid filter regex: " + e.getMessage());
    }
  }

  @Override
  public final String toString() {
    return "filter{type=" + type + ", regex=" + regex + '}';
  }
}
