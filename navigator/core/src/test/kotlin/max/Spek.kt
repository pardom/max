package max

import com.google.common.truth.Truth.assertThat
import max.Navigator.Direction
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.net.URI

enum class Action(
    val functionName: String,
    val direction: Navigator.Direction,
    val call: (TestNavigator, URI) -> Boolean
) {
    PUSH("push", Direction.FORWARD, { navigator, route -> navigator.push(route) }),
    POP("pop", Direction.BACKWARD, { navigator, _ -> navigator.pop() }),
    POP_TO("popTo", Direction.BACKWARD, { navigator, route -> navigator.popTo(route) }),
    POP_TO_ROOT("popToRoot", Direction.BACKWARD, { navigator, _ -> navigator.popToRoot() })
}

fun SpecBody.shouldPush(navigator: TestNavigator, route: URI, vararg captures: Pair<String, Any>) {
    return should(true, navigator, Action.PUSH, route, captures.toMap())
}

fun SpecBody.shouldPop(navigator: TestNavigator, route: URI, vararg captures: Pair<String, Any>) {
    return should(true, navigator, Action.POP, route, captures.toMap())
}

fun SpecBody.shouldPopTo(navigator: TestNavigator, route: URI, vararg captures: Pair<String, Any>) {
    return should(true, navigator, Action.POP_TO, route, captures.toMap())
}

fun SpecBody.shouldPopToRoot(navigator: TestNavigator, route: URI, vararg captures: Pair<String, Any>) {
    return should(true, navigator, Action.POP_TO_ROOT, route, captures.toMap())
}

fun SpecBody.shouldNotPush(navigator: TestNavigator, route: URI, vararg captures: Pair<String, Any>) {
    return should(false, navigator, Action.PUSH, route, captures.toMap())
}

fun SpecBody.shouldNotPop(navigator: TestNavigator, route: URI, vararg captures: Pair<String, Any>) {
    return should(false, navigator, Action.POP, route, captures.toMap())
}

fun SpecBody.shouldNotPopTo(navigator: TestNavigator, route: URI, vararg captures: Pair<String, Any>) {
    return should(false, navigator, Action.POP_TO, route, captures.toMap())
}

fun SpecBody.shouldNotPopToRoot(navigator: TestNavigator, route: URI, vararg captures: Pair<String, Any>) {
    return should(false, navigator, Action.POP_TO_ROOT, route, captures.toMap())
}

fun SpecBody.should(
    should: Boolean,
    navigator: TestNavigator,
    action: Action,
    route: URI,
    captures: Map<String, Any>
) {
    on("${action.functionName} of '$route'") {
        val did = action.call(navigator, route)
        if (should) {
            it("should handle the route") {
                assertThat(did).isTrue()
            }
            it("should have a top route of '$route'") {
                assertThat(route).isEqualTo(navigator.route)
            }
            it("should have top params of '$captures'") {
                val expected = normalizeForComparison(captures)
                val actual = normalizeForComparison(navigator.params)
                assertThat(expected).isEqualTo(actual)
            }
            it("should have direction of '${action.direction}'") {
                assertThat(action.direction).isEqualTo(navigator.direction)
            }
        } else {
            it("should not handle the route") {
                assertThat(did).isFalse()
            }
        }
    }
}

private fun normalizeForComparison(map: Map<*, *>): Map<*, *> {
    val normalized = map.toMutableMap()
    normalized[Matcher.PARAM_SPLAT] =
            if (normalized.containsKey(Matcher.PARAM_SPLAT))
                (normalized[Matcher.PARAM_SPLAT] as Array<*>).toMutableList()
            else mutableListOf<String>()
    return normalized
}
