package sortpom.wrapper;

import org.apache.commons.io.IOUtils;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import sortpom.parameter.PluginParameters;
import sortpom.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of a wrapper factory that sorts xml according to
 * sort order from fileUtil.
 * <p/>
 * Thank you Christian Haelg for your sortProperties patch.
 *
 * @author Bjorn Ekryd
 */
public class WrapperFactoryImpl implements WrapperFactory {

    /** How much the sort order index should increase for each element type */
    private static final int SORT_ORDER_INCREMENT = 100;

    /** Start value for sort order index. */
    private static final int SORT_ORDER_BASE = 1000;

    private final FileUtil fileUtil;

    private final ElementSortOrderMap elementSortOrderMap = new ElementSortOrderMap();
    private final ElementWrapperCreator elementWrapperCreator = new ElementWrapperCreator(elementSortOrderMap);
    private final TextWrapperCreator textWrapperCreator = new TextWrapperCreator();

    /**
     * Instantiates a new wrapper factory impl.
     *
     * @param fileUtil the file util
     */
    public WrapperFactoryImpl(final FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    /** Initializes the class with sortpom parameters. */
    public void setup(PluginParameters pluginParameters) {
        elementWrapperCreator.setup(pluginParameters);
        textWrapperCreator.setup(pluginParameters);
    }

    /** @see WrapperFactory#createFromRootElement(org.jdom.Element) */
//    @Override
    public WrapperOperations createFromRootElement(final Element rootElement) {
        initializeSortOrderMap();
        return new GroupWrapper(create((Content) rootElement));
    }

    /**
     * Creates sort order map from chosen sort order.
     *
     */
    private void initializeSortOrderMap() {
        try {
            Document document = createDocumentFromDefaultSortOrderFile();
            addElementsToSortOrderMap(document.getRootElement(), SORT_ORDER_BASE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JDOMException e) {
            throw new RuntimeException(e);
        }
    }

    private Document createDocumentFromDefaultSortOrderFile()
            throws JDOMException, IOException {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(fileUtil.getDefaultSortOrderXmlBytes());
            SAXBuilder parser = new SAXBuilder();
            return parser.build(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Processes the chosen sort order. Adds sort order element and sort index to
     * a map.
     */
    void addElementsToSortOrderMap(final Element element, int baseSortOrder) {
        elementSortOrderMap.addElement(element, baseSortOrder);
        final List<Element> castToChildElementList = castToChildElementList(element);
        // Increments the sort order index for each element
        int sortOrder = baseSortOrder;
        for (Element child : castToChildElementList) {
            sortOrder += SORT_ORDER_INCREMENT;
            addElementsToSortOrderMap(child, sortOrder);
        }
    }

    /** @see WrapperFactory#create(org.jdom.Content) */
    @SuppressWarnings("unchecked")
//    @Override
    public <T extends Content> Wrapper<T> create(final T content) {
        if (content instanceof Element) {
            return (Wrapper<T>) elementWrapperCreator.createWrapper((Element) content);
        }
        if (content instanceof Comment) {
            return new UnsortedWrapper<T>(content);
        }
        if (content instanceof Text) {
            return (Wrapper<T>) textWrapperCreator.createWrapper((Text) content);
        }
        return new UnsortedWrapper<T>(content);
    }

    /**
     * Performs getChildren for an element and casts the result to ArrayList of
     * Elements.
     */
    @SuppressWarnings("unchecked")
    private List<Element> castToChildElementList(final Element element) {
        return new ArrayList<Element>(element.getChildren());
    }

}
