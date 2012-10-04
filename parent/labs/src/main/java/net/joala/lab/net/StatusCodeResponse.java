package net.joala.lab.net;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * A prepared response which just serves a status code and empty response body.
 * </p>
 *
 * @since 10/4/12
 */
public class StatusCodeResponse implements Response {
  private final int statusCode;

  /**
   * Constructor defining the status code to return.
   *
   * @param statusCode status code
   */
  public StatusCodeResponse(final int statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public void write(final HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(statusCode, 0L);

    final OutputStream os = exchange.getResponseBody();
    os.close();
  }

  /**
   * Factory for status code response.
   *
   * @param statusCode status code to provide on request
   * @return response
   */
  public static Response statusCode(final int statusCode) {
    return new StatusCodeResponse(statusCode);
  }
}