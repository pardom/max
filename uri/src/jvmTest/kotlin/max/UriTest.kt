package max

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object UriTest : Spek({

    fun Suite.uri(path: String, block: Pair<Suite, Uri>.() -> Unit) {
        block(this to Uri.parse(path))
    }

    fun Pair<Suite, Uri>.shouldParse(
        scheme: String?,
        authority: String?,
        path: String,
        query: String?,
        fragment: String?
    ) {
        val (suite, uri) = this
        with(suite) {
            it("Should parse successfully") {
                assertEquals(Uri(scheme, authority, path, query, fragment), uri)
            }
        }
    }

    fun Pair<Suite, Uri>.shouldDerive(
        userInfo: String?,
        host: String?,
        port: Int?
    ) {
        val (suite, uri) = this
        with(suite) {
            it("Should have a userInfo of $userInfo") {
                assertEquals(userInfo, uri.userInfo)
            }
            it("Should have a host of $host") {
                assertEquals(host, uri.host)
            }
            it("Should have a port of $port") {
                assertEquals(port, uri.port)
            }
        }
    }


    describe("complete URI") {
        uri("scheme://user:pass@host:81/path?query#fragment") {
            shouldParse("scheme", "user:pass@host:81", "/path", "query", "fragment")
            shouldDerive("user:pass", "host", 81)
        }
    }
//    describe("URI is not normalized") {
//        uri("ScheMe://user:pass@HoSt:81/path?query#fragment") {
//            shouldParse("ScheMe", "user:pass@HoSt:81", "/path", "query", "fragment")
//            shouldDerive("user:pass", "HoSt", 81)
//        }
//    }
//    describe("URI without scheme") {
//        uri("//user:pass@HoSt:81/path?query#fragment") {
//            shouldParse(null, "user:pass@HoSt:81", "/path", "query", "fragment")
//            shouldDerive("user:pass", "HoSt", 81)
//        }
//    }
//    describe("URI without empty authority only") {
//        uri("//") {
//            shouldParse(null, "", "", null, null)
//            shouldDerive(null, "", null)
//        }
//    }
//    describe("URI without userinfo") {
//        uri("scheme://HoSt:81/path?query#fragment") {
//            shouldParse("scheme", "HoSt:81", "/path", "query", "fragment")
//            shouldDerive(null, "HoSt", 81)
//        }
//    }
//    describe("URI with empty userinfo") {
//        uri("scheme://@HoSt:81/path?query#fragment") {
//            shouldParse("scheme", "HoSt:81", "/path", "query", "fragment")
//            shouldDerive("", "HoSt", 81)
//        }
//    }
//    describe("URI without port") {
//        uri("scheme://user:pass@host/path?query#fragment") {
//            shouldParse("scheme", "user:pass@host", "/path", "query", "fragment")
//            shouldDerive("user:pass", "host", null)
//        }
//    }
//    describe("URI with an empty port") {
//        uri("scheme://user:pass@host:/path?query#fragment") {
//            shouldParse("scheme", "user:pass@host", "/path", "query", "fragment")
//            shouldDerive("user:pass", "host", null)
//        }
//    }
//    describe("URI without user info and port") {
//        uri("scheme://host/path?query#fragment") {
//            shouldParse("scheme", "host", "/path", "query", "fragment")
//            shouldDerive(null, "host", null)
//        }
//    }
//    describe("URI with host IP") {
//        uri("scheme://10.0.0.2/p?q#f") {
//            shouldParse("scheme", "10.0.0.2", "/p", "q", "f")
//            shouldDerive(null, "10.0.0.2", null)
//        }
//    }
//    describe("URI with scoped IP") {
//        uri("scheme://[fe80:1234::%251]/p?q#f") {
//            shouldParse("scheme", "[fe80:1234::%251]", "/p", "q", "f")
//            shouldDerive(null, "[fe80:1234::%251]", null)
//        }
//    }
//    describe("URI with IP future") {
//        uri("scheme://[vAF.1::2::3]/p?q#f") {
//            shouldParse("scheme", "[vAF.1::2::3]", "/p", "q", "f")
//            shouldDerive(null, "[vAF.1::2::3]", null)
//        }
//    }
//    describe("URI without authority") {
//        uri("scheme:path?query#fragment") {
//            shouldParse("scheme", null, "path", "query", "fragment")
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI without authority and scheme") {
//        uri("/path") {
//            shouldParse(null, null, "/path", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with empty host") {
//        uri("scheme:///path?query#fragment") {
//            shouldParse("scheme", "", "/path", "query", "fragment")
//            shouldDerive(null, "", null)
//        }
//    }
//    describe("URI with empty host and without scheme") {
//        uri("///path?query#fragment") {
//            shouldParse(null, "", "/path", "query", "fragment")
//            shouldDerive(null, "", null)
//        }
//    }
//    describe("URI without path") {
//        uri("scheme://[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]?query#fragment") {
//            shouldParse("scheme", "[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]", "", "query", "fragment")
//            shouldDerive(null, "[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]", null)
//        }
//    }
//    describe("URI without path and scheme") {
//        uri("//[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]?query#fragment") {
//            shouldParse(null, "[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]", "", "query", "fragment")
//            shouldDerive(null, "[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]", null)
//        }
//    }
//    describe("URI without scheme with IPv6 host and port") {
//        uri("//[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]:42?query#fragment") {
//            shouldParse(null, "[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]:42", "", "query", "fragment")
//            shouldDerive(null, "[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]", 42)
//        }
//    }
//    describe("complete URI without scheme") {
//        uri("//user@[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]:42?q#f") {
//            shouldParse(null, "user@[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]:42", "", "q", "f")
//            shouldDerive("user", "[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]", 42)
//        }
//    }
//    describe("URI without authority and query") {
//        uri("scheme:path#fragment") {
//            shouldParse("scheme", null, "path", null, "fragment")
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with empty query") {
//        uri("scheme:path?#fragment") {
//            shouldParse("scheme", null, "path", "", "fragment")
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with query only") {
//        uri("?query") {
//            shouldParse(null, null, "", "query", null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI without fragment") {
//        uri("tel:05000") {
//            shouldParse("tel", null, "05000", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with empty fragment") {
//        uri("scheme:path#") {
//            shouldParse("scheme", null, "path", null, "")
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with fragment only") {
//        uri("#fragment") {
//            shouldParse(null, null, "", null, "fragment")
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with empty fragment only") {
//        uri("#") {
//            shouldParse(null, null, "", null, "")
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI without authority 2") {
//        uri("path#fragment") {
//            shouldParse(null, null, "path", null, "fragment")
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with empty query and fragment") {
//        uri("?#") {
//            shouldParse(null, null, "", "", "")
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with absolute path") {
//        uri("/?#") {
//            shouldParse(null, null, "/", "", "")
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with absolute authority") {
//        uri("https://thephpleague.com./p?#f") {
//            shouldParse("https", "thephpleague.com.", "/p", "", "f")
//            shouldDerive(null, "thephpleague.com.", null)
//        }
//    }
//    describe("URI with absolute path only") {
//        uri("/") {
//            shouldParse(null, null, "/", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI with empty query only") {
//        uri("?") {
//            shouldParse(null, null, "", "", null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("relative path") {
//        uri("../relative/path") {
//            shouldParse(null, null, "../relative/path", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("complex authority") {
//        uri("http://a_.!~*\'(-)n0123Di%25%26:pass;:&=+$,word@www.zend.com") {
//            shouldParse("http", "a_.!~*\'(-)n0123Di%25%26:pass;:&=+$,word@www.zend.com", "", null, null)
//            shouldDerive("a_.!~*\'(-)n0123Di%25%26:pass;:&=+$,word", "www.zend.com", null)
//        }
//    }
//    describe("complex authority without scheme") {
//        uri("//a_.!~*\'(-)n0123Di%25%26:pass;:&=+$,word@www.zend.com") {
//            shouldParse(null, "a_.!~*\'(-)n0123Di%25%26:pass;:&=+$,word@www.zend.com", "", null, null)
//            shouldDerive("a_.!~*\'(-)n0123Di%25%26:pass;:&=+$,word", "www.zend.com", null)
//        }
//    }
//    describe("single word is a path") {
//        uri("http") {
//            shouldParse(null, null, "http", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("URI scheme with an empty authority") {
//        uri("http://") {
//            shouldParse("http", "", "", null, null)
//            shouldDerive(null, "", null)
//        }
//    }
//    describe("single word is a path, no") {
//        uri("http:::/path") {
//            shouldParse("http", null, "::/path", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("fragment with pseudo segment") {
//        uri("http://example.com#foo=1/bar=2") {
//            shouldParse("http", "example.com", "", null, "foo=1/bar=2")
//            shouldDerive(null, "example.com", null)
//        }
//    }
//    describe("empty string") {
//        uri("") {
//            shouldParse(null, null, "", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("complex URI") {
//        uri("htà+d/s:totot") {
//            shouldParse(null, null, "htà+d/s:totot", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("scheme only URI") {
//        uri("http:") {
//            shouldParse("http", null, "", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("RFC3986 LDAP example") {
//        uri("ldap://[2001:db8::7]/c=GB?objectClass?one") {
//            shouldParse("ldap", "[2001:db8::7]", "/c=GB", "objectClass?one", null)
//            shouldDerive(null, "[2001:db8::7]", null)
//        }
//    }
//    describe("RFC3987 example") {
//        uri("http://bébé.bé./有词法别名.zh") {
//            shouldParse("http", "bébé.bé.", "/有词法别名.zh", null, null)
//            shouldDerive(null, "bébé.bé.", null)
//        }
//    }
//    describe("colon detection respect RFC3986 (1)") {
//        uri("http://example.org/hello:12?foo=bar#test") {
//            shouldParse("http", "example.org", "/hello:12", "foo=bar", "test")
//            shouldDerive(null, "example.org", null)
//        }
//    }
//    describe("colon detection respect RFC3986 (2)") {
//        uri("/path/to/colon:34") {
//            shouldParse(null, null, "/path/to/colon:34", null, null)
//            shouldDerive(null, null, null)
//        }
//    }
//    describe("scheme with hyphen") {
//        uri("android-app://org.wikipedia/http/en.m.wikipedia.org/wiki/The_Hitchhiker%27s_Guide_to_the_Galaxy") {
//            shouldParse("android-app", "org.wikipedia", "/http/en.m.wikipedia.org/wiki/The_Hitchhiker%27s_Guide_to_the_Galaxy", null, null)
//            shouldDerive(null, "org.wikipedia", null)
//        }
//    }
//    describe("scheme with non-leading digit") {
//        uri("s3://somebucket/somefile.txt") {
//            shouldParse("s3", "somebucket", "/somefile.txt", null, null)
//            shouldDerive(null, "somebucket", null)
//        }
//    }
})
