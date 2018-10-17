package max

import java.net.URI

interface Router<T : Router.Request> : RouterBody<T> {

    fun handle(route: URI): Boolean

    fun routerFor(route: URI): Router<T>?

    fun match(route: URI): Map<String, Any>

    fun matches(route: URI): Boolean

    interface Handler<in T : Request> {

        fun handle(request: T)

        companion object {

            fun none() = object : Handler<Request> {
                override fun handle(request: Request) {
                }
            }

            fun unknown() = object : Handler<Request> {
                override fun handle(request: Request) {
                    throw IllegalArgumentException("Unknown route for ${request.route}")
                }
            }

        }

    }

    interface Request {

        val route: URI

        val params: Map<String, String>

        val splat: Array<String>

        interface Creator<T : Request> {

            fun create(route: URI, params: Map<String, String>, splat: Array<String>): T

            companion object {

                fun default(): Creator<Request.Default> = object : Creator<Request.Default> {
                    override fun create(route: URI, params: Map<String, String>, splat: Array<String>) =
                        Request.Default(route, params, splat)
                }

            }

        }

        class Default(
            override val route: URI,
            override val params: Map<String, String>,
            override val splat: Array<String>
        ) : Request

    }

    private class Impl<T : Router.Request>(
        private val matcher: Matcher,
        private val handler: Handler<T>?,
        private val creator: Request.Creator<T>
    ) : Router<T> {

        private val children = mutableListOf<Impl<T>>()

        override fun handle(route: URI): Boolean {
            val router = routerFor(route)
            if (router?.handler != null) {
                val params = router.match(route)
                val request = creator.create(route, Matcher.params(params), Matcher.splat(params))
                router.handler.handle(request)
                return true
            }
            return false
        }

        override fun routerFor(route: URI): Impl<T>? {
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

        override fun match(route: URI): Map<String, Any> {
            return matcher.match(route.path) + parseQuery(route)
        }

        override fun matches(route: URI): Boolean {
            return matcher.matches(route.path)
        }

        override fun path(path: String, init: RouterBody<T>.() -> Unit) {
            val router = Impl(matcher.create(path), null, creator)
            router.init()
            addChild(router)
        }

        override fun route(path: String, handler: Handler<T>) {
            val router = Impl(matcher.create(path), handler, creator)
            addChild(router)
        }

        private fun addChild(route: Impl<T>): Impl<T> {
            children.add(route)
            return this
        }

        private fun parseQuery(route: URI): Map<String, String> {
            return route.query
                ?.split('&')
                ?.fold(emptyMap()) { map, param ->
                    val (key, value) = param.split('=')
                    map + (key to value)
                }
                ?: emptyMap()
        }

    }

    companion object {

        const val SPLAT = Matcher.SPLAT

        operator fun invoke(init: RouterBody<out Request>.() -> Unit): Router<out Request> {
            return invoke(init, Request.Creator.default())
        }

        operator fun <T : Request> invoke(init: RouterBody<T>.() -> Unit, creator: Request.Creator<T>): Router<T> {
            val router = Impl(Matcher.empty(), Handler.unknown(), creator)
            router.init()
            return router
        }

    }

}

