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
        creator.direction = Direction.REPLACE
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
        creator.direction = Direction.FORWARD
        if (topRoute != route && router.handle(route)) {
            return stack.add(route)
        }
        return false
    }

    fun pop(): Boolean {
        creator.direction = Direction.BACKWARD
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            return router.handle(stack.last())
        }
        return false
    }

    fun popTo(route: URI): Boolean {
        creator.direction = Direction.BACKWARD
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
        val direction: Direction
    ) : Router.Request {

        class Creator : Router.Request.Creator<Request> {

            var direction: Direction = Direction.REPLACE

            override fun create(route: URI, params: Map<String, Any?>): Request {
                return Request(route, params, direction)
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
