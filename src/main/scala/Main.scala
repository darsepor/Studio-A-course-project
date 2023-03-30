package base
import scala.collection.mutable.Buffer
object Main extends App {
  
    val map = new Atlas(Buffer.tabulate(30, 30, 30)((_, _, _) => null))
    val random = scala.util.Random

        for (x <- 1 until 30) {
            for (y <- 0 until 30) {
                for (z <- 0 until 29) {
                    val determination = random.nextInt(4)
                    map.landscape(x)(y)(z) = determination match {
                        case 0 => new Water(None)
                        case 1 => new Plain(None)
                        case 2 => new Hill(None)
                        case 3 => new Fort(None)
                    }
                }
            }
        }
    

    val players = List(new AI("Opponent"), new Human("You"))
    map.landscape(0)(0)(0) = new Plain(Some(City("Robotopolis", players(0))))
    map.landscape(30)(30)(30) = new Plain(Some(City("Hometown", players(0))))
    var turn = 0
    var gameOver = false

    while(!gameOver){
        players(0).advance_turn
        

    }


}