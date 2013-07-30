package demos.validate;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Value<T> {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private T value;
    private boolean valid;
    
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