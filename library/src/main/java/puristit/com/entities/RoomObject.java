package puristit.com.entities;

/**
 * Created by Anas on 2/25/2018.
 */

public class RoomObject {

    private long id;
    private String name;
    private String status;
    private String agent;


    public long getId() {
        return id;
    }

    public RoomObject setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public RoomObject setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public RoomObject setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getAgent() {
        return agent;
    }

    public RoomObject setAgent(String agent) {
        this.agent = agent;
        return this;
    }
}
