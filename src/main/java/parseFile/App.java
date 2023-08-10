package parseFile;
import java.util.List;

public class App {
    private String id;
    private Trigger trigger;
    private List<Action> actions;
    public App(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "App{" +
                "id='" + id + '\'' +
                ", trigger=" + trigger +
                ", actions=" + actions +
                '}';
    }
}
