# marked4j

A small wrapper around [https://github.com/chjj/marked](marked).

[![Clojars Project](http://clojars.org/org.clojars.rhg135/marked4j/latest-version.svg)](http://clojars.org/org.clojars.rhg135/marked4j)

## Usage

Pass a markdown string to `com.rhg135.marked4j/marked`.

    (require '[com.rhg135.marked4j :refer (marked)])
	(marked "# Hello World!")

Optionally the first argument may be a handle from `com.rhg135.marked4j/new-handle`.

The last argument is optional and must be a map as per `com.rhg135.marked4j/*options*`

If an engine is provided option is *mandatory*.

## License

No rights reserved. Do whatever you want to my code. But the actual parser is not mine, so beware.

Attribution would be appreciated so much.
