package base
import scala.collection.mutable.Buffer
import scala.math.sqrt
abstract class Hex(inhabitant:Option[Entity], val q:Int, val r:Int, val s:Int){
    var unit = inhabitant
    assert(r+q+s==0)
    val isWater = false
}

case class Water(inhabitant:Option[Entity], override val q:Int,
override val r:Int, override val s:Int) extends Hex(inhabitant, r, q, s){
    override val isWater: Boolean = true
}

case class Plain(inhabitant:Option[Entity], override val q:Int,
override val r:Int, override val s:Int) extends Hex(inhabitant, r, q, s){

}

case class Hill(inhabitant:Option[Entity], override val q:Int,
override val r:Int, override val s:Int) extends Hex(inhabitant, r, q, s){
}
    
case class Fort(inhabitant:Option[Entity], override val q:Int,
override val r:Int, override val s:Int) extends Hex(inhabitant, r, q, s){

}

object LayoutOrient{
    /*(3.0 / 2.0, 0.0, sqrt(3.0) / 2.0, sqrt(3.0),
                2.0 / 3.0, 0.0, -1.0 / 3.0, sqrt(3.0) / 3.0,
                0.0);*/
    val f0 = 3.0/2.0
    val f1 = 0.0
    val f2 = sqrt(3.0)/2.0
    val f3 = sqrt(3.0)
    val b0 = 2.0 / 3.0
    val b1 = 0.0
    val b2 = -1.0 / 3.0
    val b3 = sqrt(3.0) / 3.0
    val initang = 0.0
    def tuplecoords(h:Hex): (Double, Double) = {

        ((f0 * h.q + f1 * h.r), (f2 * h.q + f3 * h.r))
    }

}
