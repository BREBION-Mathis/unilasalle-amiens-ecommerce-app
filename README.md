# FakeStore - Application Android E-Commerce 📱

Application mobile native Android de commerce en ligne développée en **Kotlin** avec **Jetpack Compose**.

Ce projet a été réalisé dans le cadre du module de développement mobile, en respectant les principes de l'**architecture MVVM** et du **Clean Code**. L'application consomme l'API publique [FakeStoreAPI](https://fakestoreapi.com/) pour afficher les produits, et implémente une persistance locale des données pour le panier et l'historique des commandes.

---

## ✨ Fonctionnalités Principales

### 🛍️ Catalogue & Navigation

- **Listing Produits** : Affichage des produits sous forme de grille responsive
- **Filtrage par Catégorie** : Navigation fluide entre les catégories via des "Chips" (Electronics, Jewelery, etc.)
- **Recherche Dynamique** : Barre de recherche instantanée (filtrage local pour la performance)
- **Tri Intelligent** : Possibilité de trier les produits par prix (croissant/décroissant)
- **Détail Produit** : Vue immersive avec image HD, description complète, rating et prix

### 🛒 Gestion du Panier (Persistant)

- **Ajout/Suppression** : Gestion complète des quantités (+/-) depuis le panier
- **Calcul Automatique** : Le total et le nombre d'articles se mettent à jour réactivement
- **Persistance (DataStore)** : Le panier est sauvegardé localement sur le téléphone. Si l'utilisateur quitte l'application, il retrouve son panier au retour

### 📦 Commande & Historique

- **Validation (Checkout)** : Simulation d'un processus de commande
- **Historique** : Les commandes validées sont archivées localement et consultables avec le détail, la date et le montant total

---

## 🏗️ Architecture Technique

Le projet suit rigoureusement le pattern **MVVM** (Model - View - ViewModel) pour assurer une séparation claire des responsabilités et faciliter la maintenabilité.

### Schéma de l'Architecture

```
                UI (Jetpack Compose)
                    ↕️ Observe State / Send Events
                ViewModel
                    ↕️ Call Business Logic
                Repository
                    ↕️
    ┌─────────────────────────────────┐
    │                                 │
HTTP Requests                   Read/Write JSON
    │                                 │
    ↓                                 ↓
Remote Data Source            Local Data Source
(Retrofit)                     (DataStore)
```

### Choix Techniques

#### 🎨 UI : Jetpack Compose

- Approche déclarative moderne
- Utilisation du **Unidirectional Data Flow (UDF)** : L'UI observe un état immuable et émet des événements vers le ViewModel

#### ⚙️ Logique : ViewModel & Coroutines

- Les ViewModel survivent aux changements de configuration (rotation d'écran)
- Utilisation des **Coroutines** et de `viewModelScope` pour gérer les appelles asynchrones sans bloquer le thread principal
- **StateFlow** est utilisé pour exposer des flux de données réactifs à l'UI

#### 💾 Données : Repository Pattern

- Le Repository agit comme une **source de vérité unique**
- Abstrait l'origine des données pour le reste de l'application

#### 🌐 Réseau & Stockage

- **Retrofit + Gson** : Pour les appels API REST typés
- **Coil** : Pour le chargement asynchrone et le cache des images
- **DataStore Preferences** : Pour la persistance locale légère sous forme de JSON sérialisé

---

## 📂 Structure du Projet

L'arborescence respecte la logique métier :

```
com.unilasalle.ecommerce/
├── data/                      # COUCHE DATA (Model & Repository)
│   ├── api/                   # Configuration Retrofit et Interface API
│   ├── model/                 # Data Classes (Product, CartItem, Order)
│   └── repository/            # Logique d'accès aux données (ProductRepo, CartRepo...)
│
├── ui/                        # COUCHE VUE (Jetpack Compose)
│   ├── components/            # Composants réutilisables (ProductCard, Chips...)
│   ├── screens/               # Écrans complets (List, Detail, Cart, History)
│   └── theme/                 # Design System (Couleurs, Typographie...)
│
├── viewmodel/                 # COUCHE LOGIQUE (Gestion d'état)
│   ├── ProductViewModel.kt    # Gestion du catalogue, filtres, recherche
│   ├── CartViewModel.kt       # Logique du panier et calculs
│   └── OrderViewModel.kt      # Gestion de la validation et de l'historique
│
└── MainActivity.kt            # Point d'entrée et Navigation (NavHost)
```

---

## 🚀 Installation et Lancement

### Prérequis

- Android Studio Ladybug (ou version récente)
- SDK Android 34+

### Étapes

1. **Cloner le projet**

   ```bash
   git clone https://github.com/votre-repo/ecommerce-app.git
   cd ecommerce-app
   ```

2. **Ouvrir le projet dans Android Studio**

3. **Attendre la synchronisation Gradle**

4. **Lancer sur un émulateur ou un appareil physique**

   - Émulateur recommandé : Pixel 7
   - Ou connecter un appareil Android avec USB Debugging activé

---

## 💡 Détails d'Implémentation Notables

### ⚡ Gestion de la Concurrence

L'utilisation de `items(list)` dans `LazyColumn` et `LazyVerticalGrid` assure des performances optimales même avec beaucoup de produits, en ne rendant que les éléments visibles à l'écran.

### 🔍 Search Bar Optimisée

La recherche filtre un cache local dans le ViewModel pour éviter de spammer l'API à chaque frappe clavier, garantissant une UX instantanée.

### 🧭 Navigation

Utilisation de **Navigation Compose** avec passage d'arguments pour découpler les écrans.
