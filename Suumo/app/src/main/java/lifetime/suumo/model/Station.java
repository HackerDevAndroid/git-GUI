package lifetime.suumo.model;

import java.io.Serializable;

/**
 * Created by MyPC on 28/09/2017.
 */

public class Station implements Serializable {
    private String name;
    private int id;

    public Station() {
    }

    public Station(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
