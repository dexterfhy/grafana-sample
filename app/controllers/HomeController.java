package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Optional;

import static kafka.SingletonKafkaProvider.KAFKA_MANAGER;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    public Result publishKafkaMessage(Http.Request request) {
        var jsonBody = request.body().asJson();

        KAFKA_MANAGER.send(
            Optional.ofNullable(jsonBody.get("message"))
                .map(JsonNode::asText)
                .orElse("")
        );

        return ok();
    }

}
