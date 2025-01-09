package app;

import diagramme.Model;
import diagramme.Vues.VuePrincipale;
import diagramme.controler.ControlleurAfficherClasses;
import diagramme.controler.ExportationControler;
import diagramme.controler.ImportationControler;
import javafx.application.Application;
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
        MenuBar menuBar = new MenuBar();

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
        visibiliteMenuItem.setOnAction(new ControlleurAfficherClasses(model));
        visibiliteMenu.getItems().add(visibiliteMenuItem);

        menuBar.getMenus().addAll(fichierMenu, exportMenu, visibiliteMenu);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(diagramArea);

        // Configuration de la scène
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
