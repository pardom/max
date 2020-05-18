package max

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Based on https://github.com/sinatra/mustermann/blob/master/mustermann/spec/sinatra_spec.rb
 */
object MatcherTest : Spek({

    fun matcher(path: String, block: Pair<Suite, Matcher>.() -> Unit) {
        describe("A Matcher for \"$path\"") {
            block(this to Matcher.create(path))
        }
    }

    fun normalizeCaptures(captures: Map<String, Any>): Map<String, Any> {
        return if (captures.contains(Matcher.SPLAT)) captures
        else captures + (Matcher.SPLAT to emptyList<String>())
    }

    fun Pair<Suite, Matcher>.shouldMatch(path: String, vararg captures: Pair<String, Any>) {
        val (suite, matcher) = this
        with(suite) {
            context("matching on \"$path\"") {
                it("should match") {
                    assertTrue(matcher.matches(path))
                }
                it("should capture $captures") {
                    val expected = normalizeCaptures(captures.toMap())
                    val actual = matcher.match(path)
                    assertEquals(expected, actual)
                }
            }
        }
    }

    fun Pair<Suite, Matcher>.shouldNotMatch(path: String) {
        val (suite, matcher) = this
        with(suite) {
            context("matching on \"$path\"") {
                it("should not match") {
                    assertFalse(matcher.matches(path))
                }
            }
        }
    }

    matcher("") {
        shouldMatch("")
        shouldNotMatch("/")
    }
    matcher("/") {
        shouldMatch("/")
        shouldNotMatch("/foo")
    }
    matcher("/foo") {
        shouldMatch("/foo")
        shouldNotMatch("/bar")
        shouldNotMatch("/foo/bar")
    }
    matcher("/foo/bar") {
        shouldMatch("/foo/bar")
        shouldNotMatch("/foo%2Fbar")
        shouldNotMatch("/foo%2fbar")
    }
    matcher("/:foo") {
        shouldMatch("/foo", "foo" to "foo")
        shouldMatch("/bar", "foo" to "bar")
        shouldMatch("/foo.bar", "foo" to "foo.bar")
        shouldMatch("/%0Afoo", "foo" to "%0Afoo")
        shouldMatch("/foo%2fbar", "foo" to "foo%2fbar")
        shouldNotMatch("/foo?")
        shouldNotMatch("/foo/bar")
        shouldNotMatch("/")
        shouldNotMatch("/foo/")
    }
    matcher("/föö") {
        shouldMatch("/f%C3%B6%C3%B6")
    }
    matcher("/:foo/:bar") {
        shouldMatch("/foo/bar", "foo" to "foo", "bar" to "bar")
        shouldMatch("/foo.bar/bar.foo", "foo" to "foo.bar", "bar" to "bar.foo")
        shouldMatch("/user@example.com/name", "foo" to "user@example.com", "bar" to "name")
        shouldMatch("/10.1/te.st", "foo" to "10.1", "bar" to "te.st")
        shouldMatch("/10.1.2/te.st", "foo" to "10.1.2", "bar" to "te.st")
        shouldNotMatch("/foo%2Fbar")
        shouldNotMatch("/foo%2fbar")
    }
    matcher("/hello/:person") {
        shouldMatch("/hello/Frank", "person" to "Frank")
    }
    matcher("/?:foo?/?:bar?") {
        shouldMatch("/hello/world", "foo" to "hello", "bar" to "world")
        shouldMatch("/hello", "foo" to "hello", "bar" to "")
        shouldMatch("/", "foo" to "", "bar" to "")
        shouldMatch("", "foo" to "", "bar" to "")
        shouldNotMatch("/hello/world/")
    }
    matcher("/*") {
        shouldMatch("/", Matcher.SPLAT to listOf(""))
        shouldMatch("/foo", Matcher.SPLAT to listOf("foo"))
        shouldMatch("/foo/bar", Matcher.SPLAT to listOf("foo/bar"))
    }
    matcher("/:foo/*") {
        shouldMatch("/foo/bar/baz", "foo" to "foo", Matcher.SPLAT to listOf("bar/baz"))
        shouldMatch("/foo/", "foo" to "foo", Matcher.SPLAT to listOf(""))
        shouldMatch("/h%20w/h%20a%20y", "foo" to "h%20w", Matcher.SPLAT to listOf("h%20a%20y"))
        shouldNotMatch("/foo")
    }
    matcher("/test$/") {
        shouldMatch("/test$/")
    }
    matcher("/te+st/") {
        shouldMatch("/te+st/")
        shouldNotMatch("/test/")
        shouldNotMatch("/teest/")
    }
    matcher("/path with spaces") {
        shouldMatch("/path%20with%20spaces")
        shouldMatch("/path%2Bwith%2Bspaces")
        shouldMatch("/path+with+spaces")
    }
    matcher("/foo&bar") {
        shouldMatch("/foo&bar")
    }
    matcher("/*/:foo/*/*") {
        shouldMatch("/bar/foo/bling/baz/boom", "foo" to "foo", Matcher.SPLAT to listOf("bar", "bling", "baz/boom"))
    }
    matcher("/test.bar") {
        shouldMatch("/test.bar")
        shouldNotMatch("/test0bar")
    }
    matcher("/:file.:ext") {
        shouldMatch("/pony.jpg", "file" to "pony", "ext" to "jpg")
        shouldMatch("/pony%2Ejpg", "file" to "pony", "ext" to "jpg")
        shouldMatch("/pony%2ejpg", "file" to "pony", "ext" to "jpg")
        shouldMatch("/pony%E6%AD%A3%2Ejpg", "file" to "pony%E6%AD%A3", "ext" to "jpg")
        shouldMatch("/pony%e6%ad%a3%2ejpg", "file" to "pony%e6%ad%a3", "ext" to "jpg")
        shouldMatch("/pony正%2Ejpg", "file" to "pony正", "ext" to "jpg")
        shouldMatch("/pony正%2ejpg", "file" to "pony正", "ext" to "jpg")
        shouldMatch("/pony正..jpg", "file" to "pony正.", "ext" to "jpg")
        shouldNotMatch("/.jpg")
    }
    matcher("/:id/test.bar") {
        shouldMatch("/3/test.bar", "id" to "3")
        shouldMatch("/2/test.bar", "id" to "2")
        shouldMatch("/2E/test.bar", "id" to "2E")
        shouldMatch("/2e/test.bar", "id" to "2e")
        shouldMatch("/%2E/test.bar", "id" to "%2E")
    }
    matcher("/10/:id") {
        shouldMatch("/10/test", "id" to "test")
        shouldMatch("/10/te.st", "id" to "te.st")
    }
    matcher("/10.1/:id") {
        shouldMatch("/10.1/test", "id" to "test")
        shouldMatch("/10.1/te.st", "id" to "te.st")
    }
    matcher("/foo?") {
        shouldMatch("/fo")
        shouldMatch("/foo")
        shouldNotMatch("")
        shouldNotMatch("/")
        shouldNotMatch("/f")
        shouldNotMatch("/fooo")
    }
    matcher("/:f00") {
        shouldMatch("/a", "f00" to "a")
    }
    matcher("/:_X") {
        shouldMatch("/a", "_X" to "a")
    }
    matcher("/:foo.?") {
        shouldMatch("/a.", "foo" to "a.")
        shouldMatch("/xy", "foo" to "xy")
    }
})
