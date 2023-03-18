package base
import scala.collection.mutable.Buffer
import scala.collection.immutable.HashSet
class Atlas(var landscape:Buffer[Buffer[Buffer[Hex]]]){
    //I'll try to use cube coordinates
    def neighbours(x:Int, y:Int, z:Int):HashSet[Hex] = {
        HashSet[Hex](landscape(x-1)(y+1)(z),landscape(x+1)(y-1)(z),
        landscape(x-1)(y)(z+1),landscape(x+1)(y)(z-1),landscape(x)(y-1)(z+1),
        landscape(x)(y+1)(z-1))

    }
    
}




