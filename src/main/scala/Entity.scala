package base

import scalafx.scene.shape.Circle
import scalafx.scene.shape.Polygon

class Entity(val owner:Player/*, location:Hex*/){
    //var where = location
    val isCity = false
}


case class City (override val owner:Player, val associatedcircle:Circle)
extends Entity(owner){
    var hitpoints = 100
    def produceUnit = ???
    override val isCity: Boolean = true

}

case class Soldier (override val owner:Player, val associatedpolygon:Polygon)
extends Entity(owner){
    var hitpoints = 25
    def buildFort = ???
    def attack = ???

}

case class BattleShip (override val owner:Player, val associatedpolygon:Polygon)
extends Entity(owner){
    var hitpoints = 15
    def rangedAttack = ???

}

