# MacroMagic — acessibilidade legítima

Os componentes Kotlin ficam em `app/src/main/java/com/macromagic/accessibility/`.

- Projeto Android com Gradle 8.9, Android Gradle Plugin 8.7.3, Kotlin 2.0.21,
  Compose e compile/target SDK 35.
- `MacroExecutionEngine` cria um único `GestureDescription.StrokeDescription`
  contínuo para um arrasto vertical, interpolando o caminho em `steps`.
- `CoordinateMapper` converte DP/coordenadas normalizadas em pixels e impede
  endpoints fora da tela.
- `ProfileRepository` salva somente perfis locais em `SharedPreferences`.

O motor precisa ser chamado por um `AccessibilityService` habilitado pelo
usuário e só está disponível a partir do Android 7.0 (`API 24`), que é quando
`dispatchGesture` foi introduzido.

Para compilar localmente, instale o Android SDK Platform 35 e Build Tools
35.0.0, depois execute `./gradlew assembleDebug` a partir deste diretório.