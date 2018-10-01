package max

import java.net.URI

class Router<T : Router.Request> private constructor(
    private val matcher: Matcher,
    private val handler: Handler<T>?,
    private val creator: Request.Creator<T>
) : RouterBody<T> {

    private val children = mutableListOf<Router<T>>()

    fun handle(route: URI): Boolean {
        val router = routerFor(route)
        if (router?.handler != null) {
            val request = creator.create(route, router.match(route))
            router.handler.handle(request)
            return true
        }
        return false
    }

    fun routerFor(route: URI): Router<T>? {
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

    fun match(route: URI): Map<String, Any?> {
        return matcher.match(route.path) + parseQuery(route)
    }

    fun matches(route: URI): Boolean {
        return matcher.matches(route.path)
    }

    override fun path(path: String, init: RouterBody<T>.() -> Unit) {
        val router = Router(Matcher.create(path, matcher), null, creator)
        router.init()
        addChild(router)
    }

    override fun route(path: String, handler: Handler<T>) {
        val router = Router(Matcher.create(path, matcher), handler, creator)
        addChild(router)
    }

    private fun addChild(route: Router<T>): Router<T> {
        children.add(route)
        return this
    }

    private fun parseQuery(route: URI): Map<String, Any?> {
        return route.query
            ?.split(',')
            ?.fold(emptyMap()) { map, param ->
                val (key, value) = param.split('=')
                map + (key to value)
            }
            ?: emptyMap()
    }

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

        val params: Map<String, Any?>

        interface Creator<T : Request> {

            fun create(route: URI, params: Map<String, Any?>): T

            companion object {

                fun default(): Creator<Request.Default> = object : Creator<Request.Default> {
                    override fun create(route: URI, params: Map<String, Any?>) = Request.Default(route, params)
                }

            }

        }

        data class Default(
            override val route: URI,
            override val params: Map<String, Any?>
        ) : Request

    }

    companion object {

        operator fun invoke(init: RouterBody<out Request>.() -> Unit): Router<out Request> {
            return invoke(init, Request.Creator.default())
        }

        operator fun <T : Request> invoke(init: RouterBody<T>.() -> Unit, creator: Request.Creator<T>): Router<T> {
            val router = Router(Matcher.empty(), Handler.unknown(), creator)
            router.init()
            return router
        }

    }

}

