package fr.viveris.xlsdiff.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

public class Exceptions extends Exception implements Iterable<Exception> {
    private static final long           serialVersionUID = 1L;

    private final Collection<Exception> exceptions       = new LinkedList<>();

    public void add(final Exception exception) {
        this.exceptions.add(exception);
    }

    public void add(final Exceptions exceptions) {
        this.exceptions.addAll(exceptions.exceptions);
    }

    @Override
    public Iterator<Exception> iterator() {
        return this.exceptions.iterator();
    }

    @Override
    public String getMessage() {
        final Collection<String> messages = new LinkedList<>();
        for (final Exception exception : this) {
            messages.add(exception.getMessage());
        }
        return StringUtils.join(messages, System.getProperty("line.separator"));
    }

    public boolean hasErrors() {
        return !this.exceptions.isEmpty();
    }

    public void throwIfNeed() throws Exceptions {
        if (!hasErrors()) {
            return;
        }
        throw this;
    }
}
