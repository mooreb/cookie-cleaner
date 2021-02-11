# cookie-cleaner

By default: report on any cookies sent to this webapp.

With query parameter:
  * add=1

add a cookie of the form:
  * name: now-{epoch}
  * value: localized "now" string


With query parameter:
  * clear=1

For each cookie in the incoming request: send back a Set-Cookie header with the following properties:
  * the exact same name/path/domain/secure/version as the input cookie under consideration, and
  * expires=0 (aka 1970-01-01T00:00:00Z)

This effectively deletes the cookie by relying on behavior required of the browser; Namely: the requirement to immediately evict any expired cookie.

For more information please see:
  * https://en.wikipedia.org/wiki/HTTP_cookie
  * https://tools.ietf.org/html/rfc6265#section-5.3

Which reads, in part:

   A cookie is "expired" if the cookie has an expiry date in the past.

   The user agent MUST evict all expired cookies from the cookie store
   if, at any time, an expired cookie exists in the cookie store.

   At any time, the user agent MAY "remove excess cookies" from the
   cookie store if the number of cookies sharing a domain field exceeds
   some implementation-defined upper bound (such as 50 cookies).

   At any time, the user agent MAY "remove excess cookies" from the
   cookie store if the cookie store exceeds some predetermined upper
   bound (such as 3000 cookies).
