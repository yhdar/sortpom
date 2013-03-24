package sortpom.wrapper;

import org.jdom.Content;
import org.jdom.Text;

/**
 * A wrapper that lets is element be unsorted
 *
 * @author Bjorn
 */
public final class ThrowAwayContentWrapper implements Wrapper<Text> {
    public static final ThrowAwayContentWrapper INSTANCE = new ThrowAwayContentWrapper();

    /** Instantiates a new wrapper, whose content will be thrown away. */
    private ThrowAwayContentWrapper() {
    }

    /** @see sortpom.wrapper.Wrapper#getContent() */
    //@Override
    public Text getContent() {
        throw new UnsupportedOperationException();
    }

    /** @see sortpom.wrapper.Wrapper#isBefore(sortpom.wrapper.Wrapper) */
    //@Override
    public boolean isBefore(final Wrapper<? extends Content> wrapper) {
        throw new UnsupportedOperationException();
    }

    /** @see sortpom.wrapper.Wrapper#isContentElement() */
    //@Override
    public boolean isContentElement() {
        throw new UnsupportedOperationException();
    }

    /** @see sortpom.wrapper.Wrapper#isResortable() */
    //@Override
    public boolean isResortable() {
        throw new UnsupportedOperationException();
    }

    //@Override
    public String toString() {
        throw new UnsupportedOperationException();
    }

    //@Override
    public String toString(String indent) {
        throw new UnsupportedOperationException();
    }
}
