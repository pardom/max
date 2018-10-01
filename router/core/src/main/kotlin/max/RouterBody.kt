package max

interface RouterBody<T : Router.Request> {

    fun path(path: String, init: RouterBody<T>.() -> Unit)

    fun route(path: String, handler: Router.Handler<T>)

    fun route(path: String, handler: (T) -> Unit) {
        return route(path, object : Router.Handler<T> {
            override fun handle(request: T) {
                handler(request)
            }
        })
    }

}
