# kotlinx-serialization keeps @Serializable companion fields
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class **$$serializer {
    *** descriptor;
}
-keepclassmembers class com.example.otnet.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.otnet.** {
    kotlinx.serialization.KSerializer serializer(...);
}
