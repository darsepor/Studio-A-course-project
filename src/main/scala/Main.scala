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
        val opponent = new AI("it")
        val root =  new BorderPane{
            
        }

        val size = 30
        val center = 500
        val mapsize = 7
        for(q <- -mapsize to mapsize){
            val lower = scala.math.max(-mapsize, -q-mapsize)
            val upper = scala.math.min(mapsize, -q+mapsize)
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
                    val xcoord = size*g._1+center 
                    val ycoord = size*g._2+center
                    val offsets = Buffer[(Double, Double)]()
                    
                    for(i <- 0 until 6){
                        val angle = 2.0 * scala.math.Pi * i /6
                        offsets += ((scala.math.cos(angle)*size, scala.math.sin(angle)*size))
                    }
                    
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
                            
                            getamenu(event.screenX, event.screenY, polyhex, testsubject)
                            
                        }

                    }
                
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
                if(you.currency>=Entity.citycost&&logichex.unit.isEmpty){
                buildcityhuman(logichex, polyhex)
                you.currency = you.currency - Entity.citycost
                refreshwealth()
                takeTurnAI()
                }
                

            }
            def createbattleshipwrapper(event: ActionEvent): Unit = {
                
                if(you.currency>=Entity.shipcost&&logichex.unit.isEmpty){
                placebattleshiphuman(logichex, polyhex)
                you.currency = you.currency - Entity.shipcost
                refreshwealth()
                takeTurnAI()
                }
            }
            def soldierwrapper(event: ActionEvent): Unit = {
                
                if(you.currency>=Entity.soldcost&&logichex.unit.isEmpty){
                placesoldierhuman(logichex, polyhex)
                you.currency = you.currency - Entity.soldcost
                refreshwealth()
                takeTurnAI()

                }
            }
            val menu = new ContextMenu {
                
                
                val newcity = new MenuItem("New city")
                val soldier = new MenuItem("Recruit soldier")
                val battleship = new MenuItem("Place Battleship")
                val doNothing = new MenuItem("Close")
                
                
                newcity.onAction = buildcitywrapper
                battleship.onAction = createbattleshipwrapper
                soldier.onAction = soldierwrapper
                
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
            }
            
            menu.show(root, x, y)
            
        }
        

        //Function for updating the amount of in-game currency the player has in the GUI
        def refreshwealth():Unit = {
            textProperty.set(s"Gold: ${you.currency}")
        }
        

        //Setting up the ScalaFX scene
        val scene = new Scene(root)
        stage = new JFXApp.PrimaryStage{
            scene = Main.scene
            width = 1000
            height = 1000
            
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
    def handleYourEnemiesAfterCombat(enemies:HashSet[(Hex, Entity)]) = {
        for((logichex, target)<- enemies){
            target match {
                    case ship: BattleShip => {
                        ship.associatedpolygon.fill = Color.Black
                        ship.associatedpolygon.onMouseClicked = (event: MouseEvent) => {
                            
                        }
                        if(ship.hitpoints<=0){
                            logichex.unit=None
                            root.children.remove(ship.associatedpolygon)
                        }
                    }
                    case city: City => {
                        city.associatedcircle.fill = Color.Black
                        city.associatedcircle.onMouseClicked = (event: MouseEvent) => {
                            
                        }
                        if(city.hitpoints<=0){
                            logichex.unit=None
                            root.children.remove(city.associatedcircle)
                        }
                    }
                    case soldier: Soldier => {
                        soldier.associatedpolygon.fill = Color.Black
                        soldier.associatedpolygon.onMouseClicked = (event: MouseEvent) => {
                           
                        }
                        if(soldier.hitpoints<=0){
                            logichex.unit=None
                            root.children.remove(soldier.associatedpolygon)
                        }
                    }
                }
            
        }
        takeTurnAI()
    }

    def renormalizeHexesAfterMovement(hexSet:HashSet[(Polygon, Hex)]):Unit = {
        for((polyhex, logichex) <- hexSet){
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
                            renormalizeHexesAfterMovement(radius)
                            takeTurnAI()
                        }
                        else if(event.button == MouseButton.Secondary) { 
                            renormalizeHexesAfterMovement(radius)
                        }
                        
                       
                    }
            }
            
        }
        def handleSoldierCombat(soldier:Soldier): Unit = {
            
            val currentLocation:Hex = themap.landscape
            .find(a=> a._2.unit.nonEmpty&&a._2.unit.get==soldier).get._2
            val enemies = themap.neighboursWithinRadiusTuples(currentLocation, 1)
            .filter(a => a._2.unit.nonEmpty&&a._2.unit.get.owner!=you).map(a=> (a._2, a._2.unit.get))
            for((logichex, target) <- enemies){
                target match {
                    case ship: BattleShip => {
                        ship.associatedpolygon.fill = Color.DarkRed
                        ship.associatedpolygon.onMouseClicked = (event: MouseEvent) => {
                            if(event.button==MouseButton.Primary){
                                ship.attackedBySoldier()
                                handleYourEnemiesAfterCombat(enemies)
                                
                            }
                        }
                    }
                    case city: City => {
                        city.associatedcircle.fill = Color.DarkRed
                        city.associatedcircle.onMouseClicked = (event: MouseEvent) => {
                            if(event.button==MouseButton.Primary){
                                city.attackedBySoldier()
                                handleYourEnemiesAfterCombat(enemies)
                            }
                        }
                    }
                    case soldier: Soldier => {
                        soldier.associatedpolygon.fill = Color.DarkRed
                        soldier.associatedpolygon.onMouseClicked = (event: MouseEvent) => {
                            if(event.button==MouseButton.Primary){
                                soldier.attackedBySoldier()
                                handleYourEnemiesAfterCombat(enemies)
                            }
                        }
                    }
                }
            }
            

            
           
            
        }

        def handleBattleshipMovement(ship:BattleShip): Unit = {
            val currentLocation:Hex = themap.landscape
            .find(a=> a._2.unit.nonEmpty&&a._2.unit.get==ship).get._2
                val radiusone = themap.neighboursWithinRadiusTuples(currentLocation, 1)
                .filter(_._2.isWater).filter(_._2.unit.isEmpty)
                var radius = HashSet[(Polygon, Hex)]().union(radiusone)
                for((polyhex, logichex) <- radiusone){
                    radius = radius.union(themap.neighboursWithinRadiusTuples(logichex, 1)
                    .filter(_._2.isWater).filter(_._2.unit.isEmpty))
                }
                radius.foreach(_._1.fill =(Color.BlueViolet))
                
                for((polyhex, logichex) <- radius){
                    polyhex.onMouseClicked = (event: MouseEvent) => {
                            if (event.button == MouseButton.Primary) { 
                                currentLocation.unit = None
                                logichex.unit=Some(ship)
                                ship.associatedpolygon.translateX_=(polyhex.translateX.toDouble- 20)
                                ship.associatedpolygon.translateY_=(polyhex.translateY.toDouble- 8)
                                
                                renormalizeHexesAfterMovement(radius)
                                takeTurnAI()
                            }
                            else if(event.button == MouseButton.Secondary) { 
                                renormalizeHexesAfterMovement(radius)
                            }
                            
                        
                        }
                }
        }

        def handleBattleshipCombat(ship:BattleShip):Unit = {
            val currentLocation:Hex = themap.landscape
            .find(a=> a._2.unit.nonEmpty&&a._2.unit.get==ship).get._2
                val enemies = themap.neighboursWithinRadiusTuples(currentLocation, 2)
                .filter(a => a._2.unit.nonEmpty&&a._2.unit.get.owner!=you).map(a=> (a._2, a._2.unit.get))
                for((logichex, target) <- enemies){
                    target match {
                        case ship: BattleShip => {
                            ship.associatedpolygon.fill = Color.DarkRed
                            ship.associatedpolygon.onMouseClicked = (event: MouseEvent) => {
                                if(event.button==MouseButton.Primary){
                                    ship.attackedByShip()
                                    handleYourEnemiesAfterCombat(enemies)
                                    
                                }
                            }
                        }
                        case city: City => {
                            city.associatedcircle.fill = Color.DarkRed
                            city.associatedcircle.onMouseClicked = (event: MouseEvent) => {
                                if(event.button==MouseButton.Primary){
                                    city.attackedByShip()
                                    handleYourEnemiesAfterCombat(enemies)
                                }
                            }
                        }
                        case soldier: Soldier => {
                            soldier.associatedpolygon.fill = Color.DarkRed
                            soldier.associatedpolygon.onMouseClicked = (event: MouseEvent) => {
                                if(event.button==MouseButton.Primary){
                                    soldier.attackedByShip()
                                    handleYourEnemiesAfterCombat(enemies)
                                }
                            }
                        }
                    }
                }


        }
    




    //I will put all AI-related stuff below this line for now.
    
    
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
            opponent.currency=opponent.currency - Entity.citycost
            /*circle.onMouseClicked  = (event: MouseEvent) => {
                        ???
                    }*/

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
            opponent.currency=opponent.currency - Entity.soldcost
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
            opponent.currency=opponent.currency - Entity.shipcost
            /*star.onMouseClicked  = (event: MouseEvent) => {
                        ???
                    }*/
        }


    val remainingSpace = themap.landscape.filter(_._2.unit.isEmpty).filterNot(_._2.isWater)
    .diff(themap.neighboursWithinRadiusTuples(firstCityHuman._2, 4)).toArray
    val firstCityAI = remainingSpace(random.nextInt(remainingSpace.size))
    cityPlacementAI(firstCityAI._2, firstCityAI._1)
    
        def soldiers_ships_cities(logichexes:HashSet[Hex]):(HashSet[Hex], HashSet[Hex], HashSet[Hex]) = {
            val soldiers = HashSet[Hex]()
            val ships = HashSet[Hex]()
            val cities = HashSet[Hex]()
            for( logichex <- logichexes){
                logichex.unit.get match {
                    case s: Soldier => soldiers.addOne(logichex)
                    case b: BattleShip => ships.addOne(logichex)
                    case c: City => cities.addOne(logichex)
                }
            }
            return (soldiers, ships, cities)
        }

    def payout():Unit ={
        you.currency = you.currency + 5
        opponent.currency = opponent.currency + 5
    }
    //A basic rules-based bot
    def takeTurnAI():Unit = {
        val possesions = themap.landscape.filter(a=> a._2.unit.nonEmpty&&a._2.unit.get.owner==opponent)
        /*
            What the bot can do by decreasing priority
            a)Attack something. Prefers to use soldiers. Prefers to attack battleships, then 
                soldiers and, at last, cities. Some randomization will be present, of course.
            b)Place down an unit if enough resources to do so. 
            c)Movement towards things to attack. Generally should move towards your cities.
        */
        //val yourCities = themap.landscape.filter(a=> a._2.unit.nonEmpty&&a._2.unit.get.isCity&&a._2.unit.get.owner==you)
        val tuple = soldiers_ships_cities(possesions.map(_._2))
        var attackResult = attackTargetFirstAI(tuple._1, tuple._2)

        if(attackResult.nonEmpty){
            payout()
            refreshwealth()
            return
        }

        else if(opponent.currency>=40){
            
            
            
            
            val chance = random.nextDouble()
            //Very, very limited. Spawns units towards the player's spawn. Not much chasing.
            if(chance<=0.5){
                val soldierCandidates = tuple._3.flatMap(a => themap.neighboursWithinRadiusTuples(a, 1))
                .filter(a => a._2.unit.isEmpty&&(!a._2.isWater))
                val location = soldierCandidates.minBy(a => themap.distance(a._2, firstCityHuman._2))
                soldierPlacementAI(location._2, location._1)
            }
            else if(chance<=0.8){
                val shipCandidates = tuple._3.flatMap(a => themap.neighboursWithinRadiusTuples(a, 1))
                .filter(a => a._2.unit.isEmpty&&(a._2.isWater))
                val location = shipCandidates.minBy(a => themap.distance(a._2, firstCityHuman._2))
                battleshipPlacementAI(location._2, location._1)
            }
            else{
                val cityCandidates = possesions.map(_._2).flatMap(a => themap.neighboursWithinRadiusTuples(a, 1))
                .filter(a => a._2.unit.isEmpty&&(!a._2.isWater))
                val location = cityCandidates.minBy(a => themap.distance(a._2, firstCityHuman._2))
                cityPlacementAI(location._2, location._1)
            }
            
        }
        else{
            val humanPossessions = themap.landscape.filter(a => a._2.unit.nonEmpty&&a._2.unit.get.owner==you).toArray
            for(movable <- possesions.filterNot(_._2.unit.get.isCity)){

                movable._2.unit.get match {
                    case b: BattleShip => {
                        val space = themap.neighboursWithinRadiusTuples(movable._2, 2).filter(a => a._2.unit.isEmpty&&a._2.isWater)
                        if(space.nonEmpty){
                            val where = space.minBy(a => themap.distance(a._2, humanPossessions(random.nextInt(humanPossessions.size))._2))
                            movable._2.unit=None
                            where._2.unit=Some(b)
                            b.associatedpolygon.translateX_=(where._1.translateX.toDouble-20)
                            b.associatedpolygon.translateY_=(where._1.translateY.toDouble-8)
                            payout()
                            refreshwealth()
                            return
                        }
                    }
                    case s: Soldier => {
                        val space = themap.neighboursWithinRadiusTuples(movable._2, 1).filter(a => a._2.unit.isEmpty&&(!a._2.isWater))
                        if(space.nonEmpty){
                            val where = space.minBy(a => themap.distance(a._2, humanPossessions(random.nextInt(humanPossessions.size))._2))
                            movable._2.unit=None
                            where._2.unit=Some(s)
                            s.associatedpolygon.translateX_=(where._1.translateX.toDouble-10)
                            s.associatedpolygon.translateY_=(where._1.translateY.toDouble-10)
                            payout()
                            refreshwealth()
                            return

                        }
                    }
                }
            }
        }


        payout()
        refreshwealth()
        return
    }
    
    def attackTargetFirstAI(soldiers:HashSet[Hex], ships:HashSet[Hex]):Option[Entity] = {
        //Six sets of decreasing priority
        val soldierTargets = soldiers.map(a => themap.neighbours(a)
        .filter(b => b.unit.nonEmpty&&b.unit.get.owner==you)).flatten//.map(a => (a, 0))
        val shipTargets = ships.map(a => themap.neighboursWithinRadiusTuples(a, 2).map(_._2)
        .filter(b => b.unit.nonEmpty&&b.unit.get.owner==you)).flatten//.map(a => (a, 1))
        
        val soldTargTuple = soldiers_ships_cities(soldierTargets)
        val shipTargTuple =  soldiers_ships_cities(shipTargets)
        val list = List(soldTargTuple._2, soldTargTuple._1, soldTargTuple._3).flatten.map(a=> (a, 0))
        .concat(List(shipTargTuple._2, shipTargTuple._1, shipTargTuple._3).flatten.map(a=> (a, 1)))
        if(list.nonEmpty){
            val finalTarget = list(0)
            val randDouble = random.nextDouble()
            if(randDouble<=0.8){
                if(finalTarget._2==0)
                {
                    finalTarget._1.unit.get.attackedBySoldier()
                    
                }
                else{
                    finalTarget._1.unit.get.attackedByShip()
                }
                
                
                
            }
            else{
                val finalTarget = list(random.nextInt(list.size))
                if(finalTarget._2==0)
                {
                    finalTarget._1.unit.get.attackedBySoldier()
                    
                }
                else{
                    finalTarget._1.unit.get.attackedByShip()
                }



                
            }
            if(finalTarget._1.unit.get.hitpoints<=0){
                    val keepTheEntity = finalTarget._1.unit
                    finalTarget._1.unit=None
                    keepTheEntity.get match{
                        case b: BattleShip => root.children.remove(b.associatedpolygon)
                        case c: City => root.children.remove(c.associatedcircle)
                        case s: Soldier => root.children.remove(s.associatedpolygon)
                    }
                    return keepTheEntity
                }
            return finalTarget._1.unit
        }

        else{
            return None
        }
    }
    






    


    


}