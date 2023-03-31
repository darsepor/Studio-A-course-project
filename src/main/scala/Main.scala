package base
import scala.collection.mutable.Buffer
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.layout.Pane

import scalafx.scene.Scene
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Polygon
import scalafx.geometry.Insets
object Main extends JFXApp {

    
/*
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
    map.landscape(29)(29)(29) = new Plain(Some(City("Hometown", players(0))))
    var turn = 0
    var gameOver = false
*/
    /*while(!gameOver){
        players(0).advance_turn


    }
    
    
*/
        
        val root =  new Pane{
            
        }
        val size = 15
        val center = 150
        
        //val polyhexes = Buffer.tabulate(30, 30, 30)((_, _, _) => null)
        for(q <- -5 to 5){
            val lower = scala.math.max(-5, -q-5)
            val upper = scala.math.min(5, -q+5)
            for(r <- lower to upper){
                //for(k <- 0 until 4){
                    val testsubject = new Water(None, q, r, -q-r)
                    val g = LayoutOrient.tuplecoords(testsubject)
                    val xcoord = size*g._1+center //width*(i + j/2.0)
                    val ycoord = size*g._2+center//heighth*(j*0.75+k*1.5)
                    val offsets = Buffer[(Double, Double)]()
                    
                    for(i <- 0 until 6){
                        val angle = 2.0 * scala.math.Pi * i /6
                        offsets += ((scala.math.cos(angle)*size, scala.math.sin(angle)*size))
                    }
                    /*width/2, 0,
                    width, heighth/4,
                    width, heighth*3/4,
                    width/2, heighth,
                    0, heighth*3/4,
                    0, heighth/4,*/
                    offsets.map(a => (a._2+center, a._2+center))
                    val polyhex:Polygon =   Polygon(
                        offsets(0)._1, offsets(0)._2,
                        offsets(1)._1, offsets(1)._2,
                        offsets(2)._1, offsets(2)._2,
                        offsets(3)._1, offsets(3)._2,
                        offsets(4)._1, offsets(4)._2,
                        offsets(5)._1, offsets(5)._2,

                    )
                    polyhex.translateX = xcoord
                    polyhex.translateY = ycoord
                    //polyhex.fill = Color.White
                    if(q==0&& r==0){
                     //   polyhex.fill = Color.Black
                    }
                    else{
                        polyhex.fill = Color.White
                    }
                    polyhex.stroke = Color.Black
                    
                    root.children += polyhex
                    
                //}
            }
        }
        val col = 30
        val row = 30
        val scene = new Scene(root)
        stage = new JFXApp.PrimaryStage{
            scene = Main.scene
            width = 500
            height = 500
            /*width = row*width + width/2
            height = row * heighth + heighth/4 **/
        }

    
    
}