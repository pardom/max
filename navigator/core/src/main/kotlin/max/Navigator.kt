package max

import java.net.URI

class Navigator private constructor(
    initialRoute: URI,
    private val router: Router<Request>,
    private val creator: Request.Creator
) {

    private val stack = mutableListOf(initialRoute)

    val topRoute: URI
        get() = stack.lastOrNull()
            ?: throw IllegalStateException("Route stack is empty.")

    val topParams: Map<String, Any?>
        get() = router.routerFor(topRoute)?.match(topRoute)
            ?: throw IllegalStateException("Top route not found in router.")

    fun set(routes: Collection<URI>): Boolean {
        creator.onSet()
        if (routes.isEmpty()) {
            throw IllegalArgumentException("Navigator stack must not be empty.")
        }
        for (route in routes) {
            if (!stack.add(route)) {
                return false
            }
        }
        return router.handle(topRoute)
    }

    fun push(route: URI): Boolean {
        creator.onPush()
        if (topRoute != route && router.handle(route)) {
            return stack.add(route)
        }
        return false
    }

    fun pop(): Boolean {
        creator.onPop()
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            return router.handle(stack.last())
        }
        return false
    }

    fun popTo(route: URI): Boolean {
        creator.onPop()
        while (stack.size > 1 && stack.last() != route) {
            stack.removeAt(stack.lastIndex)
            if (!router.handle(stack.last())) {
                return false
            }
        }
        return true
    }

    fun popToRoot(): Boolean {
        return popTo(URI(""))
    }

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

    companion object {

        operator fun invoke(initialRoute: URI, init: RouterBody<Navigator.Request>.() -> Unit): Navigator {
            val creator = Request.Creator()
            return Navigator(initialRoute, Router(init, creator), creator)
        }

    }

}
