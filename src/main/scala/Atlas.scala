package base
import scala.collection.mutable.Buffer
import scala.math.abs
import scala.collection.mutable.HashSet
import scalafx.scene.shape.Polygon
class Atlas(val landscape:scala.collection.mutable.HashSet[(Polygon, Hex)]){
    //I'll try to use cube coordinates
    def neighbours(tile:Hex):HashSet[Hex] = {
        val qt = tile.q
        val rt = tile.r
        val st = tile.s


        landscape.map(a => a._2).filter(a => (abs(a.q-qt)==1&&abs(a.r-rt)==1&&st==a.s)||(abs(a.q-qt)==1&&abs(a.s-st)==1&&rt==a.r)||(abs(a.s-st)==1&&abs(a.r-rt)==1&&qt==a.q))
    }
    
    
}




