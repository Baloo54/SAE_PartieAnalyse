package app;

import diagramme.Model;
import diagramme.Vues.VuePrincipale;
import diagramme.controler.ControlleurAfficherClasses;
import diagramme.controler.ExportationControler;
import diagramme.controler.ImportationControler;
import diagramme.controler.ModificationControler;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Classe principale qui exécute l'application.
 */
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        double height = Screen.getPrimary().getBounds().getHeight();
        double width = Screen.getPrimary().getBounds().getWidth();
        primaryStage.setTitle("Générateur de diagrammes de classes");

        VBox diagramArea = new VBox();
        diagramArea.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: lightgray;");

        // Modèle
        Model model = new Model();

        // Vue
        VuePrincipale principal = new VuePrincipale();
        model.ajouterObservateur(principal);
        diagramArea.getChildren().add(principal);

        // Contrôleurs
        ImportationControler importationControler = new ImportationControler(model, primaryStage);
        ExportationControler exportationControler = new ExportationControler(model);

        // Menu principal



        //Menu "Modifier"
        //Bar
        MenuBar menuBar = new MenuBar();
        //Menu
        Menu EditMenu = new Menu("ModifierDiagramme");
        //Item
        MenuItem AjoutClasse = new MenuItem("Ajouter une classe");
        MenuItem AjoutMethode = new MenuItem("Ajouter une méthode");
        MenuItem AjoutAttribut = new MenuItem("Ajouter un attribut");
        //Ajout des items au menu
        EditMenu.getItems().addAll(AjoutClasse, AjoutMethode, AjoutAttribut);

        //Ajout Action
        AjoutClasse.setOnAction(new ModificationControler(model, "ajoutClasse"));
        AjoutMethode.setOnAction(new ModificationControler(model, "ajoutMethode"));
        AjoutAttribut.setOnAction(new ModificationControler(model, "ajoutAttribut"));



        // Menu "Fichier"
        Menu fichierMenu = new Menu("Fichier");
        MenuItem fichierMenuItem = new MenuItem("Charger un fichier");
        fichierMenuItem.setOnAction(importationControler);
        fichierMenu.getItems().add(fichierMenuItem);

        // Menu "Exporter"
        Menu exportMenu = new Menu("Exporter");
        MenuItem exportPuml = new MenuItem("Exporter en PUML");
        MenuItem exportPng = new MenuItem("Exporter en PNG");

        exportPuml.setOnAction(e -> {
            exportationControler.setExportType("puml");
            exportationControler.handle(e);
        });

        exportPng.setOnAction(e -> {
            exportationControler.setExportType("png");
            exportationControler.handle(e);
        });

        exportMenu.getItems().addAll(exportPuml, exportPng);

        // Menu "Visibilité"
        Menu visibiliteMenu = new Menu("Visibilité");
        MenuItem visibiliteMenuItem = new MenuItem("Afficher les classes");

        VBox sidebar = new VBox();
        sidebar.setAlignment(Pos.TOP_CENTER);
        VBox methodbar = new VBox();
        methodbar.setAlignment(Pos.TOP_CENTER);
        ControlleurAfficherClasses controlleurAfficherClasses = new ControlleurAfficherClasses(model, sidebar,methodbar);

     visibiliteMenuItem.setOnAction(controlleurAfficherClasses);
        visibiliteMenu.getItems().add(visibiliteMenuItem);

        menuBar.getMenus().addAll(fichierMenu, exportMenu, visibiliteMenu,EditMenu);

        model.ajouterObservateur(controlleurAfficherClasses);
        BorderPane root = new BorderPane();
        root.setBottom(menuBar);
        root.setCenter(diagramArea);
        root.setLeft(sidebar);
        root.setRight(methodbar);

        // Configuration de la scène
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
