# marked4j

A small wrapper around [https://github.com/chjj/marked](marked).

[![Clojars Project](http://clojars.org/org.clojars.rhg135/marked4j/latest-version.svg)](http://clojars.org/org.clojars.rhg135/marked4j)

## Usage

Pass a markdown string to `marked4j.core/marked`.

    (require '[marked4j.core :refer (marked)])
	(marked "# Hello World!")

Optionally the first arg may be a `javax.script.ScriptEngine`
with the marked code loaded; which is conveniently available via the no args
`marked4j.core/new-engine`.

The last arg is optional and may be a map or an instance of `marked4j.core.MarkedOptions`
If a map, turns it into an object and validates it.
Otherwise assumes it's good to pass into marked.

If an engine is provided option is *mandatory*.

## Options Map

### Keys
  * `:github-flavored-markdown` - if present must be a falsey value or a seq of keys to set to true per marked's options.
  * `:pedantic`, `sanitize`, and `smartypants` - correspond to marked's options of the name. default to false. boolean.
  * `:smart-lists` - corresponds to marked's `smartLists` option. defaults to false. boolean

If `:github-flavored-markdown` is falsey :gfm is set to false and all dependant options too.

## License

No rights reserved. Do whatever you want to my code. But the actual parser is not mine, so beware.

Attribution would be appreciated so much.
