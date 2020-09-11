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
import nu.validator.maven.plugin.configuration.ValidatorCfg;

import java.util.List;

/**
 *
 * @author Nicolai Ehemann, 2020-09-04
 */
class NVUValidatorConfiguration {

  /**
   * The fileset configuration
   */
  private List<FileSetCfg> filesets;

  /**
   * Fail on first error
   */
  private boolean failfast;

  /**
   * Use ASCII quotes instead of unicode quotes (default true)
   */
  private boolean asciiquotes = true;

  /**
   * Validator configuration
   */
  private ValidatorCfg validatorCfg;

  public List<FileSetCfg> getFilesets() {
    return filesets;
  }

  public void setFilesets(final List<FileSetCfg> filesets) {
    this.filesets = filesets;
  }

  public boolean isFailfast() {
    return failfast;
  }

  public void setFailfast(final boolean failfast) {
    this.failfast = failfast;
  }

  public boolean isAsciiquotes() {
    return asciiquotes;
  }

  public void setAsciiquotes(final boolean asciiquotes) {
    this.asciiquotes = asciiquotes;
  }

  public ValidatorCfg getValidatorCfg() {
    return validatorCfg;
  }

  public void setValidatorCfg(final ValidatorCfg validatorCfg) {
    this.validatorCfg = validatorCfg;
  }


}
