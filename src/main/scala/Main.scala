package base
import scala.collection.mutable.Buffer
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.layout.Pane
import scalafx.scene.control.{ContextMenu, MenuItem}
import scalafx.scene.text.{Font, FontWeight, Text}
import scalafx.scene.Scene
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Polygon
import scalafx.geometry.Insets
import scala.collection.immutable.HashMap
import scalafx.scene.layout.BorderPane
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.MouseButton
import scalafx.stage.Window
import scalafx.geometry.Pos
import scalafx.event.ActionEvent
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.scene.shape.Circle
import scala.util.control.Breaks._
object Main extends JFXApp {
    val random = scala.util.Random
    var turn = 0
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
        //val polygontohex = scala.collection.mutable.HashSet[(Polygon, Hex)]()
        val themap = new Atlas(new scala.collection.mutable.HashSet[(Polygon, Hex)]())
        val you =  Human("You")
        val root =  new BorderPane{
            
        }
        val size = 30
        val center = 500
        var aMenuOpen = false
        //val polyhexes = Buffer.tabulate(30, 30, 30)((_, _, _) => null)
        for(q <- -5 to 5){
            val lower = scala.math.max(-5, -q-5)
            val upper = scala.math.min(5, -q+5)
            for(r <- lower to upper){
                //for(k <- 0 until 4){
                    val water =  Water(None, q, r, -q-r)
                    val plain =  Plain(None, q, r, -q-r)
                    val hill =  Hill(None, q, r, -q-r)

                    val determine = random.nextInt(3)
                    var testsubject:Hex = null
                    if(determine==0){
                        testsubject = water
                    }
                    else if (determine ==1){
                        testsubject = hill
                    }
                    else{
                        testsubject = plain
                    }
                    
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
                    //offsets.map(a => (a._1+1000, a._2))
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
                    
                    polyhex.stroke = Color.Black
                    testsubject match {
                        case Hill(inhabitant, q, r, s) => polyhex.fill_=(Color.Gray)
                        case Water(inhabitant, q, r, s) => polyhex.fill_=(Color.LightCyan)
                        case Plain(inhabitant, q, r, s) => polyhex.fill_=(Color.LightGreen)
                    }
                    root.children += polyhex
                    themap.landscape.addOne((polyhex, testsubject))

                    polyhex.onMouseClicked = (event: MouseEvent) => {
                        if (event.button == MouseButton.Secondary&&(!aMenuOpen)) { 
                            //aMenuOpen = true
                            getamenu(event.screenX, event.screenY, polyhex, testsubject)
                            
                        }
                    //themap.landscape.add(testsubject)

                    }
                //}
            }
        }
        val inArray = themap.landscape.toArray
        var test = inArray(random.nextInt(inArray.size))
        while(test._2.isWater){
            test = inArray(random.nextInt(inArray.size))
        }
        buildcityhuman(test._2, test._1)
        def doWeHaveYourCityNear(logichex:Hex):Boolean = {
            themap.neighbours(logichex)
            .exists(a => a.unit.nonEmpty&&a.unit.get.isCity&&a.unit.get.owner==you)
        }
        def gotAnyUnitsNear(logichex:Hex):Boolean = {
            themap.neighbours(logichex)
            .exists(a => a.unit.nonEmpty&&a.unit.get.owner==you)
        }

