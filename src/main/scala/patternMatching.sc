sealed trait Planet {
  def name: String
}

case class Terrestrial(name: String) extends Planet

case class GasGiant(name: String, composition: Map[String, Double]) extends Planet

val mars: Planet = Terrestrial("Mars")
val jupiter: Planet = GasGiant("Jupiter", Map("hydrogen" -> 0.89, "helium" -> 0.10, "methane" -> .003))
val planets = List(mars, jupiter)

// Simple matching based on type
mars match {
  case planet: Terrestrial => s"${planet.name} is small and rocky"
  case planet: GasGiant => s"${planet.name} is big and gaseous"
}

// case-class deconstruction
def describePlanet(planet: Planet) = planet match {
  case Terrestrial(name) => s"$name is a small rocky planet"

  case GasGiant(name, composition) =>
    val pf: PartialFunction[(String, Double), String] = {
      case (element: String, amount: Double) if amount < .01 => s"trace amounts of $element"
      case (element: String, amount: Double) => s"${amount * 100}% $element"
    }
    val stuff = composition.map(pf).mkString(", ")
    s"$name is a gas giant made of $stuff"
}

describePlanet(jupiter)

// Recursive matching
def describePlanets(list: List[Planet]): Unit = list match {
  case head :: tail =>
    println(describePlanet(head))
    describePlanets(tail)
  case Nil => println("done")
}

describePlanets(planets)

// "deconstructed" case class
class Spaceship(val name: String, _engines: Int, _crew: Int, _speed: Double) {
  def engines: Int = _engines

  def crew: Int = _crew

  def speed: Double = _speed


  override def toString = s"Spaceship($name, crew=$crew, speed=$speed*c)"
}

val theFalcon = new Spaceship("Millenium Falcon", 8, 2, 1.5)

object Spaceship {
  // case constructor, for omitting 'new'
  def apply(name: String, engines: Int, crew: Int, speed: Double): Spaceship = {
    println(s"using 'case' constructor for $name")
    new Spaceship(name, engines, crew, speed)
  }

  def unapply(arg: Spaceship): Option[(Int, Int, Double)] = Some(arg.engines, arg.crew, arg.speed)
}

val xWing = Spaceship("T-65 X-Wing", crew = 1, engines = 4, speed = 1.0)

// Extractor with value assignment
val Spaceship(xWingEngines, xWingCrew, _) = xWing

// Extractor in partial function
xWing match {
  case Spaceship(crew, _, _) => s"The X-Wing has a crew of $crew"
}

// Extractor can have any name
object SpaceshipName {
  def unapply(arg: Spaceship): Option[String] = Some(arg.name)
}

theFalcon match {
  case SpaceshipName(name) => s"The $name is the fastest ship in the galaxy."
}

// Final form of an extractor, the no-arg
object FasterThanLightspeed {
  def unapply(arg: Spaceship): Boolean = arg.speed > 1
}

List(xWing, theFalcon).map({
  case ship@FasterThanLightspeed() => s"${ship.name} is faster than lightspeed"
  case ship: Any => s"${ship.name} is not faster than lightspeed"
})
