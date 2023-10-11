# soccer-score
The project provides a simple soccer score table using Kotlin as the solution language and Groovy together with Spock
for testing.

By no means it is a deployment ready project. Some parts of it are better worked through, and the other a deliberitely
neglected to demonstrate specific techniques of problem solving and coding. You can find a list of assumptions and
highlights of the solution.

## Assumptions
- Application does not use a durable storage mechanism. All data put into it will be lost when the process running the
  app finished.
- Application should model an online data table of soccer game scores. Therefore, its capacity is not designed for
  tracking large amounts of data. A reasonable limit would be 1000 games.
- The table should be highly accessible by providing a summary of a finxed amount og games. Therefore, a cache of games
  is managed internally.
- The table should be accessible in a multithreaded environment. Therefore, all data modification operations are
  synchronized to a single flow and happen one-after-anouther to provide data consistency.
- The time for a single data modification operation should be minimized. Therefore, application contains an indexing
  data structure for fast data positioning, and uses binary search since the data in the table is sorted. As a result,
  the application uses extended amount of data for storing teams order 3 * O(n) where n is the number of teams. Such an
  amount of memory is not necessary, but discretionary for time complexity optimization.
- The application was coded in a TDD process. Therefore, it is assumed that the functionality is a side effect of the
  test suite. Some code structures, especially the stateless methods in a companion object, could have been
  encapsulated, but it was chosen to make them available for testing as units of code.

## Highlights
- Using Spock to write test in a behavioral manner in the given-when-then notation
- Using parameterized tests as the to step approach to growing the source code. First step by dummy implementation to
  pass a single test case, and second step by generalized implementation to cover as wide domain socpe as possible.
- Some test cases are not written as parameterized test. The exception to the rule above was made to save time when
  implementation is trivial.
- Application uses synchronization of itself as the data consistency mechanism. This approach seems simpler then any
  other, e.g. single-threaded thread pools, or synchronized/concurrent collections.
- ScoreBoardService used the interface segregation principle by conforming to a single protocol. Doe to the single
  responsibility of the service, there is no need to implement other interfaces. It is more a remark to emphasise then
- Reason for choosing Kotlin over Java. The main reason for choosing Kotlin in providing consistency by immutability
  and null avoidance. A sample use case is the summary cache, which can be kept consistent and limit the number of
  creating copies of lists by having an immutable collection that contains immutable elements to share them with
  external clients.