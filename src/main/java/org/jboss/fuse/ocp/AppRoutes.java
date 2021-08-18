package org.jboss.fuse.ocp;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.jackson.ListJacksonDataFormat;

import java.util.List;

public class AppRoutes extends EndpointRouteBuilder {

    private final String createTable = new StringBuilder()
        .append("CREATE TABLE task_list (\n")
        .append("   id SERIAL,\n")
        .append("   task varchar(255)\n")
        .append(");")
        .toString();

    @Override
    public void configure() {
        from(platformHttp("/get/{id}").httpMethodRestrict("GET"))
            .setBody(e -> String.format("SELECT * FROM task_list WHERE id=%s;", e.getIn().getHeader("id")))
            .to("jdbc://dataSource")
            .marshal().json()
            .unmarshal(new ListJacksonDataFormat(Task.class))
            .process(exchange -> {
                List<Task> tasks = exchange.getIn().getBody(List.class);
                if (tasks.size() == 1) {
                    exchange.getIn().setBody(tasks.get(0).getTask());
                } else {
                    exchange.getIn().setBody(null);
                }
            })
            .log("Got task #${header.id}: ${body}");

        from(platformHttp("/init").httpMethodRestrict("GET"))
            .setBody().constant(createTable)
            .to("jdbc://dataSource")
            .setBody().constant("Tasks table created")
            .log("Initialized DB");

        from(platformHttp("/add").httpMethodRestrict("POST"))
            .setBody(e -> String.format("INSERT INTO task_list (task) VALUES ('%s');", e.getIn().getBody()))
            .to("jdbc://dataSource")
            .log("Added new task '${body}'")
            .setBody().constant("Success");

        from(platformHttp("/drop").httpMethodRestrict("GET"))
            .setBody().constant("DROP TABLE task_list;")
            .to(jdbc("dataSource"))
            .log("Dropped task_list table")
            .setBody().constant("Tasks table dropped");
    }
}
