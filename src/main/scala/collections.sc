
import scala.collection.{AbstractSeq, immutable}
import scala.util.{Failure, Success, Try}
// Collections

//// immutable List
assert(List(1, 2, 3) == 1 :: 2 :: 3 :: Nil)

val head = "foo"
val tail = List("bar")

// Cons
val cons = head :: tail
assert(cons.size == 2)

// Nil
assert(Nil.isEmpty)

// Seq-prepend
assert(tail.+:(head) == cons)
assert(head +: tail == cons)

// lists are functions: (Int) => T
assert(cons(1) == "bar")

//// immutable Set
var set = Set(1, 1, 2, 2, 3)
assert(set contains 3)

// 'Adding' elements
assert(set == set + 1)
set += 4 // equivalent to set = set + 4
assert(set contains 4)

// sets are functions: (T) => Boolean
assert(set(1))
assert(!set(5))

//// Map
var map = Map("foo" -> 1, "bar" -> 2)

// maps are collections of pairs
assert(map.head == ("foo", 1))
assert(map.size == 2)

val entry = ("baz", 3)
map += entry


// partial function: (K) => V
assert(map.isDefinedAt("bar"))
assert(map("foo") == 1)

map.get("baz")
map.get("bingo")
map.get("none such")

//// Option
val option = Some("a value")
None

option.contains("a value")
option.size

None.orElse(Some("an alternative"))
Some(1).getOrElse {
  throw new RuntimeException()
}

//// Either
val either: Either[Boolean, String] = Either.cond(1 > 2, "conditiion was true", false)

val projection: Either[Int, Int] = either
  .left.map(bool => if (bool) 1 else 0)
  .right.map((s: String) => s.length)

either match {
  case Left(bool) => s"was a boolean: $bool"
  case Right(string) => s"was a string: $string"
}

//// Try

val tryCatch: Try[String] = Try {
  throw new RuntimeException("risky operation")
}

// either a Success or a Failure
tryCatch.transform(success => Failure(new RuntimeException(success)), failure => Success(failure.getMessage))

// defaults
tryCatch.getOrElse("default value")

// conversions
tryCatch.toOption
tryCatch.toEither


//// Comprehensions
val seq = List(1, 2, 3)

// iterate, no return
for (elem <- seq) println(elem)
seq.foreach(elem => println(elem))

// iterate, with return
for (elem <- seq) yield elem * 2
seq.map(elem => elem * 2)

// Nested comprehensions
val collection: Map[String, Seq[Int]] = Map(
  "evens" -> (0 to 10 by 2),
  "odds" -> (1 to 11 by 2),
  "prime" -> (1 :: 2 :: 3 :: 5 :: 7 :: 11 :: Nil)
)

for {
  (name, range) <- collection
  upper = name.toUpperCase
  value <- range if name.nonEmpty && value >= 6
} yield s"$upper $value"

for {
  (name, range) <- collection
  upper = name.toUpperCase
  value <- range if name.nonEmpty && value >= 6
} yield s"$upper $value"

collection.flatMap { entry =>
  val name = entry._1
  val range = entry._2
  val upper = name.toUpperCase
  range.withFilter(value => name.nonEmpty && value >= 6).map(value => s"$upper $value")
}
