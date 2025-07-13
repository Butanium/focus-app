# Focus App

Une application Android de productivité qui vous aide à limiter le temps d'utilisation de votre téléphone.

## Fonctionnalités

### Core
- **Timer automatique** : Se lance automatiquement quand vous déverrouillez votre téléphone
- **Notifications intelligentes** : Après 5 minutes (configurable), vous recevez une notification avec 3 options :
  - Rappeler dans 5 minutes (configurable)
  - Rappeler dans 15 minutes (configurable)  
  - Désactiver pour cette session
- **Bouton pause/reprendre** : Contrôle manuel depuis l'application
- **Service en arrière-plan** : Fonctionne en permanence sans interruption

### Réglages
- **Délai initial** : Modifiez la durée avant la première notification (1-30 min)
- **Rappel court** : Durée du rappel court (1-30 min)
- **Rappel long** : Durée du rappel long (1-60 min)
- **Vibration** : Activez/désactivez la vibration des notifications

### Fonctionnalités avancées
- **Pause automatique durant les appels** : Le timer se pause pendant vos appels téléphoniques
- **Whitelist d'applications** : Configurez des apps qui pausent automatiquement le timer
- **Service d'accessibilité** : Détecte quelle application est ouverte pour la whitelist

## Installation

1. Clonez le repository
2. Ouvrez le projet dans Android Studio
3. Compilez et installez sur votre appareil Android

## Permissions requises

- `FOREGROUND_SERVICE` : Pour le service en arrière-plan
- `POST_NOTIFICATIONS` : Pour les notifications de rappel
- `VIBRATE` : Pour la vibration (optionnelle)
- `READ_PHONE_STATE` : Pour détecter les appels
- `BIND_ACCESSIBILITY_SERVICE` : Pour la whitelist d'applications (optionnelle)

## Architecture

### Fichiers principaux
- `MainActivity.kt` : Interface principale avec réglages
- `FocusTimerService.kt` : Service en arrière-plan pour le timer
- `ScreenReceiver.kt` : Détecte le déverrouillage d'écran et les appels
- `NotificationActionReceiver.kt` : Gère les actions des notifications
- `WhitelistActivity.kt` : Interface de gestion de la whitelist
- `AppWhitelistService.kt` : Service d'accessibilité pour la whitelist

### Stockage
- `SharedPreferences` : Sauvegarde des réglages et état du timer

## Utilisation

1. **Première utilisation** : L'app démarre automatiquement le service au lancement
2. **Usage normal** : Le timer se lance à chaque déverrouillage d'écran
3. **Notifications** : Choisissez votre action quand la notification apparaît
4. **Pause manuelle** : Utilisez le bouton dans l'app pour pause/reprendre
5. **Whitelist** : Configurez les apps qui pausent le timer automatiquement

## Code minimal

Le code est volontairement simple et minimal pour faciliter les modifications :
- Architecture Android standard
- Pas de frameworks complexes
- Code Kotlin lisible
- ~8 fichiers principaux seulement