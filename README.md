# RestrictedAuthBrowser

Minimal Android browser for login, signup, and OAuth flows on devices where Chrome or Android System WebView may be unavailable. It embeds **GeckoView**, not Android `WebView`.

## Requirements and build

- Android 5.0 (API 21) or later
- JDK 17
- Android SDK 35

```bash
./gradlew test assembleDebug
```

Open the repository root directly in Android Studio. GeckoView is pinned to `145.0.20251124145406`; Mozilla's Maven repository is configured in `settings.gradle.kts`.

## Architecture

- `MainActivity`: lifecycle and the deliberately minimal, non-editable UI.
- `BrowserController`: owns one GeckoSession and connects Gecko delegates to the UI.
- `GeckoRuntimeProvider`: process-wide Gecko runtime.
- `NavigationPolicy`: central `ALLOW` / `BLOCK` / `OPEN_EXTERNAL` decision point.
- `ExternalIntentHandler`: safely hands supported custom schemes back to Android.
- `DomInspector`: non-mutating JavaScript proof of concept (`document.title`).
- `SafeLog`: strips paths, credentials, queries, and fragments from logged URLs.

## MVP limitations

There are no tabs, address editing, downloads UI, history UI, bookmarks, sharing, settings, extensions, or AI-based authentication classification. New-window requests are kept in the existing session. External custom schemes are opened only when Android reports a handler.

## Roadmap

Add Restricted Auth Mode at `NavigationPolicy`, track redirect/navigation metadata, collect carefully scoped page signals through `DomInspector`, classify login/signup/reset/MFA/OAuth/consent flows, and add configurable scheme and domain allowlists.
