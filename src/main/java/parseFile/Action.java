package parseFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import util.RawJsonDeserializer;

import java.util.ArrayList;
import java.util.List;

public class Action {
    private List<RuntimeActions> actions;
    private Delay delay;

    public Action(){}

    public List<RuntimeActions> getActions() {
        return actions;
    }

    public void setActions(List<RuntimeActions> actions) {
        this.actions = actions;
    }

    public Delay getDelay() {
        return delay;
    }

    public void setDelay(Delay delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "Action{" +
                "actions=" + actions +
                ", delay=" + delay +
                '}';
    }
}
