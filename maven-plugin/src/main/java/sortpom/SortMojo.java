package sortpom;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import sortpom.exception.ExceptionHandler;
import sortpom.exception.FailureException;
import sortpom.logger.MavenLogger;
import sortpom.parameter.PluginParameters;
import sortpom.parameter.PluginParametersBuilder;

import java.io.File;

/**
 * Mojo (Maven plugin) that sorts the pom file for a maven project.
 *
 * @author Bjorn Ekryd
 * @goal sort
 * @threadSafe true
 */
@SuppressWarnings({"UnusedDeclaration", "JavaDoc"})
public class SortMojo extends AbstractMojo {
    /**
     * This is the File instance that refers to the location of the pom that
     * should be sorted.
     *
     * @parameter expression="${sort.pomFile}" default-value="${project.file}"
     */
    private File pomFile;

    /**
     * Should a backup copy be created for the sorted pom.
     *
     * @parameter expression="${sort.createBackupFile}" default-value="true"
     */
    private boolean createBackupFile;

    /**
     * Name of the file extension for the backup file.
     *
     * @parameter expression="${sort.backupFileExtension}" default-value=".bak"
     */
    private String backupFileExtension;

    /**
     * Encoding for the files.
     *
     * @parameter expression="${sort.encoding}" default-value="UTF-8"
     */
    private String encoding;

    /**
     * Line separator for sorted pom. Can be either \n, \r or \r\n
     *
     * @parameter expression="${sort.lineSeparator}"
     * default-value="${line.separator}"
     */
    private String lineSeparator;

    /**
     * Should empty xml elements be expanded or not. Example:
     * &lt;configuration&gt;&lt;/configuration&gt; or &lt;configuration/&gt;
     *
     * @parameter expression="${sort.expandEmptyElements}" default-value="true"
     */
    private boolean expandEmptyElements;

    /**
     * Should blank lines in the pom-file be perserved. A maximum of one line is preserved between each tag.
     *
     * @parameter expression="${sort.keepBlankLines}" default-value="false"
     */
    private boolean keepBlankLines;

    /**
     * Number of space characters to use as indentation. A value of -1 indicates
     * that tab character should be used instead.
     *
     * @parameter expression="${sort.nrOfIndentSpace}" default-value="2"
     */
    private int nrOfIndentSpace;

    /**
     * Should blank lines (if preserved) have indentation.
     *
     * @parameter expression="${sort.indentBlankLines}" default-value="false"
     */
    private boolean indentBlankLines;

    /**
     * Choose between a number of predefined sort order files.
     *
     * @parameter expression="${sort.predefinedSortOrder}"
     */
    private String predefinedSortOrder;

    /**
     * Custom sort order file.
     *
     * @parameter expression="${sort.sortOrderFile}"
     */
    private String sortOrderFile;

    /**
     * Comma-separated ordered list how dependencies should be sorted. Example: scope,groupId,artifactId
     * If scope is specified in the list then the scope ranking is COMPILE, PROVIDED, SYSTEM, RUNTIME, IMPORT and TEST.
     * The list can be seprated by ,;:
     *
     * @parameter expression="${sort.sortDependencies}" default-value=""
     */
    private String sortDependencies;

    /**
     * Comma-separated ordered list how plugins should be sorted. Example: groupId,artifactId
     * The list can be seprated by ,;:
     *
     * @parameter expression="${sort.sortPlugins}" default-value=""
     */
    private String sortPlugins;

    /**
     * Should the Maven pom properties be sorted alphabetically. Affects both
     * project/properties and project/profiles/profile/properties
     *
     * @parameter expression="${sort.sortProperties}" default-value="false"
     */
    private boolean sortProperties;

    private final SortPomImpl sortPomImpl = new SortPomImpl();

    public SortMojo() {
    }

    /**
     * Execute plugin.
     *
     * @throws org.apache.maven.plugin.MojoFailureException
     *          exception that will be handled by plugin framework
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    //@Override
    public void execute() throws MojoFailureException {
        setup();
        sortPom();
    }

    void setup() throws MojoFailureException {
        PluginParameters pluginParameters = new PluginParametersBuilder()
                .setPomFile(pomFile)
                .setBackupInfo(createBackupFile, backupFileExtension)
                .setEncoding(encoding)
                .setFormatting(lineSeparator, expandEmptyElements, keepBlankLines)
                .setIndent(nrOfIndentSpace, indentBlankLines)
                .setSortOrder(sortOrderFile, predefinedSortOrder)
                .setSortEntities(sortDependencies, sortPlugins, sortProperties).createPluginParameters();
        try {
            sortPomImpl.setup(new MavenLogger(getLog()), pluginParameters);
        } catch (FailureException fex) {
            ExceptionHandler.throwMojoFailureException(fex);
        }
    }

    private void sortPom() throws MojoFailureException {
        try {
            sortPomImpl.sortPom();
        } catch (FailureException fex) {
            ExceptionHandler.throwMojoFailureException(fex);
        }
    }

}
