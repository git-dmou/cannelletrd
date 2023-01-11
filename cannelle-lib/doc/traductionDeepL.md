# Traduction de modules avec une API externe

## traduction avec DeepL

DeepL permet de traduire des contenus en ligne ou via une API qui peut être intégrée à une application.
L'accès à l'API DeepL se fait via un compte gratuit ou payant. A la création d'un compte, une authentication key est obtenue, 
qui permettra l'accès au service.
La mesure de la consommation de crédit est basée sur la quantité de caractères des textes traduits.

Les textes à traduire peuvent être envoyés au format texte pour une traduction unitaire, ou au format XML.

[doc API Deepl](https://www.deepl.com/fr/docs-api/xml/markup/)

## fonctionnalité Traduction de modules

L'objectif de la fonctionnalité est de créer un nouveau module, entièrement traduit dans une langue cible :
- création d'une nouveau contenu dans la langue cible :
  - même titre non traduit que le module de base
  - identifiant = identifiant du module de base + discriminateur
    - ex : module01 ---> module01_EN pour une traduction en anglais
- traduction du fichier XL source
  - pour permettre de regénérer le module traduit à partir de ses sources


## reste à faire pour une mise en place complète (au 10/01/2023)

- [] traduction du fichier source XL
- [] paramétrage DeepL
      - actuellement clé d'accès en dur --> OK pour démo
- [] mise à jour des fichiers properties
      - [OK] cannelle_v6_fr.properties
      - [] cannelle_v6_en.properties
      - [] cannelle_v6_es.properties
      - [] cannelle_v6_nl.properties
- [] voir bugs mise en forme du module traduit
- [] traduction titre et description du module ?
  - ajouter une nouvelle propriété "titre sommaire" à traduire ? 
    - pour dissocier le titre du module et le titre du sommaire du module 
  - ajouter simplement l'appel à DeepL dans la méthode prévue
- REFACTO :
    - [] refacto pour traduction par module au lieu de traduction par écran pour optimisation

  
### diagramme traitements de la traduction de modules

````mermaid
classDiagram

%% relations :
%%

TransformAPI --> CannelleTreatment
CannelleTreatment ..> ImportModuleService
ImportModuleService ..> XlsModuleParserService : prop 1ere page XL + TRD méta
XlsModuleParserService ..> Parameters
ImportModuleService ..> ModuleGeneratorService : creation du module
ImportModuleService ..> XlsScreenParserService : via ScreenParserServiceFactory creation et TRD des écrans
XlsScreenParserService ..> Parameters
ImportModuleService ..> ScreenGeneratorService : ???
ImportModuleService ..> ContentService : attributions des écrans au module
XlsScreenParserService ..> ScreenFactory : pour chaque écran
ScreenFactoryWithTranslation --|> ScreenFactory
ScreenFactory ..> CannelleScreenParameters 
ScreenFactory ..> CannelleScreenParamTranslator : appel pour chaque Ecran a optimiser pour 1 seul appel par module
CannelleScreenParamTranslator ..> CannelleScreenParameters
CannelleScreenParamTranslator ..> DeeplTranslator
DeeplTranslator --|> ITranslatorAPI


classesFillesPourChaqueParam --|> AbstractScreenParameter : 1 classe par type de param
AbstractScreenParameter --|> IScreenParameter
IScreenParameter --|> ITranslatable
CannelleScreenParameters o-- classesFillesPourChaqueParam : contient

%% champs et methodes :
%%

TransformAPI : +translate()
TransformAPI : -translateTRT()
TransformAPI : url /api/v1/transform/translate
XlsModuleParserService : Parameters parameters
XlsModuleParserService : ResourcesHandler resourcesHandler
XlsModuleParserService : +getModuleProperties()
XlsScreenParserService : Parameters parameters
XlsScreenParserService : ResourcesHandler resourcesHandler
XlsScreenParserService : +getScreens()
ScreenFactory : +parseScreen()
ScreenFactory : #translateScreen() vide
ScreenFactoryWithTranslation : #translateScreen() override super
CannelleScreenParamTranslator : +from() CannelleScreenParamTranslator
CannelleScreenParamTranslator : +to() CannelleScreenParamTranslator
CannelleScreenParamTranslator : +translate()
CannelleTreatment : +translateFromSource() ContentVersion
CannelleTreatment : -process()
ImportModuleService : +importModule() ContentVersion
ModuleGeneratorService : +createModule() ContentVersion
ScreenGeneratorService : +storeScreens()
ContentService : +setChildren()
ITranslatable : +getTranslatableValue()
ITranslatable : +getTranslatableResponse()
Parameters : param ecrans à partir de 
Parameters : cannelle_v6_fr.properties
Parameters : cannelle_v6_en.properties
Parameters : cannelle_v6_es.properties
Parameters : cannelle_v6_nl.properties
````


## Traduction du fichier source XL

Reste à Faire

## tests 

la totalité de la chaine de traitement transformAPI / translate n'a pas été montée en test.
La complexité des traitements implique de trouver une solution de gestion des dépendences qui permette d'isoler les tests.
Principales sources de dépendences :
- ThaleiaApplication
- ThaleiaSession
- modèle
- Dao

En revanche, la traduction des paramètres des modules est testées (classes filles de AbstractScreenParameter).
Les classes XlsScreenParserService, XlsModuleParserService, peuvent être exécutées dans des environnements de tests,
les problèmes de dépendences sont résolues par les classes dans *helperclassesfortests*.
Cela permet d'accélérer les développements des traitements qui dépendent de ces classes, puisque on peux les exécuter et débugger sans recompiler Thaleia. 

### tests API DeepL

cf dans cannelle-lib/src/test
- DeepLApiTest.java
- DeepLApiXmlFileNoAssertTest.java


### tests de la gestion de la traduction des différents paramètres XL :
- CannelleScreenParamTranslatorTest.java
  - impact des tag html
  - paramètres à traduire
  - paramètres à ne pas traduire
  - traduction de plusieurs paramètres


### non régression (partielle)

- ModuleScreensCreationNONREGRESSIONTest

Permet de tester l'impact de la traduction sur la génération des écran (avant assemblage dans un module)
nécessite quelques adaptations pour rendre indépendant de la machine du testeur

### tests d'analyse

XlsModuleParserServiceTest : pour comprendre XlsModuleParserService
testEcranIndividuelsBasiqueClasseVirtuelle : traduction d'un module XL injecter en entrée
UcTranslateModuleTest.java : traitement d'un fichier XL
DeepLApiXmlFileNoAssertTest.java : utilisation de l'API java fourni par DeepL --> non utilisé dans Thaleia

### ressources de test

- helperclassesfortests
  - contients des classes modifiées afin de s'abstraire de Thaleia et faire tourner la traduction des modules dans des classes de Test JUnit

- A FAIRE :
  - utiliser Mockito pour prendre en charge les methodes qui dépendent de 
    - ThaleiaApplication
    - ThaliaSession
  - résoudre les problèmes de dépendences au modèle (ex: model/user)
    - utilisation de snapshot ?
    - cf FakeLocale et FakeUser dans ModuleScreensCreationNONREGRESSIONTest
  - résoudre les problèmes de dépendences aux DAO