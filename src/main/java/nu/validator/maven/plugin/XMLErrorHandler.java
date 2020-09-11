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

import nu.validator.maven.plugin.ValidationError.Type;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Nicolai Ehemann, 2020-09-03
 */
public class XMLErrorHandler implements ErrorHandler {

  private final boolean asciiQuotes;

  private final Path basePath;

  /**
   * List of validation errors.
   */
  private List<ValidationError> errors = new ArrayList<>();

  public XMLErrorHandler(final boolean asciiQuotes, final File basedir) {
    this.asciiQuotes = asciiQuotes;
    this.basePath = Paths.get(basedir.getAbsolutePath());
  }

  /**
   * Clear the list of validation errors.
   */
  public void clear() {
    this.errors.clear();
  }

  /**
   * Register a warning
   *
   * @param e
   */
  @Override
  public void warning(final SAXParseException e) {
    this.errors.add(createValidationError(Type.WARNING, e));
  }

  /**
   * Register an error
   *
   * @param e
   */
  @Override
  public void error(final SAXParseException e) {
    this.errors.add(createValidationError(Type.ERROR, e));
  }

  /**
   * Register a fatal error
   *
   * @param e
   */
  @Override
  public void fatalError(final SAXParseException e) {
    this.errors.add(createValidationError(Type.FATAL, e));
  }

  /**
   * @return a list of validation errors.
   */
  public List<ValidationError> getErrors() {
    return this.errors;
  }

  /**
   * @param error an error
   */
  public void addError(final ValidationError error) {
    this.errors.add(error);
  }

  private ValidationError createValidationError(final Type type, final SAXParseException e) {
    return new ValidationError(type, toRelativePath(e.getSystemId()), e.getLineNumber(), e.getColumnNumber(), this.asciiQuotes ? this.quotesToAscii(e.getMessage()) : e.getMessage());
  }

  private String toRelativePath(final String path) {
    return basePath.relativize(Paths.get(URI.create(path))).toString();
  }

  private String quotesToAscii(final String message) {
    return message.replace('“', '"').replace('”', '"');
  }
}
