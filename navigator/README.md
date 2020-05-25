# Max Navigator

## Example

```kotlin
val navigator = Navigator(initialRoute = Uri.parse("https://github.com")) {
    path("/:user") {
        route("") { request ->
            respondWithUserRepos()
        }
        route("/:repo") { request ->
            val repo = request.params.getValue("repo")
            respondWithUserRepo(repo)
        }       
    }
}

navigator.push(Uri.parse("https://github.com/pardom"))
navigator.push(Uri.parse("https://github.com/pardom/max"))
navigator.pop()
```
