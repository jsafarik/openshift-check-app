package org.jboss.fuse.ocp;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.ListJacksonDataFormat;

import javax.enterprise.event.Observes;

import java.util.List;

import io.quarkus.runtime.ShutdownEvent;

public class TaskRoutes extends RouteBuilder {

    private final static String TABLE_NAME = "task_list";

    private final static String DIRECT_CREATE = "direct:create";
    private final static String DIRECT_READ_ALL = "direct:readAll";
    private final static String DIRECT_READ = "direct:read";
    private final static String DIRECT_UPDATE = "direct:update";
    private final static String DIRECT_DELETE = "direct:delete";

    void onStop(@Observes ShutdownEvent event) {
        dropTable();
    }

    @Override
    public void configure() throws Exception {
        createTable();

        defineEndpoints();
        create();
        read();
        readAll();
        update();
        delete();
    }

    private void createTable() {
        String command = String.format("CREATE TABLE %s (id SERIAL,task varchar(255));", TABLE_NAME);
        from("timer:init?period=10000&repeatCount=1")
            .setBody(constant(command))
            .to("jdbc://dataSource")
            .log("Created table " + TABLE_NAME);
    }

    private void dropTable() {
        String command = String.format("DROP TABLE %s;", TABLE_NAME);
        from("timer:destroy?period=10000&repeatCount=1")
            .setBody(constant(command))
            .to("jdbc://dataSource")
            .log(TABLE_NAME + " table dropped");
    }

    private void defineEndpoints() {
        rest()
            .post("/add").consumes("text/plain").to(DIRECT_CREATE)
            .get("/get").to(DIRECT_READ_ALL)
            .get("/get/{id}").to(DIRECT_READ)
            .put("/update/{id}").consumes("text/plain").to(DIRECT_UPDATE)
            .delete("/delete/{id}").to(DIRECT_DELETE);
    }

    private void create() {
        from(DIRECT_CREATE)
            .setBody(e -> String.format("INSERT INTO %s (task) VALUES ('%s');", TABLE_NAME, e.getIn().getBody()))
            .to("jdbc://dataSource")
            .setBody(constant("OK"))
            .log("Task created");
    }

    private void readAll() {
        from(DIRECT_READ_ALL)
            .setBody(e -> String.format("SELECT * FROM %s;", TABLE_NAME))
            .to("jdbc://dataSource")
            .marshal().json()
            .setBody(e -> e.getIn().getBody(String.class))
            .log("Read all tasks");
    }

    private void read() {
        from(DIRECT_READ)
            .setBody(e -> String.format("SELECT * FROM %s where id=%s;", TABLE_NAME, e.getIn().getHeader("id")))
            .to("jdbc://dataSource")
            .marshal().json()
            .unmarshal(new ListJacksonDataFormat(Task.class))
            .setBody(e -> e.getIn().getBody(List.class).stream().findFirst().orElse(null))
            .marshal().json()
            .log("Read single task");
    }

    private void update() {
        from(DIRECT_UPDATE)
            .setBody(e -> String.format("UPDATE %s SET task = '%s' where id=%s;", TABLE_NAME, e.getIn().getBody(), e.getIn().getHeader("id")))
            .to("jdbc://dataSource")
            .setBody(constant("OK"))
            .log("Task updated");
    }

    private void delete() {
        from(DIRECT_DELETE)
            .setBody(e -> String.format("DELETE FROM %s where id=%s;", TABLE_NAME, e.getIn().getHeader("id")))
            .to("jdbc://dataSource")
            .setBody(constant("OK"))
            .log("Task deleted");
    }
}
