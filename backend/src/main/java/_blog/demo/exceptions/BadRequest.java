package _blog.demo.exceptions;

public class BadRequest extends RuntimeException {
    public BadRequest(String msg) {
        super(msg);
    }
}
