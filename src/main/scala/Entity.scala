package base

import scalafx.scene.shape.Circle
import scalafx.scene.shape.Polygon

abstract class Entity(val owner:Player/*, location:Hex*/){
    //var where = location
    val isCity = false
    var hitpoints = 1
    def attackedBySoldier():Unit = {
        hitpoints = hitpoints - 10
    }
    def attackedByShip():Unit = {
        hitpoints = hitpoints - 5
    }
}


class City (override val owner:Player, val associatedcircle:Circle)
extends Entity(owner){
    hitpoints = 100
    
    override val isCity: Boolean = true

}

class Soldier (override val owner:Player, val associatedpolygon:Polygon)
extends Entity(owner){
    hitpoints = 30
    

}

class BattleShip (override val owner:Player, val associatedpolygon:Polygon)
extends Entity(owner){
    hitpoints = 15
    

}

object Entity{
    val citycost = 40
    val shipcost = 15
    val soldcost = 10
}