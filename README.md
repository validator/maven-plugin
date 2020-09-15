# nvu-maven-plugin

maven plugin for HTML validation, using the [Nu Html Checker (v.Nu)](https://validator.github.io/)

The plugin can be configured to be run in the *valdiation* phase of the maven
build and cause the build to fail in case errors are reported.

## Usage

Add the plugin to your pom.xml:

    <build>
        <plugins>
            <plugin>
                <groupId>nu.validator</groupId>
                <artifactId>vnu-maven-plugin</artifactId>
                <version>1.0.0</version>

                <!-- The plugin should hook into the validate goal -->
                <executions>
                    <execution>
                        <goals>
                            <goal>validate</goal>
                        </goals>
                    </execution>
                </executions>

                <configuration>
                    <filesets>
                        <fileset>
                            <directory>...</directory>
                            ...
                        </fileset>
                    </filesets>
                    [<validator>...</validator>]
                </configuration>
            </plugin>
        </plugins>
    </build>

The configuration is also made in the pom.xml. There are defaults in place,
you only need to configure at least one fileset to point to the files to be
validated.

The fileset configuration consists of a directory (relative to the maven
project root) and an optional ant glob directive to filter the contents of
the directory. Directories are scanned recursively by default. Example:

    <fileset>
        <directory>src/main/resources/templates</directory>
        <glob>**.html</glob>
    </fileset>

The validator can also be configured:

    <validator>
        <warnings>WARN</warnings>
        <forceType>HTML</forceType>
        <filters>
            <filter>
                ...
            </filter>
        </filters>
    </validator>

Here, all settings are optional.

### Handling warnings

By default, warnings are displayed as such and don't cause the build to
fail. It can be configured through the <warnings> directive:

- WARN (default) - show warnings
- IGNORE - supress warnings
- ERROR - treat as errors (affects filtering, see below)

### Forcing file type

By default, the file type (html/xhtml, css, svg) is determined by the
file extension. Through the <forceType> directive, it is possible to
force all files to be considered on of types HTML, CSS, SVG or XHTML.

### Filtering

It is possible to filter warnings and/or errors based on <type> (WARN,
ERROR, ALL, optional, default ALL) and a <regex>. Filtered items
are completely ignored. Mutiple filters can be configured. Example:

    <filter>
        <regex>Duplicate ID "[a-zA-Z0-9.]*"</regex>
        <type>ERROR</type>
    </filter>

## Additional (general) options

### Failfast

Fail on first error (default is to process all errors and fail in the end
if there are errors)

    <failfast>true</failfast>

### ASCII / Unicode quotes

By default, quotes in the errors messages will be displayed as ASCII quotes.
It is possible to switch to unicode quites by setting this option to 'false'

    <asciiquotes>false</asciiquotes>
