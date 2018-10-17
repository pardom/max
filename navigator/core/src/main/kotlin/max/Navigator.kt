package max

import java.net.URI

interface Navigator {

    fun routes(): Collection<URI>

    fun top(): URI

    fun params(): Map<String, String>

    fun param(name: String): String?

    fun splat(): Array<String>

    fun set(vararg routes: URI): Boolean

    fun set(routes: Collection<URI>): Boolean

    fun push(route: URI): Boolean

    fun pop(): Boolean

    fun popTo(route: URI): Boolean

    fun popToRoot(): Boolean

    enum class Direction {
        FORWARD, BACKWARD, REPLACE
    }

    class Request(
        override val route: URI,
        override val params: Map<String, String>,
        override val splat: Array<String>,
        val prevRoute: URI,
        val prevParams: Map<String, String>,
        val prevSplat: Array<String>,
        val direction: Direction
    ) : Router.Request {

        class Creator : Router.Request.Creator<Request> {

            private var route = URI("")
            private var params = emptyMap<String, String>()
            private var splat = emptyArray<String>()
            private var direction = Direction.REPLACE

            fun onSet() {
                direction = Direction.REPLACE
            }

            fun onPush() {
                direction = Direction.FORWARD
            }

            fun onPop() {
                direction = Direction.BACKWARD
            }

            override fun create(route: URI, params: Map<String, String>, splat: Array<String>): Request {
                val request = Request(route, params, splat, this.route, this.params, this.splat, direction)
                this.route = route
                this.params = params
                this.splat = splat
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

        override fun params(): Map<String, String> {
            val params = router.routerFor(top())?.match(top())
                ?: throw IllegalStateException("Top route not found in router.")
            return Matcher.params(params)
        }

        override fun param(name: String): String? {
            return params()[name]
        }

        override fun splat(): Array<String> {
            return Matcher.splat(params())
        }

        override fun set(vararg routes: URI): Boolean {
            return set(routes.toList())
        }

        override fun set(routes: Collection<URI>): Boolean {
            if (routes.isEmpty()) {
                throw IllegalArgumentException("Navigator stack must not be empty.")
            }
            for (route in routes) {
                if (!hasRouter(route)) {
                    return false
                }
            }
            stack.clear()
            stack.addAll(routes)
            creator.onSet()
            router.handle(top())
            return true
        }

        override fun push(route: URI): Boolean {
            if (top() == route) {
                return false
            }
            if (!hasRouter(route)) {
                return false
            }
            stack.add(route)
            creator.onPush()
            router.handle(route)
            return true
        }

        override fun pop(): Boolean {
            if (stack.size <= 1) {
                return false
            }
            stack.removeAt(stack.lastIndex)
            creator.onPop()
            router.handle(top())
            return true
        }

        override fun popTo(route: URI): Boolean {
            if (!stack.contains(route)) {
                return false
            }
            while (stack.size > 1 && top() != route) {
                stack.removeAt(stack.lastIndex)
            }
            creator.onPop()
            router.handle(top())
            return true
        }

        override fun popToRoot(): Boolean {
            if (stack.size == 1) {
                return false
            }
            return popTo(stack.first())
        }

        private fun hasRouter(route: URI): Boolean {
            return router.routerFor(route) != null
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
