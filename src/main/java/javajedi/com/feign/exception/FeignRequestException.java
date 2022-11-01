package javajedi.com.feign.exception;

import javajedi.com.data.HttpResponse;

public class FeignRequestException extends RuntimeException {

    private final HttpResponse httpResponse;

    public FeignRequestException(HttpResponse httpResponse){
        super(httpResponse.getMessage());
        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

}
