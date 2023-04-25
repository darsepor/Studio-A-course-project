package base
import scala.collection.mutable.Buffer
import scala.math.abs
import scala.math.max
import scala.collection.mutable.HashSet
import scalafx.scene.shape.Polygon
class Atlas(val landscape:scala.collection.mutable.HashSet[(Polygon, Hex)]){
    //I'll try to use cube coordinates
    def neighbours(tile:Hex):HashSet[Hex] = {
        val qt = tile.q
        val rt = tile.r
        val st = tile.s


        landscape.map(a => a._2).filter(a => max(abs(a.q-qt), max(abs(a.r-rt), abs(st-a.s)))<=1)
        
    }
    def neighboursWithinRadiusTuples(tile:Hex, radius:Int) = {
        val qt = tile.q
        val rt = tile.r
        val st = tile.s
        landscape.filter(a => max(abs(a._2.q-qt), max(abs(a._2.r-rt), abs(st-a._2.s)))<=radius)
    }
    
}




