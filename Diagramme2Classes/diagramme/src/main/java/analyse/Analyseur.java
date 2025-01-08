package analyse;

import classes.*;
import classes.Package;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

import analyse.loader.LoaderExterne;

/**
 * Classe réalisant l'analyse d'une classe Java.
 * Cette classe permet d'examiner dynamiquement la structure d'une classe, y compris ses attributs,
 * ses méthodes, ses modificateurs et ses interfaces implémentées.
 */
public class Analyseur {

    // Instance unique de l'analyseur (Singleton)
    private static Analyseur INSTANCE;

    /**
     * Retourne l'instance unique de l'analyseur.
     * 
     * @return L'instance de l'analyseur.
     */
    public static Analyseur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Analyseur();
        }
        return INSTANCE;
    }

    /**
     * Analyse une classe à partir de son chemin sous forme de chaîne de caractères.
     * 
     * @param chemin Chemin vers la classe à analyser.
     * @return Un objet Classe contenant les résultats de l'analyse.
     * @throws ClassNotFoundException Si la classe n'est pas trouvée.
     * @throws IOException 
     */
    public Package analyserClasse(String chemin) throws ClassNotFoundException, IOException  {
        Class classe = LoaderExterne.getInstance().loadClassFromFile(chemin);
        String packageNom = classe.getPackage().getName();
        ArrayList<Package> p = new ArrayList<>();
        //package composé ou non
        if(packageNom.split("\\.").length == 1){
            p.add(new Package(""));
        }else {
            for (String s : packageNom.split("\\.")) {
                p.add(new Package(s));
            }
        }

        String type = classe.isInterface() ? "interface" : "class";

        Classe classeAnalysee = new Classe(type, classe.getSimpleName());

        // Analyse des modificateurs
        ArrayList<String> modifiers = getModifierClasse(classe);
        for (String modifier : modifiers) {
            classeAnalysee.addModificateur(modifier);
        }

        // Analyse des attributs
        for (Field field : classe.getDeclaredFields()) {
            Attribut attribut = analyserAttribut(field);
            classeAnalysee.addAttribut(attribut);
        }

        // Analyse des méthodes
        for (Method method : classe.getDeclaredMethods()) {
            Methode methode = analyserMethode(method);
            classeAnalysee.addMethode(methode);
        }

        // Analyse des interfaces implémentées
        for (Class<?> interfaceClass : classe.getInterfaces()) {
            Interface inter = new Interface("interface", interfaceClass.getSimpleName());
            String packageInterface = interfaceClass.getPackage().getName();
            ArrayList<Package> pInterface = new ArrayList<>();
            //package composé ou non
            if (packageInterface.split("\\.").length == 1) {
                pInterface.add(new Package(""));
            }
            else {
                for (String s : packageInterface.split("\\.")) {
                    pInterface.add(new Package(s));
                }
            }
            classeAnalysee.addInterface(inter);
        }
        if (type.equals("class")) {
            classeAnalysee.setClasseParent(new Classe("class", classe.getSuperclass().getSimpleName()));
        }
        p.getLast().ajouterClasse(classeAnalysee);
        for (int i = p.size() - 1; i > 0; i--) {
            p.get(i - 1).addPackage(p.get(i));
        }
        return p.getFirst();
    }

    /**
     * Analyse un attribut d'une classe.
     * 
     * @param field Attribut à analyser.
     * @return Un objet Attribut représentant l'attribut analysé.
     */
    private Attribut analyserAttribut(Field field) {
        Attribut attribut = new Attribut(field.getType().getSimpleName(), field.getName());
        attribut.addModificateur(Modifier.toString(field.getModifiers()));
        return attribut;
    }

    /**
     * Analyse une méthode d'une classe.
     * 
     * @param method Méthode à analyser.
     * @return Un objet Methode représentant la méthode analysée.
     */
    private Methode analyserMethode(Method method) {
        ArrayList<HashMap<String, String>> parametres = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("type", param.getType().getSimpleName());
            paramMap.put("nom", param.getName());
            parametres.add(paramMap);
        }

        ArrayList<String> modifiers = getModifierMethode(method);
        return new Methode(method.getName(), method.getReturnType().getSimpleName(), parametres, modifiers);
    }

    /**
     * Retourne les modificateurs d'une classe sous forme de chaîne de caractères.
     * 
     * @param c Classe à analyser.
     * @return Liste des modificateurs.
     */
    private static ArrayList<String> getModifierClasse(Class c) {
        int modifiers = c.getModifiers();
        ArrayList<String> result = new ArrayList<>();
        result.add(getModifierVisibilite(modifiers));
        if (Modifier.isAbstract(modifiers)) result.add("abstract");
        if (Modifier.isFinal(modifiers)) result.add("final");
        return result;
    }

    /**
     * Retourne la visibilité d'un modificateur.
     * 
     * @param modifiers Modificateurs sous forme d'entier.
     * @return La visibilité (public, private, protected ou package).
     */
    private static String getModifierVisibilite(int modifiers) {
        if (Modifier.isPublic(modifiers)) return "public";
        if (Modifier.isPrivate(modifiers)) return "private";
        if (Modifier.isProtected(modifiers)) return "protected";
        return "package";
    }

    /**
     * Récupère les modificateurs d'une méthode sous forme de liste de chaînes de caractères.
     *
     * @param m La méthode à analyser.
     * @return Une liste des modificateurs sous forme de chaînes (par exemple : "public", "static", "final").
     */
    private static ArrayList<String> getModifierMethode(Method m) {
        int modifiers = m.getModifiers();
        ArrayList<String> result = new ArrayList<>();
        if (Modifier.isPublic(modifiers)) result.add("public");
        else if (Modifier.isPrivate(modifiers)) result.add("private");
        else if (Modifier.isProtected(modifiers)) result.add("protected");
        if (Modifier.isStatic(modifiers)) result.add("static");
        if (Modifier.isAbstract(modifiers)) result.add("abstract");
        if (Modifier.isFinal(modifiers)) result.add("final");
        if (Modifier.isSynchronized(modifiers)) result.add("synchronized");
        return result;
    }


    /**
     * Affiche les résultats de l'analyse dans la console.
     */
    public void afficherResultats(Interface analyseClasse) {
        System.out.println("Nom de la classe : " + analyseClasse.getNom());
        if(analyseClasse instanceof Classe){
        System.out.println("Classe parente : " + ((Classe)analyseClasse).getClasseParent().getNom());}
        System.out.println("Attributs :");
        for (Attribut field : analyseClasse.getAttributs()) {
            System.out.println(" nb modificateur - " + Modifier.toString(field.getModificateurs().size()) + " " + field.getNom());
        }
        System.out.println("Méthodes :");
        for (Methode method : analyseClasse.getMethodes()) {
            System.out.println(" - " +method.getModificateurs().size() + " " + method.getNom());
        }
    }

    /**
     * Méthode permettant d'exporter au format Puml
     * @param classeAnalysee : l'objet Classe analysé
     * @return code Puml
     */
    public String exportPuml(Classe classeAnalysee) {
        StringBuilder puml = new StringBuilder();
        puml.append("@startuml\n");
        puml.append("class ").append(classeAnalysee.getNom()).append(" {\n");

        for (Attribut attribut : classeAnalysee.getAttributs()) {
            ArrayList<String> modificateurs = (ArrayList<String>) attribut.getModificateurs();
            String visibility = getPumlModificateur(modificateurs);
            puml.append("    ")
                    .append(visibility)
                    .append(" ")
                    .append(attribut.getNom())
                    .append(" : ")
                    .append(attribut.getType())
                    .append("\n");
        }

        for (Methode methode : classeAnalysee.getMethodes()) {
            String visibility = getPumlModificateur(methode.getModificateurs());
            puml.append("    ")
                    .append(visibility)
                    .append(" ")
                    .append(methode.getNom())
                    .append("(");

            List<HashMap<String, String>> parametres = methode.getParametres();
            for (int i = 0; i < parametres.size(); i++) {
                HashMap<String, String> param = parametres.get(i);
                puml.append(param.get("nom"))
                        .append(" : ")
                        .append(param.get("type"));
                if (i < parametres.size() - 1) {
                    puml.append(", ");
                }
            }

            puml.append(") : ").append(methode.getTypeRetour()).append("\n");
        }
        puml.append("}\n");

        if (classeAnalysee.getClasseParent() != null) {
            puml.append(classeAnalysee.getClasseParent().getNom())
                    .append(" <|-- ")
                    .append(classeAnalysee.getNom())
                    .append("\n");
        }

        for (Interface inter : classeAnalysee.getInterfaces()) {
            puml.append("interface ").append(inter.getNom()).append(" {\n");

            for (Methode methode : inter.getMethodes()) {
                puml.append("    + ").append(methode.getNom()).append("()\n");
            }
            puml.append("}\n");

            puml.append(inter.getNom()).append(" <|.. ").append(classeAnalysee.getNom()).append("\n");
        }
        puml.append("@enduml\n");
        return puml.toString();
    }


    /**
     * Méthode permettant de traduire les modificateurs au format Puml
     * @param modificateurs : liste des modificateurs
     * @return modificateurs au format Puml
     */
    private String getPumlModificateur(List<String> modificateurs) {
        if (modificateurs.contains("private")) return "-";
        if (modificateurs.contains("protected")) return "#";
        return "+";
    }
}
