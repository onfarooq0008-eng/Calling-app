# R8 / ProGuard rules for the release build.
#
# Jetpack Compose, AndroidX and DataStore all ship their own consumer rules,
# so this file can stay almost empty for a small app like Dice Roll.
#
# If you ever add a library that uses reflection (JSON parsers, Retrofit, ...),
# add its keep rules here. Keep rules look like this:
#
#   -keep class com.example.mylibrary.** { *; }

# Keep the file name + line numbers in crash reports (easier debugging on Play Console).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
