package diagramme.controler;

import classes.Interface;
import diagramme.Model;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class ControlleurVisibilite implements EventHandler<ActionEvent> {
    private Model model;
    private Interface classe;
    public ControlleurVisibilite(Model model, Interface classe) {
        this.model = model;
        this.classe = classe;
    }
    @Override
    public void handle(ActionEvent actionEvent) {
        model.changerVisibilite(classe);
    }
}
