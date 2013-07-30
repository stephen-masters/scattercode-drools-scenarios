package demos.validate;

public class Message {
    private Object fact;
    private String message;

    public Object getFact() {
        return fact;
    }

    public void setFact(Object fact) {
        this.fact = fact;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message(Object fact, String message) {
        setFact(fact);
        setMessage(message);
    }
}