package _blog.demo.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String msg){
        super(msg);
    }
}
