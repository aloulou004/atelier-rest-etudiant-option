# Atelier REST - gestion des options et des étudiants

Projet Maven WAR réalisant l'atelier SOA 2026-2027 avec JAX-RS (Jersey 2), JSON et XML. Les données sont conservées en mémoire pendant l'exécution de Tomcat et reviennent aux valeurs d'exemple après un redémarrage.

## Lancer le projet

Prérequis : JDK 17, Maven et Tomcat 9.

1. Exécuter `mvn clean package` dans ce dossier.
2. Copier `target/Gestion_Options_Etudiants-1.0-SNAPSHOT.war` dans le dossier `webapps` de Tomcat sous le nom `Gestion_Options_Etudiants.war`.
3. Démarrer Tomcat, puis ouvrir `http://localhost:8080/Gestion_Options_Etudiants/rest/options`.

La base des URL est `http://localhost:8080/Gestion_Options_Etudiants/rest`.

## Ressources exposées

| Méthode | Chemin | Résultat |
| --- | --- | --- |
| POST | `/options` | Crée une option depuis un corps JSON |
| GET | `/options` | Liste toutes les options en JSON |
| GET | `/options?domaine=Informatique` | Filtre les options par domaine |
| GET | `/options/{code}` | Lit une option en JSON |
| PUT | `/options/{code}` | Modifie une option depuis un corps JSON |
| DELETE | `/options/{code}` | Supprime une option |
| POST | `/etudiants` | Crée un étudiant depuis un corps JSON |
| GET | `/etudiants` | Liste tous les étudiants en JSON |
| GET | `/etudiants/{identifiant}` | Lit un étudiant en JSON |
| PUT | `/etudiants/{identifiant}` | Modifie un étudiant depuis un corps JSON |
| DELETE | `/etudiants/{identifiant}` | Supprime un étudiant |
| GET | `/etudiants/option?codeOption=1` | Liste les étudiants de l'option en XML |

Les opérations réussies renvoient `200` (ou `204` pour DELETE). Une ressource ou une option associée absente renvoie `404`. Les doublons renvoient `409`, et les corps ou paramètres incomplets renvoient `400`.

Pour créer un étudiant, le corps JSON doit contenir au moins un identifiant et une option existante, par exemple :

```json
{
  "identifiant": "I010",
  "nom": "Ben Ali",
  "prenom": "Sami",
  "option": { "codeOption": 1 },
  "anneeEtude": 2026,
  "email": "sami@example.com"
}
```

## Vérification et captures

Les opérations et les codes HTTP ont été vérifiés sur Tomcat. Les captures suivantes proviennent des réponses réelles du service :

- [Liste des options](screenshots/options.png)
- [Liste des étudiants](screenshots/etudiants.png)
- [Étudiants de l'option 1 en XML](screenshots/etudiants-option-xml.png)
