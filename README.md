# URL Encoded Query String

This project consists of a single class. Download the source for it, insert
into your own projects and use as you see fit under the BSD license.

There is also an optional unit test class, which offers good code coverage.

## Usage

Represents a www-form-urlencoded query string containing an (ordered) list of parameters.

An instance of this class represents a query string encoded using the
`www-form-urlencoded` encoding scheme, as defined by [HTML 4.01 Specification]
(http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4.1), and
[HTML 4.01 Specification: Ampersands in URI attribute values]
(http://www.w3.org/TR/1999/REC-html401-19991224/appendix/notes.html#h-B.2.2). This
is a common encoding scheme of the query component of a URI, though the
[RFC 2396 URI specification](http://www.ietf.org/rfc/rfc2396.txt) itself does not
define a specific format for the query component.

This class provides static methods for creating UrlEncodedQueryString instances by
parsing URI and string forms. It can then be used to create, retrieve, update and
delete parameters, and to re-apply the query string back to an existing URI.

### Encoding and decoding

UrlEncodedQueryString automatically encodes and decodes parameter
names and values to and from `www-form-urlencoded` encoding by using
`java.net.URLEncoder` and `java.net.URLDecoder`, which follow the
[HTML 4.01 Specification: Non-ASCII characters in URI attribute values]
(http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars)
recommendation.

### Multivalued parameters

Often, parameter names are unique across the name/value pairs of
a `www-form-urlencoded` query string. However, it is permitted for the same parameter
name to appear in multiple name/value pairs, denoting that a single parameter has multiple
values. This less common use case can lead to ambiguity when adding parameters - is the 'add' a
'replace' (of an existing parameter, if one with the same name already exists) or an 'append'
(potentially creating a multivalued parameter, if one with the same name already exists)?

This requirement significantly shapes the `UrlEncodedQueryString` API. In particular
there are:

* `set` methods for setting a parameter, potentially replacing an existing value
* `append` methods for adding a parameter, potentially creating a multivalued
parameter
* `get` methods for returning a single value, even if the parameter has multiple
values
* `getValues` methods for returning multiple values

### Retrieving parameters

UrlEncodedQueryString can be used to parse and retrieve parameters
from a query string by passing either a URI or a query string:

````
    URI uri = new URI("http://java.sun.com?forum=2");
    UrlEncodedQueryString queryString = UrlEncodedQueryString.parse(uri);
    System.out.println(queryString.get("forum"));
````

### Modifying parameters

UrlEncodedQueryString can be used to set, append or remove
parameters from a query string:

````
    URI uri = new URI("/forum/article.jsp?id=2&amp;para=4");
    UrlEncodedQueryString queryString = UrlEncodedQueryString.parse(uri);
    queryString.set("id", 3);
    queryString.remove("para");
    System.out.println(queryString);
````

When modifying parameters, the ordering of existing parameters is maintained. Parameters are
`set` and `removed` in-place, while `appended` parameters are
added to the end of the query string.

### Applying the Query

UrlEncodedQueryString can be used to apply a modified query string
back to a URI, creating a new URI:

````
    URI uri = new URI("/forum/article.jsp?id=2");
    UrlEncodedQueryString queryString = UrlEncodedQueryString.parse(uri);
    queryString.set("id", 3);
    uri = queryString.apply(uri);
````

When reconstructing query strings, there are two valid separator parameters defined by the W3C
(ampersand "&" and semicolon ";"), with ampersand being the most common. The
`apply` and `toString` methods both default to using an ampersand, with
overloaded forms for using a semicolon.

### Thread Safety

This implementation is not synchronized. If multiple threads access a
query string concurrently, and at least one of the threads modifies the query string, it must be
synchronized externally. This is typically accomplished by synchronizing on some object that
naturally encapsulates the query string.

## History

This project was developed in close collaboration with Sun over a period of 18 months as part of
the Peabody initiative, in response to RFE 6306820. For various reasons it did not make it into the
JDK, so is instead released here under a BSD license.
