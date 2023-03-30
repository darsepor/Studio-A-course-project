package base
import scala.collection.mutable.Buffer

abstract class Hex(inhabitant:Option[Entity]){
    var unit = inhabitant
}

case class Water(inhabitant:Option[Entity]) extends Hex(inhabitant){
    
}

case class Plain(inhabitant:Option[Entity]) extends Hex(inhabitant){

}

case class Hill(inhabitant:Option[Entity]) extends Hex(inhabitant){

}
    
case class Fort(inhabitant:Option[Entity]) extends Hex(inhabitant){

}