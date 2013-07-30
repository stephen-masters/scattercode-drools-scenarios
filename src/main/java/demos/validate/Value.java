package demos.validate;

public class Value<T> {
    private T value;
    private boolean valid;
    
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        T oldValue = this.value;
        this.value = value;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        boolean oldValue = this.valid;
        this.valid = valid;
    }
}
