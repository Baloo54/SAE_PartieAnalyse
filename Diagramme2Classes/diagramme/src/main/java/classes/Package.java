package classes;


import java.util.ArrayList;

public class Package {
    private String nom;
    private ArrayList<Interface> interfaces;

    public Package(String nom) {
        this.nom = nom;
        this.interfaces = new ArrayList<Interface>();
    }

    public String getNom() {
        return nom;
    }

    public void ajouterClasse(Interface classe) {

    }


}
