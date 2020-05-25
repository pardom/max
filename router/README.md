# Max Router

## Example

```kotlin
val router = Router {
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

router.handle(Uri.parse("https://github.com/pardom/max"))
```