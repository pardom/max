package max

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe

class MatcherTest : Spek({
    describe("Matcher") {
        matcher("") {
            shouldMatch(it, "")
            shouldNotMatch(it, "/")
        }
        matcher("/") {
            shouldMatch(it, "/")
            shouldNotMatch(it, "/foo")
        }
        matcher("/foo") {
            shouldMatch(it, "/foo")
            shouldNotMatch(it, "/bar")
            shouldNotMatch(it, "/foo/bar")
        }
        matcher("/foo/bar") {
            shouldMatch(it, "/foo/bar")
            shouldNotMatch(it, "/foo%2Fbar")
            shouldNotMatch(it, "/foo%2fbar")
        }
        matcher("/:foo") {
            shouldMatch(it, "/foo")
            shouldMatch(it, "/bar")
            shouldMatch(it, "/foo.bar")
            shouldMatch(it, "/%0Afoo")
            shouldMatch(it, "/foo%2fbar")
            shouldMatch(it, "/bar")
            shouldNotMatch(it, "/foo?")
            shouldNotMatch(it, "/foo/bar")
            shouldNotMatch(it, "/")
            shouldNotMatch(it, "/foo/")
        }
        matcher("/föö") {
            shouldMatch(it, "/f%C3%B6%C3%B6")
        }
        matcher("/:foo/:bar") {
            shouldMatch(it, "/foo/bar", "foo" to "foo", "bar" to "bar")
            shouldMatch(it, "/foo.bar/bar.foo", "foo" to "foo.bar", "bar" to "bar.foo")
            shouldMatch(it, "/user@example.com/name", "foo" to "user@example.com", "bar" to "name")
            shouldMatch(it, "/10.1/te.st", "foo" to "10.1", "bar" to "te.st")
            shouldMatch(it, "/10.1.2/te.st", "foo" to "10.1.2", "bar" to "te.st")
            shouldNotMatch(it, "/foo%2Fbar")
            shouldNotMatch(it, "/foo%2fbar")
        }
        matcher("/hello/:person") {
            shouldMatch(it, "/hello/Frank", "person" to "Frank")
        }
        matcher("/?:foo?/?:bar?") {
            shouldMatch(it, "/hello/world", "foo" to "hello", "bar" to "world")
            shouldMatch(it, "/hello", "foo" to "hello", "bar" to "")
            shouldMatch(it, "/", "foo" to "", "bar" to "")
            shouldMatch(it, "", "foo" to "", "bar" to "")
            shouldNotMatch(it, "/hello/world/")
        }
        matcher("/*") {
            shouldMatch(it, "/")
            shouldMatch(it, "/foo", "splat" to arrayOf("foo"))
            shouldMatch(it, "/foo/bar", "splat" to arrayOf("foo/bar"))
        }
        matcher("/:foo/*") {
            shouldMatch(it, "/foo/bar/baz", "foo" to "foo", "splat" to arrayOf("bar/baz"))
            shouldMatch(it, "/foo/", "foo" to "foo", "splat" to arrayOf(""))
            shouldMatch(it, "/h%20w/h%20a%20y", "foo" to "h%20w", "splat" to arrayOf("h%20a%20y"))
            shouldNotMatch(it, "/foo")
        }
        matcher("/test$/") {
            shouldMatch(it, "/test$/")
        }
        matcher("/te+st/") {
            shouldMatch(it, "/te+st/")
            shouldNotMatch(it, "/test/")
            shouldNotMatch(it, "/teest/")
        }
        matcher("/path with spaces") {
            shouldMatch(it, "/path%20with%20spaces")
            shouldMatch(it, "/path%2Bwith%2Bspaces")
            shouldMatch(it, "/path+with+spaces")
        }
        matcher("/foo&bar") {
            shouldMatch(it, "/foo&bar")
        }
        matcher("/*/:foo/*/*") {
            shouldMatch(it, "/bar/foo/bling/baz/boom", "foo" to "foo", "splat" to arrayOf("bar", "bling", "baz/boom"))
        }
        matcher("/test.bar") {
            shouldMatch(it, "/test.bar")
            shouldNotMatch(it, "/test0bar")
        }
        matcher("/:file.:ext") {
            shouldMatch(it, "/pony.jpg", "file" to "pony", "ext" to "jpg")
            shouldMatch(it, "/pony%2Ejpg", "file" to "pony", "ext" to "jpg")
            shouldMatch(it, "/pony%2ejpg", "file" to "pony", "ext" to "jpg")
            shouldMatch(it, "/pony%E6%AD%A3%2Ejpg", "file" to "pony%E6%AD%A3", "ext" to "jpg")
            shouldMatch(it, "/pony%e6%ad%a3%2ejpg", "file" to "pony%e6%ad%a3", "ext" to "jpg")
            shouldMatch(it, "/pony正%2Ejpg", "file" to "pony正", "ext" to "jpg")
            shouldMatch(it, "/pony正%2ejpg", "file" to "pony正", "ext" to "jpg")
            shouldMatch(it, "/pony正..jpg", "file" to "pony正.", "ext" to "jpg")
            shouldNotMatch(it, "/.jpg")
        }
        matcher("/:id/test.bar") {
            shouldMatch(it, "/3/test.bar", "id" to "3")
            shouldMatch(it, "/2/test.bar", "id" to "2")
            shouldMatch(it, "/2E/test.bar", "id" to "2E")
            shouldMatch(it, "/2e/test.bar", "id" to "2e")
            shouldMatch(it, "/%2E/test.bar", "id" to "%2E")
        }
        matcher("/10/:id") {
            shouldMatch(it, "/10/test", "id" to "test")
            shouldMatch(it, "/10/te.st", "id" to "te.st")
        }
        matcher("/10.1/:id") {
            shouldMatch(it, "/10.1/test", "id" to "test")
            shouldMatch(it, "/10.1/te.st", "id" to "te.st")
        }
        matcher("/foo?") {
            shouldMatch(it, "/fo")
            shouldMatch(it, "/foo")
            shouldNotMatch(it, "")
            shouldNotMatch(it, "/")
            shouldNotMatch(it, "/f")
            shouldNotMatch(it, "/fooo")
        }
        matcher("/:f00") {
            shouldMatch(it, "/a", "f00" to "a")
        }
        matcher("/:_X") {
            shouldMatch(it, "/a", "_X" to "a")
        }
        matcher("/:foo.?") {
            shouldMatch(it, "/a.", "foo" to "a.")
            shouldMatch(it, "/xy", "foo" to "xy")
        }
    }
})
