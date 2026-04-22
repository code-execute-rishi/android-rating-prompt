# AwesomeRating Android

Java + XML Android rating dialog library. Three UX variants, Material 3, light + dark themes,
built-in email feedback with opt-in device info. Distributable via JitPack.

## Install

**Step 1** — add JitPack to your root `settings.gradle`:
```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2** — add the dep to your app module:
```groovy
implementation 'com.github.yourname:awesomerating-android:0.1.0'

// Only if you use Google in-app review:
implementation 'com.google.android.play:review:2.0.2'
```

## Minimum usage

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState == null) {
        AwesomeRating.with(this)
                .variant(Variant.STARS)
                .threshold(RatingThreshold.FOUR)
                .enableEmailFeedback("support@myapp.com")
                .showIfMeetsConditions();
    }
}
```

Defaults: minDays=3, minLaunches=5, threshold=FOUR.

## Variants

| Variant | Best for | UX |
|---------|----------|-----|
| `Variant.EMOJI`  | casual apps | 5 emoji chips, tap-to-submit |
| `Variant.STARS`  | utility apps (default) | classic 5-star bar |
| `Variant.BINARY` | brevity / highest conversion | Love-it / Needs-work cards |

## Feedback modes

- `enableEmailFeedback(MailSettings)` — low ratings open a mail composer to your team
- `enableCustomFeedback(listener)` — you get a callback with the raw text + device info
- `disableFeedback()` — low ratings see a brief thank-you and close

When feedback is enabled, the form shows:
- multi-line EditText (500-char counter)
- "Include device info" checkbox (model / Android version / app version)
- live device-info preview chip (only shown when box is ticked)

## Branding

Zero-config: inherits `colorPrimary` + `colorSurface` from the host theme.

Programmatic:
```java
AwesomeRating.with(this)
    .primaryColor(0xFFEF4444)
    .positiveColor(0xFF10B981)
    .negativeColor(0xFFF59E0B)
    .icon(R.drawable.my_logo)
    ...
```

Theme overlay:
```xml
<style name="MyApp.RatingTheme" parent="Theme.AwesomeRating">
    <item name="arPrimaryColor">@color/brand</item>
    <item name="arCornerRadiusSheet">32dp</item>
    <item name="arHeadlineFontFamily">@font/my_display</item>
</style>
```
```java
.customTheme(R.style.MyApp_RatingTheme)
```

Available theme attrs: `arPrimaryColor`, `arOnPrimaryColor`, `arPositiveColor`, `arNegativeColor`,
`arStarFillColor`, `arSheetBackgroundColor`, `arSurfaceVariantColor`, `arTextPrimaryColor`,
`arTextSecondaryColor`, `arEditTextBackgroundColor`, `arDragHandleColor`,
`arCornerRadiusSheet`, `arCornerRadiusButton`, `arCornerRadiusEditText`,
`arHeadlineFontFamily`, `arBodyFontFamily`, `arIconDrawable`.

## Google In-App Review

```java
AwesomeRating.with(this)
    .useGoogleInAppReview()
    .onInAppReviewCompleted(ok -> log.debug("review launched: " + ok))
    .showIfMeetsConditions();
```

After the first successful flow, subsequent prompts use the "show again" thresholds
(`minDaysToShowAgain`, `minLaunchesToShowAgain`).

## Requirements

- Android 5.0+ (API 21)
- AppCompatActivity / FragmentActivity
- Material Components 1.12+
- `Theme.Material3.DayNight` or descendant on the host activity

## License

Apache 2.0.
