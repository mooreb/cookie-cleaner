# cookie-cleaner
by default: report on any cookies sent to this webapp.
with query parameter:
  * add=1
add a cookie of the form:
  * name: now-{epoch}
  * value: localized "now" string
with query parameter:
  * clear=1
send back Set-Cookie headers (of the same name/path/domain/secure/version) which effectively delete the cookie by setting the expires epoch to 0.

For more information please see:
  * https://en.wikipedia.org/wiki/HTTP_cookie
  * https://tools.ietf.org/html/rfc6265#section-5.3

   A cookie is "expired" if the cookie has an expiry date in the past.

   The user agent MUST evict all expired cookies from the cookie store
   if, at any time, an expired cookie exists in the cookie store.
