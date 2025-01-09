package app;

import analyse.loader.LoaderExterne;
import diagramme.Model;
import diagramme.Vues.VuePrincipale;
import diagramme.controler.ExportationControler;
import diagramme.controler.ImportationControler;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
        // Récupération des dimensions de l'écran de l'utilisateur
        double height = Screen.getPrimary().getBounds().getHeight();
        double width = Screen.getPrimary().getBounds().getWidth();
        primaryStage.setTitle("Générateur de diagrammes de classes");

        VBox diagramArea = new VBox();
        diagramArea.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: lightgray;");

        // Modèle
        Model model = new Model();

        // Vue
        LoaderExterne.getInstance().loadClassFromFile("out/production/SAE_Diagramme2Classe/diagramme/Model.class"); // permet de rendre chargeable la classe Model
        VuePrincipale principal = new VuePrincipale();
        model.ajouterObservateur(principal);
        diagramArea.getChildren().add(principal);

        // Contrôleurs
        ImportationControler importationControler = new ImportationControler(model, primaryStage);
        ExportationControler exportationControler = new ExportationControler(model);

        // Boutons
        Button fichierButton = new Button("Fichier");
        fichierButton.setOnAction(importationControler);

        // MenuButton pour exporter
        MenuButton exportMenuButton = new MenuButton("Exporter");

        // Menu pour l'exportation
        MenuItem exportPuml = new MenuItem("Exporter en PUML");
        MenuItem exportPng = new MenuItem("Exporter en PNG");

        // Actions des éléments du menu
        exportPuml.setOnAction(e -> {
            exportationControler.setExportType("puml");
            exportationControler.handle(e);
        });

        exportPng.setOnAction(e -> {
            exportationControler.setExportType("png");
            exportationControler.handle(e);
        });

        // Ajouter les éléments au menu
        exportMenuButton.getItems().addAll(exportPuml, exportPng);

        // Layout des boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(fichierButton, exportMenuButton);

        // Mise en page du root
        BorderPane root = new BorderPane();
        root.setTop(null); // Pas de menu en haut, seulement les boutons
        root.setCenter(diagramArea);
        root.setBottom(buttonBox);

        // Configuration de la scène
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
