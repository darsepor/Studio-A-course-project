package base
import scala.collection.mutable.Buffer
import scala.math.abs
import scala.math.max
import scala.collection.mutable.HashSet
import scalafx.scene.shape.Polygon
class Atlas(val landscape:scala.collection.mutable.HashSet[(Polygon, Hex)]){
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
    def distance(tile1:Hex, tile2:Hex):Int = {
        (abs(tile1.q-tile2.q)+abs(tile1.r-tile2.r)+abs(tile1.s-tile2.s))/2
    }

}




