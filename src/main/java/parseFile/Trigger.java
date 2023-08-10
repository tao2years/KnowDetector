package parseFile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Trigger {
    private List<RuntimeCondition> conditions;
    private List<RuntimeEvents> events;
    public Trigger(){}

    public List<RuntimeCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<RuntimeCondition> conditions) {
        this.conditions = conditions;
    }

    public List<RuntimeEvents> getEvents() {
        return events;
    }

    public void setEvents(List<RuntimeEvents> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Trigger{" +
                "conditions=" + conditions +
                ", events=" + events +
                '}';
    }
}
