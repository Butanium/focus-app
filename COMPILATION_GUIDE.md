# Guide de Compilation - Focus App

## Prérequis

1. **Android Studio** (version 2023.1 ou plus récente)
2. **Android SDK** (API 24-34)
3. **Java 8+** ou **Kotlin 1.8+**

## Étapes de compilation

### 1. Configuration initiale
```bash
# Cloner ou ouvrir le projet
cd focus-app

# S'assurer que gradlew est exécutable
chmod +x gradlew
```

### 2. Compilation via Android Studio
1. Ouvrir Android Studio
2. **File** → **Open** → Sélectionner le dossier `focus-app`
3. Attendre le sync Gradle automatique
4. **Build** → **Make Project** (Ctrl+F9)

### 3. Compilation en ligne de commande
```bash
# Debug build
./gradlew assembleDebug

# Release build (pour production)
./gradlew assembleRelease

# Installer directement sur un appareil connecté
./gradlew installDebug
```

## Fichiers générés

- **APK Debug** : `app/build/outputs/apk/debug/app-debug.apk`
- **APK Release** : `app/build/outputs/apk/release/app-release.apk`

## Résolution de problèmes

### Erreur "SDK not found"
```bash
# Dans Android Studio
File → Project Structure → SDK Location
# Définir le chemin vers Android SDK
```

### Erreur de compilation Kotlin
```bash
# Nettoyer et recompiler
./gradlew clean
./gradlew assembleDebug
```

### Erreur de permissions
```bash
# Sur Linux/Mac
chmod +x gradlew
```

## Test sur émulateur

1. **Android Studio** → **AVD Manager**
2. Créer un AVD avec **API 24+**
3. Lancer l'émulateur
4. `./gradlew installDebug`

## Installation sur appareil physique

1. Activer **Options développeur** sur l'appareil
2. Activer **Débogage USB**
3. Connecter via USB
4. `./gradlew installDebug`

## Vérification post-installation

1. Accorder les permissions :
   - Notifications
   - Accès téléphone (pour les appels)
   - Accessibilité (pour whitelist - optionnel)

2. Tester les fonctionnalités :
   - Déverrouillage d'écran → Timer démarre
   - Attendre 5 min → Notification apparaît
   - Tester les boutons de notification
   - Tester pause/reprendre depuis l'app

Le code est syntaxiquement correct et prêt à être compilé !