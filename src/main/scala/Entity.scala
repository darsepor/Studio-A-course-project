package base

class Entity(val name:String, var owner:Player, var location:Hex){
    
}


class City (name:String, owner:Player, location:Hex)
extends Entity(name, owner, location){
    var hitpoints = 100
    def produceUnit = ???

}

class Soldier (name:String, owner:Player, location:Hex)
extends Entity(name, owner, location){
    var hitpoints = 25
    def buildFort = ???
    def attack = ???

}

class BattleShip (name:String, owner:Player, location:Hex)
extends Entity(name, owner, location){
    var hitpoints = 15
    def rangedAttack = ???

}

