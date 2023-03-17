package base

class Player(var name:String){
    var currency = 100
}

class Human(name:String) extends Player(name)

class AI(name:String) extends Player(name)