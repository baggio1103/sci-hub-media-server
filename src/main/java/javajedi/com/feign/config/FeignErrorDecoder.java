package javajedi.com.feign.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import javajedi.com.data.HttpResponse;
import javajedi.com.feign.exception.FeignRequestException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    ErrorDecoder errorDecoder = new Default();

    @Override
    @SneakyThrows
    public Exception decode(String s, Response response) {
        ObjectMapper mapper = new ObjectMapper();
        byte[] body = new byte[0];
        if (response.body() != null) {
            body = Util.toByteArray(response.body().asInputStream());
        }
        HttpResponse httpResponse = mapper.readValue(body, HttpResponse.class);
        if (HttpStatus.valueOf(response.status()).is4xxClientError()) {
            return new FeignRequestException(httpResponse);
        }
        return errorDecoder.decode(s, response);
    }

}
