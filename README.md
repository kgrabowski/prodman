## Overview

Major modifications to the skeleton code include:

1. Extracting the original `FxService` code into a new `CurrencyLayerGateway` class
3. Implementing currency-conversion functionality inside the `FxService` class
4. Adding the required functionality of fetching a single product to the `ProductService` class
5. Adding some custom exceptions, together with improved error handling
6. Creating the `Currency` class
7. Using `BigDecimal` in place of `Double`

I have also added some unit tests around the new functionality.

Below, I'll go a bit more into the reasons behind each of the above decisions.

**Note:** Even though the problem statement said to return _all_ product fields, I've left the `deleted` and `version`
fields out. I went back and forth between including them: if implementing soft deletes, having the `deleted` field would
be useful in the returned product list. Since `version` is used for optimistic locking, it would only make sense to
return it if we allowed updating products, which we don't at the moment. At any rate, adding those fields to the
response would be trivial.

### Extracting `CurrencyLayerGateway`

Taking a look at the original `FxService` class, I immediately thought that it will have too many responsibilities. If I
put the currency conversion code inside the service, it would violate the Single Responsibility Principle. Not only
would the service fetch the exchange rates from a third-party API, it would also contain the logic to properly convert
the currency values.

Bundling those two functionalities together would make it hard to test. I would either need to use partial mocking
(stubbing out fetching the quotes when testing the currency conversion), or write unwieldy, hard-to-follow tests that
would bundle the behaviors together.

Instead, I opted to follow the _Gateway_ pattern, and extracted the code that talks to the third-party API into a new
class. For extra separation, I've introduced the `FxGateway` interface. In a production environment, it's feasible to
use more than one implementation of `FxGateway`, e.g. as a fall-back when one service is unavailable. This separation
would also allow us to write abstract contract tests against the interface, should we choose to do so.

### Implementing Currency Conversion

It seems logical that `FxService` should handle the currency conversion - it represents a foreign exchange, after all.

Putting _only_ this functionality inside the `FxService` allows us to follow the Single Responsibility Principle.
Additionally, code dealing with money is typically tricky, containing lots of edge conditions. This is especially true
with respect to rounding, which will be discussed a bit later. Because of this, I wanted to be able to test this
functionality directly, without it being tangled with anything else.

### Fetching Single Products

I've added the functionality to both add and request a single product to the `ProductService`. I've also bundled there
the ability to convert the price to another currency, and to increment the view count.

Currency conversion could be done inside the `ProductController`. However, I like to keep the view classes as
branch-free and light-weight as possible. Adding currency conversion there would necessitate testing through the
controller, which I generally tend to avoid (except very few integration tests for happy/error paths).

To increment the view count, we need access to the underlying `Product`. However, as it stands, the controller only
deals with DTOs and not the domain classes. Another options would be to leave the `incrementViews()` method inside
the `ProductService` and change it to take product ID. Thus, the controller could fetch the DTO and ask
the `ProductService` to increase the view count. This would make the method to fetch the product from the product
service more generic. In a real application, this is most likely the route I would take.

As a side note, incrementing view counts withing the _GET_ method has its obvious drawbacks. Any proxy in the HTTP call
chain is free to cache the result of the _GET_ request. This would make the whole implementation unreliable. I could
have added response headers to disable caching, but nothing prevents proxies from ignoring them completely.

### Custom Error Handling

Good API design is more than just implementing the required functionality. It also requires that we communicate errors
in a clear way. To that end, I ended up creating some custom exceptions in the `exception` package.

In a real project, approach taken here is far from satisfactory. Error handling requires a systematic approach, not an
ad-hoc one. First, a proper exception hierarchy needs to be established, even if very simple. This would allow us to
differentiate between domain-level errors and system-level errors. This, in turn, would allow us to decide which
exceptions to display to end users, which needs to be logged with extra context, and which errors could be safely
retried (using appropriate back-off strategies).

### Currency Enum

Complex domain code is much easier to maintain if we deal with domain concepts like currency, instead of strings
everywhere. The `Currency` class represents one such concept, implemented as a `Value Object` (from the Domain-Driven
Design). I've added some custom parsing code so clients can issue request like `?currency=cad` or `?currency=USD`,
without forcing them to remember the canonical representation.

### Representing Monetary Values

Representing money with the double data type is, in general, not a good idea. Doubles cannot represent the values
accurately, and there is often a major rounding error present especially around currency conversions.

For simplicity, I've decided to go with the `BigDecimal` class. It allows us to represent monetary values accurately and
it facilitates rounding after conversions. However, in a real project you might need to consider using a custom `Money`
class, based on fixed-point math. `BigDecimal` math tends to get very slow when doing lots of computations.

## Ideas for Improvement

There are lots of improvements that could be made to this solution:

* Use an actual restful API, with correct links to root/child entities etc.
* Better error handling, as mentioned before
* Fetching multiple products, using some sort of extra query criteria (e.g. include/exclude deleted items)
* Implement soft deletions using the provided `deleted` field
* Updating the products via PUT and PATCH requests
* More tests
