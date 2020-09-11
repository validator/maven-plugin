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

import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Nicolai Ehemann
 */
public class FileSetCfg {

  /**
   * The directory of the files to be checked
   * @since 1.0.0
   */
  @Parameter(required = true)
  private String directory;

  /**
   * Globbing pattern to filter the files in the source directory (as specified
   * by the {@link java.nio.file.FileSystem#getPathMatcher(java.lang.String)
   * getPathMatcher()} method)
   * @since 1.0.0
   */
  @Parameter(defaultValue = "*")
  private String glob;

  public FileSetCfg() {
  }

  public final String getDirectory() {
    return directory;
  }

  public final void setDirectory(final String directory) {
    this.directory = directory;
  }

  public final String getGlob() {
    return glob;
  }

  public final void setGlob(final String glob) {
    this.glob = glob;
  }

  public final void validate() throws MojoInvalidConfigurationException {
    if (directory == null) {
      throw new MojoInvalidConfigurationException("Missing required <directory> in <fileset>");
    }
  }
}
