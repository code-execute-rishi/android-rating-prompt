# Keep public API
-keep public class com.xyz.awesomerating.AwesomeRating { *; }
-keep public class com.xyz.awesomerating.AwesomeRating$* { *; }
-keep public class com.xyz.awesomerating.Builder { *; }
-keep public class com.xyz.awesomerating.RatingConfig { *; }
-keep public class com.xyz.awesomerating.Variant { *; }
-keep public class com.xyz.awesomerating.RatingThreshold { *; }
-keep public class com.xyz.awesomerating.FeedbackMode { *; }
-keep public class com.xyz.awesomerating.MailSettings { *; }
-keep public class com.xyz.awesomerating.model.DeviceInfo { *; }
-keep public interface com.xyz.awesomerating.callbacks.** { *; }

# Serializable fields must survive obfuscation (dialog config passed via Bundle)
-keepclassmembers class com.xyz.awesomerating.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Play Core In-App Review is optional compileOnly dep
-dontwarn com.google.android.play.core.review.**
