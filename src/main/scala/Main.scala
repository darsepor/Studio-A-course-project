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
import javafx.scene.input
import scala.collection.mutable.HashSet
object Main extends JFXApp {
    val random = scala.util.Random



    //MAP CREATION. Setting things up
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

                    val determine = random.nextDouble()
                    var testsubject:Hex = null
                    if(determine<=0.36){
                        testsubject = water
                    }
                    else if (determine<=0.56){
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
                        if (event.button == MouseButton.Secondary) { 
                            //aMenuOpen = true
                            getamenu(event.screenX, event.screenY, polyhex, testsubject)
                            
                        }

                    }
                //}
            }
        }
        //Placing the first city belonging to the player pseudo-randomly, not on water
        val inArray = themap.landscape.toArray.filter(a=> !a._2.isWater)
        var firstCityHuman = inArray(random.nextInt(inArray.size))
        buildcityhuman(firstCityHuman._2, firstCityHuman._1)



        //Couple of helper functions for placing entities
        def doWeHaveYourCityNear(logichex:Hex):Boolean = {
            themap.neighbours(logichex)
            .exists(a => a.unit.nonEmpty&&a.unit.get.isCity&&a.unit.get.owner==you)
        }
        def gotAnyUnitsNear(logichex:Hex):Boolean = {
            themap.neighbours(logichex)
            .exists(a => a.unit.nonEmpty&&a.unit.get.owner==you)
        }
        //A menu for placing entities, shows up when a "polyhex" receives a secondary mouse button press
        //(See above in the set-up)
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
        //Old test-helper functions
        def action1(event: ActionEvent): Unit = {
            you.currency=0
            //textProperty.set(s"Gold: ${you.currency}")
            
            refreshwealth()
        }
        def action13(event: ActionEvent): Unit = {
            you.currency=75
            //textProperty.set(s"Gold: ${you.currency}")
            refreshwealth()
        }

        //Function for updating the amount of in-game currency the player has in the GUI
        def refreshwealth():Unit = {
            textProperty.set(s"Gold: ${you.currency}")
        }
        /* a test if neighbours works for(i <- themap.neighbours(Water(None, 0, 0, 0))){
            polygontohex.find(a => a._2==i).get._1.fill_=(Color.Yellow)
        }*/
        //val col = 30
        //val row = 30

        //Setting up the ScalaFX scene
        val scene = new Scene(root)
        stage = new JFXApp.PrimaryStage{
            scene = Main.scene
            width = 1000
            height = 1000
            /*width = row*width + width/2
            height = row * heighth + heighth/4 **/
        }
    
        
        //In-game currency being shown in the GUI
        val textProperty = StringProperty(s"Gold: ${you.currency}")
        val text = new Text()
        text.font = Font.font("Arial", FontWeight.Bold, 18)
        text.fill = Color.Black
        text.textProperty().bind(textProperty)
        root.top_=(text)
        

        //Functions for placing entities, called in by the menu above
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
            val city = new City( you, circle)
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
            
            val ship = new BattleShip(you, star)
            logichex.unit = Option(ship)
            star.onMouseClicked  = (event: MouseEvent) => {
                        if(event.button==MouseButton.Primary) { 
                            handleBattleshipMovement(ship)
                            
                        }
                        else if(event.button==MouseButton.Secondary) { 
                            handleBattleshipCombat(ship)
                            
                        }
                    }
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
            val soldier = new Soldier(you, star)
            logichex.unit = Option(soldier)

            star.onMouseClicked  = (event: MouseEvent) => {
                        if(event.button==MouseButton.Primary) { 
                            handleSoldierMovement(soldier)
                            
                        }
                        else if(event.button==MouseButton.Secondary) { 
                            handleSoldierCombat(soldier)
                            
                        }
                    }
        }


        
    //Functions for moving player-owned entities and combat

        def handleSoldierMovement(soldier:Soldier): Unit = {
            
            val currentLocation:Hex = themap.landscape
            .find(a=> a._2.unit.nonEmpty&&a._2.unit.get==soldier).get._2
            val radius = themap.neighboursWithinRadiusTuples(currentLocation, 1)
            .filterNot(_._2.isWater).filter(_._2.unit.isEmpty)
            radius.foreach(_._1.fill =(Color.BlueViolet))
            
            for((polyhex, logichex) <- radius){
                polyhex.onMouseClicked = (event: MouseEvent) => {
                        if (event.button == MouseButton.Primary) { 
                            currentLocation.unit = None
                            logichex.unit=Some(soldier)
                            soldier.associatedpolygon.translateX_=(polyhex.translateX.toDouble- 10)
                            soldier.associatedpolygon.translateY_=(polyhex.translateY.toDouble- 10)
                                for((polyhex, logichex) <- radius){
                                    logichex match {
                                        case Hill(inhabitant, q, r, s) => polyhex.fill_=(Color.Gray)
                                        case Water(inhabitant, q, r, s) => polyhex.fill_=(Color.LightCyan)
                                        case Plain(inhabitant, q, r, s) => polyhex.fill_=(Color.LightGreen)
                                    }
                                    polyhex.onMouseClicked = (event: MouseEvent) => {
                                        if (event.button == MouseButton.Secondary) { 
                                            
                                            getamenu(event.screenX, event.screenY, polyhex, logichex)
                                            
                                        }

                                    }
                                }
                        }
                        else if(event.button == MouseButton.Secondary) { 
                            
                                for((polyhex, logichex) <- radius){
                                    logichex match {
                                        case Hill(inhabitant, q, r, s) => polyhex.fill_=(Color.Gray)
                                        case Water(inhabitant, q, r, s) => polyhex.fill_=(Color.LightCyan)
                                        case Plain(inhabitant, q, r, s) => polyhex.fill_=(Color.LightGreen)
                                    }
                                    polyhex.onMouseClicked = (event: MouseEvent) => {
                                        if (event.button == MouseButton.Secondary) { 
                                            
                                            getamenu(event.screenX, event.screenY, polyhex, logichex)
                                            
                                        }

                                    }
                                }
                        }
                        
                       
                    }
            }
            
        }
        def handleSoldierCombat(soldier:Soldier): Unit = {
            
            val currentLocation:Hex = themap.landscape.find(a=> a._2.unit.nonEmpty&&a._2.unit.get==soldier).get._2
            val enemies = themap.neighboursWithinRadiusTuples(currentLocation, 1)
            .filter(a => a._2.unit.nonEmpty&&a._2.unit.get.owner!=you).map(_._2.unit.get)
            for(target <- enemies){
                target match {
                    case a: BattleShip => a.associatedpolygon.fill = Color.DarkRed
                    case b: City => b.associatedcircle.fill = Color.DarkRed
                    case c: Soldier => c.associatedpolygon.fill = Color.DarkRed
                }
            }
            

            
           
            
        }

    def handleBattleshipMovement(ship:BattleShip): Unit = {
        val currentLocation:Hex = themap.landscape.find(a=> a._2.unit.nonEmpty&&a._2.unit.get==ship).get._2
            val radiusone = themap.neighboursWithinRadiusTuples(currentLocation, 1).filter(_._2.isWater).filter(_._2.unit.isEmpty)
            var radius = HashSet[(Polygon, Hex)]().union(radiusone)
            for((polyhex, logichex) <- radiusone){
                radius = radius.union(themap.neighboursWithinRadiusTuples(logichex, 1).filter(_._2.isWater).filter(_._2.unit.isEmpty))
            }
            radius.foreach(_._1.fill =(Color.BlueViolet))
            
            for((polyhex, logichex) <- radius){
                polyhex.onMouseClicked = (event: MouseEvent) => {
                        if (event.button == MouseButton.Primary) { 
                            currentLocation.unit = None
                            logichex.unit=Some(ship)
                            ship.associatedpolygon.translateX_=(polyhex.translateX.toDouble- 20)
                            ship.associatedpolygon.translateY_=(polyhex.translateY.toDouble- 8)
                                for((polyhex, logichex) <- radius){
                                    polyhex.fill = Color.LightCyan
                                    polyhex.onMouseClicked = (event: MouseEvent) => {
                                        if (event.button == MouseButton.Secondary) { 
                                            //aMenuOpen = true
                                            getamenu(event.screenX, event.screenY, polyhex, logichex)
                                            
                                        }

                                    }
                                }
                        }
                        else if(event.button == MouseButton.Secondary) { 
                            
                                for((polyhex, logichex) <- radius){
                                    polyhex.fill = Color.LightCyan
                                    polyhex.onMouseClicked = (event: MouseEvent) => {
                                        if (event.button == MouseButton.Secondary) { 
                                            //aMenuOpen = true
                                            getamenu(event.screenX, event.screenY, polyhex, logichex)
                                            
                                        }

                                    }
                                }
                        }
                        
                       
                    }
            }
    }
    def handleBattleshipCombat(ship:BattleShip):Unit = {
        val currentLocation:Hex = themap.landscape.find(a=> a._2.unit.nonEmpty&&a._2.unit.get==ship).get._2
            val enemies = themap.neighboursWithinRadiusTuples(currentLocation, 2)
            .filter(a => a._2.unit.nonEmpty&&a._2.unit.get.owner!=you).map(_._2.unit.get)
            for(target <- enemies){
                target match {
                    case a: BattleShip => a.associatedpolygon.fill = Color.DarkRed
                    case b: City => b.associatedcircle.fill = Color.DarkRed
                    case c: Soldier => c.associatedpolygon.fill = Color.DarkRed
                }
            }

    }
    def disableAllPolyhexesExcept(enabled:HashSet[(Polygon,Hex)]){
        ???
    }
    def disableAllUnitsExcept(enabled:Array[Entity]){
        ???
    }
    //I will put all AI-related stuff below this line for now.
    val opponent = new AI("it")
    def cityPlacementAI(logichex:Hex, polyhex:Polygon):Unit = {
        if(logichex.unit.nonEmpty){
                return
            }
            
            val circle = new Circle {
                centerX = polyhex.translateX.toDouble
                centerY = polyhex.translateY.toDouble
                radius = 15
                fill = Color.Black 
                stroke = Color.Black 
            }
            root.children += circle
            val city = new City(opponent, circle)
            logichex.unit = Option(city)
            circle.onMouseClicked  = (event: MouseEvent) => {
                        ???
                    }
    }
    def soldierPlacementAI(logichex:Hex, polyhex:Polygon):Unit = {
            if(logichex.unit.nonEmpty){
                return
            }
            
            val star:Polygon =  Polygon(
                0.00, 0.00,
                20.0, 0.00,
                00.0, 20.0,
                20.0, 20.0,
                
                
                
            )
            
            star.fill = Color.Black 
            star.stroke = Color.Black 
            star.translateX_=(polyhex.translateX.toDouble- 10)
            star.translateY_=(polyhex.translateY.toDouble- 10)
            root.children += star
            val soldier = new Soldier(opponent, star)
            logichex.unit = Option(soldier)

            /*star.onMouseClicked  = (event: MouseEvent) => {
                        ???
                    }*/
        }
    def battleshipPlacementAI(logichex:Hex, polyhex:Polygon):Unit = {
            if(logichex.unit.nonEmpty){
                return
            }

            val star:Polygon =  Polygon(
                10.0, 0.0,
                30.0, 0.0,
                40.0, 16.0,
                0.0, 16.0
                
                
            )
            
            star.fill = Color.Black 
            star.stroke = Color.Black 
            star.translateX_=(polyhex.translateX.toDouble - 20)
            star.translateY_=(polyhex.translateY.toDouble -8)
            root.children += star
            
            val ship = new BattleShip(opponent, star)
            logichex.unit = Option(ship)
            /*star.onMouseClicked  = (event: MouseEvent) => {
                        ???
                    }*/
        }


    val remainingSpace = themap.landscape.filter(_._2.unit.isEmpty).filterNot(_._2.isWater)
    .diff(themap.neighboursWithinRadiusTuples(firstCityHuman._2, 4)).toArray
    val firstCityAI = remainingSpace(random.nextInt(remainingSpace.size))
    cityPlacementAI(firstCityAI._2, firstCityAI._1)



    


}