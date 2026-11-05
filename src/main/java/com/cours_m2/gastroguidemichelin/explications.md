# Détails de mes choix

## Choix communs

### La structure des packages

J'ai opté pour une structure "feature based", c'est-à-dire que chaque package correspond à une fonctionnalité de l'application.
Cela permet de fluidifier la navigation dans le projet et de regrouper les classes qui sont liées entre elles.

### Les entités

L'usage d'un identifiant de type Long avait pour but d'éviter des conversions inutiles, 
ce type répondant parfaitement aux besoins de l'application.

### Les DTOs

J'ai décidé de conserver un seul type de réponse afin de toujours pouvoir récupérer l'ensemble des informations nécessaires à un affichage.
Que ce soit lors de la création ou suppréssion d'une entité. 

### Les Services

Mon choix de toujours récupérer une entité et retourner une entité également permet aux méthodes du service de ne pas dépendre des DTOs.
Cela permet de ne pas avoir à modifier les services si l'on décide de changer les DTOs.

## Restaurant



## Evaluation

- Pour la recherche d'évaluations par mots-clés, j'ai d'abord utilisé les @QuequestParam puis implémenté une recherche avec Lucene.
- L'ajout d'une évaluation par (POST /restaurants/{idRes}/evaluations), représente le fait qu'on devrait savoir le restaurant qui reçoit le commentaire.
Et le numéro de restaurant n'étant pas une donnée critique, elle est passée en paramètre de l'URL.

## Stockage S3

J'ai premièrement implémenté la génération d'URL pré-signée pour le stockage et la récupération d'images dans le bucket S3.
Par la suite, j'ai mis en place une sauvegarde directe des images lors de la création d'un restaurant.