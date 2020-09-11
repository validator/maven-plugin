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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Nicolai Ehemann
 */
public final class ApplicationProperties {

  @SuppressWarnings("checkstyle:constantname")
  private static final ApplicationProperties configuration = new ApplicationProperties();

  public static final String PROJECT_VERSION;
  public static final String PROJECT_URL;

  static {
    final InputStream is = ApplicationProperties.class.getResourceAsStream("/application.properties");
    final Properties p = new Properties();
    try {
      p.load(is);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to instantiate NVU Configuration");
    }
    PROJECT_VERSION = p.getProperty("version");
    PROJECT_URL = p.getProperty("url");
  }

  private ApplicationProperties() {
  }

  public static ApplicationProperties getInstance() {
    return configuration;
  }

}
