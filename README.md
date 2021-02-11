# cookie-cleaner
by default: report on any cookies sent to this webapp.

With query parameter:
  * add=1

add a cookie of the form:
  * name: now-{epoch}
  * value: localized "now" string

With query parameter:
  * clear=1

send back Set-Cookie headers (of the same name/path/domain/secure/version) which effectively delete the cookie by setting the expires epoch to 0.

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
