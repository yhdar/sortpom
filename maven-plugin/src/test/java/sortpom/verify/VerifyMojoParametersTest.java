package sortpom.verify;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import sortpom.SortPomImpl;
import sortpom.VerifyMojo;
import sortpom.XmlProcessor;
import sortpom.parameter.VerifyFailType;
import sortpom.util.FileUtil;
import sortpom.util.ReflectionHelper;
import sortpom.wrapper.ElementWrapperCreator;
import sortpom.wrapper.TextWrapperCreator;
import sortpom.wrapper.WrapperFactoryImpl;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class VerifyMojoParametersTest {
    private final File pomFile = mock(File.class);

    private SortPomImpl sortPomImpl;
    private FileUtil fileUtil;
    private VerifyMojo verifyMojo;
    private XmlProcessor xmlProcessor;
    private ElementWrapperCreator elementWrapperCreator;
    private TextWrapperCreator textWrapperCreator;

    @Before
    public void setup() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException, MojoFailureException {
        verifyMojo = new VerifyMojo();
        new ReflectionHelper(verifyMojo).setField("lineSeparator", "\n");
        new ReflectionHelper(verifyMojo).setField("encoding", "UTF-8");
        new ReflectionHelper(verifyMojo).setField("verifyFail", "SORT");

        sortPomImpl = new ReflectionHelper(verifyMojo).getField(SortPomImpl.class);
        fileUtil = new ReflectionHelper(sortPomImpl).getField(FileUtil.class);
        xmlProcessor = new ReflectionHelper(sortPomImpl).getField(XmlProcessor.class);
        WrapperFactoryImpl wrapperFactoryImpl = new ReflectionHelper(sortPomImpl).getField(WrapperFactoryImpl.class);
        elementWrapperCreator = new ReflectionHelper(wrapperFactoryImpl).getField(ElementWrapperCreator.class);
        textWrapperCreator = new ReflectionHelper(wrapperFactoryImpl).getField(TextWrapperCreator.class);
    }

    @Test
    public void pomFileParameter() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("pomFile", pomFile, sortPomImpl, fileUtil);
    }

    @Test
    public void createBackupFileParameter() throws Exception {
        testParameterMoveFromMojoToRestOfApplicationForBoolean("createBackupFile", true, sortPomImpl);
    }

    @Test
    public void backupFileExtensionParameter() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("backupFileExtension", ".gurka", sortPomImpl, fileUtil);
    }

    @Test
    public void encodingParameter() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("encoding", "GURKA-2000", fileUtil, sortPomImpl, xmlProcessor);
    }

    @Test
    public void lineSeparatorParameter() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("lineSeparator", "\r");

        assertEquals("\r", new ReflectionHelper(xmlProcessor).getField("lineSeparatorUtil").toString());
    }

    @Test
    public void parameterNrOfIndentSpaceShouldEndUpInXmlProcessor() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("nrOfIndentSpace", 6);

        assertEquals("      ", new ReflectionHelper(xmlProcessor).getField("indentCharacters"));
    }

    @Test
    public void expandEmptyElementsParameter() throws Exception {
        testParameterMoveFromMojoToRestOfApplicationForBoolean("expandEmptyElements", true, xmlProcessor);
    }

    @Test
    public void predefinedSortOrderParameter() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("predefinedSortOrder", "tomatoSort", fileUtil);
    }

    @Test
    public void parameterSortOrderFileShouldEndUpInFileUtil() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("sortOrderFile", "sortOrderFile.gurka");

        Object actual = new ReflectionHelper(fileUtil).getField("customSortOrderFile");
        assertEquals("sortOrderFile.gurka", actual);
    }

    @Test
    public void parameterSortDependenciesShouldEndUpInElementWrapperCreator() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("sortDependencies", "groupId,scope");

        Object sortDependencies = new ReflectionHelper(elementWrapperCreator).getField("sortDependencies");
        assertThat(sortDependencies.toString(), is("DependencySortOrder{childElementNames=[groupId, scope]}"));
    }

    @Test
    public void parameterSortPluginsShouldEndUpInWrapperFactoryImpl() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("sortPlugins", "alfa,beta");

        Object sortDependencies = new ReflectionHelper(elementWrapperCreator).getField("sortPlugins");
        assertThat(sortDependencies.toString(), is("DependencySortOrder{childElementNames=[alfa, beta]}"));
    }

    @Test
    public void parameterSortPropertiesShouldEndUpInWrapperFactoryImpl() throws Exception {
        testParameterMoveFromMojoToRestOfApplicationForBoolean("sortProperties", true, elementWrapperCreator);
    }

    @Test
    public void parameterKeepBlankLineShouldEndUpInXmlProcessor() throws Exception {
        testParameterMoveFromMojoToRestOfApplicationForBoolean("keepBlankLines", true, textWrapperCreator);
    }

    @Test
    public void parameterIndentBlankLineShouldEndUpInXmlProcessor() throws Exception {
        testParameterMoveFromMojoToRestOfApplicationForBoolean("indentBlankLines", true, xmlProcessor);
    }

    @Test
    public void parameterVerifyFailShouldEndUpInXmlProcessor() throws Exception {
        testParameterMoveFromMojoToRestOfApplication("verifyFail", "STOP");

        assertEquals(VerifyFailType.STOP, new ReflectionHelper(sortPomImpl).getField("verifyFailType"));
    }

    private void testParameterMoveFromMojoToRestOfApplication(String parameterName, Object parameterValue,
                                                              Object... whereParameterCanBeFound) throws
            Exception {
        new ReflectionHelper(verifyMojo).setField(parameterName, parameterValue);

        new ReflectionHelper(verifyMojo).executeMethod("setup");

        for (Object someInstanceThatContainparameter : whereParameterCanBeFound) {
            Object actual = new ReflectionHelper(someInstanceThatContainparameter).getField(parameterName);
            assertSame(parameterValue, actual);
        }
    }

    private void testParameterMoveFromMojoToRestOfApplicationForBoolean(String parameterName, boolean parameterValue,
                                                                        Object... whereParameterCanBeFound) throws
            Exception {
        new ReflectionHelper(verifyMojo).setField(parameterName, parameterValue);

        new ReflectionHelper(verifyMojo).executeMethod("setup");

        for (Object someInstanceThatContainparameter : whereParameterCanBeFound) {
            Object actual = new ReflectionHelper(someInstanceThatContainparameter).getField(parameterName);
            assertEquals(parameterValue, actual);
        }
    }

}
