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

import nu.validator.maven.plugin.configuration.DocumentType;
import nu.validator.maven.plugin.configuration.FileSetCfg;
import nu.validator.maven.plugin.configuration.FilterCfg;
import nu.validator.maven.plugin.configuration.ValidatorCfg;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import nu.validator.validation.SimpleDocumentValidator;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXException;

/**
 *
 * @author Nicolai Ehemann, 2020-09-04
 */
public class NVUValidator {

  private static final String SCHEMA_URL_HTML = "http://s.validator.nu/html5-all.rnc";
  private static final String SCHEMA_URL_SVG = "http://s.validator.nu/svg-xhtml5-rdf-mathml.rnc";
  private static final String SCHEMA_URL_XHTML = "http://s.validator.nu/xhtml5-all.rnc";

  private final XMLErrorHandler errorHandler;

  private final FileSystem fileSystem;

  private final Log log;

  private final NVUValidatorConfiguration configuration;

  private final String nvuVersion;

  NVUValidator(final MavenProject project, final FileSystem fileSystem, final Log log, final NVUValidatorConfiguration configuration) {
    nvuVersion = SimpleDocumentValidator.class.getPackage().getImplementationVersion();
    log.info("Running Nu HtmlChecker (v.Nu) v" + nvuVersion);
    this.fileSystem = fileSystem;
    this.log = log;
    this.errorHandler = new XMLErrorHandler(configuration.isAsciiquotes(), project.getBasedir());
    this.configuration = configuration;
  }

  private boolean trueOrFailIf(final boolean fail) throws MojoFailureException {
    if (fail) {
      throw new MojoFailureException("Validation failed");
    }
    return true;
  }

  /**
   * Validiert die in der Konfiguration angegebenen Filesets
   *
   * @throws MojoFailureException
   */
  void validate() throws MojoFailureException {
    boolean failed = false;
    for (final FileSetCfg fileset : configuration.getFilesets()) {
      this.errorHandler.clear();
      if (!validateFileset(setupDocumentValidator(), fileset, configuration.getValidatorCfg())) {
        failed = trueOrFailIf(configuration.isFailfast());
      }
    }
    if (failed) {
      throw new MojoFailureException("Validation failed");
    }
  }

  private SimpleDocumentValidator setupDocumentValidator() {
    final SimpleDocumentValidator documentValidator = new SimpleDocumentValidator(true, false, true);
    this.setSchema(documentValidator, SCHEMA_URL_HTML);
    return documentValidator;
  }

