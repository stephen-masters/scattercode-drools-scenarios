package demos.validate;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Value<T> {
    private final PropertyChangeSupport pcs;

    private T value;
    private boolean valid;

    // PropertyChangeSupport only keeps 'this' as the source of the events it
    // fires; it calls nothing on it, so a subclass need not be initialised yet.
    @SuppressWarnings("this-escape")
    public Value() {
        this.pcs = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        T oldValue = this.value;
        this.value = value;

        pcs.firePropertyChange("value", oldValue, value);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        boolean oldValue = this.valid;
        this.valid = valid;

        pcs.firePropertyChange("valid", oldValue, valid);
    }
}
