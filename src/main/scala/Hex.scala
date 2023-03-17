package base
import scala.collection.mutable.Buffer

class Hex(var inhabitant:Option[Entity]) 

class Water(inhabitant:Option[Entity]) extends Hex(inhabitant)

class Plain(inhabitant:Option[Entity]) extends Hex(inhabitant)

class Hill(inhabitant:Option[Entity]) extends Hex(inhabitant)
    
class Fort(inhabitant:Option[Entity]) extends Hex(inhabitant)