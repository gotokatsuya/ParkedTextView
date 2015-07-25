# ParkedTextView
A EditText with a constant text in the end.

![](https://github.com/gotokatsuya/ParkedTextView/blob/master/doc/demo-gif.gif)


## How to use

```xml

<com.goka.parkedtextview.ParkedTextView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/parked_text_view"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@android:color/transparent"
    android:layout_centerInParent="true"
    android:textSize="24sp"
    app:parkedText=".slack.com"
    app:parkedHint="yourteam"
    app:parkedTextColor="FFFFFF"
    app:parkedHintColor="CCCCCC"
    app:parkedTextBold="true"
    />

```

## Gradle

Coming soon.

```java
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.goka.parkedtextview:parkedtextview:1.0.0'
}
```


## Release
1.0.0
 First release.


## Reference
[ParkedTextField for iOS](https://github.com/gmertk/ParkedTextField)
