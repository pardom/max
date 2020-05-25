# Max Matcher

## Example

```kotlin
val matcher = Matcher("/:user/:repo")
val uri = Uri.parse("http://github.com/pardom/max")

matcher.matches(uri) // true
matcher.match(uri) // mapOf("user" to  "pardom", "repo" to "max")
```