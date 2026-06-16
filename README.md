# OTNet Android (starter)

A Netflix-style Android streaming app built with Jetpack Compose and AndroidX
Media3, fully powered by the [OTNet](https://otnet.io) catalog, playback and
DRM APIs. Drop in your own publisher API key and the entire app — homepage,
browse, content detail, full-screen Widevine player — runs against your
catalogue.

Style is closer to Netflix / Prime Video: deep-navy backdrop, large
auto-advancing hero pager, horizontally-scrolling rows grouped by genre,
cinematic content detail, immersive full-screen player.

<!-- Drop an emulator screenshot at docs/home.png and uncomment:
![Home screen](docs/home.png)
-->

## Features

- **Home** — auto-advancing hero pager, `ContentRow` per `/catalog/homepage`
  row (portrait or landscape tiles)
- **Browse** — category list from `/catalog/categories/tree`, tap into an
  adaptive poster grid backed by `/catalog/content/category/:id`
- **Content detail** — backdrop with vertical gradient fade, `titleImage`
  fallback to large title text, meta pills, Play CTA, synopsis, episode list
  for series (sorted by `sortOrder`)
- **Full-screen player** — Media3 / ExoPlayer in an `AndroidView`, DASH
  preferred (HLS fallback), session-mode Widevine DRM via OTNet's
  `/playback/drm/session` + `/playback/drm/license` flow
- **Polished states** — every screen renders one of loading / data / error
  through a paired `ViewModel<StateFlow<UiState>>` + `StatePlaceholder` —
  never a blank composable

## Tech stack

- Kotlin 1.9+ (K2 with Compose Compiler plugin)
- Jetpack Compose Material 3
- AndroidX Media3 (ExoPlayer) 1.3.x — DASH + HLS + Widevine
- Retrofit 2 + OkHttp 4 + kotlinx.serialization
- Coil 2 for image loading
- AndroidX Lifecycle / Navigation Compose

## Quick start

```bash
git clone git@github.com:OTNetMedia/otnet-ott-streaming-android-mobile.git
cd otnet-ott-streaming-android-mobile
cp local.properties.example local.properties
# edit local.properties and paste your OTNet publisher key:
#   OTNET_API_KEY=otn_xxx…
```

Open the project in Android Studio Iguana+, let it run a Gradle sync, then
build & run on a Widevine-capable device or emulator (API 26+).

> The app will refuse to start if `OTNET_API_KEY` is missing — `OTNetApplication`
> throws an `IllegalStateException` with the missing-key instructions.

## Configuration

| Setting | Where | Description |
|---|---|---|
| `OTNET_API_KEY` | `local.properties` (gitignored) | Your publisher API key from [otnet.io](https://otnet.io). Sent as `X-Api-Key` on every catalog/playback request. |

`app/build.gradle.kts` reads `OTNET_API_KEY` from (in order):

1. `local.properties` — recommended for dev
2. `OTNET_API_KEY` env var — recommended for CI
3. Gradle property (`-POTNET_API_KEY=…`) — escape hatch

It is exposed to Kotlin as `BuildConfig.OTNET_API_KEY`.

## Project layout

```
app/src/main/java/com/example/otnet/
  OTNetApplication.kt           App-class. wires AppDeps with the publisher key
  MainActivity.kt               Edge-to-edge, forces dark theme
  data/
    api/
      ApiKeyInterceptor.kt      X-Api-Key on every request
      NetworkModule.kt          Single OkHttp + Retrofit instance
      OTNetService.kt           Retrofit interface (catalog/* + playback/drm/*)
    models/                     @Serializable models with optional fields +
                                accessor extensions (displayTitle, posterUrl, …)
  ui/
    OTNetApp.kt                 NavHost shell + bottom bar
    AppDeps.kt                  Singleton holder for the Retrofit service
    home/                       HomeScreen + HomeViewModel + HomeUiState
    browse/                     BrowseScreen + CategoryDetailScreen + VMs
    detail/                     ContentDetailScreen + VM
    player/                     PlayerScreen + MediaItemFactory (DRM)
    components/                 PosterCard / LandscapeCard / ContentRow /
                                HeroPager / StatePlaceholder
    theme/                      Color + Typography + OTNetTheme
```

## OTNet APIs used

All calls go through `data/api/OTNetService.kt`, which sends the publisher
key on every request via `ApiKeyInterceptor`. Key endpoints:

| Surface | OTNet endpoint |
|---|---|
| Homepage | `GET /catalog/homepage` |
| Content detail | `GET /catalog/content/:id` |
| Series children | `GET /catalog/content/:id/children` |
| Category browse | `GET /catalog/content/category/:id` |
| Categories tree | `GET /catalog/categories/tree` |
| DRM session | `POST /playback/drm/session` |
| DRM license | `POST /playback/drm/license?token=…&system=widevine` (called by ExoPlayer) |

See the [OTNet API docs](https://otnet.io/docs/api) for the full surface.

## DRM playback

OTNet runs session-mode DRM unconditionally. For any variant where
`variant.drm != null`, `MediaItemFactory.buildMediaItem`:

1. POSTs `{ contentId, mediaIndex }` to `/playback/drm/session` → gets a
   short-lived JWT
2. Configures the Media3 `MediaItem` with
   `MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID).setLicenseUri(...)`
   pointing at `playback/drm/license?token=<jwt>&system=widevine`
3. ExoPlayer's built-in `MediaDrm` stack handles provisioning + key requests

Clear variants (`variant.drm == null`) skip the DRM block entirely and play
straight through.

## Security notes

- `OTNET_API_KEY` lives in `local.properties`, which is gitignored. It is
  baked into `BuildConfig` at build time and is shipped inside the APK — for
  a public app you would replace this with a server-side mint flow.
- HTTP logging is set to `BASIC` (method + URL + status only) so request
  bodies aren't dumped to Logcat.
- Cleartext traffic is disabled in `AndroidManifest.xml`
  (`android:usesCleartextTraffic="false"`).

## What's deliberately out of scope (for follow-up prompts)

- Viewer sign-in / DataStore token storage
- Watch progress (`/viewer/progress`, `/device/progress`)
- Search, EPG / Live TV grid, profile picker
- My List
- BBFC rating badges
- Player error telemetry (`/telemetry/player-error`)

Each of those is a localised follow-up rather than a rewrite — the
architecture (ViewModel + StateFlow + sealed UiState + single Retrofit
instance behind `AppDeps`) is the same regardless.

## Links

- [OTNet](https://otnet.io)
- [OTNet docs](https://otnet.io/docs)
- [API reference](https://otnet.io/docs/api)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [AndroidX Media3](https://developer.android.com/media/media3)

## License

Choose a license that fits your project. This template ships without one.
