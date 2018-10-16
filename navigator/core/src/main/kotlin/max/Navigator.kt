package max

import java.net.URI

interface Navigator {

    fun routes(): Collection<URI>

    fun top(): URI

    fun params(): Map<String, Any?>

    fun param(name: String): Any?

    fun set(vararg routes: URI): Boolean

    fun set(routes: Collection<URI>): Boolean

    fun push(route: URI): Boolean

    fun pop(): Boolean

    fun popTo(route: URI): Boolean

    fun popToRoot(): Boolean

    enum class Direction {
        FORWARD, BACKWARD, REPLACE
    }

    data class Request(
        override val route: URI,
        override val params: Map<String, Any?>,
        val prevRoute: URI,
        val prevParams: Map<String, Any?>,
        val direction: Direction
    ) : Router.Request {

        class Creator : Router.Request.Creator<Request> {

            private var route: URI = URI("")
            private var params: Map<String, Any?> = emptyMap()
            private var direction: Direction = Direction.REPLACE

            fun onSet() {
                direction = Direction.REPLACE
            }

            fun onPush() {
                direction = Direction.FORWARD
            }

            fun onPop() {
                direction = Direction.BACKWARD
            }

            override fun create(route: URI, params: Map<String, Any?>): Request {
                val request = Request(route, params, this.route, this.params, direction)
                this.route = route
                this.params = params
                return request
            }

        }

    }

    private class Impl(
        initialRoute: URI,
        private val router: Router<Request>,
        private val creator: Request.Creator
    ) : Navigator {

        private val stack = mutableListOf<URI>()

        init {
            set(initialRoute)
        }

        override fun routes(): Collection<URI> {
            return stack
        }

        override fun top(): URI {
            return routes().lastOrNull()
                ?: throw IllegalStateException("Route stack is empty.")
        }

        override fun params(): Map<String, Any?> {
            return router.routerFor(top())?.match(top())
                ?: throw IllegalStateException("Top route not found in router.")
        }

        override fun param(name: String): Any? {
            return params()[name]
        }

        override fun set(vararg routes: URI): Boolean {
            return set(routes.toList())
        }

        override fun set(routes: Collection<URI>): Boolean {
            creator.onSet()
            if (routes.isEmpty()) {
                throw IllegalArgumentException("Navigator stack must not be empty.")
            }
            stack.clear()
            for (route in routes) {
                if (!stack.add(route)) {
                    return false
                }
            }
            return router.handle(top())
        }

        override fun push(route: URI): Boolean {
            creator.onPush()
            if (top() != route && router.handle(route)) {
                return stack.add(route)
            }
            return false
        }

        override fun pop(): Boolean {
            creator.onPop()
            if (stack.size > 1) {
                stack.removeAt(stack.lastIndex)
                return router.handle(stack.last())
            }
            return false
        }

        override fun popTo(route: URI): Boolean {
            creator.onPop()
            while (stack.size > 1 && stack.last() != route) {
                stack.removeAt(stack.lastIndex)
                if (!router.handle(stack.last())) {
                    return false
                }
            }
            return true
        }

        override fun popToRoot(): Boolean {
            return popTo(URI(""))
        }

    }

    companion object {

        const val SPLAT = Matcher.SPLAT

        operator fun invoke(initialRoute: URI, init: RouterBody<Navigator.Request>.() -> Unit): Navigator {
            val creator = Request.Creator()
            return Impl(initialRoute, Router(init, creator), creator)
        }

    }

}
