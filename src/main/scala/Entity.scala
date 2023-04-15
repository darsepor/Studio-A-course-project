package base

import scalafx.scene.shape.Circle

class Entity(val name:String, val owner:Player/*, location:Hex*/){
    //var where = location
}


case class City (override val name:String, override val owner:Player, val associatedcircle:Circle)
extends Entity(name, owner){
    var hitpoints = 100
    def produceUnit = ???

}

case class Soldier (override val name:String, override val owner:Player)
extends Entity(name, owner){
    var hitpoints = 25
    def buildFort = ???
    def attack = ???

}

case class BattleShip (override val name:String, override val owner:Player)
extends Entity(name, owner){
    var hitpoints = 15
    def rangedAttack = ???

}

