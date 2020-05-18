package max

import max.Navigator.Direction
import org.spekframework.spek2.style.specification.Suite
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

enum class Action(
    val functionName: String,
    val direction: Navigator.Direction,
    val call: (TestNavigator, Uri) -> TestNavigator.Result
) {
    PUSH("push", Direction.FORWARD, { navigator, route -> navigator.push(route) }),
    POP("pop", Direction.BACKWARD, { navigator, _ -> navigator.pop() }),
    POP_TO("popTo", Direction.BACKWARD, { navigator, route -> navigator.popTo(route) }),
    POP_TO_ROOT("popToRoot", Direction.BACKWARD, { navigator, _ -> navigator.popToRoot() })
}

fun Suite.shouldPush(navigator: TestNavigator, route: Uri, vararg captures: Pair<String, Any>) {
    return should(true, navigator, Action.PUSH, route, captures.toMap())
}

fun Suite.shouldPop(navigator: TestNavigator, route: Uri, vararg captures: Pair<String, Any>) {
    return should(true, navigator, Action.POP, route, captures.toMap())
}

fun Suite.shouldPopTo(navigator: TestNavigator, route: Uri, vararg captures: Pair<String, Any>) {
    return should(true, navigator, Action.POP_TO, route, captures.toMap())
}

fun Suite.shouldPopToRoot(navigator: TestNavigator, route: Uri, vararg captures: Pair<String, Any>) {
    return should(true, navigator, Action.POP_TO_ROOT, route, captures.toMap())
}

fun Suite.shouldNotPush(navigator: TestNavigator, route: Uri, vararg captures: Pair<String, Any>) {
    return should(false, navigator, Action.PUSH, route, captures.toMap())
}

fun Suite.shouldNotPop(navigator: TestNavigator, route: Uri, vararg captures: Pair<String, Any>) {
    return should(false, navigator, Action.POP, route, captures.toMap())
}

fun Suite.shouldNotPopTo(navigator: TestNavigator, route: Uri, vararg captures: Pair<String, Any>) {
    return should(false, navigator, Action.POP_TO, route, captures.toMap())
}

fun Suite.shouldNotPopToRoot(navigator: TestNavigator, route: Uri, vararg captures: Pair<String, Any>) {
    return should(false, navigator, Action.POP_TO_ROOT, route, captures.toMap())
}

fun Suite.should(
    should: Boolean,
    navigator: TestNavigator,
    action: Action,
    route: Uri,
    captures: Map<String, Any>
) {
    context("on ${action.functionName} of '$route'") {
        val result = action.call(navigator, route)
        if (should) {
            it("it should handle the route") {
                assertTrue(result.handled)
            }
            it("it should have a top route of '$route'") {
                assertEquals(route, result.request.route)
            }
            it("it should have top params of '$captures'") {
                val expected = normalizeForComparison(captures)
                val actual = normalizeForComparison(result.request.params)
                assertEquals(expected, actual)
            }
            it("it should have direction of '${action.direction}'") {
                assertEquals(action.direction, result.request.direction)
            }
        } else {
            it("it should not handle the route") {
                assertFalse(result.handled)
            }
        }
    }
}

private fun normalizeForComparison(map: Map<*, *>): Map<*, *> {
    val normalized = map.toMutableMap()
    normalized[Navigator.SPLAT] =
        if (normalized.containsKey(Navigator.SPLAT)) {
            (normalized[Navigator.SPLAT] as List<*>).toMutableList()
        } else {
            mutableListOf<String>()
        }
    return normalized
}