  private void setSchema(final SimpleDocumentValidator documentValidator, final String schemaUrl) {
    if (!schemaUrl.equals(documentValidator.getMainSchemaUrl())) {
      try {
        documentValidator.setUpMainSchema(schemaUrl, errorHandler);
        documentValidator.setUpValidatorAndParsers(errorHandler, false, false);
      } catch (SAXException ex) {
        Logger.getLogger(NVUValidatorMojo.class.getName()).log(Level.SEVERE, null, ex);
      } catch (Exception ex) {
        Logger.getLogger(NVUValidatorMojo.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private boolean validateFileset(final SimpleDocumentValidator documentValidator, final FileSetCfg fileset, final ValidatorCfg validatorCfg) throws MojoFailureException {
    this.log.info("Validating fileset " + fileset.getDirectory());
    boolean validationFailed = false;
    final SortedSet<String> files = getFiles(fileset);

    for (String file : files) {
      log.debug("  Validating file " + file);
      try {
        checkFile(documentValidator, Paths.get(file).toFile(), validatorCfg);
      } catch (FileNotFoundException ex) {
        throw new MojoFailureException(String.format("File not found: %s", file));
      } catch (IOException ex) {
        throw new MojoFailureException(String.format("Error reading file: %s", file));
      } catch (SAXException ex) {
        this.errorHandler.addError(new ValidationError(ValidationError.Type.WARNING, file, -1, -1, ex.getMessage()));
      }
    }
    int warnings = 0;
    int errors = 0;
    for (ValidationError error : this.errorHandler.getErrors()) {
      switch (error.getType()) {
        case WARNING:
          switch (validatorCfg.getWarnings()) {
            case WARN:
              if (!isFiltered(error, validatorCfg.getFilters())) {
                this.log.warn(error.toString());
                warnings++;
              }
              break;
            case ERROR:
              if (!isFiltered(error, validatorCfg.getFilters())) {
                this.log.error(error.toString());
                validationFailed = trueOrFailIf(configuration.isFailfast());
                errors++;
              }
              break;
            case IGNORE:
            default:
          }
          break;
        case FATAL:
        case ERROR:
          if (!isFiltered(error, validatorCfg.getFilters())) {
            this.log.error(error.toString());
            validationFailed = trueOrFailIf(configuration.isFailfast());
            errors++;
          }
          break;
        default:
      }
    }
    this.log.info(String.format("Found %d warnings and %d errors", warnings, errors));
    return !validationFailed;
  }

  private SortedSet<String> getFiles(final FileSetCfg fileset) throws MojoFailureException {
    final Path srcDirectory = getPath(fileset.getDirectory());

    if (!Files.exists(srcDirectory)) {
      throw new MojoFailureException("Mojo error: directory " + fileset.getDirectory() + " does not exist.");
    } else if (!Files.isDirectory(srcDirectory)) {
      throw new MojoFailureException("Mojo error: " + fileset.getDirectory() + " is not a directory.");
    }
    final SortedSet<String> files = new TreeSet<>();
    final PathMatcher pathMatcher = this.fileSystem.getPathMatcher("glob:" + fileset.getGlob());

    try {
      Files.walk(srcDirectory, FileVisitOption.FOLLOW_LINKS)
          .filter(path -> Files.isRegularFile(path) && pathMatcher.matches(srcDirectory.relativize(path)))
          .forEach(path -> files.add(path.toString()));
    } catch (IOException exception) {
      throw new MojoFailureException("Mojo execution failed due to I/O error (" + exception.getMessage() + ").");
    }
    return files;
  }

  private Path getPath(final String source) {
    return this.fileSystem.getPath(source);
  }

  private void checkFile(final SimpleDocumentValidator documentValidator, final File file, final ValidatorCfg validatorCfg) throws IOException, SAXException {
    DocumentType documentType;
    if (validatorCfg.getForceType() != null) {
      documentType = validatorCfg.getForceType();
      if (documentType == null) {
        documentType = DocumentType.HTML;
      }
    } else {
      final String extension = getFileExtension(file.getName());
      documentType = Stream.of(DocumentType.values())
          .filter(type -> type.getExtensions().contains(extension))
          .findAny()
          .orElse(null);
      if (documentType == null) {
        log.warn("Unable to determine file type of file " + file.getAbsolutePath());
      }
    }
    if (documentType != null) {
      switch (documentType) {
        case HTML:
          checkHtmlFile(documentValidator, file, true);
          break;
        case CSS:
          checkCssFile(documentValidator, file, true);
          break;
        case SVG:
          checkSvgFile(documentValidator, file);
          break;
        case XHTML:
          checkXHTMLFile(documentValidator, file);
          break;
        default:
      }
    }
  }

  private String getFileExtension(final String filename) {
    String extension = "";

    final int i = filename.lastIndexOf('.');
    if (i > 0) {
      extension = filename.substring(i + 1);
    }
    return extension;
  }

  private boolean isFiltered(final ValidationError error, final List<FilterCfg> filters) {
    return filters.stream()
        .anyMatch(filter -> (
                filter.getType() == FilterCfg.FilterType.ALL
                  || filter.getType().getErrorType() == error.getType())
                && filter.getPattern().matcher(error.getMessage()).find());
  }

  private void checkHtmlFile(final SimpleDocumentValidator documentValidator, final File file, final boolean asUTF8) throws IOException, SAXException {
    this.setSchema(documentValidator, SCHEMA_URL_HTML);
    documentValidator.checkHtmlFile(file, asUTF8);
  }

  private void checkSvgFile(final SimpleDocumentValidator documentValidator, final File file) throws IOException, SAXException {
    this.setSchema(documentValidator, SCHEMA_URL_SVG);
    documentValidator.checkXmlFile(file);
  }

  private void checkCssFile(final SimpleDocumentValidator documentValidator, final File file, final boolean asUTF8) throws IOException, SAXException {
    documentValidator.checkCssFile(file, asUTF8);
  }

  private void checkXHTMLFile(final SimpleDocumentValidator documentValidator, final File file) throws IOException, SAXException {
    this.setSchema(documentValidator, SCHEMA_URL_XHTML);
    documentValidator.checkXmlFile(file);
  }
}
