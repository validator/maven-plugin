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

public class ValidationError {

  /**
   * Error types.
   */
  public enum Type {
    /**
     * Warning type.
     */
    WARNING,
    /**
     * Error type.
     */
    ERROR,
    /**
     * Fatal type.
     */
    FATAL
  }

  /**
   * Type of the error.
   */
  private final Type type;

  /**
   * Path of the file the error occured in
   */
  private final String path;

  /**
   * Line where the error occurred.
   */
  private final int line;

  /**
   * Column where the error occurred.
   */
  private final int column;

  /**
   * Message of the error.
   */
  private final String message;

  /**
   * Constructor.
   *
   * @param type error type
   * @param line line where the error occurred
   * @param column column where the error occurred
   * @param message message of the error
   */
  public ValidationError(final Type type, final String path, final int line, final int column, final String message) {
    this.type = type;
    this.path = path;
    this.line = line;
    this.column = column;
    this.message = message;
  }

  /**
   * @return error type
   */
  public Type getType() {
    return this.type;
  }

  /**
   * @return file where the error occured
   */
  public String getPath() {
    return path;
  }

  /**
   * @return line where the error occurred
   */
  public int getLine() {
    return this.line;
  }

  /**
   * @return column where the error occurred
   */
  public int getColumn() {
    return this.column;
  }

  /**
   * @return message of the error
   */
  public String getMessage() {
    return this.message;
  }

  /**
   * @return the error in a human readable format
   */
  @Override
  public String toString() {
    return String.format("%s line %d column %d: %s: %s", this.path, this.line, this.column, this.type.toString(), this.message);
  }
}
