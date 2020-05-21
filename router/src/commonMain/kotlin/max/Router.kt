package max

interface Router<T : Router.Request> {

    fun handle(route: Uri): Boolean

    fun routerFor(route: Uri): Router<T>?

    fun match(route: Uri): Map<String, Any>

    fun matches(route: Uri): Boolean

    interface Handler<in T : Request> {

        fun handle(request: T)

    }

    interface Request {

        val route: Uri
        val params: Map<String, String>
        val splat: List<String>

        interface Creator<T : Request> {

            fun create(route: Uri, params: Map<String, String>, splat: List<String>): T

            companion object {

                fun default(): Creator<Default> = object : Creator<Default> {
                    override fun create(route: Uri, params: Map<String, String>, splat: List<String>) =
                        Default(route, params, splat)
                }

            }

        }

        data class Default(
            override val route: Uri,
            override val params: Map<String, String>,
            override val splat: List<String>
        ) : Request

    }

    interface Builder<T : Request> {

        fun path(path: String, init: Builder<T>.() -> Unit)

        fun route(path: String, handler: Handler<T>)

        fun route(path: String, handler: (T) -> Unit) = route(path, object : Handler<T> {
            override fun handle(request: T) {
                handler(request)
            }
        })

    }

    private class Impl<T : Request>(
        private val matcher: Matcher,
        private val handler: Handler<T>?,
        private val creator: Request.Creator<T>
    ) : Router<T>, Builder<T> {

        private val children = mutableListOf<Impl<T>>()

        override fun handle(route: Uri): Boolean {
            val router = routerFor(route)
            if (router?.handler != null) {
                val params = router.match(route)
                val request = creator.create(route, Matcher.params(params), Matcher.splat(params))
                router.handler.handle(request)
                return true
            }
            return false
        }

        override fun routerFor(route: Uri): Impl<T>? {
            for (child in children) {
                val router = child.routerFor(route)
                if (router != null) {
                    return router
                }
            }
            if (matches(route)) {
                return this
            }
            return null
        }

        override fun match(route: Uri): Map<String, Any> {
            return matcher.match(route.path)
        }

        override fun matches(route: Uri): Boolean {
            return matcher.matches(route.path)
        }

        override fun path(path: String, init: Builder<T>.() -> Unit) {
            val router = Impl(matcher.append(path), null, creator)
            router.init()
            addChild(router)
        }

        override fun route(path: String, handler: Handler<T>) {
            val router = Impl(matcher.append(path), handler, creator)
            addChild(router)
        }

        private fun addChild(router: Impl<T>): Impl<T> {
            children.add(router)
            return this
        }

    }

    companion object {

        const val SPLAT = Matcher.SPLAT

        operator fun invoke(init: Builder<out Request>.() -> Unit): Router<out Request> {
            return invoke(init, Request.Creator.default())
        }

        operator fun <T : Request> invoke(init: Builder<T>.() -> Unit, creator: Request.Creator<T>): Router<T> {
            val router = Impl(Matcher.empty(), null, creator)
            router.init()
            return router
        }

    }

}