        def getamenu(x: Double, y: Double, polyhex:Polygon, logichex:Hex) = {

            def buildcitywrapper(event: ActionEvent): Unit = {
                buildcityhuman(logichex, polyhex)
                //aMenuOpen = false

            }
            def createbattleshipwrapper(event: ActionEvent): Unit = {
                placebattleshiphuman(logichex, polyhex)
                //aMenuOpen = false
            }
            def soldierwrapper(event: ActionEvent): Unit = {
                placesoldierhuman(logichex, polyhex)
                //aMenuOpen = false
            }
            val menu = new ContextMenu {
                
                //val menuitem1 = new MenuItem("Action1")
                //val menuitem2 = new MenuItem("Action13")
                val newcity = new MenuItem("New city")
                val soldier = new MenuItem("Recruit soldier")
                val battleship = new MenuItem("Place Battleship")
                val doNothing = new MenuItem("Close")
                
                //menuitem1.onAction = action1
                //menuitem2.onAction = action13
                newcity.onAction = buildcitywrapper
                battleship.onAction = createbattleshipwrapper
                soldier.onAction = soldierwrapper
                //items.addAll(menuitem1, menuitem2, newcity, battleship, soldier)
                logichex match {
                    case Water(inhabitant, q, r, s) => {
                        if(logichex.unit.isEmpty&&doWeHaveYourCityNear(logichex)){
                            items.addAll(battleship, doNothing)
                        }
                    
                    }
                    case _ => {
                        if(logichex.unit.isEmpty&&doWeHaveYourCityNear(logichex)){
                            items.addAll(soldier, doNothing)
                        }
                        if(logichex.unit.isEmpty&&gotAnyUnitsNear(logichex)){
                            items.addAll(newcity, doNothing)
                        }
                    }
                }
                //items.addOne(doNothing)
            }
            
            menu.show(root, x, y)
            
        }

        def action1(event: ActionEvent): Unit = {
            you.currency=0
            //textProperty.set(s"Gold: ${you.currency}")
            
            refreshwealth()
        }
        def action13(event: ActionEvent): Unit = {
            you.currency=69
            //textProperty.set(s"Gold: ${you.currency}")
            refreshwealth()
        }
        def refreshwealth():Unit = {
            textProperty.set(s"Gold: ${you.currency}")
        }
        /* a test if neighbours works for(i <- themap.neighbours(Water(None, 0, 0, 0))){
            polygontohex.find(a => a._2==i).get._1.fill_=(Color.Yellow)
        }*/
        //val col = 30
        //val row = 30
        val scene = new Scene(root)
        stage = new JFXApp.PrimaryStage{
            scene = Main.scene
            width = 1000
            height = 1000
            /*width = row*width + width/2
            height = row * heighth + heighth/4 **/
        }
    
        

        val textProperty = StringProperty(s"Gold: ${you.currency}")
        val text = new Text()
        text.font = Font.font("Arial", FontWeight.Bold, 18)
        text.fill = Color.Black
        text.textProperty().bind(textProperty)
        root.top_=(text)
        
        def buildcityhuman(logichex:Hex, polyhex:Polygon):Unit = {
            if(logichex.unit.nonEmpty){
                return
            }
            
            val circle = new Circle {
                centerX = polyhex.translateX.toDouble
                centerY = polyhex.translateY.toDouble
                radius = 15
                fill = Color.White 
                stroke = Color.Black 
            }
            root.children += circle
            val city = City( you, circle)
            logichex.unit = Option(city)
        }
    
        def placebattleshiphuman(logichex:Hex, polyhex:Polygon):Unit = {
            if(logichex.unit.nonEmpty){
                return
            }
            val star:Polygon =  Polygon(
                10.0, 0.0,
                30.0, 0.0,
                40.0, 16.0,
                0.0, 16.0
                
                
            )
            
            star.fill = Color.White 
            star.stroke = Color.Black 
            star.translateX_=(polyhex.translateX.toDouble - 20)
            star.translateY_=(polyhex.translateY.toDouble -8)
            root.children += star
            val ship = BattleShip(you, star)
            logichex.unit = Option(ship)
        }

        def placesoldierhuman(logichex:Hex, polyhex:Polygon):Unit = {
            if(logichex.unit.nonEmpty){
                return
            }
            
            val star:Polygon =  Polygon(
                0.00, 0.00,
                20.0, 0.00,
                00.0, 20.0,
                20.0, 20.0,
                
                
                
            )
            
            star.fill = Color.White 
            star.stroke = Color.Black 
            star.translateX_=(polyhex.translateX.toDouble- 10)
            star.translateY_=(polyhex.translateY.toDouble- 10)
            root.children += star
            val soldier = Soldier(you, star)
            logichex.unit = Option(soldier)
        }
        
}